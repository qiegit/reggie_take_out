package com.atqie.reggie.service.impl;

import com.atqie.reggie.common.BaseContext;
import com.atqie.reggie.common.CustomerException;
import com.atqie.reggie.dto.OrdersDto;
import com.atqie.reggie.entity.*;
import com.atqie.reggie.mapper.OrderMapper;
import com.atqie.reggie.service.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @Author 郄
 * @Date 2022/6/25 9:27
 * @Description:
 */
@Service
public class OrderServiceImpl  extends ServiceImpl<OrderMapper, Orders> implements OrderService {

    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressBookService addressBookService;

    @Autowired
    private ShoppingCartService shoppingCartService;

    public void submit(Orders orders){

//        1.查询用户信息
        Long userId = BaseContext.getCurrentId();
        User user = userService.getById(userId);
//        2.查询地址信息
        Long addressBookId = orders.getAddressBookId();
        AddressBook addressBook = addressBookService.getById(addressBookId);
        if (addressBook == null){
            throw new CustomerException("收货地址为空，不能下单");
        }
//        查询购物车信息
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId,userId );
        List<ShoppingCart> shoppingCarts = shoppingCartService.list(shoppingCartLambdaQueryWrapper);
        if (shoppingCarts == null){
            throw new CustomerException("购物车为空，不能下单");
        }
//        3.插入一条订单信息
        long orderId = IdWorker.getId();  //订单号

        AtomicInteger amount = new AtomicInteger(0);

        List<OrderDetail> orderDetails = shoppingCarts.stream().map((item) ->{
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setImage(item.getImage());
            orderDetail.setName(item.getName());
            orderDetail.setNumber(item.getNumber());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setAmount(item.getAmount());
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;
        }).collect(Collectors.toList());

        orders.setNumber(String.valueOf(orderId));
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setOrderTime(LocalDateTime.now());
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(user.getPhone());
        orders.setUserId(user.getId());
        orders.setUserName(addressBook.getConsignee());
        orders.setStatus(2);

        orders.setAmount(new BigDecimal(amount.get()));
        orders.setAddress((addressBook.getProvinceName()==null?"" : addressBook.getProvinceName())
                        +(addressBook.getCityName()==null?"" : addressBook.getCityName())
                        +(addressBook.getDistrictName()==null?"" : addressBook.getDistrictName())
                        +(addressBook.getDetail()==null?"" : addressBook.getDetail()));

        this.save(orders);

//        4.构建购物车详细信息
        orderDetailService.saveBatch(orderDetails);

//        5.清空购物车
        shoppingCartService.remove(shoppingCartLambdaQueryWrapper);
    }
}
