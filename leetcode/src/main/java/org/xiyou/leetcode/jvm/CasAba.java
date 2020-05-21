package org.xiyou.leetcode.jvm;


import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * @author xiyou
 * @version 1.0
 * xiyou-todo CAS ABA问题测试
 * @date 2020/5/21 9:32
 */
public class CasAba {
    private static AtomicInteger atomicInt = new AtomicInteger(100);

    public static void main(String[] args) {


        Thread intT1 = new Thread(new Runnable() {
            @Override
            public void run() {
                atomicInt.compareAndSet(100, 99);
                System.out.println(atomicInt.get() + "---100变成99");
                atomicInt.compareAndSet(99, 100);
                System.out.println(atomicInt.get() + "---99变成100");
            }
        });

        Thread intT2 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    intT1.join();
                    TimeUnit.SECONDS.sleep(1);
                    System.out.println(atomicInt.get() + "---接下来如果是100，就更新成功101");
                } catch (InterruptedException e) {
                }
                boolean c3 = atomicInt.compareAndSet(100, 101);
                System.out.println(c3); // true
            }
        });

        intT1.start();
        intT2.start();
    }
}
