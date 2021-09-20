## 1. CAS原理

CAS（Compare And Swap 比较并且替换）是乐观锁的一种实现方式，是一种轻量级锁，JUC 中很多工具类的实现就是基于 CAS 的，也可以理解为自旋锁

JUC是指`import java.util.concurrent`下面的包，

比如：`import java.util.concurrent.atomic.AtomicInteger;`

最终实现是汇编指令：`lock cmpxchg` 不仅仅是CAS的底层实现而且它是`volatile 、synchronized` 的底层实现

```java
lock的意思就是在执行的时候不允许被打断
假如2个CPU同时读一块内存，修改并且放回去，就会出现不一致的情况
lock就是当一个CPU访问这块内存的时候会加锁，不允许其它CPU访问
（锁的不是总线，锁的是一个北桥的信号）
cmpechg 就是比较交换
```

### 1.1CAS如何实现线程安全？

线程在读取数据时不进行加锁，在准备写回数据时，先去查询原值，操作的时候比较原值是否修改，若未被其他线程修改则写回，若已被修改，则重新执行读取流程

举个栗子：现在一个线程要修改数据库的name，修改前我会先去数据库查name的值，发现name=“**A**”，拿到值了，我们准备修改成name=“**b**”，在修改之前我们判断一下，原来的name是不是等于“**A**”，如果被其他线程修改就会发现name不等于“**A**”，我们就不进行操作，如果原来的值还是A，我们就把name修改为“**b**”，至此，一个流程就结束了。

### 1.2 CAS会带来什么问题？

1. 如果一致循环,CPU开销过大

是因为CAS操作长时间不成功的话，会导致一直自旋，相当于死循环了，CPU的压力会很大。

1. ABA问题

#### 1.2.1 什么是ABA问题？

1. 线程1读取了数据A
2. 线程2读取了数据A
3. 线程2通过CAS比较，发现值是A没错，可以把数据A改成数据B
4. 线程3读取了数据B
5. 线程3通过CAS比较，发现数据是B没错，可以把数据B改成了数据A
6. 线程1通过CAS比较，发现数据还是A没变，就写成了自己要改的值FF

在这个过程中任何线程都没做错什么，但是值被改变了，线程1却没有办法发现，其实这样的情况出现对结果本身是没有什么影响的，但是我们还是要防范，怎么防范我下面会提到。

#### PS-CAS会死循环吗？

可能会，但是几率非常小，因为要想死循环就是一个线程总是修改不了，要多少个线程可能才会出现这个情况

### 1.3 AtomicInteger原理

AtomicInteger的自增函数incrementAndGet（）

因为自增不会再改回来了，ABA的问题原因是因为有人改回来了

其实是调用Unsafe类的getAndAddInt

```java
    private static final Unsafe unsafe = Unsafe.getUnsafe();
	// 通过Unsafe计算出value变量在对象中的偏移，该偏移值下边会用到
    private static final long valueOffset;

    static {
        try {
            // 通过Unsafe计算出value变量在对象中的偏移，该偏移值下边会用到
            valueOffset = unsafe.objectFieldOffset
                (AtomicInteger.class.getDeclaredField("value"));
        } catch (Exception ex) { throw new Error(ex); }
    }

    private volatile int value;
```



```java
    public final int incrementAndGet() {
        return U.getAndAddInt(this, VALUE, 1) + 1;
    }


    public final int getAndAddInt(Object var1, long var2, int var4) {
        int var5;
        do {
            // 获得对象var1，内存中偏移量为var2的成员变量值。
            var5 = this.getIntVolatile(var1, var2);
        } while(!this.compareAndSwapInt(var1, var2, var5, var5 + var4));

        return var5;
    }
```

- var1: Object
- var2: valueOffset
- var5: 当前该变量在内存中的值
- var4需要新增的量
- var5 + var4: 需要写进去的值

大概意思就是循环判断给定偏移量的值是否等于内存中的偏移量的值，直到成功才退出，看到do while的循环没。

### AtomicInteger ABA的问题

```java
public class L {

    private static AtomicInteger atomicInt = new AtomicInteger(100);

    public static void main(String[] args) {


        Thread intT1 = new Thread(new Runnable() {
            @Override
            public void run() {
                atomicInt.compareAndSet(100, 99);
                System.out.println(atomicInt.get() + "---100变成99");
                atomicInt.compareAndSet(99, 100);
                System.out.println(atomicInt.get() + "---99变成100");
            }
        });

        Thread intT2 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    intT1.join();
                    TimeUnit.SECONDS.sleep(1);
                    System.out.println(atomicInt.get() + "---接下来如果是100，就更新成功101");
                } catch (InterruptedException e) {
                }
                boolean c3 = atomicInt.compareAndSet(100, 101);
                System.out.println(c3); // true
            }
        });

        intT1.start();
        intT2.start();
    }
}
```

