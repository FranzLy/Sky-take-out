package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.entity.Dish;

public interface DishService {

    /**
     * 新增菜品
     * @param dishDTO
     */
    public void saveWithFalvors(DishDTO dishDTO);
}
