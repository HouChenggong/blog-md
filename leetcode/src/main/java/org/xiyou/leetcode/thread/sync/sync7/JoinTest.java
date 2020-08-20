package org.xiyou.leetcode.thread.sync.sync7;

/**
 * @author xiyouyan
 * @date 2020-08-20 09:43
 * @description /**
 * * 不使用synchronized,两个线程同时a++
 * * 发现结果不固定，而且肯定比20000小
 */


public class JoinTest {



    /**
     * 下面的代码结果肯定是：先打印end，然后再打印thread....run
     */
    public static void test1() {
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("thread....run");

            }
        });
        t1.start();
        System.out.println("end");
    }

    /**
     * 如何让end在线程t1执行完之后再执行？
     */
    public static void test12() throws InterruptedException {
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("thread....run");

            }
        });
        t1.start();
        //让主线程等待t1执行完之后再执行
        t1.join();
        System.out.println("end");
    }

    /**
     * @throws InterruptedException 让线程T1 ，t2 顺序执行，而且main线程最后执行
     */
    public static void test13() throws InterruptedException {
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("thread1....run");

            }
        });
        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("thread2....run");

            }
        });
        t1.start();
        //等待t1结束，这时候t2线程并未启动
        t1.join();
        //t2启动
        t2.start();
        //等待t2结束
        t2.join();
        System.out.println("end");
    }


    /**
     * @throws InterruptedException 让线程T1 ，t2 并行执行，而且main线程最后执行
     */
    public static void test14() throws InterruptedException {
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("thread1....run");

            }
        });
        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("thread2....run");

            }
        });
        //t1启动
        t1.start();
        //t2启动
        t2.start();
        //等待t1结束
        t1.join();
        //等待t2结束
        t2.join();
        System.out.println("end");
    }


    /**
     * @throws InterruptedException 让线程T1 ，t2 ,t2顺序执行，而且t1执行完之后才是t2,t2执行完之后才是t3
     */
    public static void test15() throws InterruptedException {
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("thread1....run");

            }
        });
        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    t1.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("thread2....run");

            }
        });
        Thread t3 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    t2.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("thread3....run");

            }
        });
        t1.start();
        t2.start();
        t3.start();
    }

    public static void main(String[] args) throws Exception {
        test15();
    }



}
