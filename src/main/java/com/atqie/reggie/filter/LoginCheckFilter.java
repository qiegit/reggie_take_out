package com.atqie.reggie.filter;


import com.alibaba.fastjson.JSON;
import com.atqie.reggie.common.BaseContext;
import com.atqie.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * @Author 郄
 * @Date 2022/6/20 16:42
 * @Description:
 */
@Slf4j
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
public class LoginCheckFilter implements Filter {
//  路径匹配器，适用通配符
    public static final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;


//        1.判断请求是否需要处理
        String requestURI = request.getRequestURI();


        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/login",
                "/user/sendMsg"
        };
        boolean check = check(urls, requestURI);
        if (check){
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
//    2. 判断是否登录
        if (request.getSession().getAttribute("employee")!=null){
            log.info("登录成功");
            Long employeeId = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(employeeId);

            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        //    2-2. 移动端判断是否登录
        if (request.getSession().getAttribute("user")!=null){
            log.info("登录成功");
            Long userId = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);

            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));

        return;


    }

    /**
     * 检查路径是否匹配
     * @return
     */
    public boolean check(String[] urls,String requet){
        for (String u : urls) {
            boolean match = antPathMatcher.match(u, requet);
            if (match){
                return true;
            }
        }
        return false;
    }
}
