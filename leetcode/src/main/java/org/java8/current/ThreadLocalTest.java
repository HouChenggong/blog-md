package org.java8.current;

import org.springframework.scheduling.annotation.Async;

/**
 * Description
 * <p>
 * </p>
 * DATE 2021/5/29.
 *
 * @author .
 */

public class ThreadLocalTest {
    static ThreadLocal<String> threadLocal = new ThreadLocal<>();

    public static void main(String[] args) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ThreadLocalTest.threadLocal.set("猿天地");
                new Service().call();

            }
        }).start();

    }
}


class Service {
    public void call() {
        System.out.println("Service:" + Thread.currentThread().getName());
        System.out.println("Service:" + ThreadLocalTest.threadLocal.get());
        new Dao().call();
        new Service2().call();
    }
}

class Dao {
    @Async
    public void call() {
        System.out.println("==========================");
        System.out.println("Dao:" + Thread.currentThread().getName());
        System.out.println("Dao:" + ThreadLocalTest.threadLocal.get());
        try {
            Thread.sleep(3300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class Service2 {
    public void call() {
        System.out.println("==========================");
        System.out.println("Service2:" + Thread.currentThread().getName());
        System.out.println("Service2:" + ThreadLocalTest.threadLocal.get());
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Service2新线程:" + Thread.currentThread().getName());
                System.out.println("Service2新线程:" + ThreadLocalTest.threadLocal.get());
            }
        }).start();
    }
}

