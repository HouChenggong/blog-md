package org.xiyou.leetcode.thread;

import java.util.concurrent.Exchanger;

/**
 * @author xiyou
 * @version 1.0
 * xiyou-todo Exchanger
 * https://mp.weixin.qq.com/s/aYfUlwxxW9jm2tStcuIN7g
 * 只能两个线程互相交换
 * @date 2020/5/10 20:48
 */
public class T12_TestExchanger {
    static Exchanger<String> exchanger = new Exchanger<>();

    public static void main(String[] args) {
        new Thread(() -> {
            String s = "T1";
            try {
                s = exchanger.exchange(s);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + " " + s);

        }, "t1").start();


        new Thread(() -> {
            String s = "T2";
            try {
                s = exchanger.exchange(s);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + " " + s);

        }, "t2").start();


    }
}
