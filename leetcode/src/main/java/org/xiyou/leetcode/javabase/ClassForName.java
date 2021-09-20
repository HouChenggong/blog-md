package org.xiyou.leetcode.javabase;

/**
 * @author xiyou
 * @version 1.0
 * xiyou-todo Class.forName 和 ClassLoader 到底有啥区别？
 * @date 2020/5/12 23:00
 */
public class ClassForName {
    //静态代码块
    static {
        System.out.println("执行了静态代码块");
    }

    //静态变量
    private static String staticFiled = staticMethod();

    //赋值静态变量的静态方法
    public static String staticMethod() {
        System.out.println("执行了静态方法");
        return "给静态字段赋值了";
    }
}
