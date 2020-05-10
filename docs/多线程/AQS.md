## JUC 

```java
package java.util.concurrent;包下面的
```

### Exchanger

package java.util.concurrent;包下面的Exchanger

Exchanger可以用于遗传算法，遗传算法里需要选出两个人作为交配对象，这时候会交换 

两人的数据，并使用交叉规则得出2个交配结果。Exchanger也可以用于校对工作，比如我们需 

要将纸制银行流水通过人工的方式录入成电子银行流水，为了避免错误，采用AB岗两人进行 

录入，录入到Excel之后，系统需要加载这两个Excel，并对两个Excel数据进行校对，看看是否 

录入一致，代码如代码清单8-8所示。 

exchanger你可以把它想象成一个容器， 这个容器有两个值，两个线程，有两个格的位置，第一个线程执行到exchanger exchange的时候，阻塞， 但是要注意我这个exchange方法的时候是往里面扔了一个值，你可以认为吧T1扔到第一个格子了， 然后第二个线程开始执行，也执行到这句话了，exchange,他把自己的这个值T2扔到第二个格子里。接下来这两个哥们儿交换一下，T1扔给T2，T2扔给T1， 两个线程继续往前跑。



exchange只能是两个线程之间，交换这个东西只能两两进行。

```java
import java.util.concurrent.Exchanger;

/**
 * @author xiyou
 * @version 1.0
 * xiyou-todo Exchanger
 * https://mp.weixin.qq.com/s/aYfUlwxxW9jm2tStcuIN7g
 * 只能两个线程互相交换
 * @date 2020/5/10 20:48
 */
public class T12_TestExchanger {
    static Exchanger<String> exchanger = new Exchanger<>();

    public static void main(String[] args) {
        new Thread(() -> {
            String s = "T1";
            try {
                s = exchanger.exchange(s);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + " " + s);

        }, "t1").start();


        new Thread(() -> {
            String s = "T2";
            try {
                s = exchanger.exchange(s);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + " " + s);

        }, "t2").start();


    }
}
```

# 

## AQS

```java
package java.util.concurrent.locks;包下面的AQS：AbstractQuenedSynchronizer
```

AQS：AbstractQuenedSynchronizer抽象的队列式同步器。是除了java自带的synchronized关键字之外的锁机制。
AQS的全称为（AbstractQueuedSynchronizer），这个类在java.util.concurrent.locks包

**AQS的核心思想** ：

用volatile修饰一个共享变量，线程通过CAS取改变，成功获取锁，失败进入等待队列，等待被唤醒，所以AQS是自旋锁

J.U.C是基于AQS实现的，AQS是一个同步器，设计模式是模板模式。
核心数据结构：双向链表 + state(锁状态)

底层是CAS

**实现了AQS的锁有：自旋锁、互斥锁、读锁写锁、条件产量、信号量、栅栏都是AQS的衍生物**



AQS 定义了两种资源共享方式：
1.**Exclusive**：独占，只有一个线程能执行，如ReentrantLock
2.**Share**：共享，多个线程可以同时执行，如Semaphore、CountDownLatch、ReadWriteLock，CyclicBarrier

**AQS底层使用了模板方法模式**

### Semaphore信号量

信号量是一种固定资源的限制的一种并发工具包，基于AQS实现的，在构造的时候会设置一个值，代表着资源数量。信号量主要是应用于是用于多个共享资源的互斥使用，和用于并发线程数的控制（druid的数据库连接数，就是用这个实现的），信号量也分公平和非公平的情况，基本方式和reentrantLock差不多，在请求资源调用task时，会用自旋的方式减1，如果成功，则获取成功了，如果失败，导致资源数变为了0，就会加入队列里面去等待。调用release的时候会加一，补充资源,并唤醒等待队列。



如果我Semaphore s = new Semaphore(1)写的是1，我取一下，acquire一下他就变成0，当变成0之后别人是acquire不到的，然后继续执行，线程结束之后注意要s.release(), 执行完该执行的就把他release掉，release又把0变回去1， 还原化。



Semaphore的含义就是限流，比如说你在买票，Semaphore写5就是只能有5个人可以同时买票。acquire的意思叫获得这把锁，线程如果想继续往下执行，必须得从Semaphore里面获得一 个许可， 他一共有5个许可用到0了你就得给我等着。

#### 测试Semaphore

```java
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class SemaphoreExample1 {
    // 请求的数量
    private static final int threadCount = 20;

    public static void main(String[] args) throws InterruptedException {
        // 创建一个具有固定线程数量的线程池对象（如果这里线程池的线程数量给太少的话你会发现执行的很慢）
        ExecutorService threadPool = Executors.newFixedThreadPool(30);
        // 一次只能允许执行的线程数量。
        final Semaphore semaphore = new Semaphore(5);

        for (int i = 0; i < threadCount; i++) {
            final int threadnum = i;
            threadPool.execute(() -> {// Lambda 表达式的运用
                try {
                    semaphore.acquire();// 获取一个许可，所以可运行线程数量为20/1=20
                    test(threadnum);
                    semaphore.release();// 释放一个许可
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            });
        }
        threadPool.shutdown();
        System.out.println("finish");
    }

    public static void test(int threadnum) throws InterruptedException {
        Thread.sleep(2000);// 模拟请求的耗时操作
        System.out.println("threadnum:" + threadnum);
        Thread.sleep(2000);// 模拟请求的耗时操作
    }
}
```

- 测试的结果你会发现间隔一定时间后，都是5个一组出现的，这样就验证了我们的推测

