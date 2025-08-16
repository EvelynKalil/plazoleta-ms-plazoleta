package com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.usecase;

import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.api.*;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.*;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.Order;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.spi.OrderPersistencePort;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.exception.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderUseCaseTest {

    @Mock private OrderPersistencePort orderPersistencePort;
    @Mock private DishServicePort dishServicePort;
    @Mock private NotificationServicePort notificationServicePort;
    @Mock private UserServicePort userServicePort;
    @Mock private TraceabilityServicePort traceabilityServicePort;

    @InjectMocks
    private OrderUseCase orderUseCase;

    private UUID customerId;
    private UUID restaurantId;
    private UUID dishId;
    private UUID orderId;
    private UUID employeeId;
    private String token;
    private OrderItem item;
    private Order baseOrder;

    @BeforeEach
    void setUp() {
        customerId = UUID.randomUUID();
        restaurantId = UUID.randomUUID();
        dishId = UUID.randomUUID();
        orderId = UUID.randomUUID();
        employeeId = UUID.randomUUID();
        token = "mock-token";

        item = new OrderItem(dishId, 2);

        baseOrder = new Order();
        baseOrder.setCustomerId(customerId);
        baseOrder.setRestaurantId(restaurantId);
        baseOrder.setItems(List.of(item));
    }

    @Test
    void createOrder_clienteConPedidoActivo() {
        when(orderPersistencePort.existsActiveOrderByCustomer(customerId)).thenReturn(true);

        assertThrows(OrderAlreadyExistsException.class,
                () -> orderUseCase.createOrder(baseOrder, token));

        verify(orderPersistencePort).existsActiveOrderByCustomer(customerId);
        verifyNoMoreInteractions(orderPersistencePort);
    }

    @Test
    void createOrder_itemsDuplicados() {
        baseOrder.setItems(List.of(item, new OrderItem(dishId, 1)));
        when(orderPersistencePort.existsActiveOrderByCustomer(customerId)).thenReturn(false);

        assertThrows(DuplicateOrderItemException.class,
                () -> orderUseCase.createOrder(baseOrder, token));
    }

    @Test
    void createOrder_pedidoSinItems() {
        baseOrder.setItems(Collections.emptyList());
        when(orderPersistencePort.existsActiveOrderByCustomer(customerId)).thenReturn(false);

        assertThrows(EmptyOrderException.class,
                () -> orderUseCase.createOrder(baseOrder, token));
    }

    @Test
    void createOrder_platoNoPerteneceAlRestaurante() {
        when(orderPersistencePort.existsActiveOrderByCustomer(customerId)).thenReturn(false);
        when(dishServicePort.getDishById(dishId))
                .thenReturn(Dish.builder().id(dishId).restaurantId(UUID.randomUUID()).active(true).build());

        assertThrows(DishNotFromRestaurantException.class,
                () -> orderUseCase.createOrder(baseOrder, token));
    }

    @Test
    void createOrder_pedidoValido() {
        when(orderPersistencePort.existsActiveOrderByCustomer(customerId)).thenReturn(false);
        when(dishServicePort.getDishById(dishId))
                .thenReturn(Dish.builder().id(dishId).restaurantId(restaurantId).active(true).build());

        when(orderPersistencePort.save(any(Order.class)))
                .thenAnswer(inv -> {
                    Order o = inv.getArgument(0);
                    o.setId(UUID.randomUUID());
                    o.setCreatedAt(LocalDateTime.now());
                    return o;
                });

        Order result = orderUseCase.createOrder(baseOrder, token);

        assertNotNull(result.getId());
        assertEquals(OrderStatus.PENDIENTE, result.getStatus());
        verify(traceabilityServicePort).sendOrderStatusChange(result, token);
    }

    @Test
    void givenValidRestaurantAndStatus_whenGetOrdersByStatus_thenReturnPage() {
        Order testOrder = new Order(orderId, customerId, restaurantId, List.of(), OrderStatus.PENDIENTE, LocalDateTime.now(), null, null);
        Page<Order> page = new PageImpl<>(List.of(testOrder));

        when(orderPersistencePort.findByRestaurantAndStatus(eq(restaurantId), eq(OrderStatus.PENDIENTE), any()))
                .thenReturn(page);

        Page<Order> result = orderUseCase.getOrdersByStatus(restaurantId, "PENDIENTE", PageRequest.of(0, 5));

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getOrdersByStatus_invalido() {
        assertThrows(IllegalArgumentException.class,
                () -> orderUseCase.getOrdersByStatus(restaurantId, "INVALIDO", PageRequest.of(0, 5)));
    }

    @Test
    void assignOrderToEmployee_ok() {
        Order pendingOrder = new Order(orderId, customerId, restaurantId, List.of(), OrderStatus.PENDIENTE, LocalDateTime.now(), null, null);
        Order updated = new Order(orderId, customerId, restaurantId, List.of(), OrderStatus.EN_PREPARACION, LocalDateTime.now(), employeeId, null);

        when(orderPersistencePort.findById(orderId)).thenReturn(pendingOrder);
        when(orderPersistencePort.assignOrderToEmployee(orderId, employeeId)).thenReturn(updated);

        Order result = orderUseCase.assignOrderToEmployee(orderId, employeeId, token);

        assertEquals(OrderStatus.EN_PREPARACION, result.getStatus());
        verify(traceabilityServicePort).sendOrderStatusChange(updated, token);
    }

    @Test
    void assignOrderToEmployee_noPendiente() {
        Order notPending = new Order(orderId, customerId, restaurantId, List.of(), OrderStatus.EN_PREPARACION, LocalDateTime.now(), null, null);
        when(orderPersistencePort.findById(orderId)).thenReturn(notPending);

        assertThrows(OrderAlreadyAssignedException.class,
                () -> orderUseCase.assignOrderToEmployee(orderId, employeeId, token));
    }

    @Test
    void updateOrderStatus_aListo_ok() {
        Order enPrep = new Order(orderId, customerId, restaurantId, List.of(), OrderStatus.EN_PREPARACION, LocalDateTime.now(), employeeId, null);
        String telefono = "3001234567";

        when(orderPersistencePort.findById(orderId)).thenReturn(enPrep);
        when(orderPersistencePort.updateStatusAndPin(eq(orderId), eq(OrderStatus.LISTO), anyString()))
                .thenAnswer(inv -> {
                    enPrep.setStatus(OrderStatus.LISTO);
                    enPrep.setSecurityPin(inv.getArgument(2));
                    return enPrep;
                });
        when(userServicePort.getPhone(customerId)).thenReturn(telefono);

        Order result = orderUseCase.updateOrderStatus(orderId, employeeId, OrderStatus.LISTO, token);

        assertEquals(OrderStatus.LISTO, result.getStatus());
        assertNotNull(result.getSecurityPin());
        verify(notificationServicePort).notifyOrderReady(telefono, orderId.toString(), result.getSecurityPin());
        verify(traceabilityServicePort).sendOrderStatusChange(result, token);
    }

    @Test
    void updateOrderStatus_aEntregado_ok() {
        Order listo = new Order(orderId, customerId, restaurantId, List.of(), OrderStatus.LISTO, LocalDateTime.now(), employeeId, "1234");

        when(orderPersistencePort.findById(orderId)).thenReturn(listo);
        when(orderPersistencePort.updateStatusAndClearPin(orderId, OrderStatus.ENTREGADO))
                .thenAnswer(inv -> {
                    listo.setStatus(OrderStatus.ENTREGADO);
                    listo.setSecurityPin(null);
                    return listo;
                });

        Order result = orderUseCase.updateOrderStatus(orderId, employeeId, OrderStatus.ENTREGADO, token);

        assertEquals(OrderStatus.ENTREGADO, result.getStatus());
        assertNull(result.getSecurityPin());
        verify(traceabilityServicePort).sendOrderStatusChange(result, token);
    }

    @Test
    void updateOrderStatus_aEntregado_sinPin() {
        Order listoSinPin = new Order(orderId, customerId, restaurantId, List.of(), OrderStatus.LISTO, LocalDateTime.now(), employeeId, null);
        when(orderPersistencePort.findById(orderId)).thenReturn(listoSinPin);

        assertThrows(InvalidPinException.class,
                () -> orderUseCase.updateOrderStatus(orderId, employeeId, OrderStatus.ENTREGADO, token));
    }

    @Test
    void cancelOrder_ok() {
        Order pending = new Order(orderId, customerId, restaurantId, List.of(), OrderStatus.PENDIENTE, LocalDateTime.now(), null, null);
        when(orderPersistencePort.findById(orderId)).thenReturn(pending);
        when(orderPersistencePort.updateOrderStatus(orderId, OrderStatus.CANCELADO))
                .thenAnswer(inv -> { pending.setStatus(OrderStatus.CANCELADO); return pending; });

        Order result = orderUseCase.cancelOrder(orderId, customerId, token);

        assertEquals(OrderStatus.CANCELADO, result.getStatus());
        verify(traceabilityServicePort).sendOrderStatusChange(result, token);
    }

    @Test
    void cancelOrder_noPendiente() {
        Order enPrep = new Order(orderId, customerId, restaurantId, List.of(), OrderStatus.EN_PREPARACION, LocalDateTime.now(), null, null);
        when(orderPersistencePort.findById(orderId)).thenReturn(enPrep);

        assertThrows(IllegalArgumentException.class,
                () -> orderUseCase.cancelOrder(orderId, customerId, token));
    }

    @Test
    void cancelOrder_noEsDelCliente() {
        UUID otroCliente = UUID.randomUUID();
        Order pending = new Order(orderId, otroCliente, restaurantId, List.of(), OrderStatus.PENDIENTE, LocalDateTime.now(), null, null);
        when(orderPersistencePort.findById(orderId)).thenReturn(pending);

        assertThrows(UnauthorizedException.class,
                () -> orderUseCase.cancelOrder(orderId, customerId, token));
    }
}
