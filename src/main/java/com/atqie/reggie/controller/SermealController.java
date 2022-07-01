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
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
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
@Api(tags = "套餐接口")
public class SermealController {

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SetmealDishService setmealDishService;

    @GetMapping("/list")
    @Cacheable(value = "setmealCache",key = "#setmeal.categoryId +'_'+ #setmeal.status")
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

    //allEntries 删除全部
    @ApiOperation(value = "删除套餐接口")
    @CacheEvict(value = "setmealCache",allEntries = true)
    @DeleteMapping
    public R<String> delete(String ids){
        setmealService.deleteWithDish(ids);
        return R.success("删除成功");
    }

    @PutMapping
    @ApiOperation(value = "更新套餐接口")
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
    @CacheEvict(value = "setmealCache",allEntries = true)
    @ApiOperation(value = "保存套餐接口")
    public R<String> save(@RequestBody SetmealDto setmealDto){
        setmealService.saveWithDish(setmealDto);
        return R.success("保存成功");
    }

    @GetMapping("/page")
    @ApiOperation(value = "套餐分页接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page",value = "页码",required = true),
            @ApiImplicitParam(name = "pageSize",value = "每页记录数",required = true),
            @ApiImplicitParam(name = "name",value = "套餐名称",required = true)
    })
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
