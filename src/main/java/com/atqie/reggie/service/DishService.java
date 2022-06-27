package com.atqie.reggie.service;

import com.atqie.reggie.dto.DishDto;
import com.atqie.reggie.entity.Dish;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Author 郄
 * @Date 2022/6/22 11:12
 * @Description:
 */
public interface DishService extends IService<Dish> {

    void saveWithFlavor(DishDto dishDto);

    void updateWithFlavor(DishDto dishDto);



    DishDto getByIdWithFlavor(Long ids);
}
