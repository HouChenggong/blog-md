package org.xiyou.leetcode.thread.sync.sync7;

/**
 * @author xiyouyan
 * @date 2020-08-20 10:33
 * @description
 */
public class SyncTest1 {
    static int a = 0;

    /**
     * 无论怎么执行最后结果都不会是
     *
     * @throws InterruptedException
     */
    private static void test2() throws InterruptedException {
        new Thread(new Worker(), "线程1").start();
        new Thread(new Worker(), "线程2").start();
        Thread.sleep(2000);
        System.out.println("让主线程睡眠2秒保证2个线程执行完成，打印最后结果：" + a);
    }

    public static void main(String[] args) throws Exception {
        test2();
    }

    static class Worker implements Runnable {
        @Override
        public void run() {
            for (int i = 0; i < 10000; i++) {
                a++;
            }
        }
    }
}
