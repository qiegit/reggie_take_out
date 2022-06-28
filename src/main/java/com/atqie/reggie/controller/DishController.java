package com.atqie.reggie.controller;

import com.atqie.reggie.common.R;
import com.atqie.reggie.dto.DishDto;
import com.atqie.reggie.entity.Category;
import com.atqie.reggie.entity.Dish;
import com.atqie.reggie.entity.DishFlavor;
import com.atqie.reggie.service.CategoryService;
import com.atqie.reggie.service.DishFlavorService;
import com.atqie.reggie.service.DishService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Author 郄
 * @Date 2022/6/22 15:54
 * @Description:
 */
@RestController
@Slf4j
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

//    @GetMapping("/list")
//    public R<List<Dish>> list(Dish dish){
//        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
//        dishLambdaQueryWrapper.eq(dish.getCategoryId()!=null, Dish::getCategoryId,dish.getCategoryId() );
//        dishLambdaQueryWrapper.eq(Dish::getStatus ,1 );
//        dishLambdaQueryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
//        List<Dish> list = dishService.list(dishLambdaQueryWrapper);
//        return R.success(list);
//    }

    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){
        List<DishDto> dishDtoList = null;
        String key = "dish_"+dish.getCategoryId()+"_"+dish.getStatus();  //dish_CategoryId_status
        dishDtoList = (List<DishDto>) redisTemplate.opsForValue().get(key);
        if (dishDtoList != null){
            return R.success(dishDtoList);
        }

//        查询dish
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(dish.getCategoryId()!=null, Dish::getCategoryId,dish.getCategoryId() );
        dishLambdaQueryWrapper.eq(Dish::getStatus ,1 );
        dishLambdaQueryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> dishList = dishService.list(dishLambdaQueryWrapper);
//        构造dishdto
         dishDtoList = dishList.stream().map((item) ->{
//            赋值dish
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto );
//            分类
            Category category = categoryService.getById(item.getCategoryId());
            if (category !=null){
                dishDto.setCategoryName(category.getName());
            }
//            赋值flavors
            LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
            dishFlavorLambdaQueryWrapper.eq(DishFlavor::getDishId,item.getId() );
            List<DishFlavor> dishFlavorList = dishFlavorService.list(dishFlavorLambdaQueryWrapper);
            dishDto.setFlavors(dishFlavorList);
            return dishDto;
        }).collect(Collectors.toList());
         redisTemplate.opsForValue().set(key,dishDtoList, 60,TimeUnit.MINUTES);

        return R.success(dishDtoList);
    }

    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        String key = "dish_"+dishDto.getCategoryId()+"_1";  //dish_CategoryId_status
        dishService.updateWithFlavor(dishDto);
        redisTemplate.delete(key);
        return R.success("修改成功");
    }

    @GetMapping("/{ids}")
    public R<DishDto> getById(@PathVariable("ids") Long ids){
        DishDto dishDto = dishService.getByIdWithFlavor(ids);

        return R.success(dishDto);
    }

    @DeleteMapping
    public R<String> delete(String ids){

        dishService.removeById(ids);
        LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishFlavorLambdaQueryWrapper.eq(DishFlavor::getDishId,ids );
        dishFlavorService.remove(dishFlavorLambdaQueryWrapper);
        return R.success("删除成功");
    }


    @GetMapping("/page")
    public R<Page<DishDto>> page(int page,int pageSize,String name){
        Page<Dish> dishPage = new Page<>(page,pageSize);
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.like(name != null, Dish::getName, name);
        dishService.page(dishPage);

//        对象拷贝
        Page<DishDto> dishDtoPage = new Page<>();
        BeanUtils.copyProperties(dishPage, dishDtoPage, "records");
        List<Dish> dishPageRecords = dishPage.getRecords();
        List<DishDto> list = dishPageRecords.stream().map((item) ->{
//            对象拷贝
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
//            菜品类名
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if (categoryId!=null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }

            return dishDto;
        }).collect(Collectors.toList());

        dishDtoPage.setRecords(list);
        return R.success(dishDtoPage);
    }

    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        log.info("-==================="+dishDto);
        dishService.saveWithFlavor(dishDto);
        return R.success("添加成功");
    }
}
