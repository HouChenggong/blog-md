package org.xiyou.leetcode.thread.sync;

public class TestThread {

    public  Object o;
    synchronized void test() {

    }

    void test2() {
        synchronized (this) {

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
