package com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.output.jpa.entity;

import javax.persistence.*;
import java.util.UUID;

import org.hibernate.annotations.Type;

@Entity
@Table(name = "restaurant_employee")
public class RestaurantEmployeeEntity {

    @Id
    @GeneratedValue
    @Type(type = "uuid-binary")
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Type(type = "uuid-binary")
    @Column(name = "restaurant_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID restaurantId;

    @Type(type = "uuid-binary")
    @Column(name = "employee_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID employeeId;

    protected RestaurantEmployeeEntity() {}
    public RestaurantEmployeeEntity(UUID restaurantId, UUID employeeId) {
        this.restaurantId = restaurantId;
        this.employeeId = employeeId;
    }
}


