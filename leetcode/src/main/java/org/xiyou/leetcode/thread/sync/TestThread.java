package org.xiyou.leetcode.thread.sync;

public class TestThread {

    public  Object o;
    synchronized void test() {
        try {
            o.wait();
           boolean flag= Thread.holdsLock(o);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    void test2() {
        synchronized (this) {
         o.notify();
        }
    }

   static synchronized void  test3(){

    }

     void test4(){
        synchronized (TestThread.class){

        }
    }

    void test5(){
        synchronized (o){

        }
    }
}
