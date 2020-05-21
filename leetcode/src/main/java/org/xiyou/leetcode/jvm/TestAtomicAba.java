package org.xiyou.leetcode.jvm;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicStampedReference;

/**
 * @author xiyou
 * @version 1.0
 * xiyou-todo 解决CAS ABA问题的方式就是加一个版本
 * @date 2020/5/21 9:32
 */
public class TestAtomicAba {
    private static AtomicStampedReference atomicStampedRef = new AtomicStampedReference(100, 0);


    public static void main(String[] args) {


        Thread refT1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                }
                atomicStampedRef.compareAndSet(100, 99, atomicStampedRef.getStamp(), atomicStampedRef.getStamp() + 1);
                System.out.println(atomicStampedRef.getReference() + "---100变成99");
                atomicStampedRef.compareAndSet(99, 100, atomicStampedRef.getStamp(), atomicStampedRef.getStamp() + 1);
                System.out.println(atomicStampedRef.getReference() + "---99变成100");
            }
        });

        Thread refT2 = new Thread(new Runnable() {
            @Override
            public void run() {

                int stamp = atomicStampedRef.getStamp();
                try {
                    refT1.join();
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                }
                System.out.println(atomicStampedRef.getStamp());
                System.out.println(atomicStampedRef.getReference() + "---接下来如果是100，就更新成功101");
                boolean c3 = atomicStampedRef.compareAndSet(100, 101, stamp, stamp + 1);
                System.out.println(c3); // false
            }
        });

        refT1.start();
        refT2.start();


    }
}