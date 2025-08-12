package com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.output.jpa.repository;

import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.output.jpa.entity.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import javax.transaction.Transactional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<OrderEntity, UUID> {
    boolean existsByCustomerIdAndStatusIn(UUID customerId, Iterable<String> statuses);

    @EntityGraph(attributePaths = "items") // carga ítems para mostrar "todos los campos"
    Page<OrderEntity> findByRestaurantIdAndStatus(UUID restaurantId, String status, Pageable pageable);

    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE OrderEntity o SET o.assignedEmployeeId = :employeeId, o.status = 'EN_PREPARACION' WHERE o.id = :orderId")
    int assignOrderToEmployee(@Param("orderId") UUID orderId, @Param("employeeId") UUID employeeId);

}
