package com.atqie.reggie.mapper;

import com.atqie.reggie.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author 郄
 * @Date 2022/6/24 9:00
 * @Description:
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
