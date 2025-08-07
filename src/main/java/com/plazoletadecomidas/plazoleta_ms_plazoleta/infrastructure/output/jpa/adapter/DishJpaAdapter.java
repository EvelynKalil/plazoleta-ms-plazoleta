package com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.output.jpa.adapter;

import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.Dish;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.spi.DishPersistencePort;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.exception.NotFoundException;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.output.jpa.entity.DishEntity;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.output.jpa.mapper.DishEntityMapper;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.output.jpa.repository.DishRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.UUID;

@Service
public class DishJpaAdapter implements DishPersistencePort {

    private final DishRepository repository;
    private final DishEntityMapper mapper;

    public DishJpaAdapter(DishRepository repository, DishEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Dish saveDish(Dish dish) {
        DishEntity entity = mapper.toEntity(dish);
        DishEntity saved = repository.save(entity);
        return mapper.toModel(saved);
    }

    @Override
    public void updateDish(UUID id, String description, Integer price) {
        DishEntity dish = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Plato no encontrado con id: " + id));

        dish.setDescription(description);
        dish.setPrice(price);

        repository.save(dish);
    }

    @Override
    public Dish getDishById(UUID id) {
        DishEntity entity = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Plato no encontrado con id: " + id));
        return mapper.toModel(entity);
    }

    @Override
    @Transactional
    public void toggleDishStatus(UUID dishId, boolean enabled) {
        DishEntity entity = repository.findById(dishId)
                .orElseThrow(() -> new RuntimeException("Plato no encontrado con id: " + dishId));

        entity.setActive(enabled);
        repository.save(entity);
    }
}
