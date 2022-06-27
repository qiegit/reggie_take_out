package com.atqie.reggie.controller;

import com.atqie.reggie.common.R;
import com.atqie.reggie.entity.Employee;
import com.atqie.reggie.service.EmployeeService;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;


/**
 * @Author 郄
 * @Date 2022/6/18 19:06
 * @Description:
 */
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;


    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee){
        Long employee1 = (Long) request.getSession().getAttribute("employee");
//        employee.setUpdateUser(employee1);
//        employee.setUpdateTime(LocalDateTime.now());

        long current = Thread.currentThread().getId();
        log.info("update当前线程id为："+current);
        employeeService.updateById(employee);
        return R.success("员工信息修改成功");
    }

    /**
     * 根据id查找用户，请求为restful格式，PathVariable
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable("id") Long id){
        log.info("查询用户id："+id);
        Employee byId = employeeService.getById(id);
        return R.success(byId);
    }

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){

        Page pageInfo = new Page(page,pageSize);

        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.like(StringUtils.isNotEmpty(name), Employee::getName, name);
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        employeeService.page(pageInfo, queryWrapper);

        return R.success(pageInfo);

    }


    @PostMapping
    public R<String> save(HttpServletRequest request,@RequestBody Employee employee){

       employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
//       employee.setCreateTime(LocalDateTime.now());
//       employee.setUpdateTime(LocalDateTime.now());
//        Long employeeId = (Long) request.getSession().getAttribute("employee");
//        employee.setCreateUser(employeeId);
//        employee.setUpdateUser(employeeId);

        employeeService.save(employee);
        return R.success("新增员工成功");
    }


    @PostMapping("/login")
    public R<Employee> login(@RequestBody Employee employee, HttpServletRequest request){

//        1.将页面提交的密码进行MD5加密
        String password = DigestUtils.md5DigestAsHex(employee.getPassword().getBytes());
//       2.根据页面提交的用户名查找数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);
//        3.数据库用户名是唯一索引，查看是否存在
        if (emp == null){
            return R.error("用户不存在，请重新输入");
        }
//        4.对比密码是否正确
        if (!password.equals(emp.getPassword())){
            return R.error("密码错误，请重新输入");
        }
//        5.查看用户是否可用
        if (emp.getStatus()==0){
            return R.error("用户已禁用");
        }
//        6.登录成功，讲id仿佛session
        request.getSession().setAttribute("employee", emp.getId());
        return  R.success(emp);
    }

    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest httpServletRequest){
        httpServletRequest.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

}
