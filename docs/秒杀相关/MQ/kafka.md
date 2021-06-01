## 零拷贝

操作系统的核心是内核，独立于普通的应用程序，可以访问受保护的内存空间，也有访问底层硬件设备的权限。

为了避免用户进程直接操作内核，保证内核安全，操作系统将虚拟内存划分为两部分，一部分是内核空间（Kernel-space），一部分是用户空间（User-space）。

#### 传统读写

参考：[零拷贝与Kafka](https://mp.weixin.qq.com/s/dbnpPEF0FBB5A5xH21OoeQ)

传统模式下，数据从网络传输到文件需要 4 次数据拷贝、4 次上下文切换和两次系统调用

```java
data = socket.read()// 读取网络数据 
File file = new File() 
file.write(data)// 持久化到磁盘
file.flush()
```

这一过程实际上发生了四次数据拷贝：

1. 首先通过 DMA copy 将网络数据拷贝到内核态 Socket Buffer
2. 然后应用程序将内核态 Buffer 数据读入用户态（CPU copy）
3. 接着用户程序将用户态 Buffer 再拷贝到内核态（CPU copy）
4. 最后通过 DMA copy 将数据拷贝到磁盘文件

### 零拷贝的几种方式

#### 直接I/O

数据直接跨过内核，在用户地址空间与I/O设备之间传递，内核只是进行必要的虚拟存储配置等辅助工作；

#### 避免内核和用户空间之间的数据拷贝

当应用程序不需要对数据进行访问时，则可以避免将数据从内核空间拷贝到用户空间

#### copy on write

写时拷贝技术，数据不需要提前拷贝，而是当需要修改的时候再进行部分拷贝

- mmap
- sendFile
- sockmap
- Splice && tee



### Kafka与mmap

- 网络数据持久化到磁盘 (Producer 到 Broker)

传统模式下，数据从网络传输到文件需要 4 次数据拷贝、4 次上下文切换和两次系统调用。

**Memory Mapped Files**：简称 mmap，也有叫 **MMFile** 的，使用 mmap 的目的是将内核中读缓冲区（read buffer）的地址与用户空间的缓冲区（user buffer）进行映射。从而实现内核缓冲区与应用程序内存的共享，省去了将数据从内核读缓冲区（read buffer）拷贝到用户缓冲区（user buffer）的过程。它的工作原理是直接利用操作系统的 Page 来实现文件到物理内存的直接映射。完成映射之后你对物理内存的操作会被同步到硬盘上。

使用这种方式可以获取很大的 I/O 提升，省去了用户空间到内核空间复制的开销。

mmap 也有一个很明显的缺陷——不可靠，写到 mmap 中的数据并没有被真正的写到硬盘，操作系统会在程序主动调用 flush 的时候才把数据真正的写到硬盘。Kafka 提供了一个参数——`producer.type` 来控制是不是主动flush；如果 Kafka 写入到 mmap 之后就立即 flush 然后再返回 Producer 叫同步(sync)；写入 mmap 之后立即返回 Producer 不调用 flush 就叫异步(async)，默认是 sync。

### Kakfa与sendFile

- 磁盘文件通过网络发送（Broker 到 Consumer）
- 传统方式实现：先读取磁盘、再用 socket 发送，实际也是进过四次 copy

```java
buffer = File.read 
Socket.send(buffer)
```

这一过程可以类比上边的生产消息：

1. 首先通过系统调用将文件数据读入到内核态 Buffer（DMA 拷贝）
2. 然后应用程序将内存态 Buffer 数据读入到用户态 Buffer（CPU 拷贝）
3. 接着用户程序通过 Socket 发送数据时将用户态 Buffer 数据拷贝到内核态 Buffer（CPU 拷贝）
4. 最后通过 DMA 拷贝将数据拷贝到 NIC Buffer



Linux 2.4+ 内核通过 sendfile 系统调用，提供了零拷贝。数据通过 DMA 拷贝到内核态 Buffer 后，直接通过 DMA 拷贝到 NIC Buffer，无需 CPU 拷贝。这也是零拷贝这一说法的来源。除了减少数据拷贝外，因为整个读文件 - 网络发送由一个 sendfile 调用完成，整个过程只有两次上下文切换，因此大大提高了性能。



Kafka 在这里采用的方案是通过 NIO 的 `transferTo/transferFrom` 调用操作系统的 sendfile 实现零拷贝。总共发生 2 次内核数据拷贝、2 次上下文切换和一次系统调用，消除了 CPU 数据拷贝

## Kafka
### Kafka zookeeper

Kafka 使用 ZooKeeper 来保存与分区和代理相关的元数据，并选举出一个代理作为集群控制器。不过，Kafka 开发团队想要消除对 Zookeeper 的依赖，这样就可以以更可伸缩和更健壮的方式来管理元数据，从而支持更多的分区，还能够简化 Kafka 的部署和配置。



所以 Apache Kafka 为什么要移除 Zookeeper 的依赖？Zookeeper 有什么问题？实际上，问题不在于 ZooKeeper 本身，而在于外部元数据管理的概念。

### Canal+Kafka实现增量数据同步

- 优点：实现数据近实时数据同步
- 缺点：
  - 依赖Canal+Kafka系统复杂性和稳定性需要高可用保证
  - 存在MQ顺序性消费问题

### Canal+Kakfa消息顺序性消费问题

参考:[官网](https://github.com/alibaba/canal/wiki/Canal-Kafka-RocketMQ-QuickStart)

binlog本身是有序的，写入到mq之后如何保障顺序是很多人会比较关注，在issue里也有非常多人咨询了类似的问题，这里做一个统一的解答

1. **canal目前选择支持的kafka/rocketmq，本质上都是基于本地文件的方式来支持了分区级的顺序消息的能力，也就是binlog写入mq是可以有一些顺序性保障，这个取决于用户的一些参数选择**
2. **canal支持MQ数据的几种路由方式：单topic单分区，单topic多分区、多topic单分区、多topic多分区**

- canal.mq.dynamicTopic，主要控制是否是单topic还是多topic，针对命中条件的表可以发到表名对应的topic、库名对应的topic、默认topic name
- canal.mq.partitionsNum、canal.mq.partitionHash，主要控制是否多分区以及分区的partition的路由计算，针对命中条件的可以做到按表级做分区、pk级做分区等

3. **canal的消费顺序性，主要取决于描述2中的路由选择，举例说明：**

- 单topic单分区，可以严格保证和binlog一样的顺序性，缺点就是性能比较慢，单分区的性能写入大概在2~3k的TPS
- **多topic单分区，可以保证表级别的顺序性，一张表或者一个库的所有数据都写入到一个topic的单分区中，可以保证有序性，针对热点表也存在写入分区的性能问题**
- 单topic、多topic的多分区，如果用户选择的是指定table的方式，那和第二部分一样，保障的是表级别的顺序性(存在热点表写入分区的性能问题)，如果用户选择的是指定pk hash的方式，那只能保障的是一个pk的多次binlog顺序性 ** pk hash的方式需要业务权衡，这里性能会最好，但如果业务上有pk变更或者对多pk数据有顺序性依赖，就会产生业务处理错乱的情况. 如果有pk变更，pk变更前和变更后的值会落在不同的分区里，业务消费就会有先后顺序的问题，需要注意



## Kakfa为啥那么快？

#### 顺序读写

#### 多分区并发写

- 相比RocketMQ，Kafka一个topic如果有多个分区，就会有多个文件，也就是文件数量和分区数量一致。所以Kafka多文件写肯定比RocketMQ单文件写速度更快

#### PageCache存储

Broker 收到数据后，写磁盘时只是将数据写入 Page Cache，并不保证数据一定完全写入磁盘。从这一点看，可能会造成机器宕机时，Page Cache 内的数据未写入磁盘从而造成数据丢失。但是这种丢失只发生在机器断电等造成操作系统不工作的场景，而这种场景完全可以由 Kafka 层面的 Replication 机制去解决

- producer生产消息时，会使用pwrite()系统调用【对应到Java NIO中是FileChannel.write() API】按偏移量写入数据，并且都会先写入page cache里。
- consumer消费消息时，会使用sendfile()系统调用【对应FileChannel.transferTo() API】，零拷贝地将数据从page cache传输到broker的Socket buffer，再通过网络传输。

- 同时，page cache中的数据会随着内核中flusher线程的调度以及对sync()/fsync()的调用写回到磁盘，就算进程崩溃，也不用担心数据丢失。
- 另外，如果consumer要消费的消息不在page cache里，才会去磁盘读取，并且会顺便预读出一些相邻的块放入page cache，以方便下一次读取。

使用 Page Cache 的好处：

- I/O Scheduler 会将连续的小块写组装成大块的物理写从而提高性能
- I/O Scheduler 会尝试将一些写操作重新按顺序排好，从而减少磁盘头的移动时间
- 充分利用所有空闲内存（非 JVM 内存）。如果使用应用层 Cache（即 JVM 堆内存），会增加 GC 负担
- 读操作可直接在 Page Cache 内进行。如果消费和生产速度相当，甚至不需要通过物理磁盘（直接通过 Page Cache）交换数据
- 如果进程重启，JVM 内的 Cache 会失效，但 Page Cache 仍然可用

kafka是多副本的，当你配置了同步复制之后。多个副本的数据都在page cache里 面，出现多个副本同时挂掉的概率比1个副本挂掉，概率就小很多了

#### 零拷贝技术

- 相比RocketMQ的同步和异步刷盘而言，kafka都是异步刷盘，不过对应的ACK机制有三种
  - 0- 生产者不等待broker的ack，继续发送消息 
  - 1- 等待leader落盘返回
  -  -1-主从全部落盘才返回ack
- 零拷贝技术
  - 生产者到Broker用的是：mmFile，实现顺序的快速写入
  - Broker到消费者用的是：sendFile，将磁盘文件读到 OS 内核缓冲区后，转到 NIO buffer进行网络发送，减少 CPU 消耗

#### 网络IO批处理

在很多情况下，系统的瓶颈不是 CPU 或磁盘，而是网络IO。

因此，除了操作系统提供的低级批处理之外，Kafka 的客户端和 broker 还会在通过网络发送数据之前，在一个批处理中累积多条记录 (包括读和写)。记录的批处理分摊了网络往返的开销，使用了更大的数据包从而提高了带宽利用率。

#### 数据压缩

Producer 可将数据压缩后发送给 broker，从而减少网络传输代价，目前支持的压缩算法有：Snappy、Gzip、LZ4。数据压缩一般都是和批处理配套使用来作为优化手段的。

#### Kafka处理写流程

**对于Produce请求**：Server端的I/O线程统一将请求中的数据写入到操作系统的PageCache后立即返回，当消息条数到达一定阈值后，Kafka应用本身或操作系统内核会触发强制刷盘操作

主要利用了操作系统的ZeroCopy机制，当Kafka Broker接收到读数据请求时，会向操作系统发送sendfile系统调用，操作系统接收后，首先试图从PageCache中获取数据（如中间流程图所示）；如果数据不存在，会触发缺页异常中断将数据从磁盘读入到临时缓冲区，随后通过DMA操作直接将数据拷贝到网卡缓冲区中等待后续的TCP传输。

综上所述，Kafka对于单一读写请求均拥有很好的吞吐和延迟。处理写请求时，数据写入PageCache后立即返回，数据通过异步方式批量刷入磁盘，既保证了多数写请求都能有较低的延迟，同时批量顺序刷盘对磁盘更加友好。处理读请求时，实时消费的作业可以直接从PageCache读取到数据，请求延迟较小，同时ZeroCopy机制能够减少数据传输过程中用户态与内核态的切换，大幅提升了数据传输的效率。

### Kafka多Partition情况消费延迟问题

参考：[美团处理Kafka性能实践](https://iteblog.blog.csdn.net/article/details/113154551)、[快手处理Kafka性能问题](https://mp.weixin.qq.com/s/1Dwu2Z8Lv88e_Mp1ne7XLg)

kafka的高性能，来源于顺序文件读写和操作系统缓存pagecache的支持，在单partition，单consumer的场景下，kafka表现的非常优秀。但是，如果同一机器上，存在不同的partition,甚至，消费模式有实时和延迟消费的混合场景，将会出现PageCache资源竞争，导致缓存污染，影响broker的服务的处理效率。

### 如何理解Kafka缓存污染？

Producer将数据发送到Broker，PageCache会缓存这部分数据。当所有Consumer的消费能力充足时，所有的数据都会从PageCache读取，全部Consumer实例的延迟都较低。此时如果其中一个Consumer出现消费延迟，根据读请求处理流程可知，此时会触发磁盘读取，在从磁盘读取数据的同时会预读部分数据到PageCache中。当PageCache空间不足时，会按照LRU策略开始淘汰数据，此时延迟消费的Consumer读取到的数据会替换PageCache中实时的缓存数据。后续当实时消费请求到达时，由于PageCache中的数据已被替换掉，会产生预期外的磁盘读取。这样会导致两个后果

### 解决Kafka性能问题方案

消除实时消费与延迟消费间的PageCache竞争，如：让延迟消费作业读取的数据不回写PageCache，或增大PageCache的分配量等

- 选择SSD
  - 让近实时的消息全部落到SSD上
  - 防止延时消息落到SSD上

### RocketMQ与KafKa区别

- KafKa每个partion都单独存放，而RocketMQ同一个Broker中所有的队列的数据都存放在一个CommitLog中，KafKa这种方式一旦 Topic 的 Partition 数量过多队列文件会过多，那么会给磁盘的 IO 读写造成比较大的压力，也就造成了性能瓶颈。所以 RocketMQ 进行了优化，消息主题统一存储在 CommitLog 中。

  - ##### rocketmq不是仅仅把partition改成了ConsumeQueue

  - ##### ConsumeQueue里面是存储消息的存储地址，但是不存储消息，相当于索引

​	



- 速度的话：Kafka是百万级别并发、RocketMQ是十万级别
- 注册中心：Kafka是zookeeper、RocketMQ是NameServer
- kafka都是异步刷盘，但是有三种方式
  - 0-生产者不等待broker的ack，继续发送消息 
  - 1- 等待leader落盘返回
  -  -1-主从全部落盘才返回ack
- RocketMQ支持延迟消息，Kafka不支持
- RocketMQ支持Push和pull两种模式，Kafka只有一种poll
  - 但是RocketMQ的push模式是基于pull和长轮询做的
  - 当consumer过来请求时，broker会保持当前连接一段时间 默认15s,如果这段时间内有消息到达，则立刻返回给consumer；15s没消息的话则返回空然后重新请求。这种方式的缺点就是服务端要保存consumer状态，客户端过多会一直占用资源。
- Kafka不支持分布式事务，RocketMQ支持
- 

