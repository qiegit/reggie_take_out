package com.atqie.reggie.dto;

import com.atqie.reggie.entity.Dish;
import com.atqie.reggie.entity.DishFlavor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author 郄
 * @Date 2022/6/22 18:44
 * @Description:
 */
@Data
public class DishDto extends Dish {

    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