#### Semaphore 应用

- acquire（） release（） 可用于对象池，资源池的构建，比如静态全局对象池，数据库连接池；
- 可创建计数为1的S，作为互斥锁（二元信号量）

### CountDownLatch

等待多线程完成的CountDownLatch,所以countDownLatch一定意义上是join的高级版本

1. 其实CountDownLatch实现的功能无非就是等所有的线程执行完毕后执行另一个事情，
	那为什么非要用它呢？
2.  join() 方法也可以实现类似功能，为什么不用呢？
3. 因为：如果只有两三个线程还好，如果数量过多，那得写多少join啊
	而且提前结束任务还得捕获InterruptException异常，繁琐...
4. 线程之间通信  wait/notify ，然后进入synchronized同步代码块，检查计数器不为0 ，然后调用wait（）方法
	直到为0，则用notifyAll唤醒等待的最后线程
5. 为什么不用notify呢？
	因为会有大量synchronized同步块，还可以出现假唤醒

```java
import java.util.concurrent.CountDownLatch;

/**
 * xiyou-todo CountDownLatch的使用
 * 调用await()方法的线程会被挂起，它会等待直到count值为0才继续执行
 * public void await() throws InterruptedException { };
 * 和await()类似，只不过等待一定的时间后count值还没变为0的话就会继续执行
 * public boolean await(long timeout, TimeUnit unit) throws InterruptedException { };
 * 将count值减1
 * public void countDown() { };
 */
public class TestCountDownLatch {
    final CountDownLatch latch = new CountDownLatch(11);
    Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            System.out.println("线程" + Thread.currentThread().getId() + "先执行一次");
            latch.countDown();
            System.out.println("线程" + Thread.currentThread().getId() + "先执行一次结束，剩余" + latch.getCount());

        }
    });

    public void test() {
        thread.start();
        for (int i = 0; i < 10; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {

                    System.out.println("线程" + Thread.currentThread().getId() + "..........start");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("线程" + Thread.currentThread().getId() + ".........end...");
                    latch.countDown();
                }
            }).start();
        }


        try {

            latch.await();
            System.out.println("调用await()方法的线程会被挂起，它会等待直到count值为0才继续执行");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("11个线程已执行完毕");
    }

    public static void main(String[] args) {
        TestCountDownLatch c = new TestCountDownLatch();
        c.test();
    }
}
```



### 同步屏障CyclicBarrier

CyclicBarrier的字面意思是可循环使用（Cyclic）的屏障（Barrier）。它要做的事情是，让一 

组线程到达一个屏障（也可以叫同步点）时被阻塞，直到最后一个线程到达屏障时，屏障才会 

开门，所有被屏障拦截的线程才会继续运行。

```java
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CyclicBarrierExample3 {
    // 请求的数量
    private static final int threadCount = 9;
    // 需要同步的线程数量
    private static final CyclicBarrier cyclicBarrier = new CyclicBarrier(3, () -> {
        System.out.println("------当线程数达到之后，优先执行------");
    });

    public static void main(String[] args) throws InterruptedException {
        // 创建线程池
        ExecutorService threadPool = Executors.newFixedThreadPool(10);

        for (int i = 0; i < threadCount; i++) {
            final int threadNum = i;
            Thread.sleep(1000);
            threadPool.execute(() -> {
                try {
                    test(threadNum);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            });
        }
        threadPool.shutdown();
    }

    public static void test(int threadnum) throws InterruptedException, BrokenBarrierException {
        System.out.println("threadnum:" + threadnum + "is ready");
        cyclicBarrier.await();
        System.out.println("threadnum:" + threadnum + "is finish");
    }

}
```

- 看结果其实就是每次等到了3个，执行一个方法

```sql
threadnum:0is ready
threadnum:1is ready
threadnum:2is ready
------当线程数达到之后，优先执行------
threadnum:2is finish
threadnum:0is finish
threadnum:1is finish
threadnum:3is ready
threadnum:4is ready
threadnum:5is ready
------当线程数达到之后，优先执行------
threadnum:5is finish
threadnum:3is finish
threadnum:4is finish
threadnum:6is ready
threadnum:7is ready
threadnum:8is ready
------当线程数达到之后，优先执行------
threadnum:8is finish
threadnum:7is finish
threadnum:6is finish
```



#### CountDownLatch和CyclicBarrier的区别是什么

CountDownLatch是等待其他线程执行到某一个点的时候，在继续执行逻辑（子线程不会被阻塞，会继续执行），只能被使用一次。最常见的就是join形式，主线程等待子线程执行完任务，在用主线程去获取结果的方式（当然不一定），内部是用计数器相减实现的（没错，又特么是AQS），AQS的state承担了计数器的作用，初始化的时候，使用CAS赋值，主线程调用await（）则被加入共享线程等待队列里面，子线程调用countDown的时候，使用自旋的方式，减1，知道为0，就触发唤醒。

CyclicBarrier回环屏障，主要是等待一组线程到底同一个状态的时候，放闸。CyclicBarrier还可以传递一个Runnable对象，可以到放闸的时候，执行这个任务。CyclicBarrier是可循环的，当调用await的时候如果count变成0了则会重置状态，如何重置呢，CyclicBarrier新增了一个字段parties，用来保存初始值，当count变为0的时候，就重新赋值。还有一个不同点，CyclicBarrier不是基于AQS的，而是基于RentrantLock实现的。存放的等待队列是用了条件变量的方式。

### ReadWriteLock



### ReentrantLock








