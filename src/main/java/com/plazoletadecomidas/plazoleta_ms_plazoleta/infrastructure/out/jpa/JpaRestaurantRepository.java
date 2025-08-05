package com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.out.jpa;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface JpaRestaurantRepository extends JpaRepository<RestaurantEntity, UUID> {
}
