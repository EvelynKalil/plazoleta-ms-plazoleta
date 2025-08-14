package com.plazoletadecomidas.plazoleta_ms_plazoleta.application.handler;

import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto.*;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.mapper.OrderMapper;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.api.OrderServicePort;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.api.RestaurantServicePort;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.Order;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.OrderStatus;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.Role;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.exception.OrderAlreadyExistsException;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.exception.UnauthorizedException;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.security.AuthValidator;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.*;

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
        orderModel.setStatus(OrderStatus.PENDIENTE);

        responseDto = new OrderResponseDto();
        responseDto.setId(orderId.toString());
        responseDto.setStatus("PENDIENTE");
    }

    // ---------- createOrder ----------

    @Test
    @DisplayName("createOrder: crea un pedido cuando el cliente es válido")
    void createOrder_clienteValido() {
        when(authValidator.validate(tokenCliente, Role.CLIENTE)).thenReturn(customerId);
        when(orderMapper.toModel(requestDto, customerId.toString())).thenReturn(orderModel);
        when(orderServicePort.createOrder(orderModel)).thenReturn(orderModel);
        when(orderMapper.toResponse(orderModel)).thenReturn(responseDto);

        OrderResponseDto result = orderHandler.createOrder(requestDto, tokenCliente);

        assertNotNull(result);
        assertEquals(orderId.toString(), result.getId());
        assertEquals("PENDIENTE", result.getStatus());

        verify(authValidator).validate(tokenCliente, Role.CLIENTE);
        verify(orderMapper).toModel(requestDto, customerId.toString());
        verify(orderServicePort).createOrder(orderModel);
        verify(orderMapper).toResponse(orderModel);
        verifyNoMoreInteractions(orderServicePort, orderMapper, authValidator, restaurantServicePort);
    }

    @Test
    @DisplayName("createOrder: lanza excepción si ya existe un pedido activo")
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
    @DisplayName("listOrdersByStatus: empleado válido y del restaurante obtiene la página de pedidos")
    void listOrdersByStatus_ok() {
        when(authValidator.validate(tokenEmpleado, Role.EMPLEADO)).thenReturn(employeeId);
        when(restaurantServicePort.isEmployeeOfRestaurant(restaurantId, employeeId)).thenReturn(true);

        PageRequest pageable = PageRequest.of(0, 2);
        Order domainOrder1 = new Order();
        domainOrder1.setId(UUID.randomUUID());
        domainOrder1.setRestaurantId(restaurantId);
        domainOrder1.setStatus(OrderStatus.PENDIENTE);

        Order domainOrder2 = new Order();
        domainOrder2.setId(UUID.randomUUID());
        domainOrder2.setRestaurantId(restaurantId);
        domainOrder2.setStatus(OrderStatus.PENDIENTE);

        Page<Order> pageDomain = new PageImpl<>(List.of(domainOrder1, domainOrder2), pageable, 2);
        when(orderServicePort.getOrdersByStatus(restaurantId, "PENDIENTE", pageable)).thenReturn(pageDomain);

        OrderDetailResponseDto dto1 = new OrderDetailResponseDto();
        dto1.setId(domainOrder1.getId());
        dto1.setStatus("PENDIENTE");

        OrderDetailResponseDto dto2 = new OrderDetailResponseDto();
        dto2.setId(domainOrder2.getId());
        dto2.setStatus("PENDIENTE");

        when(orderMapper.toDetailResponse(domainOrder1)).thenReturn(dto1);
        when(orderMapper.toDetailResponse(domainOrder2)).thenReturn(dto2);

        Page<OrderDetailResponseDto> result =
                orderHandler.listOrdersByStatus(restaurantId, "PENDIENTE", 0, 2, tokenEmpleado);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertTrue(result.getContent().stream().allMatch(o -> "PENDIENTE".equals(o.getStatus())));

        verify(authValidator).validate(tokenEmpleado, Role.EMPLEADO);
        verify(restaurantServicePort).isEmployeeOfRestaurant(restaurantId, employeeId);
        verify(orderServicePort).getOrdersByStatus(restaurantId, "PENDIENTE", pageable);
        verify(orderMapper).toDetailResponse(domainOrder1);
        verify(orderMapper).toDetailResponse(domainOrder2);
        verifyNoMoreInteractions(orderServicePort, orderMapper, authValidator, restaurantServicePort);
    }

    @Test
    @DisplayName("listOrdersByStatus: lanza Unauthorized si el empleado no pertenece al restaurante")
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
        found.setStatus(OrderStatus.PENDIENTE);

        when(orderServicePort.findById(orderId)).thenReturn(found);
        when(restaurantServicePort.isEmployeeOfRestaurant(restaurantId, employeeId)).thenReturn(true);

        Order updated = new Order();
        updated.setId(orderId);
        updated.setRestaurantId(restaurantId);
        updated.setStatus(OrderStatus.EN_PREPARACION);

        OrderDetailResponseDto detailDto = new OrderDetailResponseDto();
        detailDto.setId(orderId);
        detailDto.setStatus("EN_PREPARACION");

        when(orderServicePort.assignOrderToEmployee(orderId, employeeId)).thenReturn(updated);
        when(orderMapper.toDetailResponse(updated)).thenReturn(detailDto);

        OrderDetailResponseDto result = orderHandler.assignOrder(orderId, tokenEmpleado);

        assertNotNull(result);
        assertEquals(orderId, result.getId());
        assertEquals("EN_PREPARACION", result.getStatus());
    }

    @Test
    @DisplayName("assignOrder: lanza Unauthorized si el pedido no pertenece al restaurante del empleado")
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
    }

    // ---------- updateOrderStatus ----------

    @Test
    @DisplayName("updateOrderStatus: actualiza el estado del pedido correctamente")
    void updateOrderStatus_ok() {
        when(authValidator.validate(tokenEmpleado, Role.EMPLEADO)).thenReturn(employeeId);

        Order updated = new Order();
        updated.setId(orderId);
        updated.setStatus(OrderStatus.LISTO);

        OrderDetailResponseDto detailDto = new OrderDetailResponseDto();
        detailDto.setId(orderId);
        detailDto.setStatus("LISTO");

        when(orderServicePort.updateOrderStatus(orderId, employeeId, OrderStatus.LISTO)).thenReturn(updated);
        when(orderMapper.toDetailResponse(updated)).thenReturn(detailDto);

        OrderDetailResponseDto result = orderHandler.updateOrderStatus(orderId, "LISTO", tokenEmpleado);

        assertNotNull(result);
        assertEquals("LISTO", result.getStatus());
    }

    @Test
    @DisplayName("updateOrderStatus: lanza IllegalArgumentException si el estado es inválido")
    void updateOrderStatus_estadoInvalido() {
        when(authValidator.validate(tokenEmpleado, Role.EMPLEADO)).thenReturn(employeeId);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> orderHandler.updateOrderStatus(orderId, "INVALIDO", tokenEmpleado));

        assertTrue(ex.getMessage().contains("Estado inválido"));
    }

    @Test
    @DisplayName("updateOrderStatus: ENTREGADO con PIN correcto → OK")
    void handler_entregado_ok() {
        when(authValidator.validate(tokenEmpleado, Role.EMPLEADO)).thenReturn(employeeId);

        Order updated = new Order();
        updated.setId(orderId);
        updated.setStatus(OrderStatus.ENTREGADO);

        OrderDetailResponseDto dto = new OrderDetailResponseDto();
        dto.setId(orderId);
        dto.setStatus("ENTREGADO");

        when(orderServicePort.updateOrderStatus(orderId, employeeId, OrderStatus.ENTREGADO)).thenReturn(updated);
        when(orderMapper.toDetailResponse(updated)).thenReturn(dto);

        OrderDetailResponseDto result = orderHandler.updateOrderStatus(orderId, "ENTREGADO", tokenEmpleado);

        assertEquals("ENTREGADO", result.getStatus());
        verify(orderServicePort).updateOrderStatus(orderId, employeeId, OrderStatus.ENTREGADO);
    }

    // HU-16: handler

    @Test
    @DisplayName("cancelOrder: cliente válido cancela su pedido PENDIENTE")
    void cancelOrder_handler_ok() {
        when(authValidator.validate(tokenCliente, Role.CLIENTE)).thenReturn(customerId);

        Order cancelled = new Order();
        cancelled.setId(orderId);
        cancelled.setStatus(OrderStatus.CANCELADO);

        OrderDetailResponseDto dto = new OrderDetailResponseDto();
        dto.setId(orderId);
        dto.setStatus("CANCELADO");

        when(orderServicePort.cancelOrder(orderId, customerId)).thenReturn(cancelled);
        when(orderMapper.toDetailResponse(cancelled)).thenReturn(dto);

        OrderDetailResponseDto result = orderHandler.cancelOrder(orderId, tokenCliente);

        assertEquals("CANCELADO", result.getStatus());
        verify(orderServicePort).cancelOrder(orderId, customerId);
    }

    @Test
    @DisplayName("cancelOrder: lanza Unauthorized si el pedido no es del cliente")
    void cancelOrder_handler_unauthorized() {
        when(authValidator.validate(tokenCliente, Role.CLIENTE)).thenReturn(customerId);

        // Simulamos que el use case lanzará UnauthorizedException por no ser dueño
        doThrow(new UnauthorizedException("No puedes cancelar pedidos de otro cliente"))
                .when(orderServicePort).cancelOrder(orderId, customerId);

        assertThrows(UnauthorizedException.class,
                () -> orderHandler.cancelOrder(orderId, tokenCliente));
    }


}
