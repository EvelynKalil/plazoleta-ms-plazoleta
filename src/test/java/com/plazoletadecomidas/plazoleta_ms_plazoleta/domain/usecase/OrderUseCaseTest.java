package com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.usecase;

import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.Dish;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.Order;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.OrderItem;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.OrderStatus;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.spi.OrderPersistencePort;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.api.DishServicePort;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.exception.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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

    @InjectMocks
    private OrderUseCase orderUseCase;

    private UUID customerId;
    private UUID restaurantId;
    private UUID dishId;
    private OrderItem item;
    private Order order;

    private UUID orderId;
    private UUID employeeId;

    @BeforeEach
    void setUp() {
        customerId = UUID.randomUUID();
        restaurantId = UUID.randomUUID();
        dishId = UUID.randomUUID();

        item = new OrderItem(dishId, 2);

        order = new Order();
        order.setCustomerId(customerId);
        order.setRestaurantId(restaurantId);
        order.setItems(List.of(item));

        orderId = UUID.randomUUID();
        employeeId = UUID.randomUUID();
    }

    @Test
    @DisplayName("Debe lanzar excepción si el cliente ya tiene un pedido activo")
    void createOrder_clienteConPedidoActivo() {
        when(orderPersistencePort.existsActiveOrderByCustomer(customerId)).thenReturn(true);

        OrderAlreadyExistsException ex = assertThrows(OrderAlreadyExistsException.class,
                () -> orderUseCase.createOrder(order));

        assertEquals("Ya tienes un pedido en proceso", ex.getMessage());
        verify(orderPersistencePort).existsActiveOrderByCustomer(customerId);
        verifyNoMoreInteractions(orderPersistencePort);
    }

    @Test
    @DisplayName("Debe lanzar excepción si hay items duplicados")
    void createOrder_itemsDuplicados() {
        OrderItem duplicado = new OrderItem(dishId, 1);
        order.setItems(Arrays.asList(item, duplicado));

        when(orderPersistencePort.existsActiveOrderByCustomer(customerId)).thenReturn(false);

        assertThrows(DuplicateOrderItemException.class,
                () -> orderUseCase.createOrder(order));
    }

    @Test
    @DisplayName("Debe lanzar excepción si un plato no pertenece al restaurante")
    void createOrder_platoNoPerteneceAlRestaurante() {
        UUID otroRestaurante = UUID.randomUUID();

        when(orderPersistencePort.existsActiveOrderByCustomer(customerId)).thenReturn(false);
        when(dishServicePort.getDishById(dishId)).thenReturn(
                Dish.builder()
                        .id(dishId)
                        .name("Pizza")
                        .price(10000)
                        .description("desc")
                        .urlImage("url")
                        .category("cat")
                        .restaurantId(otroRestaurante)
                        .active(true)
                        .build()
        );

        assertThrows(DishNotFromRestaurantException.class,
                () -> orderUseCase.createOrder(order));
    }

    @Test
    @DisplayName("Debe lanzar excepción si el pedido no tiene items")
    void createOrder_pedidoSinItems() {
        order.setItems(Collections.emptyList());

        when(orderPersistencePort.existsActiveOrderByCustomer(customerId)).thenReturn(false);

        assertThrows(EmptyOrderException.class,
                () -> orderUseCase.createOrder(order));
    }

    @Test
    @DisplayName("Debe crear un pedido válido con estado PENDIENTE")
    void createOrder_pedidoValido() {
        when(orderPersistencePort.existsActiveOrderByCustomer(customerId)).thenReturn(false);
        when(dishServicePort.getDishById(dishId)).thenReturn(
                Dish.builder()
                        .id(dishId)
                        .name("Pizza")
                        .price(10000)
                        .description("desc")
                        .urlImage("url")
                        .category("cat")
                        .restaurantId(restaurantId)
                        .active(true)
                        .build()
        );

        when(orderPersistencePort.save(any(Order.class))).thenAnswer(invocation -> {
            Order o = invocation.getArgument(0);
            o.setId(UUID.randomUUID());
            o.setCreatedAt(LocalDateTime.now());
            return o;
        });

        Order result = orderUseCase.createOrder(order);

        assertNotNull(result.getId());
        assertEquals(OrderStatus.PENDIENTE, result.getStatus());
        assertNotNull(result.getCreatedAt());
        verify(orderPersistencePort).save(any(Order.class));
    }

    @Test
    void givenValidRestaurantAndStatus_whenGetOrdersByStatus_thenReturnPage() {
        Order testOrder = new Order();
        testOrder.setId(orderId);
        testOrder.setCustomerId(customerId);
        testOrder.setRestaurantId(restaurantId);
        testOrder.setItems(List.of());
        testOrder.setStatus(OrderStatus.PENDIENTE);
        testOrder.setCreatedAt(LocalDateTime.now());

        Page<Order> page = new PageImpl<>(List.of(testOrder));

        when(orderPersistencePort.findByRestaurantAndStatus(eq(restaurantId), eq(OrderStatus.PENDIENTE), any(Pageable.class)))
                .thenReturn(page);

        Page<Order> result = orderUseCase.getOrdersByStatus(restaurantId, "PENDIENTE", PageRequest.of(0, 5));

        assertEquals(1, result.getTotalElements());
        assertEquals(OrderStatus.PENDIENTE, result.getContent().get(0).getStatus());
    }

    @Test
    void givenInvalidStatus_whenGetOrdersByStatus_thenThrowException() {
        PageRequest pageRequest = PageRequest.of(0, 5);

        assertThrows(IllegalArgumentException.class,
                () -> orderUseCase.getOrdersByStatus(restaurantId, "INVALIDO", pageRequest)
        );
    }

    @Test
    void givenPendingOrder_whenAssignOrder_thenReturnUpdatedOrder() {
        Order pendingOrder = new Order();
        pendingOrder.setId(orderId);
        pendingOrder.setCustomerId(customerId);
        pendingOrder.setRestaurantId(restaurantId);
        pendingOrder.setItems(List.of());
        pendingOrder.setStatus(OrderStatus.PENDIENTE);
        pendingOrder.setCreatedAt(LocalDateTime.now());

        Order updatedOrder = new Order();
        updatedOrder.setId(orderId);
        updatedOrder.setCustomerId(customerId);
        updatedOrder.setRestaurantId(restaurantId);
        updatedOrder.setItems(List.of());
        updatedOrder.setStatus(OrderStatus.PENDIENTE);
        updatedOrder.setCreatedAt(LocalDateTime.now());

        when(orderPersistencePort.findById(orderId)).thenReturn(pendingOrder);
        when(orderPersistencePort.assignOrderToEmployee(orderId, employeeId)).thenReturn(updatedOrder);

        Order result = orderUseCase.assignOrderToEmployee(orderId, employeeId);

        assertEquals(OrderStatus.EN_PREPARACION, result.getStatus());
        verify(orderPersistencePort).assignOrderToEmployee(orderId, employeeId);
    }

    @Test
    void givenOrderNotPending_whenAssignOrder_thenThrowException() {
        Order notPendingOrder = new Order();
        notPendingOrder.setId(orderId);
        notPendingOrder.setCustomerId(customerId);
        notPendingOrder.setRestaurantId(restaurantId);
        notPendingOrder.setItems(List.of());
        notPendingOrder.setStatus(OrderStatus.PENDIENTE);
        notPendingOrder.setCreatedAt(LocalDateTime.now());

        when(orderPersistencePort.findById(orderId)).thenReturn(notPendingOrder);

        assertThrows(OrderAlreadyAssignedException.class, () ->
                orderUseCase.assignOrderToEmployee(orderId, employeeId)
        );

        verify(orderPersistencePort, never()).assignOrderToEmployee(any(), any());
    }

}
