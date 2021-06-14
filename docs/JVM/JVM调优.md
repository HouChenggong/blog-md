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

- 大对象触发的 FullGC ：大对象，是指需要大量连续内存空间的java对象，例如很长的对象列表。此类对象会直接进入老年代，而老年代虽然有很大的剩余空间，但是无法找到足够大的连续空间来分配给当前对象，此种情况就会触发JVM进行Full GC。

```java
比如需要大量连续内存空间的对象，字符串和数组

java参数  -XX：PretenureSizeThreshold可以设置大对象大小，如果超过设置大小直接进入老年代，不会进入年轻代，但是只在Serial和ParNew两个垃圾收集器中有效

比如：`-XX:PretenureSizeThreshold=1000000 -XX:+UserSerialGC`

再创建的对象就会直接进入老年代
```

##### 3. 长期存活的对象进入老年代**

而且可以设置年龄值，默认15  `-XX:MaxTenuringThreshold`

##### 4 动态年龄判断

HotSpot虚拟机并不是永远要求对象的年龄必须达到- XX：MaxTenuringThreshold才能晋升老年代，如果在Survivor空间中相同年龄所有对象大小的总和大于 Survivor空间的一半，年龄大于或等于该年龄的对象就可以直接进入老年代，无须等到-XX

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

### 频繁FULL GC

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

### 常见参数及配置

- **9.-Xmx -Xms**

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

#### MAC 相关参数

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





#### 常测试的参数

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

  

#### 详细参数介绍

