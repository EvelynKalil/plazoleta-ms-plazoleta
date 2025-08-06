package com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.output.jpa.entity;

import lombok.*;
import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "dishes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class DishEntity {

    @Id
    @GeneratedValue
    private UUID id;

    private String name;

    private Integer price;

    private String description;

    private String urlImage;

    private String category;

    @Column(name = "restaurant_id", nullable = false)
    private UUID restaurantId;

    private boolean active = true;
}
