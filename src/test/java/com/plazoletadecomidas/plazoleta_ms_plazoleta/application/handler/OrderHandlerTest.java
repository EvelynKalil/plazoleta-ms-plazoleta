package com.plazoletadecomidas.plazoleta_ms_plazoleta.application.handler;

import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto.*;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.mapper.OrderMapper;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.api.OrderServicePort;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.api.RestaurantServicePort;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.api.TraceabilityServicePort;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.Order;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.OrderStatus;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.Role;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.exception.InvalidPinException;
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
    @Mock private TraceabilityServicePort traceabilityServicePort;

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

    @Test
    void createOrder_clienteValido() {
        when(authValidator.validate(tokenCliente, Role.CLIENTE)).thenReturn(customerId);
        when(orderMapper.toModel(requestDto, customerId.toString())).thenReturn(orderModel);
        when(orderServicePort.createOrder(orderModel, tokenCliente)).thenReturn(orderModel);
        when(orderMapper.toResponse(orderModel)).thenReturn(responseDto);

        OrderResponseDto result = orderHandler.createOrder(requestDto, tokenCliente);

        assertEquals(orderId.toString(), result.getId());
        verify(orderServicePort).createOrder(orderModel, tokenCliente);
    }

    @Test
    void createOrder_pedidoActivo() {
        when(authValidator.validate(tokenCliente, Role.CLIENTE)).thenReturn(customerId);
        when(orderMapper.toModel(requestDto, customerId.toString())).thenReturn(orderModel);
        when(orderServicePort.createOrder(orderModel, tokenCliente))
                .thenThrow(new OrderAlreadyExistsException());

        assertThrows(OrderAlreadyExistsException.class,
                () -> orderHandler.createOrder(requestDto, tokenCliente));
    }

    @Test
    void listOrdersByStatus_ok() {
        when(authValidator.validate(tokenEmpleado, Role.EMPLEADO)).thenReturn(employeeId);
        when(restaurantServicePort.isEmployeeOfRestaurant(restaurantId, employeeId)).thenReturn(true);

        PageRequest pageable = PageRequest.of(0, 2);
        Order o1 = new Order(); o1.setId(UUID.randomUUID()); o1.setStatus(OrderStatus.PENDIENTE);
        Order o2 = new Order(); o2.setId(UUID.randomUUID()); o2.setStatus(OrderStatus.PENDIENTE);
        Page<Order> pageDomain = new PageImpl<>(List.of(o1, o2), pageable, 2);

        when(orderServicePort.getOrdersByStatus(restaurantId, "PENDIENTE", pageable)).thenReturn(pageDomain);

        OrderDetailResponseDto dto1 = new OrderDetailResponseDto();
        dto1.setId(o1.getId());
        dto1.setStatus("PENDIENTE");

        OrderDetailResponseDto dto2 = new OrderDetailResponseDto();
        dto2.setId(o2.getId());
        dto2.setStatus("PENDIENTE");

        when(orderMapper.toDetailResponse(o1)).thenReturn(dto1);
        when(orderMapper.toDetailResponse(o2)).thenReturn(dto2);

        Page<OrderDetailResponseDto> result =
                orderHandler.listOrdersByStatus(restaurantId, "PENDIENTE", 0, 2, tokenEmpleado);

        assertEquals(2, result.getTotalElements());
    }

    @Test
    void listOrdersByStatus_unauthorized() {
        when(authValidator.validate(tokenEmpleado, Role.EMPLEADO)).thenReturn(employeeId);
        when(restaurantServicePort.isEmployeeOfRestaurant(restaurantId, employeeId)).thenReturn(false);

        assertThrows(UnauthorizedException.class,
                () -> orderHandler.listOrdersByStatus(restaurantId, "PENDIENTE", 0, 10, tokenEmpleado));
    }

    @Test
    void assignOrder_ok() {
        when(authValidator.validate(tokenEmpleado, Role.EMPLEADO)).thenReturn(employeeId);

        Order found = new Order(); found.setRestaurantId(restaurantId); found.setStatus(OrderStatus.PENDIENTE);
        when(orderServicePort.findById(orderId)).thenReturn(found);
        when(restaurantServicePort.isEmployeeOfRestaurant(restaurantId, employeeId)).thenReturn(true);

        Order updated = new Order(); updated.setId(orderId); updated.setStatus(OrderStatus.EN_PREPARACION);
        when(orderServicePort.assignOrderToEmployee(orderId, employeeId, tokenEmpleado)).thenReturn(updated);

        OrderDetailResponseDto dto = new OrderDetailResponseDto();
        dto.setId(orderId);
        dto.setStatus("EN_PREPARACION");

        when(orderMapper.toDetailResponse(updated)).thenReturn(dto);

        OrderDetailResponseDto result = orderHandler.assignOrder(orderId, tokenEmpleado);

        assertEquals("EN_PREPARACION", result.getStatus());
    }

    @Test
    void assignOrder_unauthorized() {
        when(authValidator.validate(tokenEmpleado, Role.EMPLEADO)).thenReturn(employeeId);
        Order found = new Order(); found.setRestaurantId(restaurantId);
        when(orderServicePort.findById(orderId)).thenReturn(found);
        when(restaurantServicePort.isEmployeeOfRestaurant(restaurantId, employeeId)).thenReturn(false);

        assertThrows(UnauthorizedException.class,
                () -> orderHandler.assignOrder(orderId, tokenEmpleado));
    }

    @Test
    void updateOrderStatus_ok() {
        when(authValidator.validate(tokenEmpleado, Role.EMPLEADO)).thenReturn(employeeId);

        Order updated = new Order(); updated.setStatus(OrderStatus.LISTO);
        when(orderServicePort.updateOrderStatus(orderId, employeeId, OrderStatus.LISTO, tokenEmpleado))
                .thenReturn(updated);

        OrderDetailResponseDto dto = new OrderDetailResponseDto();
        dto.setId(orderId);
        dto.setStatus("LISTO");

        when(orderMapper.toDetailResponse(updated)).thenReturn(dto);

        OrderDetailResponseDto result = orderHandler.updateOrderStatus(orderId, "LISTO", tokenEmpleado);

        assertEquals("LISTO", result.getStatus());
    }

    @Test
    void updateOrderStatus_estadoInvalido() {
        when(authValidator.validate(tokenEmpleado, Role.EMPLEADO)).thenReturn(employeeId);
        assertThrows(IllegalArgumentException.class,
                () -> orderHandler.updateOrderStatus(orderId, "INVALIDO", tokenEmpleado));
    }

    @Test
    void deliverOrder_ok() {
        when(authValidator.validate(tokenEmpleado, Role.EMPLEADO)).thenReturn(employeeId);

        Order found = new Order(); found.setRestaurantId(restaurantId); found.setSecurityPin("1234");
        when(orderServicePort.findById(orderId)).thenReturn(found);
        when(restaurantServicePort.isEmployeeOfRestaurant(restaurantId, employeeId)).thenReturn(true);

        Order updated = new Order(); updated.setStatus(OrderStatus.ENTREGADO);
        when(orderServicePort.updateOrderStatus(orderId, employeeId, OrderStatus.ENTREGADO, tokenEmpleado))
                .thenReturn(updated);

        OrderDetailResponseDto dto = new OrderDetailResponseDto();
        dto.setId(orderId);
        dto.setStatus("ENTREGADO");

        when(orderMapper.toDetailResponse(updated)).thenReturn(dto);

        OrderDetailResponseDto result = orderHandler.deliverOrder(orderId, "1234", tokenEmpleado);

        assertEquals("ENTREGADO", result.getStatus());
    }

    @Test
    void deliverOrder_pinIncorrecto() {
        when(authValidator.validate(tokenEmpleado, Role.EMPLEADO)).thenReturn(employeeId);
        Order found = new Order(); found.setRestaurantId(restaurantId); found.setSecurityPin("0000");
        when(orderServicePort.findById(orderId)).thenReturn(found);
        when(restaurantServicePort.isEmployeeOfRestaurant(restaurantId, employeeId)).thenReturn(true);

        assertThrows(InvalidPinException.class,
                () -> orderHandler.deliverOrder(orderId, "1234", tokenEmpleado));
    }

    @Test
    void cancelOrder_ok() {
        when(authValidator.validate(tokenCliente, Role.CLIENTE)).thenReturn(customerId);

        Order found = new Order(); found.setCustomerId(customerId);
        when(orderServicePort.findById(orderId)).thenReturn(found);

        Order updated = new Order(); updated.setStatus(OrderStatus.CANCELADO);
        when(orderServicePort.cancelOrder(orderId, customerId, tokenCliente)).thenReturn(updated);

        OrderDetailResponseDto dto = new OrderDetailResponseDto();
        dto.setId(orderId);
        dto.setStatus("CANCELADO");

        when(orderMapper.toDetailResponse(updated)).thenReturn(dto);

        OrderDetailResponseDto result = orderHandler.cancelOrder(orderId, tokenCliente);

        assertEquals("CANCELADO", result.getStatus());
    }

    @Test
    void cancelOrder_noEsDelCliente() {
        when(authValidator.validate(tokenCliente, Role.CLIENTE)).thenReturn(customerId);
        Order found = new Order(); found.setCustomerId(UUID.randomUUID());
        when(orderServicePort.findById(orderId)).thenReturn(found);

        assertThrows(UnauthorizedException.class,
                () -> orderHandler.cancelOrder(orderId, tokenCliente));
    }

    @Test
    void getOrderTrace_ok() {
        when(authValidator.validate(tokenCliente, Role.CLIENTE)).thenReturn(customerId);
        Order found = new Order(); found.setCustomerId(customerId);
        when(orderServicePort.findById(orderId)).thenReturn(found);

        OrderTraceResponseDto traceDto = new OrderTraceResponseDto();
        when(traceabilityServicePort.getOrderTrace(orderId, tokenCliente)).thenReturn(traceDto);

        OrderTraceResponseDto result = orderHandler.getOrderTrace(orderId, tokenCliente);

        assertNotNull(result);
    }

    @Test
    void getOrderTrace_noEsDelCliente() {
        when(authValidator.validate(tokenCliente, Role.CLIENTE)).thenReturn(customerId);
        Order found = new Order(); found.setCustomerId(UUID.randomUUID());
        when(orderServicePort.findById(orderId)).thenReturn(found);

        assertThrows(UnauthorizedException.class,
                () -> orderHandler.getOrderTrace(orderId, tokenCliente));
    }
}
