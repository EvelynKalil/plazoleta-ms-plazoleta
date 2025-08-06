package com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.spi;

import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.Dish;

public interface DishPersistencePort {
    Dish saveDish(Dish dish);
}
