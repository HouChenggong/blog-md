package org.xiyou.leetcode.javabase;

/**
 * @author xiyou
 * @version 1.0
 * xiyou-todo java Final的作用
 * @date 2020/5/9 9:14
 */
public class FinalExample {
    private int i;                            // 普通变量
    private final int j;                      //final 变量
    static FinalExample obj;


    public FinalExample() {     // 构造函数
        i = 1;                        // 写普通域
        j = 2;                        // 写 final 域
    }

    public static void writer() {    // 写线程 A 执行
        obj = new FinalExample();
        System.out.println("1");
    }

    public static void reader() {       // 读线程 B 执行
        FinalExample object = obj;       // 读对象引用
        int a = object.i;                // 读普通域
        int b = object.j;                // 读 final 域
        System.out.println("2");
    }

    public static void main(String[] args) {
        writer();
        reader();
    }
}
