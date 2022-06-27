package com.atqie.reggie.service.impl;

import com.atqie.reggie.entity.Employee;
import com.atqie.reggie.mapper.EmployeeMapper;
import com.atqie.reggie.service.EmployeeService;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @Author 郄
 * @Date 2022/6/18 19:02
 * @Description:
 */
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper,Employee> implements EmployeeService {

}