```java
有两种类型的 HotSpot JVM，即”server”和”client”，如果不喜欢默认的JVM类型，可以使用-server和-client参数来设置使用服务端或客户端的VM。运行参数可以简单分为三大类，如下：
一、标准参数：
用法: java [-options] class [args...](执行类)或 java [-options] -jar jarfile [args...](执行 jar 文件)
其中选项包括:
-d32 使用 32 位数据模型 (如果可用)
-d64 使用 64 位数据模型 (如果可用)
-server 选择 "server" VM 默认 VM 是 server.
-cp <目录和 zip/jar 文件的类搜索路径>
-classpath <目录和 zip/jar 文件的类搜索路径>用 ; 分隔的目录, JAR 档案和 ZIP 档案列表, 用于搜索类文件。
-D<名称>=<值> 设置系统属性
-verbose:[class|gc|jni] 启用详细输出
-version 输出产品版本并退出
-version:<值> 警告: 此功能已过时, 将在未来发行版中删除。需要指定的版本才能运行
-showversion 输出产品版本并继续
-jre-restrict-search | -no-jre-restrict-search 警告: 此功能已过时, 将在未来发行版中删除。在版本搜索中包括/排除用户专用 JRE
-? -help 输出此帮助消息
-X 输出非标准选项的帮助
-ea[:<packagename>...|:<classname>]
-enableassertions[:<packagename>...|:<classname>] 按指定的粒度启用断言
-da[:<packagename>...|:<classname>]
-disableassertions[:<packagename>...|:<classname>] 禁用具有指定粒度的断言
-esa | -enablesystemassertions 启用系统断言
-dsa | -disablesystemassertions 禁用系统断言
-agentlib:<libname>[=<选项>] 加载本机代理库 <libname>, 例如 -agentlib:hprof另请参阅 -agentlib:jdwp=help 和 -agentlib:hprof=help
-agentpath:<pathname>[=<选项>] 按完整路径名加载本机代理库
-javaagent:<jarpath>[=<选项>] 加载 Java 编程语言代理, 请参阅 java.lang.instrument
-splash:<imagepath> 使用指定的图像显示启动屏幕

二、非标准化参数，以-X开始：
-Xmixed 混合模式执行（默认）
-Xint 仅解释模式执行，强制JVM执行所有的字节码
-Xbootclasspath:<用 ; 分隔的目录和 zip/jar 文件> 设置引导类和资源的搜索路径
-Xbootclasspath/a:<用 ; 分隔的目录和 zip/jar 文件> 附加在引导类路径末尾
-Xbootclasspath/p:<用 ; 分隔的目录和 zip/jar 文件> 置于引导类路径之前
-Xdiag 显示附加诊断消息
-Xnoclassgc 禁用类垃圾收集
-Xincgc 启用增量垃圾收集
-Xloggc:<file> 将 GC 状态记录在文件中（带时间戳）
-Xbatch 禁用后台编译
-Xms<size> 设置初始 Java 堆大小
-Xmx<size> 设置最大 Java 堆大小
-Xss<size> 设置 Java 线程堆栈大小
-Xprof 输出 cpu 分析数据
-Xfuture 启用最严格的检查，预计会成为将来的默认值
-Xrs 减少 Java/VM 对操作系统信号的使用（请参阅文档）
-Xcheck:jni 对 JNI 函数执行其他检查
-Xshare:off 不尝试使用共享类数据
-Xshare:auto 在可能的情况下使用共享类数据（默认）
-Xshare:on 要求使用共享类数据，否则将失败。
-XshowSettings 显示所有设置并继续
-XshowSettings:system （仅限 Linux）显示系统或容器配置并继续
-XshowSettings:all 显示所有设置并继续
-XshowSettings:vm 显示所有与 vm 相关的设置并继续
-XshowSettings:properties 显示所有属性设置并继续
-XshowSettings:locale 显示所有与区域设置相关的设置并继续

补充：
-Xcomp JVM在第一次使用时会把所有的字节码编译成本地代码，从而带来最大程度的优化，与-Xint正好相反


二、非标准化参数，以-XX开始：
用一句话来说明XX参数的语法。所有的XX参数都以”-XX:”开始，但是随后的语法不同，取决于参数的类型。
对于布尔类型的参数，我们有”+”或”-“，然后才设置JVM选项的实际名称。例如，-XX:+<name>用于激活<name>选项，而-XX:-<name>用于注销选项。
对于需要非布尔值的参数，如string或者integer，我们先写参数的名称，后面加上”=”，最后赋值。例如， -XX:<name>=<value>给<name>赋值<value>。

-XX:+PrintCompilation 输出一些关于从字节码转化成本地代码的编译过程
-XX:+CITime 在JVM关闭时得到各种编译的统计信息
-XX:+UnlockExperimentalVMOptions 如果参数输入是正确的，并且JVM并不识别，可以设置该参数来解锁参数，例如：-XX:+LogCompilation
-XX:+LogCompilation 可以看到比-XX:+PrintCompilation 输出的足够的信息和编译器线程启动的任务
-XX:+PrintOptoAssembly 和-XX:+LogCompilation差不多，不同的是使用这个参数要求运行的服务端VM是debug版本

JVM内存管理参数：
-Xms and -Xmx (or: -XX:InitialHeapSize and -XX:MaxHeapSize) 指定JVM的初始和最大堆内存大小。例如：java -Xms128m -Xmx2g MyApp 或 java -XX:InitialHeapSize=128m -XX:MaxHeapSize=2g MyApp
-XX:+HeapDumpOnOutOfMemoryError 让JVM在发生内存溢出时自动的生成堆内存快照
-XX:HeapDumpPath=<path> 改变默认的堆内存快照生成路径
-XX:OnOutOfMemoryError 可以执行一些指令，比如发个E-mail通知管理员或者执行一些清理工作，例如-XX:OnOutOfMemoryError ="sh ~/cleanup.sh"
-XX:PermSize 永久代初始大小（jdk7）
-XX:MaxPermSize 永久代大小的最大值（jdk7）
-XX:MaxHeapSize 堆内存大小的最大值
-XX:InitialCodeCacheSize and -XX:ReservedCodeCacheSize 用来存储已编译方法生成的本地代码
-XX:+UseCodeCacheFlushing 当代码缓存被填满时让JVM放弃一些编译代码

新生代垃圾回收：
-XX:NewSize and -XX:MaxNewSize 指定新生代大小
-XX:NewRatio 作用于新生代内部区域，设置老年代与新生代的比例。例如 -XX:NewRatio=3 指定老年代/新生代为3/1. 老年代占堆大小的 3/4 ，新生代占 1/4。
-XX:SurvivorRatio 作用于新生代内部区域，指定伊甸园区(Eden)与幸存区大小比例. 例如, -XX:SurvivorRatio=10 表示伊甸园区(Eden)是 幸存区To 大小的10倍(也是幸存区From的10倍).所以,伊甸园区(Eden)占新生代大小的10/12, 幸存区From和幸存区To 每个占新生代的1/12 。注意,两个幸存区永远是一样大的。
-XX:+PrintTenuringDistribution 指定JVM 在每次新生代GC时，输出幸存区中对象的年龄分布
老年代阀值(tenuring threshold)意思是对象从新生代移动到老年代之前，经过几次GC(即, 对象晋升前的最大年龄)，一般最大值为15。
-XX:InitialTenuringThreshold 老年代阀值的初始值
-XX:MaxTenuringThreshold 老年代阀值的最大值
-XX:TargetSurvivorRatio 设定幸存区的目标使用率,例如 , -XX:MaxTenuringThreshold=10 -XX:TargetSurvivorRatio=90 设定老年代阀值的上限为10,幸存区空间目标使用率为90%。
-XX:+NeverTenure 对象永远不会晋升到老年代.当我们确定不需要老年代时，可以这样设置。这样设置风险很大,并且会浪费至少一半的堆内存。
-XX:+AlwaysTenure 表示没有幸存区,所有对象在第一次GC时，会晋升到老年代。

吞吐量收集器：
-XX:+UseSerialGC 激活串行垃圾收集器
-XX:+UseParallelGC 告诉JVM使用多线程并行执行年轻代垃圾收集
-XX:+UseParallelOldGC 除了激活年轻代并行垃圾收集，也激活了年老代并行垃圾收集
-XX:ParallelGCThreads=<value> 可以指定并行垃圾收集的线程数量。例如，-XX:ParallelGCThreads=6表示每次并行垃圾收集将有6个线程执行。
-XX:-UseAdaptiveSizePolicy 停用一些人体工程学，即将其值设为false。人体工程学，在这里即是垃圾收集器能将堆大小动态变动像GC设置一样应用到不同的堆区域，只要有证据表明这些变动将能提高GC性能。“提高GC性能”的确切含义可以由用户通过-XX:GCTimeRatio和-XX:MaxGCPauseMillis(见下文)标记来指定。
-XX:GCTimeRatio=N 指定目标应用程序线程的执行时间(与总的程序执行时间)达到N/(N+1)的目标比值。
-XX:GCTimeRatio=<value> 告诉JVM最大暂停时间的目标值(以毫秒为单位)。

CMS收集器：
-XX：+UseConcMarkSweepGC 激活CMS收集器
-XX：UseParNewGC 当使用CMS收集器时，该标志激活年轻代使用多线程并行执行垃圾回收。
-XX：+CMSConcurrentMTEnabled 当该标志被启用时，并发的CMS阶段将以多线程执行
-XX：ConcGCThreads=<value> 定义并发CMS过程运行时的线程数。比如value=4意味着CMS周期的所有阶段都以4个线程来执行。
-XX:CMSInitiatingOccupancyFraction=<value> 该值代表老年代堆空间的使用率。比如，value=75意味着第一次CMS垃圾收集会在老年代被占用75%时被触发。通常CMSInitiatingOccupancyFraction的默认值为68(之前很长时间的经历来决定的)。
-XX+UseCMSInitiatingOccupancyOnly 命令JVM不基于运行时收集的数据来启动CMS垃圾收集周期。
-XX:+CMSClassUnloadingEnabled 对永久代进行垃圾回收
-XX:+CMSIncrementalMode 开启CMS收集器的增量模式。增量模式经常暂停CMS过程，以便对应用程序线程作出完全的让步。
XX:+ExplicitGCInvokesConcurrent 命令JVM无论什么时候调用系统GC，都执行CMS GC，而不是Full GC。
-XX:+ExplicitGCInvokesConcurrentAndUnloadsClasses 保证当有系统GC调用时，永久代也被包括进CMS垃圾回收的范围内。
- XX:+ DisableExplicitGC 告诉JVM完全忽略系统的GC调用(不管使用的收集器是什么类型)。

GC日志：
-XX:+PrintGC（或者-verbose:gc）开启简单GC日志模式，为每一次新生代（young generation）的GC和每一次的Full GC打印一行信息。
-XX:PrintGCDetails 开启详细GC日志模式。
-XX:+PrintGCTimeStamps 可以将时间和日期也加到GC日志中。表示自JVM启动至今的时间戳会被添加到每一行中。
-XX:+PrintGCDateStamps 每一行添加上绝对的日期和时间。

官方文档：https://docs.oracle.com/javase/8/docs/technotes/tools/unix/java.html


综上：整理常见配置
堆设置
-Xms:初始堆大小
-Xmx:最大堆大小
-XX:NewSize=n:设置年轻代大小
-XX:NewRatio=n:设置年轻代和年老代的比值。如:为3，表示年轻代与年老代比值为1：3，年轻代占整个年轻代年老代和的1/4
-XX:SurvivorRatio=n:年轻代中Eden区与两个Survivor区的比值。注意Survivor区有两个。如：3，表示Eden：Survivor=3：2，一个Survivor区占整个年轻代的1/5
-XX:MaxPermSize=n:设置持久代大小
收集器设置
-XX:+UseSerialGC:设置串行收集器
-XX:+UseParallelGC:设置并行收集器
-XX:+UseParalledlOldGC:设置并行年老代收集器
-XX:+UseConcMarkSweepGC:设置并发收集器
垃圾回收统计信息
-XX:+PrintGC
-XX:+PrintGCDetails
-XX:+PrintGCTimeStamps
-Xloggc:filename
并行收集器设置
-XX:ParallelGCThreads=n:设置并行收集器收集时使用的CPU数。并行收集线程数。
-XX:MaxGCPauseMillis=n:设置并行收集最大暂停时间
-XX:GCTimeRatio=n:设置垃圾回收时间占程序运行时间的百分比。公式为1/(1+n)
并发收集器设置
-XX:+CMSIncrementalMode:设置为增量模式。适用于单CPU情况。
-XX:ParallelGCThreads=n:设置并发收集器年轻代收集方式为并行收集时，使用的CPU数。并行收集线程数。




```

