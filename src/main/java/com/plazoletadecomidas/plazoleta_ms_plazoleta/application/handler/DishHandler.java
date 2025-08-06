package com.plazoletadecomidas.plazoleta_ms_plazoleta.application.handler;

import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto.DishRequestDto;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.dto.DishResponseDto;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.application.mapper.DishMapper;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.api.DishServicePort;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.Dish;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DishHandler {

    private final DishServicePort dishServicePort;
    private final DishMapper dishMapper;

    public DishHandler(DishServicePort dishServicePort, DishMapper dishMapper) {
        this.dishServicePort = dishServicePort;
        this.dishMapper = dishMapper;
    }

    public DishResponseDto saveDish(DishRequestDto dto, UUID ownerId) {
        Dish model = dishMapper.toModel(dto);
        dishServicePort.saveDish(model, ownerId);
        return dishMapper.toResponseDto(model);
    }
}
