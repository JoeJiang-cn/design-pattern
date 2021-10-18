package com.joe.pattern.reflect;

/**
 * @author Joe
 * TODO description
 * 2021/9/18 16:51
 */
public class Target {
    private String value;

    public Target() {
        value = "JavaGuide";
    }

    public void publicMethod(String s) {
        System.out.println("I love " + s);
    }

    private void privateMethod() {
        System.out.println("value is " + value);
    }
}
