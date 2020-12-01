### mongo为啥好用？

- NoSql 的BSON存储
- 特有分布式ID
- 副本集：故障转移、快速复制、读写分离
  - 节点选举
- 分片集群
- 索引类型多种
  - Hash
  - 复合
  - 多键
  - background（属性）
  - 稀疏索引



## 事物支持

### 写事物

- writeConcern参数：决定一个写操作落到多少个节点上才算成功。
  - w参数：
    1：默认，要求写操作已经传播到独立的mongo实例或者副本集的Primary成员。
    0：不要求确认写操作。
    majority：要求写操作已经传播到大多数具有存储数据且具有投票权的成员。
  - j参数：
    true：写操作落到journal算成功。
    false：写操作落到内存算成功。

### 读事物

从哪里读？**位置**

- 由**readPreference参**数控制，取值如下：
- Primary（默认）：主节点，一般用户**下订单**。
- PrimaryPrefered：主节点优先，一般用户**查订单**。
- Secondary：从节点，一般用于**报表**。

### 隔离性的保障

由readConcern参数控制，取值如下：

- avaliable：读取所有可用的数据。

- local（默认）：读取所有可用并且属于当前分片的数据。

- majority：读取大多数节点上提交完成的数据，防止脏读。

  - 实现机制：节点使用MVCC机制维护多个版本，每个大多数节点确认过的版本数据作为一个快照，MongoDB通过维护多个快照实现链接不同的版本，快照维持在不再使用。、

- linearizable：线性化读取文档，保证之前所有写入的，能够保证出现网络分区的时候读取的安全，因为在读取的时候会检查所有节点。

- snapshot：读取最近快照中的数据。
  
### 如何安全的读写？

- readConcern设置为majority
- writeConcern设置为majority



### ACID的保障

- 原子性A=**4.0版本的复制集多表多行，4.2版本的分片集群多表多行，1.0版本的单表单文档**。
- 一致性C=writeConcern和readConcern。
- 隔离性I=readConcern。
- 持久性D=Journal和Replication。

## 副本集

### 复制原理

1. 一个修改**操作会被记录到oplog**，有一个**线程监听oplog**，如果有变动就会将这个变动应用到其他的数据库上。

2. 从节点在主节点上打开一个**tailable游标**，不断获取新加入的oplog，并在从库上**回放**。



### 节点选举

一个典型的复制集由3个以上具有投票权的节点构成，一个Primary接受写入操作和选举时投票，两个Secondary复制Primary节点数据和选举时投票。

具有投票权的节点两两发送心跳数据，主节点5次没有收到心跳被定义为失联状态。然后MongoDB基于Raft协议进行选举。
Replica Set中最多50个节点，具有投票权的节点最多只有7个。
影响选举的因素：整个集群必须有大多数节点存活。
被选举为主节点的条件：

- 能够与多数节点建立连接
- 具有较新的oplog
- 具有较高的优先级（可以配置）
  

## 分片集群

### 路由节点

- 提供集群的单一入口
- 转发应用端请求
- 选择合适的数据节点进行读写
- 合并多个数据额节点的返回结果
- 无状态
- **至少有两个**mongos节点

### 分片注意事项

- 分片直接数据不能重复
- 最多1024个
- 一个分片大小不要超过3T
- **如何选择片键？**
  - **基数**，基数越大越好，比如百家姓要比年龄基数大。
  - **写分布**，数据分布不均匀，比如学生的年龄。
  - **定向性**，mongos可以直接将数据定位。

### 1. mongo只查询某个字段

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



### 2. 只查询某些字段

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

 ### 3. 多表联查

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

