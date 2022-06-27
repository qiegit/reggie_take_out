package com.atqie.reggie.service.impl;

import com.atqie.reggie.common.CustomerException;
import com.atqie.reggie.entity.Category;
import com.atqie.reggie.entity.Dish;
import com.atqie.reggie.entity.Setmeal;
import com.atqie.reggie.mapper.CategoryMapper;
import com.atqie.reggie.service.CategoryService;
import com.atqie.reggie.service.DishService;
import com.atqie.reggie.service.SetmealService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author 郄
 * @Date 2022/6/22 9:48
 * @Description:
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;
    /**
     * 删除菜品分类之前检查是否有菜品或套餐
     * @param id
     */
    @Override
    public void remove(long id) {

        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId, id);
        int dishCount = dishService.count(dishLambdaQueryWrapper);
        if (dishCount>0){
            throw new CustomerException("该菜品分类下有菜品");
        }

        LambdaQueryWrapper<Setmeal> setmealQueryWrapper = new LambdaQueryWrapper<>();
        setmealQueryWrapper.eq(Setmeal::getCategoryId,id );
        int setmealCount = setmealService.count(setmealQueryWrapper);
        if (setmealCount>0){
            throw new CustomerException("该菜品分类下有套餐");
        }

        super.removeById(id);
    }
}
