## 2. Synchronied

### 1. 字节码层级

```java
//监视
monitorenter
    //退出
monitorexit
```

### 2. HotSpot层面

同步方法和同步代码块底层都是通过monitor来实现同步的。

两者的区别：同步方式是通过方法中的access_flags中设置ACC_SYNCHRONIZED标志来实现；同步代码块是通过monitorenter和monitorexit来实现

 

 

### 一个对象的组成

![](monitor对象.svg)

普通对象的话，markword8 classPoint4 padding4

数组对象 markword 8 classPoint4  数组长度4 padding 0

#### 2.2.1 JDK8markword实现表

![锁](syn锁.jpg)



**无锁-偏向锁-轻量级锁（自旋锁、自适应自旋锁）-重量级锁**

#### 2.2.2如何查看Java字节码：

1. 找到【**Plugins**】选项，可以首先确认一下是否安装ByteCode Viewer插件，如果没有安装，可以按照下图示意来进行搜索安装或者安装：**jclasslib byteCode viewer**

2. 点击菜单栏【**View**】,弹出下拉选项，在选项中找到【**Show Bytecode**】按钮，单击此按钮，来查看java类字节码。

###  加synchronized之后对象到底哪里变化了？

请运行下面的代码，注意添加Maven

```java
<dependency>
<groupId>org.openjdk.jol</groupId>
<artifactId>jol-core</artifactId>
<version>0.9</version>
</dependency>
```



```java
public class CasOptimistic {
    /**
     * 查看普通数组的构成
     */
    public static void getSimpleArr() {
        int arr[] = new int[]{};
        String arrStr = ClassLayout.parseInstance(arr).toPrintable();
        System.out.println(arrStr);
        System.out.println("----------------------");
    }

    /**
     * 查看普通对象的构成
     */
    public static void getSimple() {
        Object o = new Object();
        String str = ClassLayout.parseInstance(o).toPrintable();
        System.out.println(str);
        System.out.println("----------------------");
    }


    /**
     * 查看普通对象加上锁之后的构成，发现只有头文件变了，说明sync只与对象头文件相关
     */
    public static void getSyncSimple() {
        Object o = new Object();
        synchronized (o){
            String str = ClassLayout.parseInstance(o).toPrintable();
            System.out.println(str);
        }
        System.out.println("----------------------");
    }

    public static void main(String[] args) {
        getSimpleArr();
        getSimple();
        getSyncSimple();
    }
}
```

只要你打印下面程序的结果就会发现了，只有对象头markword变化了

```
0     4        (object header)                           05 00 00 00 (00000101 00000000 00000000 00000000) (5)
4     4        (object header)                           00 00 00 00 (00000000 00000000 00000000 00000000) (0)
      加上锁之后变成了
0     4        (object header)                           05 78 e5 e8 (00000101 01111000 11100101 11101000) (-387614715)
4     4        (object header)                           47 02 00 00 (01000111 00000010 00000000 00000000) (583)
```

### synchronized锁升级过程

所以synchronized 锁优化的过程和markword息息相关

markword中最低三位代表锁状态，第一位代表是否是偏向锁，2、3位代表锁标志位

**无锁**：对象头里面存储当前对象的hashcode，即原来的Markword组成是：001+hashcode

**偏向锁**：其实就是偏向一个用户，适用场景，只有几个线程，其中某个线程会经常访问，他就会往对象头里面添加线程id，就像在门上贴个纸条一样，占用当前线程，只要纸条存在，就可以一直用

它的意思就是说，这个锁会偏向于第一个获得它的线程，在接下来的执行过程中，假如该锁没有被其他线程所获取，没有其他线程来竞争该锁，那么持有偏向锁的线程将永远不需要进行同步操作。

在此线程之后的执行过程中，如果再次进入或者退出同一段同步块代码，并不再需要去进行**加锁**或者**解锁**操作

**轻量级锁**：比如你贴个纸条，一直使用，但是其他人不乐意了，要和你抢，只要发生抢占，synchronized就会升级变成轻量级锁，也就是不同的线程通过CAS方式抢占当前对象的指针，如果抢占成功，则把刚才的线程id改成自己栈中锁记录的指针LR（LockRecord），因为是通过CAS的方式，所以也叫自旋锁

