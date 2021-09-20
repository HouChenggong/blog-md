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
public class CompletableFutureDemo2 {
    public static void main(String[] args) throws Exception {
        CompletableFuture<String> makeCake = CompletableFuture
                .supplyAsync(() -> {
                    String dangao = "cake";
                    System.out.println("师傅准备做蛋糕" + dangao);
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("师傅做好了蛋糕" + dangao);
                    return dangao;
                });
        CompletableFuture
                .supplyAsync(() -> {
                    String niuNai = "milk";
                    System.out.println("我自己开始做牛奶" + niuNai);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("我把牛奶煮好了" + niuNai);
                    return niuNai;
                })
                .thenAcceptBothAsync(makeCake, (milk, cake) -> {
                    System.out.println(milk + "做好了");
                    System.out.println(cake + "做好了");
                    System.out.println("我开始吃早餐");
                })
                .thenRunAsync(() -> {
                    System.out.println("我已经把早餐吃完了，去上班了");
                });
        Thread.currentThread().join();
    }
}
