package com.itheima.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//检查用户是否已经完成登录
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")  //过滤器的名字和过滤路径
@Slf4j
public class LoginCheckFilter implements Filter {
//    路径匹配器，支持通配符,专门用来路径比较
    public static final AntPathMatcher PATH_MATCHER=new AntPathMatcher();
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request=(HttpServletRequest) servletRequest;
        HttpServletResponse response=(HttpServletResponse) servletResponse;

//        1、获取本次请求的URI
        String requsetURI=request.getRequestURI();
        log.info("拦截到请求：{}",requsetURI);
//        定义不需要处理的请求路径
        String []urls=new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**"
        };
//        2、判断本次请求是否需要处理
        boolean check=check(urls,requsetURI);

//        3、如果不需要处理，直接放行
        if (check){
            log.info("本次请求{}不需要处理",requsetURI);
            filterChain.doFilter(request,response);
            return;
        }

//        4、判断登陆状态，如果已登录，直接放行
        if(request.getSession().getAttribute("employee")!=null){
            log.info("用户已登录，用户id为:{}",request.getSession().getAttribute("employee"));
            Long empId= (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);
//            获取当前线程id，获取登录用户id
//            long id=Thread.currentThread().getId();
//            log.info("线程id：{}",id);

            filterChain.doFilter(request,response);
            return;
        }

//        5、如果未登录则返回未登录结果,通过输出流方式向客户端页面响应数据
        log.info("用户未登录");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }


//    路径匹配：检查本次请求是否想要放行
    public boolean check(String []urls,String requestURI){
        for(String url:urls){
            boolean match=PATH_MATCHER.match(url,requestURI);
            if (match){
                return true;
            }
        }return false;
    }
}
