### 1、mongo为啥好用？

- NoSql 的BSON存储，可以直接存储JSON和数组等复杂结构
  - 虽然mysql高版本也支持JSON
- 特有分布式ID
- 查询速度快
  - 数据可以优先从内存中返回，但是对内存的占用率比较大
  - 分片集群，搜索压力负载均衡
- 存储速度快
  - 写操作MongoDB比传统数据库快的根本原因是Mongo使用的内存映射技术 － 写入数据时候只要在内存里完成就可以返回给应用程序，这样并发量自然就很高。而保存到硬体的操作则在后台异步完成
- 副本集：故障转移、快速复制、读写分离
  - 节点选举
- 分片集群，支持横向扩展
- 分片+副本结合，保证了数据分片到多台机器的同时，也确保每个分片都有相应的备份
- 已经支持分布式事务，ACID得到了保障，后面详细介绍
- 索引类型多种，参见：[索引官网](https://docs.mongodb.com/manual/core/multikey-index-bounds/)

  - 单字段索引即普通索引
  - 复合字段索引
  - 多键索引，即数组索引

  - Hash
  - 通配符索引：如
    - db.userData.createIndex( { "userMetadata.$**" : 1 } )
    - db.userData.find({ "userMetadata.age" : { $gt : 30 } })
  - 地理空间索引
- 索引属性也有多种，参考：[索引属性](https://docs.mongodb.com/manual/core/index-ttl/)
  - TTL过期索引属性：数据过期会被删除，而不是索引被删除，切记！！
  - 唯一属性
  - 部分索引属性：比如我们只在id>1000上才创建索引
  - 不区分大小写属性
  - 隐藏索引属性：和mysql的隐藏索引效果一致
  - 稀疏索引属性：跳过缺少字段或者字段为空的数据建立索引，功能类似部分索引，目前已建议优先选择部分索引



### 2、事物支持

从4.2版本还是支持分布式事务，但至少要部署3太机器

#### 写事物参数控制

- writeConcern参数：决定一个写操作落到多少个节点上才算成功。
  - w参数：
    1：默认，要求写操作已经传播到独立的mongo实例或者副本集的Primary成员。
    0：不要求确认写操作。
    majority：要求写操作已经传播到大多数具有存储数据且具有投票权的成员。
  - j参数：
    true：写操作落到journal算成功。
    false：写操作落到内存算成功。

#### 读事物参数控制

从哪里读？**位置**

- 由**readPreference参**数控制，取值如下：
- Primary（默认）：主节点，一般用户**下订单**。
- PrimaryPrefered：主节点优先，一般用户**查订单**。
- Secondary：从节点，一般用于**报表**。



#### 如何安全的读写？

- readConcern设置为majority
- writeConcern设置为majority



### 3、事务ACID的保障

#### 一致性问题实现原理

mongo提供了2种方式可供选择，一种是强一致性，一种是最终一致性

##### 强一致性的实现方式

强一致性的保证，要求所有数据节点对同一个key值在同一时刻有同样的value值。虽然实际上可能某些节点存储的值是不一样的，但是作为一个整体，当客户端发起对某个key的数据请求时，整个集群对这个key对应的数据会达成一致

##### 举例说明

假设在我们的集群中，一个数据会被备份到N个结点。这N个节点中的某一个可能会扮演协调器的作用。它会保证每一个数据写操作会在成功同步到W个节点后才向客户端返回成功。而当客户端读取数据时，需要至少R个节点返回同样的数据才能返回读操作成功。而NWR之间必须要满足下面关系：R＋W>N

- 假设我们有3个节点ABC，如果要修改ID=1，age=10的记录，让其age=20,我们设定W=2，这样只要AB两个节点成功了，就返回事务成功。

- 读的时候，同时向ABC三个节点发送请求，这个时候C节点的数据还是旧值

- 如果设定R=1，刚好又是C节点先返回，那么就得到一个错误的值

- 如果设定R=2，C和A返回，发现数据不一致

  所以要保证强一致性，比如设定W=2，R=2，W+R>N 

- 而mongo发现数据不一致后会采取相应的行动来保证数据一致，不然一直不一致不就崩了

#### 最终一致性的实现方式

W+R>=N即可

#### 隔离性与并发实现原理

- 并发支持：
  - MVCC版本控制：WiredTiger为操作提供数据的时间点快照。快照提供了内存中数据的一致视图。
  - 类似Mysql一样的S、X、IS、IX等多种模式
- 隔离保证。
  - 根据读取的关注点，客户端可以在[持久](https://docs.mongodb.com/manual/reference/glossary/#std-term-durable)写入之前看到写入结果。要控制是否可以回滚读取的数据，客户端可以使用该`readConcern`选项。
  - mongo提供了未提交读和提交读两种方式

由readConcern参数控制，取值如下：

- avaliable：读取所有可用的数据。

- local（默认）：读取所有可用并且属于当前分片的数据。

- majority：读取大多数节点上提交完成的数据，防止脏读。

  - 实现机制：节点使用MVCC机制维护多个版本，每个大多数节点确认过的版本数据作为一个快照，MongoDB通过维护多个快照实现链接不同的版本，快照维持在不再使用。、

- linearizable：线性化读取文档，保证之前所有写入的，能够保证出现网络分区的时候读取的安全，因为在读取的时候会检查所有节点。

- snapshot：读取最近快照中的数据。

#### 持久性实现原理

- mongo 的WiredTiger引擎将预写日志（即日志）与[检查点](https://docs.mongodb.com/manual/core/wiredtiger/#std-label-storage-wiredtiger-checkpoints)结合使用， 以确保数据的持久性。
- 副本集之间的复制保证持久性



### 4、副本集

#### 副本集之间复制原理

1. 一个修改**操作会被记录到oplog**，有一个**线程监听oplog**，如果有变动就会将这个变动应用到其他的数据库上。

2. 从节点在主节点上打开一个**tailable游标**，不断获取新加入的oplog，并在从库上**回放**。



#### 副本集节点选举

一个典型的复制集由3个以上具有投票权的节点构成，一个Primary接受写入操作和选举时投票，两个Secondary复制Primary节点数据和选举时投票。

具有投票权的节点两两发送心跳数据，主节点5次没有收到心跳被定义为失联状态。然后MongoDB基于Raft协议进行选举。
Replica Set中最多50个节点，具有投票权的节点最多只有7个。
影响选举的因素：整个集群必须有大多数节点存活。
被选举为主节点的条件：

- 能够与多数节点建立连接
- 具有较新的oplog
- 具有较高的优先级（可以配置）

### 5、分片集群

#### 路由节点

- 提供集群的单一入口
- 转发应用端请求
- 选择合适的数据节点进行读写
- 合并多个数据额节点的返回结果
- 无状态

Mongos作为Sharded cluster的访问入口，所有的请求都由mongos来路由、分发、合并，这些动作对客户端driver透明，用户连接mongos就像连接mongod一样使用。

#### 分片注意事项

- 分片直接数据不能重复

- 最多1024个

- 一个分片大小不要超过3T

- **如何选择片键？**
  
  - 分片键必须有索引
  
  - **基数**，基数越大越好，比如百家姓要比年龄基数大。
  - **写分布**，数据分布不均匀不太好，比如学生的年龄。
  - **定向性**，mongos可以直接将数据定位。
#### 常见分片键

- 利用具有范围的字段分片
- 利用Hash某个字段分片

#### 为啥在新的分片集群中，所有数据都保留着一个分片上？

您的群集必须具有足够的数据才能进行分片。分片的工作原理是在分片之间迁移块，直到每个分片具有大致相同数量的块。

默认块大小为64 MB。在集群中的大块不平衡量超过[迁移阈值](https://docs.mongodb.com/manual/core/sharding-balancer-administration/#std-label-sharding-migration-thresholds)之前，MongoDB不会开始迁移 。此行为有助于防止不必要的块迁移，因为这可能会降低整个群集的性能。

如果您刚刚部署了分片群集，请确保您有足够的数据以使分片有效。如果没有足够的数据来创建八个以上的64 MB块，则所有数据将保留在一个分片上。降低[块大小](https://docs.mongodb.com/manual/core/sharding-data-partitioning/#std-label-sharding-chunk-size)设置，或向集群添加更多数据。

作为一个相关问题，系统将仅在插入或更新时拆分块，这意味着，如果您配置分片并且不继续执行插入和更新操作，则数据库将不会创建任何块。您可以等待，直到应用程序[手动](https://docs.mongodb.com/manual/tutorial/split-chunks-in-sharded-cluster/)插入数据*或* [拆分块](https://docs.mongodb.com/manual/tutorial/split-chunks-in-sharded-cluster/)。

最后，如果分片密钥的[基数](https://docs.mongodb.com/manual/core/sharding-shard-key/#std-label-sharding-shard-key-cardinality)较低，则MongoDB可能无法在数据之间创建足够的拆分。

#### 会不会出现一个分片在一个分片集群中收到不成比例的流量？

在某些情况下会！

- 在几乎所有情况下，这都是分片密钥导致的，该分片密钥无法有效地允许[写入缩放](https://docs.mongodb.com/manual/sharding/#std-label-sharding-shard-key-write-scaling)。
- 您也可能有“热块”。在这种情况下，您可以通过拆分然后迁移这些块的一部分来解决问题
- 在最坏的情况下，您可能必须考虑重新分拆数据并[选择其他](https://docs.mongodb.com/manual/core/sharding-shard-key/#std-label-sharding-internals-choose-shard-key)分片[密钥](https://docs.mongodb.com/manual/core/sharding-shard-key/#std-label-sharding-internals-choose-shard-key) 来更正此模式。

#### 什么阻止了分片群平衡？

1. 可能集群是新部署的，数据仍然保留着耽搁分片上
2. 最初是平衡的，但是后来不平衡了
   - 删除了大量的数据导致的
   - 分片[密钥](https://docs.mongodb.com/manual/reference/glossary/#std-term-shard-key)具有低[基数，](https://docs.mongodb.com/manual/core/sharding-shard-key/#std-label-sharding-shard-key-cardinality) 并且MongoDB无法进一步拆分块。
   - 数据集增长速度超过了平衡器可以在集群中分布数据的速度
     - 网络联通性能差，导致迁移需要很长时间
     - 写操作不均匀，分片键选择有问题
     - 数据增长确实太快了



### 6、mongo的缺点

- 如果要支持事务，必须至少部署3台机器
- mongo内存占用率特别高，参考[官网](https://docs.mongodb.com/manual/core/wiredtiger/#std-label-wiredtiger-RAM)
  - 因为mongo会将最近使用的数据保存在RAM中。如果您已经为查询创建了索引，并且您的工作数据集适合RAM，那么MongoDB将从内存中提供所有查询。
  - 但是mongo并不会缓存结果
  - 内存计算公式：从MongoDB 3.4开始，默认的WiredTiger内部缓存大小是以下两者中的较大者：**当然这个可以自己配置如果觉得太大**
    - 50％（RAM-1 GB），或
    - 256 MB。
    例如，在总共有4GB RAM的系统上，WiredTiger缓存将使用1.5GB RAM（`0.5 * (4 GB - 1 GB) = 1.5 GB`）。相反，一个拥有1.25 GB RAM的系统将为WiredTiger缓存分配256 MB，因为这是总RAM的一半以上减去1 GB（`0.5 * (1.25 GB - 1 GB) = 128 MB < 256 MB`）。
- CPU占用率高
  - 使用WiredTiger引擎，MongoDB支持所有集合和索引的压缩。压缩可以最大程度地减少存储使用量，但会增加CPU的开销。

### 7、mongo常用查询

#### 1. mongo只查询某个字段

```java
https://www.jianshu.com/p/24a44c4c7651
或者如下：
Query query = new Query();
query.addCriteria(Criteria.where("status").is(3));
query.skip(skipNumber);
query.limit(pageSize);
query.fields().include("name").include("status");
return mongoTemplate.find(query, CompanyInformation.class);


```



#### 2. 只查询某些字段

1. 查询全部字段，根据条件查询

```
List<CodeRulePackage> getAllByConfigFatherIdAndShow
(String configFatherId, Boolean show);
```

2.  只查询几个字段，用对象查询

其中PackageSelect是只有id，custom,customServert三个字段的对象

```
    @Query(value = "{ 'configFatherId' : ?0 ,'show' : ?1}", fields = "{ 'id' : 1, 'custom' : 1,'customServert':1}")
    List<PackageSelect> getAllOnlyIdByConfigFatherIdAndShow(String configFatherId, Boolean show);
    或者直接查询：
    
 @Query(value = "{ 'configFatherId' : ?0 ,'show' : ?1}")
   List<PackageSelect> getAllOnlyIdByConfigFatherIdAndShow(String configFatherId, Boolean show);
```

3. 用map、List查询都不可以，因为查询的结果是JSON

- 如下只查询id,app_id,timestamp三个字段，其中ID默认查询

```java
db.xxxx.find(
    {"app_id":96,"knowledge_ori_id": {"$exists": true},
     "timestamp": { "$gte": 1610035200000,"$lte" :1610121600000}
    },
    { "app_id" : 1,
     "timestamp" : 1
    }
  )
```



 #### 3. 多表联查

问题1：objectId不能和string联查，解决方案是：

```sql
collection.aggregate([
  { 
    $addFields: { "_id": { "$toString": "$_id" } }
  },
  {
    $lookup: {
      from: "category",
      localField: "_id",
      foreignField: "mId",
      as: "categories"
    }
  }
])
```

```java
db.getCollection("xxxx").aggregate({"$group":{"_id":
        {"user_name": "$user_name"}}
})
   
```

#### 时间戳聚合查询某一天的数据

```java
 {
    "_id": {"$oid": "5ff83089c3db080017fdc968"},
    "app_id": 96,
    "timestamp": 1610100873416
  },
  {
    "_id": {"$oid": "5ff8303ac3db080017fdc966"}, 
    "app_id": 96,
    "timestamp": 1610100794510
  }
```



#### 聚合查询SQL：

比如我想查询的SQL是：

```sql
select  day ,count(*) from xxx where app_id =xxx group by app_id , day
```

在mongo中将时间戳转化为时间的SQL如下：

#### mongo将时间戳转为日期格式

```java
db.xxx.aggregate(
   [
     {
       $project: {
          timestamp: 1,
          app_id: 1,
          day: {$dateToString: {format: "%Y-%m-%d %H:%M:%S:%L", date:{"$add":[new Date(0),"$timestamp"]}}},
          day8: {$dateToString: {format: "%Y-%m-%d %H:%M:%S:%L", date:{"$add":[new Date(0),"$timestamp",28800000]}}}
       }
     }
   ]
)
```

转换的结果如下：day8是多个8个小时的结果

```java
  {
    "_id": {"$oid": "5fb12ea4c33c7400175b9cb5"},
    "app_id": 45,
    "day": "2020-10-23 10:02:12:461",
    "day8": "2020-10-23 18:02:12:461",
    "timestamp": 1603447332461
  },
  {
    "_id": {"$oid": "5fb12ea4c33c7400175b9cb6"},
    "app_id": 45,
    "day": "2020-10-23 10:02:12:503",
    "day8": "2020-10-23 18:02:12:503",
    "timestamp": 1603447332503
  }
```

#### match和project组合查询

```json
db.xxxx.aggregate(
    {$match:
         {  "app_id":96,
            "timestamp": {"$exists": true},
            "timestamp": { "$gte": 1610035200000,"$lte" :1610121600000}
         }
     },
     {
       $project: {
          timestamp: 1,
         	app_id: 1,
          day: {$dateToString: {format: "%Y-%m-%d %H:%M:%S:%L", date:{"$add":[new Date(0),"$timestamp"]}}},
          day8: {$dateToString: {format: "%Y-%m-%d %H:%M:%S:%L", date:{"$add":[new Date(0),"$timestamp",28800000]}}}
       }
    }
)
```

- 查询的结果如下：

```json
{
    "_id": {"$oid": "5ff83089c3db080017fdc968"},
    "app_id": 96,
    "day": "2021-01-08 10:14:33:416",
    "day8": "2021-01-08 18:14:33:416",
    "timestamp": 1610100873416
  },
  {
    "_id": {"$oid": "5ff83089c3db080017fdc967"},
    "app_id": 96,
    "day": "2021-01-08 10:14:33:393",
    "day8": "2021-01-08 18:14:33:393",
    "timestamp": 1610100873393
  }
```

#### match、project、group 组合查询

```java
db.scsa_chat_content.aggregate(
    {$match:
         {  "app_id":96,
            "timestamp": {"$exists": true},
            "timestamp": { "$gte": 0,"$lte" :1610121600000}
         }
     },
     {
       $project: {
          app_id: 1,
          day: {$dateToString: {format: "%Y-%m-%d", date:{"$add":[new Date(0),"$timestamp"]}}},
          day8: {$dateToString: {format: "%Y-%m-%d", date:{"$add":[new Date(0),"$timestamp",28800000]}}}
       }
    },
    { $group :
         { "_id" : { "app_id" : "$app_id", "day" : "$day" }}
     }
)
```

结果如下：发现不是列表结构

```java
 {
    "_id": {
      "app_id": 96,
      "day": "2020-12-18"
    }
  },
  {
    "_id": {
      "app_id": 96,
      "day": "2021-01-08"
    }
  }
```



#### 对match project group结果再次用project封装

```json
db.scsa_chat_content.aggregate(
    {$match:
         {  "app_id":96,
            "timestamp": {"$exists": true},
            "timestamp": { "$gte": 0,"$lte" :1610121600000}
         }
     },
     {
       $project: {
          app_id: 1,
          day: {$dateToString: {format: "%Y-%m-%d", date:{"$add":[new Date(0),"$timestamp"]}}},
          day8: {$dateToString: {format: "%Y-%m-%d", date:{"$add":[new Date(0),"$timestamp",28800000]}}}
       }
    },
    { $group :
         { "_id" : { "app_id" : "$app_id", "day" : "$day" }}
     },
     { "$project" : { "app_id" : "$_id.app_id", "day" : "$_id.day"} }
)
```

现在才有列表结构，结果如下：

```json
  {
    "_id": {
      "app_id": 96,
      "day": "2020-12-18"
    },
    "app_id": 96,
    "day": "2020-12-18"
  },
  {
    "_id": {
      "app_id": 96,
      "day": "2021-01-08"
    },
    "app_id": 96,
    "day": "2021-01-08"
  }
```

#### mongo count limit skip

```java
db.xxx.aggregate(
    {$match:
         {  "app_id":96,
            "timestamp": {"$exists": true},
            "timestamp": { "$gte": 0,"$lte" :1610121600000}
         }
     },
     {
       $project: {
          app_id: 1,
          day: {$dateToString: {format: "%Y-%m-%d", date:{"$add":[new Date(0),"$timestamp"]}}},
          day8: {$dateToString: {format: "%Y-%m-%d", date:{"$add":[new Date(0),"$timestamp",28800000]}}}
       }
    },
    { $group :
         { "_id" : { "app_id" : "$app_id", "day" : "$day" }, oneDayCount :{"$sum":1}}
     },
     { "$project" : { "app_id" : "$_id.app_id", "day" : "$_id.day", "acount":"$oneDayCount"} }
)
```

查询的结果如下：发现有了oneDayCount

```json
  {
    "_id": {
      "app_id": 96,
      "day": "2020-12-18"
    },
    "acount": 30,
    "app_id": 96,
    "day": "2020-12-18"
  },
  {
    "_id": {
      "app_id": 96,
      "day": "2021-01-08"
    },
    "acount": 55,
    "app_id": 96,
    "day": "2021-01-08"
  }
```



#### Java mongo聚合API

- 聚合查询如下：

```java
private AggregationResults<XXXDTO> getDayAggregation() {
        // 现在之前的数据，可以根据场景自由限定
        Criteria query = Criteria.where("timestamp").gte(0L).lte(System.currentTimeMillis());
        query.and("app_id").is(96);
   			query.and("timestamp").exists(true);
        Aggregation agg = Aggregation.newAggregation(
                // 第二步：sql where 语句筛选符合条件的记录
                Aggregation.match(query),
                Aggregation.project("app_id", "timestamp")
                        .andExpression("{$dateToString: {date: { $add: {'$timestamp', [0]} }, format: '%Y%m%d'}}", new Date(28800000)).as("oneDay"),
                // 第三步：分组条件，设置分组字段
                Aggregation.group("app_id", "day").count().as("oneDayCount"),
                // 第四部：排序（根据某字段排序 倒序）
                Aggregation.skip(0),
                // 第五步：数量(分页)
                Aggregation.limit(100),
                Aggregation.project("app_id", "oneDay", "oneDayCount")
        );
        AggregationResults<XXXDTO> results = mongoTemplate.aggregate(agg, XXX.class, DayChatContentCountDTO.class);
        log.info("query   sql is : [{}],", agg.toString());
        return results;
    }
```

上诉给了时间戳添加了8个小时，但是因为我们存储的是时间戳所以不需要添加8个小时，如果是其它的时间格式可以添加8个小时

```java
private AggregationResults<XXXDTO> getDayAggregation() {
        // 现在之前的数据，可以根据场景自由限定
        Criteria query = Criteria.where("timestamp").gte(0L).lte(System.currentTimeMillis());
        query.and("app_id").is(96);
   			query.and("timestamp").exists(true);
        Aggregation agg = Aggregation.newAggregation(
                // 第二步：sql where 语句筛选符合条件的记录
                Aggregation.match(query),
                Aggregation.project("app_id", "timestamp")
                        .andExpression("{$dateToString: {date: { $add: {'$timestamp', [0]} }, format: '%Y%m%d'}}", new Date(0)).as("oneDay"),
                // 第三步：分组条件，设置分组字段
                Aggregation.group("app_id", "day").count().as("oneDayCount"),
                // 第四部：排序（根据某字段排序 倒序）
                Aggregation.skip(0),
                // 第五步：数量(分页)
                Aggregation.limit(100),
                Aggregation.project("app_id", "oneDay", "oneDayCount")
        );
        AggregationResults<XXXDTO> results = mongoTemplate.aggregate(agg, XXX.class, DayChatContentCountDTO.class);
        log.info("query   sql is : [{}],", agg.toString());
        return results;
    }
```

查询日志如下：

```json
[
    {
        "aggregate":"__collection__",
        "pipeline":[
            {
                "$match":{
                    "timestamp":{
                        "$gte":{
                            "$numberLong":"0"
                        },
                        "$lte":{
                            "$numberLong":"1610104027084"
                        }
                    },
                    "appId":96,
                    "timestamp":{
                        "$exists":true
                    }
                }
            },
            {
                "$project":{
                    "appId":1,
                    "timestamp":1,
                    "oneDay":{
                        "$dateToString":{
                            "date":{
                                "$add":[
                                    "$timestamp",
                                    {
                                        "$date":28800000
                                    }
                                ]
                            },
                            "format":"%Y%m%d"
                        }
                    }
                }
            },
            {
                "$group":{
                    "_id":{
                        "appId":"$appId",
                        "oneDay":"$oneDay"
                    },
                    "oneDayCount":{
                        "$sum":1
                    }
                }
            },
            {
                "$skip":{
                    "$numberLong":"0"
                }
            },
            {
                "$limit":{
                    "$numberLong":"100"
                }
            },
            {
                "$project":{
                    "appId":"$_id.appId",
                    "oneDay":"$_id.oneDay",
                    "oneDayCount":1
                }
            }
        ]
    }
]
```

查询结果如下：发现符合结果

```java

结果如下：将时间戳改为了日期
XXXDTO(appId=96, day=20201125, oneDayCount=14)
XXXDTO(appId=96, day=20201218, oneDayCount=15)
XXXDTO(appId=96, day=20210108, oneDayCount=27)
```





### 8、mongo Object ID问题

1、mongo 插入数据数据的时候ID是我们不用去处理的，只要调用mongoTemplate.save（）方法，ID问题mongo会自动插入

2、但是我们想要在数据插入之前就手动生成mongo的ID，这个时候我们可以用UUID或者雪花ID

3、如果是UUID或者其它ID，在我们用ID排序的时候会报错，报错信息如下

```
ObjectId id = new ObjectId(xxxId); query.addCriteria("id").lt(id));  //invalid hexadecimal representation of an objectid
```

意思就是如果我们用UUID，就不能用范围查询

#### 2、invalid hexadecimal representation of an objectid

我们想用ID做范围查询，我们又想在数据插入之前获取ID，这个时候怎么办呢？难道想让我监听mongo提前获取ID吗？这下吗有一篇这个文章

https://blog.csdn.net/qq_16313365/article/details/72781469

但是太麻烦，我们下面用一个简单的方式解决

#### 解决方案

```
String questionId = ObjectId.get().toString();
然后我们使用的时候直接使用即可
ObjectId id = new ObjectId(questionId);
query.addCriteria("id").lt(id));
```



### 9、Java中直接指定索引

```java
@Document(collection = "user")
@Data
@CompoundIndexes({
        @CompoundIndex(name = "idx_app_id_username_chat_time", def = "{'app_id': 1, 'user_name': 1,'chat_time':1}", unique = true)
})
public class User {

    @Id
    private String id;

    @Field("user_name")
    private String userName;
  
    @Field("app_id")
    private Long appId;

    @Field("chat_time")
    private Long chatTime;
```