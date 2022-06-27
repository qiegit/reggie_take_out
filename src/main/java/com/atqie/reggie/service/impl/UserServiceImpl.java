package com.atqie.reggie.service.impl;

import com.atqie.reggie.entity.User;
import com.atqie.reggie.mapper.UserMapper;
import com.atqie.reggie.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @Author 郄
 * @Date 2022/6/24 9:01
 * @Description:
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
