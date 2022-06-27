package com.atqie.reggie.dto;

import com.atqie.reggie.entity.Setmeal;
import com.atqie.reggie.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
