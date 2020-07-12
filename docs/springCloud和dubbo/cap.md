## CAP

- C 一致性：在不同机器上的数据同一时刻，都是同样的值，等同于访问同一个副本
- A可用性：集群某一个出现故障，集群整体是否还能响应客户端的读写请求
- P分区容错性：不同分区之间可能存在通信失败的情况
  - G1向G2发送消息，G2可能无法收到
  - 一般来说，分区容错无法避免，因此可以认为 CAP 的 P 总是成立。CAP 定理告诉我们，剩下的 C 和 A 无法同时做到。



### 为啥CAP不能共存？

1. 首先P肯定存在
2. 如果保障一致性，那么G1 写的时候，就要锁定G2的读写，执行完才放开，锁定期间G2没有可用性
3. 如果保障可用性，那么G1写的时候，G2就无法锁定
4. 所以不能同时满足



```java
- CA 单机服务
- CP  在保证一致性的前提下，肯定不能保证可用性，导致性能不高
- AP：不能保证一致性
```



### zookeeper遵循CP原则

- Apache Zookeeper 在设计时就紧遵CP原则，即任何时候对 Zookeeper 的访问请求能得到一致的数据结果，同时系统对网络分割具备容错性，但是 Zookeeper 不能保证每次服务请求都是可达的
- 从 Zookeeper 的实际应用情况来看，在使用 Zookeeper 获取服务列表时，如果此时的 Zookeeper 集群中的 Leader 宕机了，该集群就要进行 Leader 的选举，又或者 Zookeeper 集群中半数以上服务器节点不可用（例如有三个节点，如果节点一检测到节点三挂了 ，节点二也检测到节点三挂了，那这个节点才算是真的挂了），那么将无法处理该请求。所以说，Zookeeper 不能保证服务可用性。



## zookeper

Zookeeper 的数据模型是什么样子呢？它很像数据结构当中的树，也很像文件系统的目录

zookeper基本操作

- create、delete、setData写

- exists、 getChildren、getData读
- 访问的时候根据路径访问，如：getData(/root/xxx/xxx/a)

### ZK事件通知机制Watch

类似观察者模式

我们可以把 **Watch** 理解成是注册在特定 Znode 上的触发器。当这个 Znode 发生改变，也就是调用了 `create`，`delete`，`setData` 方法的时候，将会触发 Znode 上注册的对应事件，请求 Watch 的客户端会接收到异步通知。

具体交互过程如下：

- 客户端调用 `getData` 方法，`watch` 参数是 `true`。服务端接到请求，返回节点数据，并且在对应的哈希表里插入被 Watch 的 Znode 路径，以及 Watcher 列表。
- 当被 Watch 的 Znode 已删除，服务端会查找哈希表，找到该 Znode 对应的所有 Watcher，异步通知客户端，并且删除哈希表中对应的 Key-Value。

### zookeeper的作用

- 作为注册中心
- 实现分布式锁

### ZK服务注册与发现和Watch的关系

- 服务注册，其实就是把服务的IP地址和服务写入到ZK，比如xxx.xxx.service.UserServie：127.0.0.1:8080
- 服务发现：zookeeper通过getData(路径，其实就是服务名称)找到服务地址localhost:8080，而且要watch它
  - 找到之后，会把服务地址保存到ZKClient中
  - 第二次访问直接从ZKCli ent中寻找
- 监听的必要性：要监听它是否挂掉了，如果挂掉了，异步通知
  - 挂掉了之后，删除ZKClinet中存储的，然后重新寻找并存储新的地址

### ZK 分布式锁原理

- [分布式锁](./docs/秒杀相关/分布式锁/分布式锁.md)



### ZK部署单机、集群、伪集群

- 单机，挂掉了就不好了
- 集群
  - Zookeeper Service 集群是一主多从结构。
  - 在更新数据时，首先更新到主节点（这里的节点是指服务器，不是 Znode），再同步到从节点。
  - 在读取数据时，直接读取任意从节点。
  - 为了保证主从节点的数据一致性，Zookeeper 采用了 **ZAB 协议**，这种协议非常类似于一致性算法 **Paxos** 和 **Raft**
  - 投票算法https://www.jianshu.com/p/c2ced54736aa

## ZAB协议

ZAB协议的作用：集群崩溃和恢复（从新选举主节点）以及数据同步（广播实现）

Zookeeper Atomic Broadcast，有效解决了 Zookeeper 集群崩溃恢复，以及主从同步数据（广播实现）的问题。

-  集群崩溃：即主节点崩溃

### ZAB状态

- Looking ：选举状态。
- Following ：Follower 节点（从节点）所处的状态。
- Leading ：Leader 节点（主节点）所处状态。

