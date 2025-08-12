package com.plazoletadecomidas.plazoleta_ms_plazoleta.application.handler;

import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto.OrderDetailResponseDto;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto.OrderItemDto;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto.OrderRequestDto;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto.OrderResponseDto;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.mapper.OrderMapper;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.api.OrderServicePort;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.api.RestaurantServicePort;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.Order;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.Role;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.exception.OrderAlreadyExistsException;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.exception.UnauthorizedException;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.security.AuthValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderHandlerTest {

    @Mock private OrderServicePort orderServicePort;
    @Mock private OrderMapper orderMapper;
    @Mock private AuthValidator authValidator;
    @Mock private RestaurantServicePort restaurantServicePort;

    @InjectMocks
    private OrderHandler orderHandler;

    private String tokenCliente;
    private String tokenEmpleado;

    private UUID customerId;
    private UUID employeeId;
    private UUID restaurantId;
    private UUID orderId;

    private OrderRequestDto requestDto;
    private Order orderModel;
    private OrderResponseDto responseDto;

    @BeforeEach
    void setUp() {
        tokenCliente = "Bearer token-cliente";
        tokenEmpleado = "Bearer token-empleado";

        customerId = UUID.randomUUID();
        employeeId = UUID.randomUUID();
        restaurantId = UUID.randomUUID();
        orderId = UUID.randomUUID();

        OrderItemDto itemDto = new OrderItemDto();
        itemDto.setDishId(UUID.randomUUID().toString());
        itemDto.setQuantity(2);

        requestDto = new OrderRequestDto();
        requestDto.setRestaurantId(restaurantId.toString());
        requestDto.setItems(List.of(itemDto));

        orderModel = new Order();
        orderModel.setId(orderId);
        orderModel.setCustomerId(customerId);
        orderModel.setRestaurantId(restaurantId);
        orderModel.setStatus("PENDIENTE");

        responseDto = new OrderResponseDto();
        // OrderResponseDto expone setId(String), así que dejamos String aquí
        responseDto.setId(orderId.toString());
        responseDto.setStatus("PENDIENTE");
    }

    // ---------- createOrder ----------

    @Test
    @DisplayName("createOrder: debe crear un pedido si el cliente es válido")
    void createOrder_clienteValido() {
        when(authValidator.validate(tokenCliente, Role.CLIENTE)).thenReturn(customerId);
        when(orderMapper.toModel(requestDto, customerId.toString())).thenReturn(orderModel);
        when(orderServicePort.createOrder(orderModel)).thenReturn(orderModel);
        when(orderMapper.toResponse(orderModel)).thenReturn(responseDto);

        OrderResponseDto result = orderHandler.createOrder(requestDto, tokenCliente);

        assertNotNull(result);
        assertEquals("PENDIENTE", result.getStatus());
        assertEquals(orderId.toString(), result.getId());

        verify(authValidator).validate(tokenCliente, Role.CLIENTE);
        verify(orderMapper).toModel(requestDto, customerId.toString());
        verify(orderServicePort).createOrder(orderModel);
        verify(orderMapper).toResponse(orderModel);
        verifyNoMoreInteractions(orderServicePort, orderMapper, authValidator, restaurantServicePort);
    }

    @Test
    @DisplayName("createOrder: debe lanzar excepción si ya existe un pedido activo")
    void createOrder_pedidoActivo() {
        when(authValidator.validate(tokenCliente, Role.CLIENTE)).thenReturn(customerId);
        when(orderMapper.toModel(requestDto, customerId.toString())).thenReturn(orderModel);
        when(orderServicePort.createOrder(orderModel))
                .thenThrow(new OrderAlreadyExistsException("Ya tienes un pedido en proceso"));

        OrderAlreadyExistsException ex = assertThrows(OrderAlreadyExistsException.class,
                () -> orderHandler.createOrder(requestDto, tokenCliente));

        assertEquals("Ya tienes un pedido en proceso", ex.getMessage());

        verify(authValidator).validate(tokenCliente, Role.CLIENTE);
        verify(orderMapper).toModel(requestDto, customerId.toString());
        verify(orderServicePort).createOrder(orderModel);
        verifyNoMoreInteractions(orderServicePort, orderMapper, authValidator, restaurantServicePort);
    }

    // ---------- listOrdersByStatus ----------

    @Test
    @DisplayName("listOrdersByStatus: empleado válido y perteneciente al restaurante obtiene la página de pedidos")
    void listOrdersByStatus_ok() {
        when(authValidator.validate(tokenEmpleado, Role.EMPLEADO)).thenReturn(employeeId);
        when(restaurantServicePort.isEmployeeOfRestaurant(restaurantId, employeeId)).thenReturn(true);

        PageRequest pageable = PageRequest.of(0, 2);
        Order domainOrder1 = new Order();
        domainOrder1.setId(UUID.randomUUID());
        domainOrder1.setRestaurantId(restaurantId);
        domainOrder1.setStatus("PENDIENTE");

        Order domainOrder2 = new Order();
        domainOrder2.setId(UUID.randomUUID());
        domainOrder2.setRestaurantId(restaurantId);
        domainOrder2.setStatus("PENDIENTE");

        Page<Order> pageDomain = new PageImpl<>(List.of(domainOrder1, domainOrder2), pageable, 2);
        when(orderServicePort.getOrdersByStatus(restaurantId, "PENDIENTE", pageable))
                .thenReturn(pageDomain);

        OrderDetailResponseDto dto1 = new OrderDetailResponseDto();
        dto1.setId(domainOrder1.getId()); // <- UUID
        dto1.setStatus("PENDIENTE");

        OrderDetailResponseDto dto2 = new OrderDetailResponseDto();
        dto2.setId(domainOrder2.getId()); // <- UUID
        dto2.setStatus("PENDIENTE");

        when(orderMapper.toDetailResponse(domainOrder1)).thenReturn(dto1);
        when(orderMapper.toDetailResponse(domainOrder2)).thenReturn(dto2);

        Page<OrderDetailResponseDto> result =
                orderHandler.listOrdersByStatus(restaurantId, "PENDIENTE", 0, 2, tokenEmpleado);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());
        assertEquals("PENDIENTE", result.getContent().get(0).getStatus());
        assertEquals("PENDIENTE", result.getContent().get(1).getStatus());

        verify(authValidator).validate(tokenEmpleado, Role.EMPLEADO);
        verify(restaurantServicePort).isEmployeeOfRestaurant(restaurantId, employeeId);
        verify(orderServicePort).getOrdersByStatus(restaurantId, "PENDIENTE", pageable);
        verify(orderMapper).toDetailResponse(domainOrder1);
        verify(orderMapper).toDetailResponse(domainOrder2);
        verifyNoMoreInteractions(orderServicePort, orderMapper, authValidator, restaurantServicePort);
    }

    @Test
    @DisplayName("listOrdersByStatus: debe lanzar Unauthorized si el empleado no pertenece al restaurante")
    void listOrdersByStatus_unauthorized() {
        when(authValidator.validate(tokenEmpleado, Role.EMPLEADO)).thenReturn(employeeId);
        when(restaurantServicePort.isEmployeeOfRestaurant(restaurantId, employeeId)).thenReturn(false);

        UnauthorizedException ex = assertThrows(UnauthorizedException.class,
                () -> orderHandler.listOrdersByStatus(restaurantId, "PENDIENTE", 0, 10, tokenEmpleado));

        assertEquals("No puedes listar pedidos de un restaurante que no es tuyo.", ex.getMessage());

        verify(authValidator).validate(tokenEmpleado, Role.EMPLEADO);
        verify(restaurantServicePort).isEmployeeOfRestaurant(restaurantId, employeeId);
        verifyNoInteractions(orderMapper);
        verifyNoMoreInteractions(orderServicePort, restaurantServicePort, authValidator);
    }

    // ---------- assignOrder ----------

    @Test
    @DisplayName("assignOrder: empleado válido y del restaurante puede asignarse el pedido")
    void assignOrder_ok() {
        when(authValidator.validate(tokenEmpleado, Role.EMPLEADO)).thenReturn(employeeId);

        Order found = new Order();
        found.setId(orderId);
        found.setRestaurantId(restaurantId);
        found.setStatus("PENDIENTE");

        when(orderServicePort.findById(orderId)).thenReturn(found);
        when(restaurantServicePort.isEmployeeOfRestaurant(restaurantId, employeeId)).thenReturn(true);

        Order updated = new Order();
        updated.setId(orderId);
        updated.setRestaurantId(restaurantId);
        updated.setStatus("EN_PREPARACION"); // sin setEmployeeId()

        OrderDetailResponseDto detailDto = new OrderDetailResponseDto();
        detailDto.setId(orderId); // <- UUID
        detailDto.setStatus("EN_PREPARACION");

        when(orderServicePort.assignOrderToEmployee(orderId, employeeId)).thenReturn(updated);
        when(orderMapper.toDetailResponse(updated)).thenReturn(detailDto);

        OrderDetailResponseDto result = orderHandler.assignOrder(orderId, tokenEmpleado);

        assertNotNull(result);
        assertEquals(orderId, result.getId());
        assertEquals("EN_PREPARACION", result.getStatus());

        verify(authValidator).validate(tokenEmpleado, Role.EMPLEADO);
        verify(orderServicePort).findById(orderId);
        verify(restaurantServicePort).isEmployeeOfRestaurant(restaurantId, employeeId);
        verify(orderServicePort).assignOrderToEmployee(orderId, employeeId);
        verify(orderMapper).toDetailResponse(updated);
        verifyNoMoreInteractions(orderServicePort, orderMapper, authValidator, restaurantServicePort);
    }

    @Test
    @DisplayName("assignOrder: debe lanzar Unauthorized si el pedido no pertenece a un restaurante del empleado")
    void assignOrder_unauthorized() {
        when(authValidator.validate(tokenEmpleado, Role.EMPLEADO)).thenReturn(employeeId);

        Order found = new Order();
        found.setId(orderId);
        found.setRestaurantId(restaurantId);

        when(orderServicePort.findById(orderId)).thenReturn(found);
        when(restaurantServicePort.isEmployeeOfRestaurant(restaurantId, employeeId)).thenReturn(false);

        UnauthorizedException ex = assertThrows(UnauthorizedException.class,
                () -> orderHandler.assignOrder(orderId, tokenEmpleado));

        assertEquals("No puedes asignarte pedidos de otro restaurante", ex.getMessage());

        verify(authValidator).validate(tokenEmpleado, Role.EMPLEADO);
        verify(orderServicePort).findById(orderId);
        verify(restaurantServicePort).isEmployeeOfRestaurant(restaurantId, employeeId);
        verify(orderServicePort, never()).assignOrderToEmployee(any(), any());
        verifyNoInteractions(orderMapper);
        verifyNoMoreInteractions(orderServicePort, restaurantServicePort, authValidator);
    }
}
