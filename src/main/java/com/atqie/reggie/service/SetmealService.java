package com.atqie.reggie.service;

import com.atqie.reggie.dto.DishDto;
import com.atqie.reggie.dto.SetmealDto;
import com.atqie.reggie.entity.Setmeal;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Author 郄
 * @Date 2022/6/22 11:14
 * @Description:
 */
public interface SetmealService extends IService<Setmeal> {
    /**
     * 保存套餐并且保存响应菜品
     * @param setmealDto
     */
    void saveWithDish(SetmealDto setmealDto);

    SetmealDto getWithDish(Long setmealId);

    void updateWithDish(SetmealDto setmealDto);

    void deleteWithDish(String ids);
}
