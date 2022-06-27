package com.atqie.reggie.common;

/**
 * @Author 郄
 * @Date 2022/6/22 8:51
 * @Description:
 */
public class BaseContext {

    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }
    public static Long getCurrentId(){
        return threadLocal.get();
    }
}
