package org.xiyou.leetcode.thread.vola;

import java.util.concurrent.TimeUnit;

/**
 * @author xiyou
 * @version 1.0
 * xiyou-todo 测试volatile修饰数组元素
 *
 * @date 2020/5/25 21:52
 */
public class TestVolatileArr {
    public static   int[] array = new int[10];
    public static void main(String[] args) {
        new Thread(() -> {  //线程A
            try {
                TimeUnit.MILLISECONDS.sleep(100);
                array[0] = 2;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }).start();
        new Thread(()->{   //线程B
            try {
                while (true) {

                    if (array[0] == 2) {
                        System.out.println("结束");
                        break;
                    }
                    Thread.sleep(10);
                }
            }catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
