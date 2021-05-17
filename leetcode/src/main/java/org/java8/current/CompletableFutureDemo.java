package org.java8.current;

import java.util.concurrent.CompletableFuture;

/**
 * Description
 * <p>
 * </p>
 * DATE 2021/5/17.
 *
 * @author xiyou.
 */
public class CompletableFutureDemo {
    public static void main(String[] args) throws Exception {
        CompletableFuture
                .supplyAsync(() -> {
                    String dangao = "cake";
                    System.out.println("师傅准备做蛋糕:" + dangao);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("师傅已经做好了蛋糕" + dangao);
                    return dangao;
                })
                .thenAccept(cake -> {
                    System.out.println("我吃蛋糕:" + cake);
                });
        System.out.println("我先去喝牛奶");
        Thread.currentThread().join();
    }
}
