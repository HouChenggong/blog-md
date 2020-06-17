package org.xiyou.leetcode.future;

import java.util.concurrent.*;

import static org.xiyou.leetcode.future.FutureDemo2.L;


public class FutureDemo2 {
   public static   long L=1000l;

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        ExecutorService service = Executors.newCachedThreadPool();
        Future<String> future = service.submit(new RealData());

        System.out.println("RealData方法调用完毕");
        // 模拟主函数中其他耗时操作
        doOtherThing(future);
        // 获取RealData方法的结果
        System.out.println(future.get());
    }

    private static void doOtherThing(Future<String> future) throws InterruptedException {
        future.cancel(true);
        System.out.println(future.isCancelled());
        System.out.println(future.isDone());
        Thread.sleep(5000L);
    }
}

class RealData implements Callable<String> {

    public String costTime() {
        try {
            // 模拟RealData耗时操作
            System.out.println("realData start ....");
            Thread.sleep(4000L);
            System.out.println("realData end ....");
            return "result";
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "exception";
    }

    @Override
    public String call() throws Exception {
        return costTime();
    }
}
