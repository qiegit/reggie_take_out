package com.atqie.reggie.service.impl;

import com.atqie.reggie.common.CustomerException;
import com.atqie.reggie.dto.DishDto;
import com.atqie.reggie.dto.SetmealDto;
import com.atqie.reggie.entity.Setmeal;
import com.atqie.reggie.entity.SetmealDish;
import com.atqie.reggie.mapper.SetmealMapper;
import com.atqie.reggie.service.CategoryService;
import com.atqie.reggie.service.SetmealDishService;
import com.atqie.reggie.service.SetmealService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author 郄
 * @Date 2022/6/22 11:14
 * @Description:
 */
@Service
public class SermealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealService setmealService;
    @Autowired
    private SetmealDishService setmealDishService;


    public void deleteWithDish(String ids){
//        套餐正在售卖不能删除
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.in(Setmeal::getId, ids);
        setmealLambdaQueryWrapper.eq(Setmeal::getStatus, 1);
        int count = this.count(setmealLambdaQueryWrapper);
        if (count>0){
            throw new CustomerException("套餐正在售卖，不能删除");
        }
//        如果可以删除，先删除setmeal
        this.removeById(ids);

//        删除菜品和套餐
        Setmeal setmeal = this.getById(ids);
        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishLambdaQueryWrapper.in(SetmealDish::getSetmealId,setmeal.getId() );
        setmealDishService.remove(setmealDishLambdaQueryWrapper);
//        删除套餐
    }

    public void updateWithDish(SetmealDto setmealDto){
        this.updateById(setmealDto);
//         先删除再保存
        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishLambdaQueryWrapper.eq(SetmealDish::getSetmealId,setmealDto.getId() );
        setmealDishService.remove(setmealDishLambdaQueryWrapper);
//        保存
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes = setmealDishes.stream().map((item) ->{
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        setmealDishService.saveBatch(setmealDishes);
    }

    public SetmealDto getWithDish(Long setmealId){
//        查找套餐保存
        SetmealDto setmealDto = new SetmealDto();
        Setmeal setmeal = this.getById(setmealId);
        BeanUtils.copyProperties(setmeal, setmealDto);
//        查找菜品保存
        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishLambdaQueryWrapper.eq(SetmealDish::getSetmealId,setmeal.getId());
        List<SetmealDish> list = setmealDishService.list(setmealDishLambdaQueryWrapper);
        setmealDto.setSetmealDishes(list);
        return setmealDto;
    }

    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
//        保存套餐
        this.save(setmealDto);
//        保存套餐菜品

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes = setmealDishes.stream().map((item) ->{
//            获取设置setmealid
            LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
            setmealLambdaQueryWrapper.eq(setmealDto.getCategoryId()!=null, Setmeal::getCategoryId, setmealDto.getCategoryId());
            Setmeal setmeal = setmealService.getOne(setmealLambdaQueryWrapper);
            item.setSetmealId(setmeal.getId());
            return item;
        }).collect(Collectors.toList());
        setmealDishService.saveBatch(setmealDishes);
    }
}
