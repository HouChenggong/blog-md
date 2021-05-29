package org.java8.current;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Description
 * <p>
 * </p>
 * DATE 2021/5/29.
 *
 * @author .
 */
public class ThreadLocalTest2 {
    public static void main(String[] args) {
        ThreadLocal<String> threadLocal = new InheritableThreadLocal<>();
        threadLocal.set("1");
        Runnable  runnable= ()->{
            System.out.println(Thread.currentThread().getName()+"...."+threadLocal.get());
        };

        ThreadPoolExecutor executor = new java.util.concurrent.ThreadPoolExecutor(
                2,
                3,
                20,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(10),
                new ThreadPoolExecutor.CallerRunsPolicy());

        executor.execute(runnable);
        threadLocal.set("2");
        executor.execute(runnable);
        threadLocal.set("3");
        executor.execute(runnable);
    }



}