发现AtomicInteger也产生了ABA的问题

```java
99---100变成99
100---99变成100
100---接下来如果是100，就更新成功101
true
```

怎么解决？

增加版本号的方式

```java
public class TestAtomicAba {
    private static AtomicStampedReference atomicStampedRef = new AtomicStampedReference(100, 0);


    public static void main(String[] args) {


        Thread refT1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                }
                atomicStampedRef.compareAndSet(100, 99, atomicStampedRef.getStamp(), atomicStampedRef.getStamp() + 1);
                System.out.println(atomicStampedRef.getReference() + "---100变成99");
                atomicStampedRef.compareAndSet(99, 100, atomicStampedRef.getStamp(), atomicStampedRef.getStamp() + 1);
                System.out.println(atomicStampedRef.getReference() + "---99变成100");
            }
        });

        Thread refT2 = new Thread(new Runnable() {
            @Override
            public void run() {

                int stamp = atomicStampedRef.getStamp();
                try {
                    refT1.join();
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                }
                System.out.println(atomicStampedRef.getStamp());
                System.out.println(atomicStampedRef.getReference() + "---接下来如果是100，就更新成功101");
                boolean c3 = atomicStampedRef.compareAndSet(100, 101, stamp, stamp + 1);
                System.out.println(c3); // false
            }
        });

        refT1.start();
        refT2.start();


    }
}
```

//发现是更新失败了

```java
99---100变成99
100---99变成100
2
100---接下来如果是100，就更新成功101
false
```



### 1.4 实际应用保证CAS产生的ABA安全问题？

**加标志位**，例如搞个自增的字段version，操作一次就自增加一，或者version字段设置为时间戳，比较时间戳的值

举个栗子：现在我们去要求操作数据库，根据CAS的原则我们本来只需要查询原本的值就好了，现在我们一同查出他的标志位版本字段vision。

如果采用乐观锁更新，普通的更新如下，

- 普通CAS更新，但是会出现ABA问题，详情请查看CAS讲解

```sql
update table set value = newValue where  and id =XXX and value = #{oldValue}
//oldValue就是我们执行前查询出来的值
```

- 带版本号的更新方式，但是更新成功率会很低

```sql
update table set value = newValue ，vision = vision + 1 where id =XXX and
value = #{oldValue} and vision = #{vision} 
// 判断原来的值和版本号是否匹配，中间有别的线程修改，值可能相等，但是版本号100%不一样
```

- 针对秒杀的可以进一步优化：
  - 因为秒杀系统，库存都是依次递减的，所以不需要有版本号标识

```sql
update table set value = oldValue -1 where  and id =XXX and value = #{oldValue}
```

###  1.5 LongAdder引入的必要性

底层用while循环，在高并发之下，N多线程进行自旋竞争同一个字段，这无疑会给CPU造成一定的压力，所以在Java8中，提供了更完善的原子操作类：LongAdder。

我们知道，**AtomicLong**中有个内部变量**value**保存着实际的long值，所有的操作都是针对该变量进行。也就是说，高并发环境下，value变量其实是一个热点，也就是N个线程竞争一个热点。

**LongAdder**的基本思路就是***分散热点\***，将value值分散到一个数组中，不同线程会命中到数组的不同槽中，各个线程只对自己槽中的那个值进行CAS操作，这样热点就被分散了，冲突的概率就小很多。如果要获取真正的long值，只要将各个槽中的变量值累加返回。

这种做法有没有似曾相识的感觉？没错，[ConcurrentHashMap](https://segmentfault.com/a/1190000015865714#)中的“分段锁”其实就是类似的思路。



> 如有三个ThreadA、ThreadB、ThreadC，每个线程对value增加10。

对于**AtomicLong**，最终结果的计算始终是下面这个形式：

但是对于**LongAdder**来说，内部有一个`base`变量，一个`Cell[]`数组。
`base`变量：非竞态条件下，直接累加到该变量上
`Cell[]`数组：竞态条件下，累加个各个线程自己的槽`Cell[i]`中



（1）最初无竞争时只更新base；

（2）直到更新base失败时，创建cells数组；

（3）当多个线程竞争同一个Cell比较激烈时，可能要扩容；

但是LongAdder也有问题，就是他的SUM方法是一个近似值，并不是一个准确值，LongAdder可以说不是强一致性的，它是最终一致性的。

推荐阅读

https://segmentfault.com/a/1190000015865714#

https://www.cnblogs.com/tong-yuan/p/LongAdder.html