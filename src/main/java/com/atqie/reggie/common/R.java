package com.atqie.reggie.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author 郄
 * @Date 2022/6/20 11:13
 * @Description:
 */
@Data
@ApiModel("返回结果")
public class R<T> implements Serializable {

    @ApiModelProperty("编码")
    private Integer code;       //编码：成功1，错误0以及其他

    @ApiModelProperty("错误信息")
    private String msg;            //错误信息

    @ApiModelProperty("返回数据")
    private T data;                //数据

    private Map map = new HashMap();    //动态数据

//    修饰符 <代表泛型的变量> 返回值类型 方法名(参数){ }
    public static <T> R<T> success(T object){
        R<T> r =new R<T>();
        r.data = object;
        r.code=1;
        return r;
    }
    public static <T> R<T> error(String msg){
        R r = new R();
        r.msg = msg;
        r.code = 0;
        return r;
    }
    public R<T> add(String key,Object value){
        this.map.put(key, value);
        return this;
    }
}
