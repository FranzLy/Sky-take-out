package com.sky.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;

    public DishServiceImpl(DishMapper dishMapper) {
        this.dishMapper = dishMapper;
    }

    /**
     * 新增菜品口味
     * @param dishDTO
     */
    @Transactional
    @Override
    public void saveWithFalvors(DishDTO dishDTO) {
        Dish dish = new Dish();

        //拷贝
        BeanUtils.copyProperties(dishDTO, dish);

        //xml文件中sql语句是返回了dish的id属性的
        dishMapper.insertDish(dish);
        Long dishId = dish.getId();

        //插入对应口味表
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size() > 0) {
            flavors.forEach(flavor -> {
                flavor.setDishId(dishId);
            });
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 菜品删除
     * @param ids
     */
    @Override
    @Transactional
    public void deleteBatchly(List<Long> ids) {
        //查询菜品状态，售卖中的菜品不允许删除
        LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper<>();
        lqw.in(Dish::getId, ids);
        List<Dish> dishes = dishMapper.selectList(lqw);
        for (Dish dish : dishes) {
            if(dish.getStatus() == StatusConstant.ENABLE) {
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }

        //菜品若被套餐关联，同样不能删除
        LambdaQueryWrapper<SetmealDish> lqwSetmealDish = new LambdaQueryWrapper<>();
        lqwSetmealDish.in(SetmealDish::getDishId, ids);
        List<SetmealDish> setmealDishes = setmealDishMapper.selectList(lqwSetmealDish);
        if(setmealDishes != null && setmealDishes.size() > 0) {
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

        //删除菜品表中的数据
        dishMapper.deleteBatchIds(ids);

        //删除菜品对应的口味
        LambdaQueryWrapper<DishFlavor> lqwDishFlavor = new LambdaQueryWrapper<>();
        lqwDishFlavor.in(DishFlavor::getDishId, ids);
        dishFlavorMapper.delete(lqwDishFlavor);

    }
}
