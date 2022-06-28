package com.atqie.reggie.controller;

import com.atqie.reggie.common.R;
import com.atqie.reggie.entity.User;
import com.atqie.reggie.service.UserService;
import com.atqie.reggie.utils.ValidateCodeUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Author 郄
 * @Date 2022/6/24 9:01
 * @Description:
 */
@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private UserService userService;

    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){

        String phone = user.getPhone();

        if (StringUtils.isNotEmpty(phone)){
            String code = ValidateCodeUtils.generateValidateCode4String(4).toString();
            log.info("==============手机验证码："+code);
            stringRedisTemplate.opsForValue().set(user.getPhone(),code );
            stringRedisTemplate.expire(user.getPhone(), 5, TimeUnit.MINUTES);
//            session.setAttribute("code", code);
            R.success("手机验证码发送成功");
        }
        return R.error("短信发送失败");
    }

    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session){
        String phone =  String.valueOf(map.get("phone"));
        String pageCode =  String.valueOf(map.get("code"));
//        Object sessionCode = session.getAttribute("code");
        String sessionCode = stringRedisTemplate.opsForValue().get(phone);

        if (sessionCode !=null &&sessionCode.equals(pageCode)){
            stringRedisTemplate.delete(phone);
            LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
            userLambdaQueryWrapper.eq(User::getPhone,phone );
            User getUser = userService.getOne(userLambdaQueryWrapper);
            if (getUser == null){
                User user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            session.setAttribute("user",getUser.getId() );
            return R.success(getUser);
        }
        return R.error("登录失败");
    }
}
