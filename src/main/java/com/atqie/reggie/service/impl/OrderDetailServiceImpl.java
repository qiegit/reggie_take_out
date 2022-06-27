package com.atqie.reggie.service.impl;

import com.atqie.reggie.entity.OrderDetail;
import com.atqie.reggie.mapper.OrderDetailMapper;
import com.atqie.reggie.service.OrderDetailService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @Author 郄
 * @Date 2022/6/25 9:29
 * @Description:
 */
@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail>  implements OrderDetailService {
}
