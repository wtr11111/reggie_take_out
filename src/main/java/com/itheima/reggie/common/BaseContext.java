package com.itheima.reggie.common;

//基于ThreadLocal封装工具类，用于保存和获取当前登录用户id
//BaseContext的作用范围在某一个线程之内
public class BaseContext {
    private static ThreadLocal<Long> threadLocal=new ThreadLocal<>();

//    保存id
    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }
//    获取id
    public static Long getCurrentId(){
        return threadLocal.get();
    }
}
