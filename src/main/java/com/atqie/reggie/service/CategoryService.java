package com.atqie.reggie.service;

import com.atqie.reggie.entity.Category;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Author 郄
 * @Date 2022/6/22 9:47
 * @Description:
 */
public interface CategoryService extends IService<Category> {

    void remove(long id);
}
