package org.xiyou.leetcode.thread.sync;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author xiyou
 * @version 1.0
 * xiyou-todo XXX功能
 * @date 2020/5/25 16:35
 */
public class TestSync1 {
    static TestSync1 instance=new TestSync1();
    static  int i=0;
    static  int j=0;
    public synchronized  void test1(){
        for(int a=0;a<100;a++){
            i++;
            inMethod();
        }
    }
    public synchronized  void inMethod(){
        j++;
    }


    public static void main(String[] args) throws InterruptedException {
        TestSync1 testSync1=new TestSync1();
        Runnable runnable=new Runnable() {
            @Override
            public void run() {
                testSync1.test1();
            }
        };
        ConcurrentHashMap map;
        Thread t1=new Thread(runnable);
        Thread t2=new Thread(runnable);
        t1.start();t2.start();
        t1.join();
        t2.join();
        System.out.println(i);

    }
}