这个时候你可能回想，无论变成什么锁，对象头都会发生改变，那之前对象头里面存储的hashcode会不会丢失啊？

答案：不会，在发生锁的第一刻，他就会把原来的header存储在自己的线程栈中，所以不会丢失



锁撤销升级为轻量级锁之后，那么对象的Markword也会进行相应的的变化。下面先简单描述下锁撤销之后，升级为轻量级锁的过程：

1. 线程在自己的栈桢中创建锁记录 LockRecord。
2. 将锁对象的对象头中的MarkWord复制到线程的刚刚创建的锁记录中。
3. 将锁记录中的Owner指针指向锁对象。
4. 将锁对象的对象头的MarkWord替换为指向锁记录的指针。

**什么时候重量级锁？**

线程非常多，比如有的线程超过10次自旋，-XX:PreBlockSpin，或者自旋次数超过CPU核数的一半，就会升级成重量级锁，当然Java1.6之后加入了**自适应自旋锁**，JVM自己控制自旋次数

而且重量级锁是操作系统实现的

轻量级锁膨胀之后，就升级为重量级锁了。重量级锁是依赖对象内部的monitor锁来实现的，而monitor又依赖操作系统的MutexLock(互斥锁)来实现的，所以重量级锁也被成为**互斥锁**。

为什么说重量级锁开销大呢

主要是，当系统检查到锁是重量级锁之后，会把等待想要获得锁的线程进行**阻塞**，被阻塞的线程不会消耗cup。但是阻塞或者唤醒一个线程时，都需要操作系统来帮忙，这就需要从**用户态**转换到**内核态**，而转换状态是需要消耗很多时间的，有可能比用户执行代码的时间还要长。
这就是说为什么重量级线程开销很大的。



### 锁降级

要求比较严格，而且只有偏向锁回到无锁的过程，其它的没有，而且是要很长时间线程确认死了的情况下才会有

### 锁粗化

```java
    /**
     * 锁粗化 lock coarsening
     *
     * @param str
     * @return
     */
    public String test(String str) {
        int i = 0;
        StringBuffer sb = new StringBuffer();
        while (i < 100) {
            sb.append(str);
            i++;
        }
        return sb.toString();
    }
```

因为stringbuffer的append()是synchronized的，但循环里面如果每次都加锁，就会加锁、释放锁一百次，所以JVM就会将加上锁的访问粗化到这一连串的操作，比如while循环，只要加一次锁即可

- 锁粗化的模型

```java
for(int i=0;i<size;i++){
    synchronized(lock){
    }
}
```

```java
synchronized(lock){
    for(int i=0;i<size;i++){
    }
}
```



###  锁消除

```java
/**
* 锁消除 lock eliminate
*
* @param str1
* @param str2
*/
public void add(String str1, String str2) {
StringBuffer sb = new StringBuffer();
sb.append(str1).append(str2);
}
```

因为stringBuffer里面都是synchronied，所以里面的append就会消除锁

- 锁消除模型

```
public void method(){
    Object o=new Object();
    sychronized (o){
        //sout
    }
}
```

因为object对象O是内部的变量，所以根本不会存在竞争，所以代码直接没有锁，其实就是局部变量锁无效，只有成员变量锁有效

## synchronized最底层实现

还是CASsynchronized最底层实现

还是CAS

### 可重入性的实现

每个对象都会与一个monitor相关联，当某个monitor被拥有之后就会被锁住，当线程执行到monitorenter指令时，就会去尝试获得对应的monitor。步骤如下：

1. 每个monitor维护着一个记录着拥有次数的计数器。未被拥有的monitor的该计数器为0，当一个线程获得monitor（执行monitorenter）后，该计数器自增变为 1 。
   - 当同一个线程再次获得该monitor的时候，计数器再次自增；
   - 当不同线程想要获得该monitor的时候，就会被阻塞。
2. 当同一个线程释放 monitor（执行monitorexit指令）的时候，计数器再自减。当计数器为0的时候。monitor将被释放，其他线程便可以获得monitor。

 