#### 线上环境配置

```java
真实环境配置及说明如下：

-Dsun.misc.URLClassPath.disableJarChecking=true 开启类路径检查
-Xms4762m 指定JVM的初始堆内存大小
-Xmx4762m 指定JVM的最大堆内存大小
-XX:NewRatio=2 设置年轻代和年老代的比值
-XX:G1HeapRegionSize=8m 开启G1后设置的分区大小，建议逐渐增大该值，1 2 4 8 16 32
-XX:MetaspaceSize=256m 设置元空间大小
-XX:MaxMetaspaceSize=256m 设置元空间最大大小
-XX:MaxTenuringThreshold=10 设置老年代阀值的最大值为10
-XX:+UseG1GC 启动G1垃圾回收
-XX:InitiatingHeapOccupancyPercent=45 启动G1的堆空间占用比例
-XX:MaxGCPauseMillis=200 设置GC时最大停顿时间为200毫秒，建议值，G1会尝试调整Young区的块数来达到这个值
-verbose:gc 开启简单GC日志模式
-XX:+PrintGCDetails 开启详细GC日志模式
-XX:+PrintGCTimeStamps 可以将时间和日期也加到GC日志中
-XX:+PrintReferenceGC 记录回收了多少种不同引用类型的引用
-XX:+PrintAdaptiveSizePolicy
-XX:+UseGCLogFileRotation 开启日志记录，与上面方式略有不同
-XX:NumberOfGCLogFiles=6 设置GC日志文件数
-XX:GCLogFileSize=32m 设置GC日志文件大小为32M
-Xloggc:./var/run/gc.log.202101190037 将 GC 状态记录在文件中（带时间戳）
-XX:+HeapDumpOnOutOfMemoryError JVM在发生内存溢出时自动的生成堆内存快照
-XX:HeapDumpPath=./var/run/jvm_oom_error.hprof 改变默认的堆内存快照生成路径
-Dfile.encoding=UTF-8 设置系统文件的编码为UTF-8
-Dcom.sun.management.jmxremote 开启jmx远程管理
-Dcom.sun.management.jmxremote.port=9999 设置jmx远程管理端口
-Dcom.sun.management.jmxremote.ssl=false 关闭jmx远程管理ssl
-Dcom.sun.management.jmxremote.authenticate=false 关闭jmx远程管理鉴权
-XX:MaxNewSize=1660944384 设置新生代大小最大值
-XX:ConcGCThreads=1 开启了G1时设置的GC线程数
-XX:+UseCompressedClassPointers 开启类指针压缩，依赖于-XX:+UseCompressedOops，其两个开启的作用是提高内存的利用率
-XX:+UseCompressedOops 开启普通对象指针压缩，oops: ordinary object pointer
-XX:+PrintAdaptiveSizePolicy 打印自适应收集的大小，默认是关闭的
-XX:MinHeapDeltaBytes=8388608 表示当我们要扩容或者缩容的时候， 决定是否要做或者尝试扩容的时候最小扩多少，默认为192K
-XX:+ManagementServer （暂时未知）
-XX:MarkStackSize=4194304 （暂时未知）
-XX:CICompilerCount=3 最大并行编译数
-XX:CompressedClassSpaceSize=260046848 与类指针压缩有关联，它是该指针指向的空间，默认大小是1G，故可以通过该参数来进行调整。当程序引用了太多的包，有可能会造成这个空间不够用，于是会看到java.lang.OutOfMemoryError: Compressed class space，通过调大该参数进行调优。
```

