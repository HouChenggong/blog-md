package org.xiyou.leetcode.thread;

/**
 * @author xiyou
 * @version 1.0
 * xiyou-todo XXX功能
 * describe:
 * @date 2020/6/15 10:31
 */
import java.util.concurrent.*;

public class MyTestMap {

    private static final int maxSize = 5;
    public static void main(String[] args){
        ArrayBlockingQueue<Integer> queue = new ArrayBlockingQueue<Integer>(maxSize);
        new Thread(new Productor(queue)).start();
        new Thread(new Customer(queue)).start();
    }
}

class Customer implements Runnable {
    private BlockingQueue<Integer> queue;
    Customer(BlockingQueue<Integer> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        this.cusume();
    }

    private void cusume() {
        while (true) {
            try {
                int count = (int) queue.take();
                System.out.println("customer正在消费第" + count + "个商品===");

                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

class Productor implements Runnable {
    private BlockingQueue<Integer> queue;
    private int count = 1;
    Productor(BlockingQueue<Integer> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        this.product();
    }
    private void product() {
        while (true) {
            try {
                queue.put(count);
                System.out.println("生产者正在生产第" + count + "个商品");
                count++;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
