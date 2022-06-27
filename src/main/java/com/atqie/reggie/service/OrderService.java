package com.atqie.reggie.service;

import com.atqie.reggie.dto.OrdersDto;
import com.atqie.reggie.entity.Orders;
import com.baomidou.mybatisplus.extension.service.IService;


/**
 * @Author 郄
 * @Date 2022/6/25 9:26
 * @Description:
 */
public interface OrderService extends IService<Orders> {

    void submit(Orders orders);
}
