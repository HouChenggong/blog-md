### 题外话：永久代——元空间

元空间与永久代之间最大的区别在于：元空间并不在虚拟机中，而是使用本地内存。因此，默认情况下，元空间的大小仅受本地内存限制，但可以通过参数来指定元空间的大小

符号引用没有存在元空间中，而是存在native heap中，这是两个方式和位置，不过都可以算作是本地内存，在虚拟机之外进行划分，没有设置限制参数时只受物理内存大小限制，即只有占满了操作系统可用内存后才OOM。

 

- 为啥要变成元空间？
  - 字符串1.7在永久代中，会出现内存溢出和性能问题
  - 元空间不在虚拟机栈中，而是直接使用本地内存
  - 类及方法的信息等比较难确定其大小，因此对于永久代的大小指定比较困难，太小容易出现永久代溢出，太大则容易导致老年代溢出。
  - 永久代会为 GC 带来不必要的复杂度，并且回收效率偏低。

### 常量池到了哪里？

Java6和6之前，常量池是存放在方法区（永久代）中的。

Java7，将常量池是存放到了堆中。

Java8之后，取消了整个永久代区域，取而代之的是元空间。运行时常量池和静态常量池存放在元空间中，而字符串常量池依然存放在堆中。

![](.\img\堆的分代.png)

## minor GC

第一阶段：Eden到From区

1. new出的对象放到Eden（伊甸园区）
2. 一个堆默认600M，年轻代200M(Eden占80%160M，其它都是20M)，老年代400M
3. 如果Eden放满了，就要开始垃圾收集GC
4. 利用可达性算法GCroot根，找到垃圾对象，利用复制算法，把不是垃圾的对象复制到From区，垃圾的对象直接删除
5. 如果对象从Eden被复制到了From区，对象头的分代年龄+1

第二阶段：

1. 回收Eden 和From 区，Eden和From区的存活对象直接复制移动到To区，也就是说可以直接从Eden到To区，对象年龄+1,当前From为空
2. 如果Eden区又满了，这次就会把eden和to区的存活对象放到上次空的From区，对象年龄+1
3. 如此往复
4. 年龄大于15，直接放到老年代

### 老年代都有哪些数据？

##### 1. 单例、静态对象、线程池对象、Session、Spring的Bean 



#####  2.大对象直接进入老年代



```java
比如需要大量连续内存空间的对象，字符串和数组

java参数  -XX：PretenureSizeThreshold可以设置大对象大小，如果超过设置大小直接进入老年代，不会进入年轻代，但是只在Serial和ParNew两个垃圾收集器中有效

比如：`-XX:PretenureSizeThreshold=1000000 -XX:+UserSerialGC`

再创建的对象就会直接进入老年代
```

##### 3. 长期存活的对象进入老年代**

而且可以设置年龄值，默认15  `-XX:MaxTenuringThreshold`

##### 4 动态年龄判断

```
如果一批对象的大小超过了Surivor内存大小的50%（-XX：TargetSuriviorRatio可以设定大小）
此时大于等于这批对象里面年龄最大的对象将直接进入老年代
比如Survior里面有一批对象年龄未1，2，..n总和超过50%，此时就会把>=N的直接放入老年代
```

##### 5.MinorGC后存活的对象Surivor区放不下

这种情况，会把存活的对象直接放入老年代，部分可能还会再Surivor区

##### 6. 老年代空间分配担保机制

年轻代每次MinitorGC之前都会计算老年代剩余空间，如果剩余空间小于年轻代里面所有对象之和（包括垃圾对象），就会看看是否设置了-XX:HandlePromotionFailure参数（JKD默认设置），如果有则看看老年代剩余内存大小是否大于之前每一次MinorGC之后进入老年代的平均大小，如果如果小于或者参数没有设置，则触发Full GC,对老年代和年轻代一起进行垃圾回收，如果回收完还是空间不够存储新的对象，就会发生OOM







### 老年代被放满之后？怎么办？

FULL GC

## Full GC 

而且FullGC 主要是Java调优要调节的内容，minorGC如果特别频繁也要调节

调优的目的就是减少FULLGC 或者minorGC的次数



Java VisualVM安装 Visual GC

#### 模拟并发

1. 假如秒杀的时候，每秒产生300个订单，一个订单对象大概1K，放大20倍，每秒大概300K*20=6M数据，但是1秒之后就会变成垃圾数据，如果再加上查询订单，那就是每秒60M数据

