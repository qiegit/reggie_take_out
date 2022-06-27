package com.atqie.reggie.controller;

import com.atqie.reggie.common.R;
import com.atqie.reggie.dto.OrdersDto;
import com.atqie.reggie.entity.Orders;
import com.atqie.reggie.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author 郄
 * @Date 2022/6/25 9:31
 * @Description:
 */
@RestController
@Slf4j
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        orderService.submit(orders);
        return R.success("提交订单成功");
    }

}
