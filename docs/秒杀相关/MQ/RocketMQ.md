  

## RocketMQ

### 导读

这个是介绍RocketMQ基础、高可用、还有相关原理的整合文章，同时希望读的时候带着下面的问题

同时相关RocketMQ的原理和功能可以参考：[阿里云企业级RocketMQ实战](https://help.aliyun.com/product/29530.html?spm=a2c4g.11186623.6.540.4dfb307dJsuSbK)

| QA                                     | 关联QA                                                | 说明 | 其它 |
| -------------------------------------- | ----------------------------------------------------- | ---- | ---- |
| RocketMQ和Kafka的区别？                |                                                       |      |      |
|                                        | 为啥在Kafka大量使用的今天选择rocketMQ?                |      |      |
| 为啥RocketMQ要单独写一个NameServer？   |                                                       |      |      |
|                                        | NameServer遵循的是CAP中的哪个？                       |      |      |
|                                        | NameServer如何保证最终一致性？                        |      |      |
|                                        | 客户端为啥只需要和一个NameServer建立连接？            |      |      |
| 为啥RocketMQ的重试机制只针对普通消息？ |                                                       |      |      |
|                                        | 为啥重试的时候会优先选择其它Broker?                   |      |      |
|                                        |                                                       |      |      |
| 如何保证RocketMQ高可用？               |                                                       |      |      |
|                                        | 生产者、消费者、Broker、NameServer如何做到高可用？    |      |      |
| 如何保证消息不丢失？                   |                                                       |      |      |
|                                        | 生产者、消费者、Broker、OS cache如何保证不丢失？      |      |      |
| 如何保证消息不乱序？                   |                                                       |      |      |
|                                        | 发送时、存储时、消费时如何保证不乱序？                |      |      |
|                                        | 什么是瞬时乱序行为？                                  |      |      |
| 如何保证消息幂等性？                   |                                                       |      |      |
| 消费者Rebalance原理是什么？            |                                                       |      |      |
|                                        | Rebalance会带来什么问题？                             |      |      |
|                                        | Rebalance过程中消费者自己分配队列，如何保证脑裂问题？ |      |      |
|                                        | 如果某个消费者没有收到Rebalance通知怎么办？           |      |      |
| 分区有序消息是如何选择的？             |                                                       |      |      |
| 定时、延时消息原理是什么？             |                                                       |      |      |



### 集群架构

![](https://s4.51cto.com/images/blog/202010/28/4ad02c1b9946aa548684f1e1af83a357.png)



- nameServer
  - 类似zookeeper功能的一个轻量级名字服务，具有简单、集群横向扩展、无状态等特点
  - 一般一个集群部署2台NameServer服务器
  - 无状态如何理解：状态的有无实际就是数据是否会存储，有状态的话数据会被持久化，无状态的服务数据保存在内存中，NameServer本身也是一个内存服务，重启之后数据丢失
  - Kafka使用的是Zookeper保存Broker的地址信息，以及Broker的Leader选举，在RocketMQ中没有采用Broker的选举机制，所以采用无状态的NameServer来存储，由于NameServer无状态，所以集群节点间不会通信，上传数据的时候需要向所有的节点发送
- Broker
  - 消息中转器，用于消息保存和转发
  - 每个消费者会向所有的Broker进行心跳，因此每个Broker维护了所有消费者的实例
- Produce集群
  - 生产消息的集群
- Consumer集群
  - 消费消息的集群

#### 主要功能

- 高可用——不丢消息

- 高吞吐量（可以替换ActiveMQ）生产、消费

- 高可用

- 监控、报警

- 持久化——消息可以保存7天

- 多消息类型（无序、分区有序、全局有序）

- 回溯消费——多次消费


#### 消费模式

##### 集群消费

- 平分消息


  - 消息消费失败进行重复投递，大于15次，进入死信队列并报警

##### 广播消费

  - 无失败处理，需要业务方自行处理
  - 无法处理顺序消息 

#### 名词解释

##### 死信消息

-  消息消费重试发送16次失败、消息被拒绝并且不再重新投递，会产生一个名为%DLQ%+consumergroup的队列，这个队列为死信队列

##### 消息堆积

-  消息发送的速率远远大于消息消费的速率导致消息堆积

##### 同步、异步复制

复制指的是Broker之间的数据同步模式，分为同步和异步两种。

- 同步复制：生产者会等待同步复制成功后才返回消息发送成功
- 异步复制：消息写入到MasterBroker后即认为写入成功，此时有较低的写入延迟和较大的吞吐量

##### 同步、异步刷盘

刷盘指的是数据发送到Broker的内存，通常指的是PageCache后，以何种方式持久化到磁盘。

- 同步刷盘：生产者会等待数据持久化到磁盘后才返回消息发送成功，可靠性极强
- 异步刷盘：消息写入PageCache后即认为写入成功，到达一定量时自动触发刷盘，此时有较低的延迟和吞吐量

#### 功能介绍

##### 并发消费
- 消息并发消费，异常消费不会阻塞消费
- 消息消费异常，异常的消息会发回重试队列
-  默认重试16次

##### 顺序消费
  - 消息顺序消费，队列会被lock，异常消息不能跳过
  - 异常消息不能跳过，会不断重试，消息顺序性可能得到保证
##### 超时消费
  - 单个消息阻塞时间默认15分钟
  - 超时后会进入重试队列
#### 消息分类

##### 单项消息OneWay

- 不保证消息发送成功，多用于日志类型的消息，对业务无影响
- 此方式发送消息的过程耗时非常短，一般在微秒级别。

##### 同步普通消息

- 同步发送：是指消息发送方发出一条消息后，会在收到服务端返回响应之后才发下一条消息的通讯方式。

- 适用场景：重要通知邮件、报名短信通知、营销短信系统等。

##### 异步普通消息

- 异步发送：是指发送方发出一条消息后，不等服务端返回响应，接着发送下一条消息的通讯方式，但是我们要做的事，需要自己实现异步发送回调接口（SendCallback），处理响应结果

##### 全局有序消息
  - 保证顺序发送、顺序存储、顺序消费
  - 1.1）单线程发送、上一个成功才能发送下一个
  - 1.2）只能同步发送
  - 消息顺序存储
  - 2.1）由于一个消息Topic会有多个Queue，要保证顺序存储，需要将同一个业务的消息发送到同一个Queue。即对业务编号进行hash，将消息发送到同一个队列中
  - 消息顺序消费：同一时刻一个消费队列queue只能被一个线程消费

##### 分区有序消息

  - 对于一个topic所有消息会根据sharding key进行区块分区，同一个分区内的消息会严格按照FIFO顺序发布和消费

    sharding key 是顺序消息中用来区分不同分区的字段，和普通消息的key不是一个概念

##### 定时、延时消息

  - 并非精确到时分秒，而是有一个颗粒度
  - 目前默认设置为：1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h，从1s到2h分别对应着等级1到18，而阿里云中的版本(要付钱)是可以支持40天内的任何时刻（毫秒级别）
  - 需要延时、定时的场景一定要有补偿机制，做一些兜底的逻辑

##### 广播消息

  - 每一个消费者组都会收到消息

##### 事物消息

  - VKMQ 提供类似 X/Open XA 的分布事务功能，通过 VKMQ 事务消息能达到分布式事务的最终一致。

  - [事物消息最佳实战](https://mp.weixin.qq.com/s/ljSktiZYh_5W93m3yB4M-g)

  - 事物消息性能不如普通消息，事实上它会在内部产生3个消息（一阶段1个，二阶段2个），性能只有普通消息的1/3，如果事物消息体量大的话，做好容量规划

  - 最后有一些参数注意事项。在 Broker 的配置中：

    - transientStorePoolEnable 这个参数必须保持默认值 false，否则会有严重的问题。
    - endTransactionThreadPoolNums是事务消息二阶段处理线程大小，sendMessageThreadPoolNums 则指定一阶段处理线程池大小。如果二阶段的处理速度跟不上一阶段，就会造成二阶段消息丢失导致大量回查，所以建议 endTransactionThreadPoolNums 应该大于 sendMessageThreadPoolNums，建议至少 4 倍。
    - useReentrantLockWhenPutMessage 设置为 true（默认值是 false），以免线程抢锁出现严重的不公平，导致二阶段处理线程长时间抢不到锁。
    - transactionTimeOut 默认值 6 秒太短了，如果事务执行时间超过 6 秒，就可能导致消息丢失。建议改到 1 分钟左右。

     

    生产者 Client 也有一个注意事项，如果有多组 Broker，并且是 2 副本（有 1 个 Slave），应该打开 retryAnotherBrokerWhenNotStoreOK，以免某个 Slave 出现故障以后，大量消息发送失败。

##### 批量处理消息

  - 一批消息只会被一个队列消费，另一个队列消费不到

##### 条件筛选消息

  - 类似SQL过滤
### 可靠性测试

#### NameServer高可用测试

- 一台下线无影响

- 全部下线运行时

  1）对已启动的producer无影响，新启动的producer会报错

  2）对已启动的consumer无影响，新启动的Consumer不会报错，但也无法消费消息

  3）对现有broker无影响，新启动时会报错

#### Broker高可用测试

- 一台master下线——无影响
  - consumer会从slave上消费数据，消息不会丢失
  - master什么时候重启：等待slave上的消息消费完，等5秒（offset同步间隔）再重启master，减少重复消费的可能
- 2台全部下线
  - 生产者不能写入服务不可用，消费者无影响，Consumer依旧会从Slave上消费数据，数据不回丢失
  - 处理方式：重启Broker
- master重新上线
  - 会有少量重复消费，消费者无感知
  - 重新上线时机：等待slave上的消息消费完，等5秒（offset同步间隔）再重启master，减少重复消费的可能
- Broker扩容
  - 旧topic TPS无影响，新topic TPS增加
  - 消费者无感知
  - 若要增加旧topic的性能，需要增加queueSize
- slave下线
  - 同步发送时返回：SLAVE_NOT_AVAILABLE
  - 但发送可以继续进行
  - 消费者直接从Master上消费消息
- slave重新上线
  - 对于Producer无影响，此时会进行master同步数据，然后Consumer从slave进行消费
- 机器断电：会丢失少量消息，因为操作系统无机会落盘
- 磁盘满了：会删除最早的消息
- 网络带宽满了：用大量消息压测，发现带宽满了之后会有大量消息失败

#### Consumer高可用测试

- 增加Consumer
  - 会马上Rebalance
  - 注意队列数和消费者数，消费者大于queueSize时，新增的消费者会空跑

- 减少Consumer
  - 马上Rebalance

### 如何保证消息不丢失？

再说消息丢失之前，我们先来看下一个正常消息的整个链路流程

Producer——【Broker OS Cache ——磁盘】——Consumer

#### 1-producer——Broker发消息

- 场景一：单项详细（OneWay）
  - Producer不等待Broker返回结果，有可能Broker没有收到消息导致丢失消息
- 场景二：MQ不保证Producer消息发送一定成功导致消息丢失
  - 同步消息：当Producer向Broker发送消息，如果**失败次数超过限制**，则不再发送
  - 异步消息：Producer发送给broker后，Broker会通过异步机制回调给Producer，**MQ对异步消息发送失败不会自动重试**
- 对于上述同步和异步发消息场景，当Producer向Broker发消息失败，如果业务要求消息一定不能丢，推荐业务使用下述方案：
  - 一、业务把失败的消息持久化存储（如存到DB），然后启动线程通过定时任务重试，直到发到Broker成功
  - 二、业务重试的时候注意失效性、注意顺序性

#### 2-Broker OS Cache ——磁盘刷消息，同步、异步刷盘

考虑下面的场景：

1. Broker正常关机
2. Broker异常Crash
3. OS Crash
4. 集群断电，但能立即恢复供电
5. 集群无法开机（可能CPU、主板、内存等关键设备损坏）
6. 磁盘设备损坏

- 可能丢消息场景一：
  - 1~4 在异步刷盘的模式下会丢少量消息；
  - 同步刷盘模式下不会丢消息。
- 可能丢消息场景二：
  - 5~6 属于单点故障，一旦发生，该节点上的消息全部丢失，如果开启了主从，则可去从节点消费消息；
  - 如果开启了异步复制机制，VKMQ只丢少量消息。
- 建议：如果严格要求不丢失消息，开启同步刷盘，但是并发会下降很多，但是无论是哪种刷盘模式都要开启主从

目前RocketMQ存储模型使用本地磁盘进行存储，数据写入为producer -> direct memory -> pagecache -> 磁盘，数据读取如果pagecache有数据则直接从pagecache读，否则需要先从磁盘加载到pagecache中

#### 3-Consumer——Broker消费消息

- 可能丢消息场景一：Consumer消费到消息，在业务处理成功前，就提交了offset

- 可能丢消息场景二：MQ在Broker服务器上保存有过期时间（默认为48小时），过期的消息会被统一清理。所以如果在清理前没有被Consumer消费的消息会丢。

- 可能丢消息场景三：消息反复消费失败进入死信队列：

1）有序消息：这种情况失败会一直重试，会阻塞后面的消息消费；
2）无序消息：这种消息在第一次消费失败就会进入重试消息队列 ，在这个队列会以时间梯度的方式重复发送16次 （可配），再次失败会进入死信队列， 这时候会有相关告警，要再需要这条消息就需要人工介入处理

- 建议：
  - 一定要业务处理完成再提交消息成功
  - 备用方案去拉取消息
  - 具备死信消息处理机制

### 如果保证消息不乱序

所谓消息有序，指消息消费者按照消息到达消息存储服务器的顺序消费。

需要由3个阶段去保障：

#### 1-消息被发送时保持顺序

- 在同一个线程中采用同步的方式发送。

#### 2-消息被存储时保持和发送的顺序一致

确保要排序的消息路由到同一个队列中。

#### 3-消息被消费时保持和存储的顺序一致

不对收到的消息的顺序进行调整，即只要一个分区的消息只由一个线程处理即可。

VKMQ顺序消息分为全局有序和分区有序。

1）全局有序：一个topic内的所有消息都发送到同一个队列。缺点是响性能不如多个队列并行消费好。

#### 4-瞬时乱序行为？

2）分区有序：消息根据key落到不同的分区（队列），可保证同一分区内消息有序。缺点是一旦队列总数变化（如Broker重启等），会导致落点错乱，导致瞬时乱序。

### 如何保证消息不重复消费

- 业务去重

#### 常见MQ异常

- broker busy

如果消息量很小观察下，有必要进行扩容

如果消息量很大，进行扩容

- TimeOutClientQueue

  一般解决方案是扩容，还可以把磁盘换成SSD

### 最佳实战

#### Producer

##### 生产者发送消息注意事项

1. 建议**消息大小**不要超过512K,

2. **默认的发送**为同步发送，send方法会一直阻塞，等待broker端的响应。如果你关注性能问题，可以通过send(msg, callback)来发起异步调用。

3. 正常情况下**生产者组**是没有作用的，但是在发送事务消息时，如果producer中途意外宕机，broker会主动回调producer group 内的任意一台机器来确认事务的状态

4. **生产者实例**是**线程安全**的，在应用中只需要实例化一次即可

5. 性能问题

   如果你希望在一个jvm进程内使用多个producer实例来提高发送能，我们建议：

   - 使用异步发送，并且producer实例只需要3 ~ 5个即可
   - 对每一个producer 调用 setInstanceName，区别不同的生产者

6. 当客户端向broker发送请求超时时，客户端会抛出 RemotingTimeoutException，默认的超时时间是3秒。通过调用send(msg, timeout) 可以设置超时时间。**超时时间**建议不要设置过小，因为 broker 可能需要时间刷盘或向 slave 同步数据

7. 对于同一个应用最好只使用一个Topic，消息的子类型可以使用 **tags** 来标识，tags 可以由应用自由设置。当发送的消息设置了 tags 时，消费方在订阅消息时可以使用 tags 在 broker 做消息过滤。注意这里的命名虽然是复数，但是一条消息只能有一个tag

8. 消息在业务层面的唯一标识可以设置到 keys 字段，方便根据 keys 来定位消息。broker 会为每个消息创建索引（哈希索引），应用可以通过topic 、key 查询这条消息的内容(MessageExt)，以及消息被谁消费（MessageTrack，精确到consumer group）。由于是哈希索引，请尽量保证key 的唯一，这样可以避免潜在的哈希冲突

9. 消息发送不管是成功还是失败都要**打印消息日志**，日志内容务必包含 sendResult 和 key 字段

10. 对于消息不可丢失的应用，务必要有**消息重发机制**。例如如果消息发送失败，可以将消息存储到数据库，然后通过定时程序或者人工的方式触发重发

11. 调用send 同步发送消息时，假定此时设置了 isWaitStoreMsgOK=true(default is true)，只要不抛出异常就代表发送成功，但当 isWaitStoreMsgOK = false 时，发送永远返回 SEND_OK。但是对于发送“成功”会有多个状态，在 SendStatus 中定义如下：

    - FLUSH_DISK_TIMEOUT

      如果 broker 设置的 FlushDiskType = SYNC_FLUSH，当 broker 的在刷盘超时时（MessageStoreConfig.syncFlushTimeout，默认5秒）会返回该状态。此时消息任然保存在内存中，只有broker 宕机时消息才会丢失

    - FLUSH_SLAVE_TIMEOU

      如果 broker 的 role 是 SYNC_MASTER，当 slave 同步数据的时间超过了 MessageStoreConfig.syncFlushTimeout (默认5秒) 时会返回此状态。此时只有主从都宕机，并且主也没有刷盘时，消息才会丢失

    - SLAVE_NOT_AVAILABLE

      如果 broker 的 role 是 SYNC_MASTER，并且此时 slave 不可用时会返回该状态

    - SEND_OK

      发送成功。为了保证消息不丢失还需要配置 

      SYNC_MASTER or SYNC_FLUSH

12. 当发送消息时返回 FLUSH_DISK_TIMEOUT/FLUSH_SLAVE_TIMEOUT，若非常不幸的 broker 也宕机了，消息将会丢失。此时如果什么都不做，消息可能会丢失，如果重发消息，消息可能会出现重复。

    通常我们建议发送端重发消息，由消费方来保证消息消费的幂等性。



##### 消息发送失败如何处理

Producer 的 send 方法本生支持内部重试，重试逻辑如下：

1. 至多重试3次
2. 如果发送失败，则轮转到下一个broker
3. 这个方法的总耗时时间不超过 sendMsgTimeout，默认3秒
   所以发送消息已经产生超时异常的话就不会再重试。

以上策略仍不能保证消息发送一定成功，为保证消息发送一定成功，建议应用这么做：如果调用 send 同步发送失败，则尝试将消息存储到db，由后台线程定时重试，保证消息一定到达 Broker

##### 顺序消息问题

顺序消息分为分区有序和全局有序。

分区有序要求 producer 在send 时传入 MessageQueueSelector 的实现类，最终将某一类消息发送到同一队列。但是一旦发生通信异常、broker 重启等，由于队列总数发生变化，哈希取模后定位的队列会变化，会产生短暂的顺序不一致。如果业务能容忍在集群异常情况下（如某个 broker 宕机或者重启）消息短暂的乱序，使用分区有序比较合适



全局严格有序的消息即便在异常情况下也能保证消息的有序性，但是却牺牲了分布式的 failover 特性，即 broker 集群中只有要一台机器不可用，则整个集群都不可用，服务可用性会大大降低。

 

顺序消息的缺点：

- 发送顺序消息无法利用集群的 FailOver 特性
- 消费顺序消息的并行度依赖于队列数量
- 队列热点问题，个别队列由于哈希不均导致消息过多，消费速度跟不上，产生消费堆积问题
- 遇到消费失败的消息，无法跳过，当前队列需要暂停

#### consumer

##### MessageListener

1）顺序消费 MessageListenerOrderly

顺序消费时消费者会锁定队列，以确保消息被顺序消费，但是这样也会造成一定的性能损耗。当消费出现异常的时候，建议不要抛出异常，而是返回 ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT，让消费暂停一会，暂停时间由 context.setSuspendCurrentQueueTimeMillis 方法指定

2）并发消费

并发消费是推荐的消费方式，在此种模式下，消息将被并发的消费。消费出现异常时不建议抛出异常，只需要返回 ConsumeConcurrentlyStatus.RECONSUME_LATER 即可。为了保证消息肯定被至少消费一次，消息将会被重发回 broker （topic不是原topic而是这个消费组的RETRY topic），在延迟的某个时间点（默认是10秒，业务可设置，通过 delayLevelWhenNextConsume 和 MessageStoreConfig.messageDelayLevel 设置）后，再次投递到这个 ConsumerGroup，而如果一直这样重复消费都持续失败到一定次数（默认是16次，DefaultMQPushConsumer.maxReconsumeTimes），就会投递到DLQ队列。应用可以监控死信队列来做人工干预。

3）返回状态

在并行消费时可以通过返回 RECONSUME_LATER 来告诉 Consumer 当前无法消费该消息，等延时一段时间再重新消费，但是此时消费不会停止，你可以继续消费其他消息。但在顺序消费时，因为要保证消费的顺序性，所以你不能跳过失败的消息，此时你可以通过返回 SUSPEND_CURRENT_QUEUE_A_MOMENT 来告诉 Consumer 先暂停一会。

4）阻塞

不建议阻塞Listener，因为这会阻塞住线程池，同时也有可能造成消费者线程终止

##### consumer线程池 

consumer 内部通过一个 ThreadPoolExecutor 来消费消息，可以通过 setConsumeThreadMin 和 setConsumeThreadMax 来改变线程池的大小

##### 消费速度慢处理方式

1）提高消费并行度

大部分消息消费行为都属于 IO 密集型业务，适当的提高并发度可以显著的改善消费的吞吐量

2）批量方式消费

默认情况下 consumer 的 consumeMessageBatchMaxSize 为1，即一次只消费一个消息，如果应用可以批量消费消息，则可以很大程度上提高消费吞吐量

3）跳过非重要消息

当消堆积严重时可以丢弃不重要的消息

```java
final` `long` `offset = msgs.get(``0``).getQueueOffset();``final` `String maxOffset = msgs.get(``0``).getProperty(MessageConst.PROPERTY_MAX_OFFSET);``final` `long` `diff =Long.valueOf(maxOffset) - offset;``if` `(diff > ``100000``){``  ``return` `ConsumeConcurrentlyStatus.CONSUME_SUCCESS;``}
```

4) 优化消息消费过程

##### 集群消费模式

- 集群消费
  - 保证每条消息只被处理一次
  - 消费进度在服务器维护，可靠性高
  - 消息只会被分发到一台机器，若消费失败，消息重新投递但不保证会投递到原先的机器
- 广播消费
  - 顺序消息不支持广播消费
  - 消费进度在客户端维护
  - 广播模式只保证每条消息被每台服务消费一次，但是不保证消费成功，也就是消费失败并不会重新投递

### 高可用如何保证？

#### 生产者高可用

- 客户端保证
  - 重试机制，无论是同步还是异步发送，如果单个Broker发生故障，重试会选择其它的Broker保证消息正常发送
  - 保证机制，客户端容错，MQ会维护一个Broker——发送延迟的关系，利用这个关系，我们可以选择一个发送延迟级别较低的Broker来发送新消息
#### Broker高可用
  - 要绝对高可用可以同步复制、同步刷盘
  - 大部分场景可以异步复制、异步刷盘
  - 我们公司集群的选择：2台NameServer、Broker部署两组，每组都是一主一从，同步复制、异步刷盘

具体的配置是：

机器配置

| role        | cpu  | memory | disk     | os         | 网络延时 | jvm启动参数                                                  |
| :---------- | :--- | :----- | :------- | :--------- | :------- | :----------------------------------------------------------- |
| name server | 2    | 3G     | 200G HDD | centos 6.8 | 0.1 ms   | -Xms2g -Xmx2g                                                |
| broker      | 4    | 32G    | 100G SSD | centos 6.8 | 0.1 ms   | -Xms16g -Xmx16g -Xmn8g -XX:+UseG1GC -XX:G1HeapRegionSize=16m -XX:G1ReservePercent=25 -XX:InitiatingHeapOccupancyPercent=30 -XX:SoftRefLRUPolicyMSPerMB=0 -XX:SurvivorRatio=8 |

#### 消费者高可用机制

- 重试-死信机制

  - 投递16次失败后，进行死信队列，名称为：%DLQ消费中组名%
  - 假如是代码BUG，导致数据都死信了，想补偿之前的数据怎么办？

- Rebalance机制

  - Rebalance用于在Broker掉线、Topic扩容、缩容、消费者扩容、缩容等变化时，自动感知并调整

- 消费进度保存机制

##### Rebalance会带来什么负面影响呢

- 消费停滞：比如原本有一个消费者A负责消费5个队列，这个时候来了一个消费者B，那么消费者A肯定会暂停消费，直到rebalance完成
- 重复消费：由于Rebalance并不会等待消息提交后再rebalance，所以会有少量重复消费的现象。
  - 道理上消费者B在执行完Rebalance之后会接着从消费者A已经消费的offset继续开始消费，但是默认的情况下，offset是异步提交的。
  - 比如消费者A消费到的offset是10，但是异步提交给broker的offset是8，那消费者B肯定是从8开始消费，导致了两条数据重复消费。
  - 根源：消费者B不会等待消费者A提交完offset后再进行Rebalance，因此提交时间间隔越长，可能造成重复的数据越多



### MQ相关原理探究

#### [消费者Rebalance机制](https://blog.csdn.net/tianshouzhi/article/details/103607572)

基于Rebalance可能会给业务造成的负面影响，我们有必要对其内部原理进行深入剖析，以便于问题排查。我们将从Broker端和Consumer端两个角度来进行说明：

Broker端主要负责Rebalance元数据维护，以及通知机制，在整个消费者组Rebalance过程中扮演协调者的作用，而Consumer端分析，主要聚焦于单个Consumer的Rebalance流程。



##### broker在Rebalance中的作用

- 触发broker的Rebalance的原因
  - 消费者实例发生变化：如消费者上线、下线、与broker意外断开连接、订阅的topic发生变化等
  - 队列信息发生变化：如单个topic扩容、缩容，broker宕机等
- 无论是哪种原因触发了broker的Rebalance，broker都会通知当前消费者组下面的所有实例进行Rebalance
- broker会给每个消费者实例发送一个通知，每个消费者实例在接收到通知后自行触发Rebalance,也就是每个消费者实例各自Rebalance，即每个消费者实例自己给自己重新分配队列，而非Broker将分配好的结果告知Consumer，这点和Kafka类似，二者的Rebalance都是在客户端进行的，不同的是：
  - Kafka：会在消费者组的多个消费者实例中，选出一个作为Group Leader，由这个Group Leader来进行分区分配，分配结果通过Cordinator(特殊角色的broker)同步给其他消费者。相当于Kafka的分区分配只有一个大脑，就是Group Leader。
  - RocketMQ：每个消费者自己给自己分配队列，每个人都是一个大脑

##### 每个消费者自己分配队列，如何避免脑裂问题？

- 因为每个消费者自己都不知道其它消费者分配的结果，会不会出现一个队列分配给了多个消费者，或者有的队列没有分配？

- 这个rocketMQ也进行了2个维度的保证

  - 对于topic队列和消费者各自进行排序
  - 每个消费者使用相同的分配策略

  尽管每个消费者是各自给自己分配，但是因为使用的相同的分配策略，定位从队列列表中哪个位置开始给自己分配，给自己分配多少个队列，从而保证最终分配结果的一致

  ```java
  // 查看当前实例ID的位置
  int index = cidAll.indexOf(currentCID);
  //mqAll是所有队列
  for (int i = index; i < mqAll.size(); i++) {
  	// 循环加入分配结果
  	if (i % cidAll.size() == index) {
  		result.add(mqAll.get(i));
  		}
  }
  return result;
  ```

  

##### 如果某个消费者没有收到Rebalance通知怎么办？

- 每个消费者都会定时去触发Rebalance，以避免Rebalance通知失效。也就是说假如某个消费者没有收到某次的Rebalance请求，但是它自己也会周期性的进行Rebalance,默认时间是20秒





#### 代码层次看消费者Rebalance

[代码位置在：DefaultMQPushConsumerImpl.java](https://github.com/apache/rocketmq/blob/master/client/src/main/java/org/apache/rocketmq/client/impl/consumer/DefaultMQPushConsumerImpl.java)

##### 消费者启动时触发Rebalance

```java
 public synchronized void start() throws MQClientException {
 
	//1.....各种启动准备工作，省略
   
   // 2 从nameServer更新topic路由信息，收集到了Rebalance所需要的队列信息
   this.updateTopicSubscribeInfoWhenSubscriptionChanged();
   // 3 检查consumer配置，比如consumer要使用SQL92过滤，但是broker没有开启，则broker会返回错误
   this.mQClientFactory.checkClientInBroker();
   // 4 向每个broker发送心跳信息，将自己加入消费者组
   this.mQClientFactory.sendHeartbeatToAllBrokerWithLock();
   // 5 立刻触发一次Rebalance（在2和4的基础上）
   this.mQClientFactory.rebalanceImmediately();
   
 }
```

##### 消费者停止时触发Rebalance

[代码位置在：DefaultMQPushConsumerImpl.java](https://github.com/apache/rocketmq/blob/master/client/src/main/java/org/apache/rocketmq/client/impl/consumer/DefaultMQPushConsumerImpl.java)

```java
public void shutdown() {
  shutdown(0);
}

public synchronized void shutdown(long awaitTerminateMillis) {
  switch (this.serviceState) {
    case CREATE_JUST:
      break;
    case RUNNING:
      //1、停止挣扎消费中的消息
      this.consumeMessageService.shutdown(awaitTerminateMillis);
      //2、持久化offset，因为offset默认是异步提交的，为了避免重复消费，在关闭时进行offset持久化
      this.persistConsumerOffset();
      //3、向broker发送取消注册consumer的请求，这个时候broker会通知当前消费者组下的其它消费者进行Rebalance
      this.mQClientFactory.unregisterConsumer(this.defaultMQPushConsumer.getConsumerGroup());
      //4、关闭与nameServer和broker的连接
      this.mQClientFactory.shutdown();
      log.info("the consumer [{}] shutdown OK", this.defaultMQPushConsumer.getConsumerGroup());
      //5、对其尚未处理的消息
      this.rebalanceImpl.destroy();
      this.serviceState = ServiceState.SHUTDOWN_ALREADY;
      break;
    case SHUTDOWN_ALREADY:
      break;
    default:
      break;
  }
}
```

##### 消费者运行时定时任务触发Rebalance

[代码位置在RebalanceService中](https://github.com/apache/rocketmq/blob/master/client/src/main/java/org/apache/rocketmq/client/impl/consumer/RebalanceService.java)

```java
public class RebalanceService extends ServiceThread {
  // 默认时间是20秒
    private static long waitInterval =
        Long.parseLong(System.getProperty(
            "rocketmq.client.rebalance.waitInterval", "20000"));
    private final InternalLogger log = ClientLogger.getLog();
    private final MQClientInstance mqClientFactory;

    public RebalanceService(MQClientInstance mqClientFactory) {
        this.mqClientFactory = mqClientFactory;
    }

    @Override
    public void run() {
        log.info(this.getServiceName() + " service started");
				// 在消费者没有停止的情况下，也就是死循环的方式进行每隔20秒触发一次
        while (!this.isStopped()) {
          // 等待时间间隔，如果被唤醒则无需等待，直接触发
            this.waitForRunning(waitInterval);
          // 触发Rebalance
            this.mqClientFactory.doRebalance();
        }

        log.info(this.getServiceName() + " service end");
    }

    @Override
    public String getServiceName() {
        return RebalanceService.class.getSimpleName();
    }
}
```

##### 运行时监听到消费者数量发生变化触发Rebalance

[代码位置在ClientRemotingProcessor中](https://github.com/apache/rocketmq/blob/master/client/src/main/java/org/apache/rocketmq/client/impl/ClientRemotingProcessor.java)

消费者数量变化时，Broker给客户端的通知，也就是下面的步骤二。在收到通知后，其调用notifyConsumerIdsChanged进行处理，这个方法内部会立即触发Rebalance。

```java
public class ClientRemotingProcessor extends AsyncNettyRequestProcessor implements NettyRequestProcessor {
    private final InternalLogger log = ClientLogger.getLog();
    private final MQClientInstance mqClientFactory;

    public ClientRemotingProcessor(final MQClientInstance mqClientFactory) {
        this.mqClientFactory = mqClientFactory;
    }

    @Override
    public RemotingCommand processRequest(ChannelHandlerContext ctx,
        RemotingCommand request) throws RemotingCommandException {
        switch (request.getCode()) {
            // 1、检查事物消息状态
            case RequestCode.CHECK_TRANSACTION_STATE:
                return this.checkTransactionState(ctx, request);
            // 2、通知消费者数量发生变化，内部会触发Rebalance
            case RequestCode.NOTIFY_CONSUMER_IDS_CHANGED:
                return this.notifyConsumerIdsChanged(ctx, request);
            // 3、重置消费者offset
            case RequestCode.RESET_CONSUMER_CLIENT_OFFSET:
                return this.resetOffset(ctx, request);
            // 4、获得消费者状态
            case RequestCode.GET_CONSUMER_STATUS_FROM_CLIENT:
                return this.getConsumeStatus(ctx, request);
						// 5、获得消费者运行时信息
            case RequestCode.GET_CONSUMER_RUNNING_INFO:
                return this.getConsumerRunningInfo(ctx, request);
						// 6、直接消费信息 
            case RequestCode.CONSUME_MESSAGE_DIRECTLY:
                return this.consumeMessageDirectly(ctx, request);

            case RequestCode.PUSH_REPLY_MESSAGE_TO_CLIENT:
                return this.receiveReplyMessage(ctx, request);
            default:
                break;
        }
        return null;
    }
    //.....省略下面的
```

监听到变化后，触发Rebalance的代码如下：

```java
 public RemotingCommand notifyConsumerIdsChanged(ChannelHandlerContext ctx,
        RemotingCommand request) throws RemotingCommandException {
        try {
            final NotifyConsumerIdsChangedRequestHeader requestHeader =
                (NotifyConsumerIdsChangedRequestHeader) request.decodeCommandCustomHeader(NotifyConsumerIdsChangedRequestHeader.class);
            log.info("receive broker's notification[{}], the consumer group: {} changed, rebalance immediately",
                RemotingHelper.parseChannelRemoteAddr(ctx.channel()),
                requestHeader.getConsumerGroup());
          // 立刻触发一次Rebalance请求
            this.mqClientFactory.rebalanceImmediately();
        } catch (Exception e) {
            log.error("notifyConsumerIdsChanged exception", RemotingHelper.exceptionSimpleDesc(e));
        }
        return null;
    }

```

#### doRebalance具体实现

##### doRebalance的具体实现

代码在：RebalanceImpl中

在为每个topic触发Rebalance的时候，会传入一个topic 名称，还有一个是否有序的标志isOrder

对于push模式，会区分当前指定的消息监听器是否有序

对于pull模式，总是认为无序的，所以传入的总是false

```java
public void doRebalance(final boolean isOrder) {
        Map<String, SubscriptionData> subTable = this.getSubscriptionInner();
        if (subTable != null) {
          	// 某个消费者下面所有订阅的topic
            for (final Map.Entry<String, SubscriptionData> entry : subTable.entrySet()) {
                final String topic = entry.getKey();
                try {
                  // 为每一个topic逐一触发Rebalance，也就是按照topic维度进行Rebalance的
                    this.rebalanceByTopic(topic, isOrder);
                } catch (Throwable e) {
                    if (!topic.startsWith(MixAll.RETRY_GROUP_TOPIC_PREFIX)) {
                        log.warn("rebalanceByTopic Exception", e);
                    }
                }
            }
        }

        this.truncateMessageQueueNotMyTopic();
    }
```



##### 按照topic维度进行Rebalance的问题？

如果一个消费者组订阅多个Topic，可能会出现分配不均，部分消费者处于空闲状态。

比如：某个消费者组group_X下有4个消费者实例Consumer_1到Consumer_4，订阅了2个topic分别是：TopicA和TopicB,而每个topic都有2个队列，也就是topic A和topic B总共有4个队列，四个队列对应了4个消费者，也就是每个消费者1个队列。

但是真实的情况是：按照topic维度统计，只会有两个消费者拿到了队列，拿到队列的消费者分别占了topic A和topic B一个，而其它的都闲置了。



**由于订阅多个Topic时可能会出现分配不均，这是在RocketMQ中我们为什么不建议同一个消费者组订阅多个Topic的重要原因**。在这一点上，Kafka与不RocketMQ同，其是将所有Topic下的所有队列合并在一起，进行Rebalance，因此相对会更加平均。

##### 按照topicRebalance的源码

```java
private void rebalanceByTopic(final String topic, final boolean isOrder) {
        switch (messageModel) {
            case BROADCASTING: {
 							// 广播模式，省略代码...........
            }
            case CLUSTERING: {
              // 集群模式下获取：mqSet是当前topic下所有队列的集合
                Set<MessageQueue> mqSet = this.topicSubscribeInfoTable.get(topic);
              // cidAll是当前消费者组下所有消费者实例的ID
                List<String> cidAll = this.mQClientFactory.findConsumerIdList(topic, consumerGroup);
              // 判空
                if (null == mqSet) {
                    if (!topic.startsWith(MixAll.RETRY_GROUP_TOPIC_PREFIX)) {
                        log.warn("doRebalance, {}, but the topic[{}] not exist.", consumerGroup, topic);
                    }
                }
 							// 判空
                if (null == cidAll) {
                    log.warn("doRebalance, {} {}, get consumer id list failed", consumerGroup, topic);
                }

                if (mqSet != null && cidAll != null) {
                    List<MessageQueue> mqAll = new ArrayList<MessageQueue>();
                    mqAll.addAll(mqSet);
										// 1、给队列和消费者实例ID排序
                    Collections.sort(mqAll);
                    Collections.sort(cidAll);
										// 2、通过AllocateMessageQueueStrategy策略进行预分配
                    AllocateMessageQueueStrategy strategy = this.allocateMessageQueueStrategy;

                    List<MessageQueue> allocateResult = null;
                    try {
                        allocateResult = strategy.allocate(
                            this.consumerGroup,
                            this.mQClientFactory.getClientId(),
                            mqAll,
                            cidAll);
                    } catch (Throwable e) {
                        log.error("AllocateMessageQueueStrategy.allocate Exception. allocateMessageQueueStrategyName={}", strategy.getName(),
                            e);
                        return;
                    }
										// 3、分配结果去重处理
                    Set<MessageQueue> allocateResultSet = new HashSet<MessageQueue>();
                    if (allocateResult != null) {
                        allocateResultSet.addAll(allocateResult);
                    }
										// 4、根据预分配结果尝试更新ProcessQueueTable，并返回true或者false标志是否发生变化
                    boolean changed = this.updateProcessQueueTableInRebalance(topic, allocateResultSet, isOrder);			
                  	// 5、如果分配结果发生变化，则进行后续处理。因为有些经过Rebalance可能没有发生变化
                    if (changed) {
                        log.info(
                            "rebalanced result changed. allocateMessageQueueStrategyName={}, group={}, topic={}, clientId={}, mqAllSize={}, cidAllSize={}, rebalanceResultSize={}, rebalanceResultSet={}",
                            strategy.getName(), consumerGroup, topic, this.mQClientFactory.getClientId(), mqSet.size(), cidAll.size(),
                            allocateResultSet.size(), allocateResultSet);
                        this.messageQueueChanged(topic, mqSet, allocateResultSet);
                    }
                }
                break;
            }
            default:
                break;
        }
    }
```

在上面的源码中，我们总共分了5步，下面重点说下步骤2，AllocateMessageQueueStrategy接口有多种实现，分别是：

- AllocateMessageQueueAveragely平均分配，默认策略
- AllocateMessageQueueAveragelyByCircle循环分配
- AllocateMessageQueueConsistentHash一致性hash
- AllocateMessageQueueByConfig按照配置分配，也就是手动分配
- AllocateMessageQueueByMachineRoom 按照机房分配

##### 每个实例自己分配队列的代码

其实思路很简单，一个消费者组下面，所有的队列是一定的，比如1-10，而传入的消费者组的list也是一样的，这样可以通过hash进行取值

```java
public class AllocateMessageQueueAveragelyByCircle implements AllocateMessageQueueStrategy {
    private final InternalLogger log = ClientLogger.getLog();

    @Override
    public List<MessageQueue> allocate(String consumerGroup, String currentCID, List<MessageQueue> mqAll,
        List<String> cidAll) {
        if (currentCID == null || currentCID.length() < 1) {
            throw new IllegalArgumentException("currentCID is empty");
        }
        if (mqAll == null || mqAll.isEmpty()) {
            throw new IllegalArgumentException("mqAll is null or mqAll empty");
        }
        if (cidAll == null || cidAll.isEmpty()) {
            throw new IllegalArgumentException("cidAll is null or cidAll empty");
        }

        List<MessageQueue> result = new ArrayList<MessageQueue>();
        if (!cidAll.contains(currentCID)) {
            log.info("[BUG] ConsumerGroup: {} The consumerId: {} not in cidAll: {}",
                consumerGroup,
                currentCID,
                cidAll);
            return result;
        }
				// 查看当前实例ID的位置
        int index = cidAll.indexOf(currentCID);
        for (int i = index; i < mqAll.size(); i++) {
          // 循环加入分配结果
            if (i % cidAll.size() == index) {
                result.add(mqAll.get(i));
            }
        }
        return result;
    }

    @Override
    public String getName() {
        return "AVG_BY_CIRCLE";
    }
}
```



#### 消费方式Pull和Push的区别



#### 事物机制原理

#### 延时、定时消息原理

Producer将消息投递到Broker中，Broker在收到用户发送的消息后，首先将消息保存到名为：SCHEDULE_TOPIC_XXX的topic中，此时消费者无法消费该延迟消息，然后由Broker端的定时任务投递给Consumer

延时逻辑见：ScheduleMessageService，即按照配置初始化多个任务，每秒执行一次，如果满足条件则将消息投递到原有的topic中。



### QA

#### RocketMQ分区是如何选择的？

- 未指定分区的消息会轮询的方式选择分区，比如普通消息
- 指定分区的消息会根据key的hash值确定分区，比如顺序消息

| 广播消费问题                                                | 全局顺序消费问题                   | 分区顺序问题                         | consumer数量大于队列数量                                    |
| ----------------------------------------------------------- | ---------------------------------- | ------------------------------------ | ----------------------------------------------------------- |
| 广播即使消费异常也不会发回到Broker                          | 一个Broker不可用，整个集群不可用   | Broker重启、扩容、缩容会导致瞬时乱序 | 多出来的consumer实例将无法分到queue<br />也就无法消费到消息 |
| 广播无法处理顺序消费                                        | 容易产生消息堆积                   |                                      |                                                             |
| 客户端每次重启都会拉取最新消息,<br />而不是上次保存的offset | 消费失败无法跳过，当前队列需要暂停 |                                      |                                                             |

#### [1、为啥RocketMQ 要单独设计一个NameServer?](https://blog.csdn.net/tianshouzhi/article/details/103607565)

目前可以作为服务发现的组件有很多，比如Zookeper、consul等，那为啥RocketMQ要单独开发一个呢？

其实在最开始RocketMQ选择的也是Zookeper，但是繁杂的运行机制和过多的依赖导致RocketMQ最终自己开发了一个零依赖、更简洁的NameServer

- RocketMQ的架构设置决定了只需要一个轻量级的元数据服务器就足够了，只需要保证最终一致性，而不需要像Zookeeper这样的强一致性方案，因此不需要依赖另外一个中间价，降低了维护成本
- 也就是说RocketMQ在CAP理论中，选择了AP而非CP（Consistency一致性，Availability可用性，Partition分区容错性）。即只要不是所有的nameserver都挂了，rocketMQ还能使用
- 另外说下kafka目前也在做一个方案就是替换掉zookeeper



| 功能         | zookeeper                            | RocketMQ       |
| ------------ | ------------------------------------ | -------------- |
| 配置保存     | 持久化到磁盘                         | 保存在内存中   |
| 是否支持选举 | 是                                   | 否             |
| 数据一致性   | 强一致                               | 最终一致性     |
| 高可用       | 是                                   | 是             |
| 设计逻辑     | Raft选举，逻辑复杂难懂，排查问题较难 | CRUD，仅此而已 |
|              |                                      |                |



#### 2、NameServer如何保证最终一致性？

对于zookeeper这样强一致性的组件，只要数据写到了主节点，内部会将数据复制到其他节点，zookeeper使用的是Zab协议。但是NameServer集群之间是不互相通信的，无法进行数据的复制，所有某个时刻各个节点之间的数据可能不一致，那NameServer是如何保证客户端最终能拿到正确的数据呢？我们从服务注册、剔除、发现三个角度说明

- 服务注册
  - rocketMQ采用的策略是：在Broker启动的时候轮询NameServer列表，与每个NameServer服务器建立长链接，发起注册请求。NameServer内部会维护一个Broker表，用于动态存储Broker的信息
  - 同时Broker为了证明自己是活着的，每隔一段时间会上报信息到NameServer,而NameServer接收到心跳包后会更新时间戳，记录这个Broker最新的存活时间
  - 在记录Broker表的时候，可能有多个Broker同时操作，RocketMQ引入ReadWriteLock读写锁，保证同一时刻只有一个写操作。这也是ReadWriteLock读多写少的经典使用场景
- 服务剔除
  - 正常情况下：如果一个Broker关闭了，会断开与NameServer的长连接，然后会将这个Broker的信息剔除
  - 异常情况下：NameServer有一个兜底机制，每隔10秒扫描下Broker表，如果发现某个Broker最新的时间戳距离现在已经超过120秒，则将其剔除

- 服务发现，针对的是生产者和消费者

  - 生产者：一般生产者可以发送一个消息到不同的topic，因此一般在发送第一条消息时才会根据Topic获取从NameServer的路由信息
  - 消费者：订阅的topic一般是固定的，所以在启动时就会拉取

  那么生产者/消费者在工作的过程中，如果路由信息发生了变化怎么处理呢？如：Broker集群新增了节点，节点宕机或者Queue的数量发生了变化。细心的读者注意到，前面讲解NameServer在路由注册或者路由剔除过程中，并不会主动推送会客户端的，这意味着，需要由客户端拉取主题的最新路由信息。

  - 所以生产者和消费者都做了定时任务兜底机制，每隔30秒拉取最新数据，但是如果这30秒Broker挂了怎么办？这里RocketMQ引入了重试机制来解决
  - 通过重试机制能保证生产者发送消息的高可用，而且由于不需要NameServer通知众多不固定的生产者，降低了NameServer实现的复杂性

#### 3、为啥客户端不需要与所有的Name Server建立连接，而是选择其中一个？

1、NamServer是满足最终一致性的，即使某个时刻NameServer集群中各个节点的数据不同，仍能保证消息的正常发送

2、NameServer一般只有几台，但是生产者或者消费者客户端会有很多个，如果全连接，这个对于NameServer的压力也很大

3、客户端每次选择NameServer的时候，也并不是一直都访问一个NameServer节点，而是动态的选择，刚开始的时候产生一个随机值，之后每次访问值+1

```java
private static int initValueIndex() {
    Random r = new Random();
    return Math.abs(r.nextInt() % 999) % 999;
}
private final AtomicInteger namesrvIndex = new AtomicInteger(initValueIndex());
// 之后namesrvIndex+1
```



#### 4、为啥RocketMQ的重试机制只针对普通消息？

首先我们知道RocketMQ引入重试机制是为了保证生产者的高可用，但是因为NameServer在某个时刻不同NameServer节点存储的数据可能不同，重试机制进而也保证了NameServer的最终一致性，这个上面有介绍

那么为啥重试机制只针对普通消息呢？

- 因为普通消息在重试的过程中会更换Broker进行重试。那又引入了一个问题

- 为啥消息重试的时候要更换Broker，而非换当前Broker下面的不同队列呢？

  - 这里主要是考虑既然发送到当前Broker的消息失败了，那么发送到这个Broker上其它Queue的可能性依然很大，所以选择了其它Broker,因为一般是Broker当机了，而非一个Broker上的queue坏了。

    代码位置：[TopicPublishInfo](https://github.com/apache/rocketmq/blob/master/client/src/main/java/org/apache/rocketmq/client/impl/producer/TopicPublishInfo.java)

```java
public MessageQueue selectOneMessageQueue(final String lastBrokerName) {
  				// 消息第一次发送，上一个失败的Broker名称为NULL，直接round选择
            if (lastBrokerName == null) {
                return selectOneMessageQueue();
            } else {
             // 消息发送失败重试（上一个失败的Broker不为空）优先选择其它的Broker
                for (int i = 0; i < this.messageQueueList.size(); i++) {
                    int index = this.sendWhichQueue.getAndIncrement();
                    int pos = Math.abs(index) % this.messageQueueList.size();
                    if (pos < 0)
                        pos = 0;
                    MessageQueue mq = this.messageQueueList.get(pos);
                  // 直到找到一个Broker名称不相同的进行返回
                    if (!mq.getBrokerName().equals(lastBrokerName)) {
                        return mq;
                    }
                }
              // 没有其它的Broker可选，依然round选择，可能会选择到之前失败的Broker上的队列
                return selectOneMessageQueue();
            }
        }

 public MessageQueue selectOneMessageQueue() {
        int index = this.sendWhichQueue.getAndIncrement();
        int pos = Math.abs(index) % this.messageQueueList.size();
        if (pos < 0)
            pos = 0;
        return this.messageQueueList.get(pos);
    }
```

我们上面说普通消息有重试机制，而重试机制是优先换Broker，那么对于普通消息来说，重试后的消息依旧是无序的，但是如果给有序消息引入重试机制，在Broker宕机的情况下，消息会选择到其它队列上造成短暂的无序，而后相关信息经过hash还能保持有序，这时Broker又恢复了，这个时候又会带来短暂的无序。

而对于全局有序消息来说，如果当前消息所在队列的Broker宕机了，则会阻塞无限重试。

故：有序消息如果进行重试会带来短暂的无序，这个是不进行重试的一个原因，另一个重要的原因是：对于这些有序消息由于发送的时候只会发送到某个特定的Queue中，如果发送失败，重试失败的可能性依然很大，所以默认不重试，如果需要重试，则可以代码中手动重试。 

#### 5、为啥选用RocketMQ？和Kafka的区别？

在引入 RocketMQ 之前，很多公司已经在大量的使用 Kafka 了，但并非所有情况下 Kafka 都是最合适的，比如以下场景：

- 业务希望个别消费失败以后可以重试，并且不堵塞后续其它消息的消费。
- 业务希望消息可以延迟一段时间再投递。
- 业务需要发送的时候保证数据库操作和消息发送是一致的（也就是事务发送）。
- 为了排查问题，有的时候业务需要一定的单个消息查询能力。
  - rocketMQ提供了按照消息ID、消息key、topic三种查询的方式
  - 另外找到了之后也可以进行回溯消费

#### 消息是如何在RocketMQ中存储的？

- CommitLog只有一个文件，只是为了保存和读写被分为多个子文件，所有子文件通过保存第一个和最后一个的物理位点进行连接
- Broker按照时间和物理的offset顺序写CommitLog，每次写的时候加锁
- 每个子文件默认1G

- 为啥写入的时候那么快？其实RocketMQ是借鉴Kakfa的设计

  - PageCache：每个Page默认4K，因为程序一般满足局部性原理，所以程序读的时候一般会把一段文件读到pageCache中，这样如果命中PageCache就不用读磁盘了
  - PageCache的缺点：遇到脏页回写、内存回收、内存交换等会带来较大延迟
  - 零拷贝技术：比如读的时候会经历2次拷贝
  - 虚拟内存技术
  - 读写分离机制：从Master写，从slave读

  

#### 索引机制

Rocket MQ存在两种索引机制：CommitLog和ConsumerQueue

- ConsumerQueue
  - 消费队列索引：主要用于拉取消息、更新消费位点的索引
  
  - 结构是：offset、消息体大小、Tag的hash值
  
- CommitLog索引机制

    - Roctet MQ存在多个IndexFile文件，按照消息产生的时间顺序排列
    - 每个IndexFile的结构是：文件头、hash槽、索引数据
    - hash槽的设计类似Java的hashmap只是不同的是只有链表没有红黑树
    - hash碰撞的时候，hash槽中保存的是最新的消息指针，因为用户一般关系的就是最新的数据
    - 链表中每个Entry结构是：消息key的hash值、消息的offset、时间差、链表下一个指针
    
    
    
    
#### 索引如何使用

- 基于位点查询消息
    - RocketMQ支持push和pull两种模式，push是基于poll实现的
    - push和poll都是基于拉去消息进行消费和提交位点的
    - 如果是基于位点的查询，过程就是取Consumer Queue中读取消息的位点，消息体大小和Tag的hash值。首先先根据TAG的hash值过滤，然后根据offset定位CommitLog
- 基于时间段查询
    - 还是借助ConsumerQueue索引查询，因为ConsumerQueue是按照时间顺序写的
- 按照key查询
    - 直接去CommitLog先定位hash槽、然后根据offset查询



