package com.joe.pattern.reflect;

import java.lang.reflect.Method;

/**
 * @author Joe
 * TODO description
 * 2021/9/18 16:51
 */
public class ReflectTest {
    public static void main(String[] args) throws Exception {
        Class<?> clazz = Class.forName("com.joe.pattern.reflect.Target");
        Target target = (Target) clazz.newInstance();

        // 获取方法
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            System.out.println(method.getName());
        }

        Method publicMethod = clazz.getDeclaredMethod("publicMethod", String.class);
        publicMethod.invoke(target, "hahaha");

        Method privateMethod = clazz.getDeclaredMethod("privateMethod");
        privateMethod.setAccessible(true);
        privateMethod.invoke(target);
    }
}
