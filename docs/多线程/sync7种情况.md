### join的用法

#### join可以让主线程等待Thread线程完成之后再执行

- 下面的代码结果肯定是：先打印end，然后再打印thread....run

```java
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

    public static void main(String[] args) throws Exception {
        test1();
    }
```

- 如何让end在线程t1执行完之后再执行？

```java

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
        t1.join();
        System.out.println("end");
    }

    public static void main(String[] args) throws Exception {
        test12();
    }
```

#### 如何让线程顺序执行？

```java
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
//结果如下：
//thread1....run
//thread2....run
//end
```

#### 如何让2个线程并行执行？

```java
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

    public static void main(String[] args) throws Exception {
        test14();
    }
```

#### 如何让多个线程有序执行？

```java
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
```

### sysc的7种用法

#### 线程不安全的自增

```java
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
```

