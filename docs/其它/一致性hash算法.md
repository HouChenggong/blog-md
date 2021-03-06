# 一致性hash算法

https://mp.weixin.qq.com/s/1vbMieS0uOi9Albz50Woig

## 一致性hash

一致性Hash通过构建环状的Hash空间代替线性Hash空间的方法解决了这个问题

整个Hash空间被构建成一个首尾相接的环，使用一致性Hash时需要进行两次映射。

第一次，给每个节点（集群）计算Hash，然后记录它们的Hash值，这就是它们在环上的位置。

第二次，给每个Key计算Hash，然后沿着顺时针的方向找到环上的第一个节点，就是该Key储存对应的集群。

可以看到，当节点被删除时，其余节点在环上的映射不会发生改变，只是原来打在对应节点上的Key现在会转移到顺时针方向的下一个节点上去。增加一个节点也是同样的，最终都只有少部分的Key发生了失效。不过发生节点变动后，整体系统的压力已经不是均衡的了，下文中提到的方法将会解决这个问题。

### 一致性hash带来的问题

- 数据倾斜

如果节点的数量很少，而hash环空间很大（一般是 0 ~ 2^32），直接进行一致性hash上去，大部分情况下节点在环上的位置会很不均匀，挤在某个很小的区域。最终对分布式缓存造成的影响就是，集群的每个实例上储存的缓存数据量不一致，会发生严重的数据倾斜。

- 缓存雪崩

如果每个节点在环上只有一个节点，那么可以想象，当某一集群从环中消失时，它原本所负责的任务将全部交由顺时针方向的下一个集群处理。例如，当group0退出时，它原本所负责的缓存将全部交给group1处理。这就意味着group1的访问压力会瞬间增大。设想一下，如果group1因为压力过大而崩溃，那么更大的压力又会向group2压过去，最终服务压力就像滚雪球一样越滚越大，最终导致雪崩。

### 怎么解决？虚拟节点

解决上述两个问题最好的办法就是扩展整个环上的节点数量，因此我们引入了虚拟节点的概念。一个实际节点将会映射多个虚拟节点，这样Hash环上的空间分割就会变得均匀。

同时，引入虚拟节点还会使得节点在Hash环上的顺序随机化，这意味着当一个真实节点失效退出后，它原来所承载的压力将会均匀地分散到其他节点上去。



在引入足够多的虚拟节点后，一致性hash还是能够比较完美地满足负载均衡需要的。

- 都是优点？有没有缺点

Hash环上的节点非常多或者更新频繁时，查找性能会比较低下



## redis怎么实现分布式缓存的？

### redis使用hashSlot实现Key值的均匀分布和实例的增删管理

类似于Hash环，Redis Cluster采用HashSlot来实现Key值的均匀分布和实例的增删管理

首先默认分配了16384个Slot（这个大小正好可以使用2kb的空间保存），每个Slot相当于一致性Hash环上的一个节点。接入集群的所有实例将均匀地占有这些Slot，而最终当我们Set一个Key时，使用`CRC16(Key) % 16384`来计算出这个Key属于哪个Slot，并最终映射到对应的实例上去。

### redisP2P节点寻找

节点A 1365-5460
 节点B 6827-10922
 节点C 12288-16383
 节点D 0-1364,5461-6826,10923-12287

每个节点都保存有完整的`HashSlot - 节点`映射表，也就是说，每个节点都知道自己拥有哪些Slot，以及某个确定的Slot究竟对应着哪个节点。

无论向哪个节点发出寻找Key的请求，该节点都会通过`CRC(Key) % 16384`计算该Key究竟存在于哪个Slot，并将请求转发至该Slot所在的节点。

总结一下就是两个要点：**映射表**和**内部转发**