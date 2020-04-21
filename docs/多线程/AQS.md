**AQS原理**
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

##### AQS底层使用了模板方法模式