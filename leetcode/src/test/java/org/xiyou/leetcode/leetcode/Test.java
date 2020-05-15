package org.xiyou.leetcode.leetcode;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

/**
 * @author xiyou
 * @version 1.0
 * xiyou-todo XXX功能
 * @date 2020/5/12 23:11
 */

public class Test {

    public static void test45() {
        try {
            Class.forName("org.xiyou.leetcode.javabase.ClassForName");
            System.out.println("#########-------------结束符------------##########");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void test44() {
        try {
            ClassLoader.getSystemClassLoader().loadClass("org.xiyou.leetcode.javabase.ClassForName");
            System.out.println("#########-------------结束符------------##########");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        test44();

//        test45();
    }
}
