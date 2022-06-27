package com.atqie.reggie.controller;

import com.atqie.reggie.common.R;
import com.atqie.reggie.dto.SetmealDto;
import com.atqie.reggie.entity.Category;
import com.atqie.reggie.entity.Setmeal;
import com.atqie.reggie.entity.SetmealDish;
import com.atqie.reggie.service.CategoryService;
import com.atqie.reggie.service.SetmealDishService;
import com.atqie.reggie.service.SetmealService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author 郄
 * @Date 2022/6/23 16:18
 * @Description:
 */
@RestController
@Slf4j
@RequestMapping("/setmeal")
public class SermealController {

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SetmealDishService setmealDishService;

    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal){
        Long categoryId = setmeal.getCategoryId();
        Integer status = setmeal.getStatus();
//        赋值setmeal
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(categoryId != null,Setmeal::getCategoryId, categoryId);
        setmealLambdaQueryWrapper.eq(Setmeal::getStatus, status);
        setmealLambdaQueryWrapper.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> setmeals = setmealService.list(setmealLambdaQueryWrapper);
        return R.success(setmeals);
    }

    @DeleteMapping
    public R<String> delete(String ids){
        setmealService.deleteWithDish(ids);
        return R.success("删除成功");
    }

    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto){
        setmealService.updateWithDish(setmealDto);
        return R.success("修改成功");
    }


    @GetMapping("/{setmealId}")
    public R<SetmealDto> get(@PathVariable("setmealId") Long setmealId){
        SetmealDto setmealDto = setmealService.getWithDish(setmealId);
        return R.success(setmealDto);
    }
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){
        setmealService.saveWithDish(setmealDto);
        return R.success("保存成功");
    }

    @GetMapping("/page")
    public R<Page<SetmealDto>> page(int page,int pageSize,String name){
//        查询setmeal 的page
        Page<Setmeal> setmealPage = new Page(page,pageSize);
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.like(name!=null,Setmeal::getName ,name );
        setmealService.page(setmealPage,setmealLambdaQueryWrapper );

        Page<SetmealDto> setmealDtoPage = new Page<>();
//        对象拷贝为setmealDto
        BeanUtils.copyProperties(setmealPage, setmealDtoPage,"records");
//        为records赋值
        List<Setmeal> records = setmealPage.getRecords();
        List<SetmealDto> list= records.stream().map((item) ->{
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);
//            查询分类名并赋值
            Long categoryId = item.getCategoryId();
            Category categoryServiceById = categoryService.getById(categoryId);
            if (categoryServiceById!=null) {
                setmealDto.setCategoryName(categoryServiceById.getName());
            }
            return setmealDto;
        }).collect(Collectors.toList());
        setmealDtoPage.setRecords(list);
        return R.success(setmealDtoPage);
    }

}
