package com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.output.jpa.repository;

import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.output.jpa.entity.RestaurantEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface RestaurantRepository extends JpaRepository<RestaurantEntity, UUID> {
    boolean existsByNit(String nit);
    Page<RestaurantEntity> findAllByOrderByNameAsc(Pageable pageable);

    @Query("SELECT COUNT(e) > 0 FROM RestaurantEmployeeEntity e " +
            "WHERE e.restaurantId = :restaurantId AND e.employeeId = :employeeId")
    boolean isEmployeeOfRestaurant(@Param("restaurantId") UUID restaurantId,
                                   @Param("employeeId") UUID employeeId);

}