2. 假设Eden区600M数据，那么10秒之后Eden就满了，开始monitorGC，此时系统还有订单过来，也就是在回收的时候其实600数据里面至少有60M数据不是垃圾对象，那么这60M对象对应的线程就会阻塞，影响这60M对象对应线程的用户体验


#### Stop the world

无论是Ful GC 还是Minor GC都会stop the world

为啥需要stop the world

因为如果不停止，假设在做GC的时候，一个线程还没有执行完，所以对象都不是垃圾，当执行完了对象就变成了垃圾，所以刚开始GC手机的对象就会不准确



### 秒杀如何调优

1. 年轻代内存调大些，保证第二次monitorGC的时候，前一次没有被清理的对象被标记为垃圾对象

## 排查问题

把OutOfMemoryError输出出来到文件夹中，但是前提是要有这个文件夹

-XX:+HeapDumpOnOutOfMemoryError

-XX:HeapDumpPath=C:\cobot\OutOfMemoryError

输入GC细节

-XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+PrintGCDateStamps

GC日志输出

-Xloggc:E:\cobot\OutOfMemoryError\gc-%t.log

文章介绍：

https://blog.csdn.net/sinat_27933301/article/details/97842549

### 1.频繁FULL GC

- 内存泄漏
- 死循环
- 大对象（大部分是它）
  - 比如一次查询的结果集太大
  - RPC传输大对象，比如一个巨型数组
  - 单个数组对象太大

#### 结果集和传输数据过大

- 数据库查询的时候限制一下大小，或者给SQL查询的时候添加必要条件
- 特别是mybatis的条件拼接

```java
select * from user where 1=1 
<if test=" orderID != null ">and order_id = #{orderID}</if >
```

上面如果orderID为空，会导致查询全部

#### 内存泄漏

其实就是在每天程序跑的时候，定时或者在内存比较大的时候dump进行分析

```java
						可选。 				 	文件名 					 pid
jmap -dump:live,format=b,file=myjmapfile.txt 19570
```

## 参加参数

### **9.-Xmx -Xms**

这个就表示设置堆内存的最大值和最小值。这个设置了最大值和最小值后，jvm启动后，并不会直接让堆内存就扩大到指定的最大数值。而是会先开辟指定的最小堆内存，如果经过数次GC后，还不能，满足程序的运行，才会逐渐的扩容堆的大小，但也不是直接扩大到最大内存。

- Xmn**

设置新生代的内存大小。

- XX:NewRatio**

新生代和老年代的比例。比如：1：4，就是新生代占五分之一。

- XX:SurvivorRatio**

设置两个Survivor区和eden区的比例。比如：2：8 ，就是一个Survivor区占十分之一。

- XX:+HeapDumpOnOutMemoryError**

发生OOM时，导出堆的信息到文件。

- XX:+HeapDumpPath**

表示，导出堆信息的文件路径。

- XX:OnOutOfMemoryError**

当系统产生OOM时，执行一个指定的脚本，这个脚本可以是任意功能的。比如生成当前线程的dump文件，或者是发送邮件和重启系统。

- XX:PermSize -XX:MaxPermSize**

设置永久区的内存大小和最大值。永久区内存用光也会导致OOM的发生。

- Xss**

设置栈的大小。栈都是每个线程独有一个，所有一般都是几百k的大小。

## MAC 相关参数

```java
/usr/libexec/java_home -V
  //下面是我的安装位置
/Library/Java/JavaVirtualMachines/jdk1.8.0_231.jdk/Contents/Home
```

- 线程dump命令jstack

```java
三个实例演示 Java Thread Dump 日志分析
jstack Dump 日志文件中的线程状态
dump 文件里，值得关注的线程状态有：
死锁，Deadlock（重点关注） 
执行中，Runnable   
等待资源，Waiting on condition（重点关注） 
等待获取监视器，Waiting on monitor entry（重点关注）
暂停，Suspended
对象等待中，Object.wait() 或 TIMED_WAITING
阻塞，Blocked（重点关注）  
停止，Parked
```



- 堆du mp就是map

```
#用法：
jmap ‐dump:format=b,file=dumpFileName <pid>

#示例
jmap ‐dump:format=b,file=/tmp/dump.dat 11927
```

- metaspace表示元空间大小





### 我平常测试的参数

```java
-XX:+HeapDumpOnOutOfMemoryError
-XX:HeapDumpPath=/Users/xiyouyan/cobot/OutOfMemoryError
-XX:+PrintGCDetails
-XX:+PrintGCTimeStamps
-XX:+PrintGCDateStamps
-Xloggc:/Users/xiyouyan/cobot/OutOfMemoryError/gc-%t.log
-Xmx2000M
-Xms1000M
```

  



