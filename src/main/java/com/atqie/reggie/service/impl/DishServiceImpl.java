package com.atqie.reggie.service.impl;

import com.atqie.reggie.dto.DishDto;
import com.atqie.reggie.entity.Category;
import com.atqie.reggie.entity.Dish;
import com.atqie.reggie.entity.DishFlavor;
import com.atqie.reggie.mapper.DishMapper;
import com.atqie.reggie.service.CategoryService;
import com.atqie.reggie.service.DishFlavorService;
import com.atqie.reggie.service.DishService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sun.org.apache.regexp.internal.RE;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author 郄
 * @Date 2022/6/22 11:12
 * @Description:
 */
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    /**
     * 保存菜品同时保存口味
     * @param dishDto
     */

    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    @Transactional
    @Override
    public void saveWithFlavor(DishDto dishDto) {
        super.save(dishDto);

//        for (DishFlavor flavor : dishDto.getFlavors()) {
//            flavor.setDishId(dishDto.getCategoryId());
//            dishFlavorService.save(flavor);
//        }
        Long id = dishDto.getId();
        List<DishFlavor> flavors = dishDto.getFlavors();

        flavors = flavors.stream().map((item) ->{
            item.setDishId(id);
            return item;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);
    }

    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        this.updateById(dishDto);
//        先删除口味表再添加,
//        dishFlavorService.removeById(dishDto.getId());  逻辑删除会显示用户已存在

        LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishFlavorLambdaQueryWrapper.eq(DishFlavor::getDishId ,dishDto.getId() );
        dishFlavorService.remove(dishFlavorLambdaQueryWrapper);

        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item) ->{
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);
    }

    @Override
    public DishDto getByIdWithFlavor(Long ids) {
//        查询dish
        Dish dish = this.getById(ids);

//       对象拷贝
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto );

//        获取设置分类名
//        Long categoryId = dish.getCategoryId();
//        Category categoryById = categoryService.getById(categoryId);
//        String categoryByIdName = categoryById.getName();
//        dishDto.setCategoryName(categoryByIdName);

//       获取设置口味
        LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishFlavorLambdaQueryWrapper.eq(ids!=null, DishFlavor::getDishId, ids);
        List<DishFlavor> dishFlavors = dishFlavorService.list(dishFlavorLambdaQueryWrapper);
        dishDto.setFlavors(dishFlavors);
        return dishDto;
    }
}
