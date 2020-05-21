package org.xiyou.leetcode.tx;

/**
 * @author xiyou
 * @version 1.0
 * xiyou-todo XXX功能
 * @date 2020/5/20 12:20
 */

/*
 * 虚假唤醒的解决：
 *  wait要始终保证在while循环当中。
 */
public class LockTest {
    public static void main(String[] args) {
        Clerk clerk = new Clerk();
        Producter producter = new Producter(clerk);
        Customer customer = new Customer(clerk);

        new Thread(producter,"生产者A").start();
        new Thread(customer,"消费者A").start();
//        new Thread(producter,"生产者B").start();
        new Thread(customer,"消费者B").start();
    }
}

// 售货员
class Clerk {
    private int product = 0;

    // 进货
    public synchronized void add() {
        // 产品已满
        while (product >=1) {
            System.out.println(Thread.currentThread().getName() + ": " + "已满！");
            try {
                this.wait();
            } catch (InterruptedException e) {
            }
        }
        ++product;
        // 该线程从while中出来的时候，是满足条件的
        System.out.println(Thread.currentThread().getName() + ": " +"....................进货成功，剩下"+product);
        this.notifyAll();
    }

    // 卖货
    public synchronized void sale() {
        if (product <=0) {
            System.out.println(Thread.currentThread().getName() + ": " + "没有买到货");
            try {
                this.wait();
            } catch (InterruptedException e) {
            }
        }
        --product;
        System.out.println(Thread.currentThread().getName() + ":买到了货物，剩下 " + product);
        this.notifyAll();
    }
}

// 生产者
class Producter implements Runnable {
    private Clerk clerk;

    public Producter(Clerk clerk) {
        this.clerk = clerk;
    }

    // 进货
    @Override
    public void run() {
        for(int i = 0; i < 20; ++i) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
            }
            clerk.add();
        }
    }
}

// 消费者
class Customer implements Runnable {
    private Clerk clerk;
    public Customer(Clerk clerk) {
        this.clerk = clerk;
    }

    // 买货
    @Override
    public void run() {
        for(int i = 0; i < 20; ++i) {
            clerk.sale();
        }
    }
}