### 最大ZXID

最大 ZXID 也就是节点本地的最新事务编号，包含 epoch 和计数两部分。epoch 是纪元的意思，相当于 Raft 算法选主时候的 term。

可以理解为最大自增事物ID



### ZAB 的崩溃恢复

[视频介绍](https://www.bilibili.com/video/BV1St411m7tV?p=8)

假如 Zookeeper 当前的主节点挂掉了，集群会进行崩溃恢复。ZAB 的崩溃恢复分成三个阶段：

**Leader election**

- 选举阶段，此时集群中的节点处于 Looking 状态。它们会各自向其他节点发起投票，投票当中包含自己的服务器 ID 和最新事务 ID（ZXID）。

接下来，节点会用自身的 ZXID 和从其他节点接收到的 ZXID 做比较，如果发现别人家的 ZXID 比自己大，也就是数据比自己新，那么就重新发起投票，投票给目前已知最大的 ZXID 所属节点。

```
A B C D四台从节点
A向BCD通信
A<B，A投B 
B<C ,B投C
C<D. C投D
此时A没有票，A落选，但是没有任何一个大于半数
B向D寻找，B<D,D有两票，超过半数，D当选
```

- 每次投票后，服务器都会统计投票数量，判断是否有某个节点得到半数以上的投票。如果存在这样的节点，该节点将会成为准 Leader，状态变为 Leading。其他节点的状态变为 Following。
- **Discovery发现阶段**

发现阶段，用于在从节点中发现最新的 ZXID 和事务日志。或许有人会问：既然 Leader 被选为主节点，已经是集群里数据最新的了，为什么还要从节点中寻找最新事务呢？

这是为了防止某些意外情况，比如因网络原因在上一阶段产生多个 Leader 的情况。

所以这一阶段，Leader 集思广益，接收所有 Follower 发来各自的最新 epoch 值。Leader 从中选出最大的 epoch，基于此值加 1，生成新的 epoch 分发给各个 Follower。

各个 Follower 收到全新的 epoch 后，返回 ACK 给 Leader，带上各自最大的 ZXID 和历史事务日志。Leader 选出最大的 ZXID，并更新自身历史日志

- **Synchronization**

同步阶段，把 Leader 刚才收集得到的最新历史事务日志，同步给集群中所有的 Follower。只有当半数 Follower 同步成功，这个准 Leader 才能成为正式的 Leader。（可以理解为，我成为了准主节点，但是我要通知其他人这个事情，只有超过半数的人知道，我才能当准节点）



自此，故障恢复正式完成。

### ZAB消息广播

**Broadcast**

ZAB 的数据写入涉及到 Broadcast （广播）阶段，简单来说，就是 Zookeeper 常规情况下更新数据的时候，由 Leader 广播到所有的 Follower。其过程如下：

- 客户端发出写入数据请求给任意 子节点Follower。
- Follower 子节点把写入数据请求转发给 Leader。
  - 读节点不负责写，把写的请求转发给主节点
- Leader 采用二阶段提交方式，先发送 Propose 广播给 子节点Follower。
  - 类似事物，先插入日志后commit才进行持久化
  - 先保留日志，准备提交
- Follower 接到 Propose 消息，写入日志成功后，返回 ACK 消息给 Leader。
  - 从节点写入日志
- Leader 接到半数以上ACK消息，返回成功给客户端，并且广播 Commit 请求给 Follower
  - 收到半数的ACK，主节点通知所有从节点，可以持久化了

### ZK的一致性：顺序一致性

ZAB 协议既不是强一致性，也不是弱一致性，而是处于两者之间的**单调一致性（顺序一致性）**。它依靠事务 ID 和版本号，保证了数据的更新和读取是有序的。

## Eureka、ZK、Nacos注册中心的区别

[推荐地址](https://www.jianshu.com/p/9b8a746e0d90)


|                           组件名称                           | 实现语言 | CAP  | 健康检查 |
| :----------------------------------------------------------: | :------: | :--: | :------: |
| [Eureka](https://links.jianshu.com/go?to=https%3A%2F%2Fgithub.com%2FNetflix%2Feureka) |  `Java`  |  AP  |   可配   |
| [Zookeeper](https://links.jianshu.com/go?to=http%3A%2F%2Fzookeeper.apache.org%2F) |  `Java`  |  CP  |   支持   |
| [Consul](https://links.jianshu.com/go?to=https%3A%2F%2Fwww.consul.io%2F) | `Golang` |  CP  |   支持   |
| [Nacos](https://links.jianshu.com/go?to=https%3A%2F%2Fnacos.io%2Fzh-cn%2F) |  `Java`  |  AP  |   支持   |



 

