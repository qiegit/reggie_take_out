package com.atqie.reggie.service.impl;

import com.atqie.reggie.entity.ShoppingCart;
import com.atqie.reggie.mapper.ShoppingCartMapper;
import com.atqie.reggie.service.ShoppingCartService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @Author 郄
 * @Date 2022/6/24 17:17
 * @Description:
 */
@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
}
