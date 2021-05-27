### Kafka zookeeper

Kafka 使用 ZooKeeper 来保存与分区和代理相关的元数据，并选举出一个代理作为集群控制器。不过，Kafka 开发团队想要消除对 Zookeeper 的依赖，这样就可以以更可伸缩和更健壮的方式来管理元数据，从而支持更多的分区，还能够简化 Kafka 的部署和配置。



所以 Apache Kafka 为什么要移除 Zookeeper 的依赖？Zookeeper 有什么问题？实际上，问题不在于 ZooKeeper 本身，而在于外部元数据管理的概念。

## Canal+Kafka实现增量数据同步

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

相比RocketMQ，Kafka一个topic如果有多个分区，就会有多个文件，也就是文件数量和分区数量一致。所以Kafka多文件写肯定比RocketMQ单文件写速度更快

#### Kafka处理写流程

**对于Produce请求**：Server端的I/O线程统一将请求中的数据写入到操作系统的PageCache后立即返回，当消息条数到达一定阈值后，Kafka应用本身或操作系统内核会触发强制刷盘操作

主要利用了操作系统的ZeroCopy机制，当Kafka Broker接收到读数据请求时，会向操作系统发送sendfile系统调用，操作系统接收后，首先试图从PageCache中获取数据（如中间流程图所示）；如果数据不存在，会触发缺页异常中断将数据从磁盘读入到临时缓冲区，随后通过DMA操作直接将数据拷贝到网卡缓冲区中等待后续的TCP传输。

综上所述，Kafka对于单一读写请求均拥有很好的吞吐和延迟。处理写请求时，数据写入PageCache后立即返回，数据通过异步方式批量刷入磁盘，既保证了多数写请求都能有较低的延迟，同时批量顺序刷盘对磁盘更加友好。处理读请求时，实时消费的作业可以直接从PageCache读取到数据，请求延迟较小，同时ZeroCopy机制能够减少数据传输过程中用户态与内核态的切换，大幅提升了数据传输的效率。

#### Kafka多Partition情况消费延迟问题

参考：[美团处理Kafka性能实践](https://iteblog.blog.csdn.net/article/details/113154551)、[快手处理Kafka性能问题](https://mp.weixin.qq.com/s/1Dwu2Z8Lv88e_Mp1ne7XLg)

kafka的高性能，来源于顺序文件读写和操作系统缓存pagecache的支持，在单partition，单consumer的场景下，kafka表现的非常优秀。但是，如果同一机器上，存在不同的partition,甚至，消费模式有实时和延迟消费的混合场景，将会出现PageCache资源竞争，导致缓存污染，影响broker的服务的处理效率。

#### 如何理解Kafka缓存污染？

Producer将数据发送到Broker，PageCache会缓存这部分数据。当所有Consumer的消费能力充足时，所有的数据都会从PageCache读取，全部Consumer实例的延迟都较低。此时如果其中一个Consumer出现消费延迟，根据读请求处理流程可知，此时会触发磁盘读取，在从磁盘读取数据的同时会预读部分数据到PageCache中。当PageCache空间不足时，会按照LRU策略开始淘汰数据，此时延迟消费的Consumer读取到的数据会替换PageCache中实时的缓存数据。后续当实时消费请求到达时，由于PageCache中的数据已被替换掉，会产生预期外的磁盘读取。这样会导致两个后果

#### 解决Kafka性能问题方案

消除实时消费与延迟消费间的PageCache竞争，如：让延迟消费作业读取的数据不回写PageCache，或增大PageCache的分配量等

- 选择SSD
  - 让近实时的消息全部落到SSD上
  - 防止延时消息落到SSD上

### RocketMQ与KafKa区别

  KafKa每个partion都单独存放，而RocketMQ同一个Broker中所有的队列的数据都存放在一个CommitLog中，KafKa这种方式一旦 Topic 的 Partition 数量过多，

队列文件会过多，那么会给磁盘的 IO 读写造成比较大的压力，也就造成了性能瓶颈。所以 RocketMQ 进行了优化，消息主题统一存储在 CommitLog 中。