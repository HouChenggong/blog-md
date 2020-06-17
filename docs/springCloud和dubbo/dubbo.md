### dubbo实际开发中遇到的问题？

比如说：接口超时时间的配置

### dubbo新功能

2017.7阿里重启DUBBO

### dubbo2.7新功能



- 异步化改造
- 三大中心改造
- 服务治理增强

http://dubbo.apache.org/zh-cn/blog/dubbo-27-features.html



### gRpc支持

http://dubbo.apache.org/zh-cn/blog/index.html

- 添加REST接口调用

- 整合Nacos

- 异步化接口

  - Dubbo 2.7 中使用了 JDK1.8 提供的 `CompletableFuture` 原生接口对自身的异步化做了改进。`CompletableFuture` 可以支持 future 和 callback 两种调用方式，用户可以根据自己的喜好和场景选择使用，非常灵活。


## dubbo

### Dubbo提供了哪3个关键功能？
基于接口的远程调用

容错和负载均衡

自动服务注册和发现

### dubbo关键点有哪些？

- privider
- consumer
- Registry 服务注册和发现中心
- Monitor 统计服务调用次数和调用时间
- Container服务运行的容器

### dubbo服务注册流程

1. 服务容器负责启动，加载，运行服务提供者。
2. 服务提供者在启动时，向注册中心注册自己提供的服务。
3. 服务消费者在启动时，向注册中心订阅自己所需的服务。
4. 注册中心返回服务提供者地址列表给消费者，如果有变更，注册中心将基于长连接推送变更数据给消费者。
5. 服务消费者，从提供者地址列表中，基于软负载均衡算法，选一台提供者进行调用，如果调用失败，再选另一台调用。
6. 服务消费者和提供者，在内存中累计调用次数和调用时间，定时每分钟发送一次统计数据到监控中心。

### dubbo流程图

 ![](./img/dubbo-relation.jpg)

### dubbo注册中心

zookeeper 、redis、Multicast、Nacos、Simple

[注册中心分类](http://dubbo.apache.org/zh-cn/docs/user/references/registry/nacos.html)

### dubbo核心配置

| 标签                                                         | 用途         | 解释                                                         |
| ------------------------------------------------------------ | ------------ | ------------------------------------------------------------ |
| `<dubbo:service/>`                                           | 服务配置     | 用于暴露一个服务，定义服务的元信息，一个服务可以用多个协议暴露，一个服务也可以注册到多个注册中心 |
| `<dubbo:reference/>` [[2\]](http://dubbo.apache.org/zh-cn/docs/user/configuration/xml.html#fn2) | 引用配置     | 用于创建一个远程服务代理，一个引用可以指向多个注册中心       |
| `<dubbo:protocol/>`                                          | 协议配置     | 用于配置提供服务的协议信息，协议由提供方指定，消费方被动接受 |
| `<dubbo:application/>`                                       | 应用配置     | 用于配置当前应用信息，不管该应用是提供者还是消费者           |
| `<dubbo:module/>`                                            | 模块配置     | 用于配置当前模块信息，可选                                   |
| `<dubbo:registry/>`                                          | 注册中心配置 | 用于配置连接注册中心相关信息                                 |
| `<dubbo:monitor/>`                                           | 监控中心配置 | 用于配置连接监控中心相关信息，可选                           |
| `<dubbo:provider/>`                                          | 提供方配置   | 当 ProtocolConfig 和 ServiceConfig 某属性没有配置时，采用此缺省值，可选 |
| `<dubbo:consumer/>`                                          | 消费方配置   | 当 ReferenceConfig 某属性没有配置时，采用此缺省值，可选      |
| `<dubbo:method/>`                                            | 方法配置     | 用于 ServiceConfig 和 ReferenceConfig 指定方法级的配置信息   |
| `<dubbo:argument/>`                                          | 参数配置     | 用于指定方法参数配置                                         |

### 接口配置优先级


以 timeout 为例，下图显示了配置的查找顺序，其它 retries, loadbalance, actives 等类似：

- 方法级优先，接口级次之，全局配置再次之。
- 如果级别一样，则消费方优先，提供方次之。

其中，服务提供方配置，通过 URL 经由注册中心传递给消费方。

![](./img/dubbo-config-override.jpg)

**（建议由服务提供方设置超时，因为一个方法需要执行多长时间，服务提供方更清楚，如果一个消费方同时引用多个服务，就不需要关心每个服务的超时设置）。**



### 配置文件优先级

优先级从高到低：

- JVM -D参数，当你部署或者启动应用时，它可以轻易地重写配置，比如，改变dubbo协议端口；
- XML, XML中的当前配置会重写dubbo.properties中的；
- Properties，默认配置，仅仅作用于以上两者没有配置时。

### dubbo 启动检查

Dubbo 缺省会在启动时检查依赖的服务是否可用，不可用时会抛出异常，阻止 Spring 初始化完成，以便上线时，能及早发现问题，默认 `check="true"`。

可以通过 `check="false"` 关闭检查，比如，测试时，有些服务不关心，或者出现了循环依赖，必须有一方先启动。

另外，如果你的 Spring 容器是懒加载的，或者通过 API 编程延迟引用服务，请关闭 check，否则服务临时不可用时，会抛出异常，拿到 null 引用，如果 `check="false"`，总是会返回引用，当服务恢复时，能自动连上。

### 如何直连

- -D

```
java -Dcom.alibaba.xxx.XxxService=dubbo://localhost:20890
.properties
```

- XML

```
 <dubbo:reference id="xxxService"
 interface="com.alibaba.xxx.XxxService" url="dubbo://localhost:20890" />
```

- .properties

```
java -Ddubbo.resolve.file=xxx.properties

```

-xxxxxx.properties配置如下

```
com.alibaba.xxx.XxxService=dubbo://localhost:20890
```



```java
消费者上面加上注解 添加dubbo服务的url
@Reference(url = "127.0.0.1:20882")
```



```java

…
 
ReferenceConfig<XxxService> reference = new ReferenceConfig<XxxService>(); // 此实例很重，封装了与注册中心的连接以及与提供者的连接，请自行缓存，否则可能造成内存和连接泄漏
// 如果点对点直连，可以用reference.setUrl()指定目标地址，设置url后将绕过注册中心，
// 其中，协议对应provider.setProtocol()的值，端口对应provider.setPort()的值，
// 路径对应service.setPath()的值，如果未设置path，缺省path为接口名
reference.setUrl("dubbo://10.20.130.230:20880/com.xxx.XxxService"); 
 
```

###14.Dubbo配置来源有几种？分别是？

4种

- JVM System Properties，-D参数
- Externalized Configuration，外部化配置
- ServiceConfig、ReferenceConfig等编程接口采集的配置
- 本地配置文件dubbo.properties

### 如何禁用某个服务的启动检查？

```xml
<dubbo:reference interface = "com.foo.BarService" check = "false" />
```

### 负载均衡策略

Dubbo 提供了4种负载均衡实现，分别是

基于权重随机算法的 RandomLoadBalance、

基于最少活跃调用数算法的 LeastActiveLoadBalance、

基于 hash 一致性的 ConsistentHashLoadBalance，

以及基于加权轮询算法的 RoundRobinLoadBalance。

> 一致性 Hash，相同参数的请求总是发到同一提供者。
>
> 当某一台提供者挂时，原本发往该提供者的请求，基于虚拟节点，平摊到其它提供者，不会引起剧烈变动。

### 如何兼容老版本

多版本号(version)

当一个接口实现，出现不兼容升级时，可以用版本号过渡，版本号不同的服务相互间不引用。

可以按照以下的步骤进行版本迁移：

在低压力时间段，先升级一半提供者为新版本

再将所有消费者升级为新版本

然后将剩下的一半提供者升级为新版本

```xml
老版本服务提供者配置：
<dubbo:service interface="com.foo.BarService" version="1.0.0" />

新版本服务提供者配置：
<dubbo:service interface="com.foo.BarService" version="2.0.0" />

老版本服务消费者配置：
<dubbo:reference id="barService" interface="com.foo.BarService" version="1.0.0" />

新版本服务消费者配置：
<dubbo:reference id="barService" interface="com.foo.BarService" version="2.0.0" />

如果不需要区分版本，可以按照以下的方式配置：
<dubbo:reference id="barService" interface="com.foo.BarService" version="*" />
```

### 集群容错方式

```xml
Failover Cluster
失败自动切换，当出现失败，重试其它服务器。通常用于读操作，但重试会带来更长延迟。可通过 retries="2" 来设置重试次数(不含第一次)。

重试次数配置如下：
<dubbo:service retries="2" />
或
<dubbo:reference retries="2" />
或
<dubbo:reference>
    <dubbo:method name="findFoo" retries="2" />
</dubbo:reference>

Failfast Cluster
快速失败，只发起一次调用，失败立即报错。通常用于非幂等性的写操作，比如新增记录。

Failsafe Cluster
失败安全，出现异常时，直接忽略。通常用于写入审计日志等操作。

Failback Cluster
失败自动恢复，后台记录失败请求，定时重发。通常用于消息通知操作。

Forking Cluster
并行调用多个服务器，只要一个成功即返回。通常用于实时性要求较高的读操作，但需要浪费更多服务资源。可通过 forks="2" 来设置最大并行数。

Broadcast Cluster
广播调用所有提供者，逐个调用，任意一台报错则报错 [2]。通常用于通知所有提供者更新缓存或日志等本地资源信息。

集群模式配置
按照以下示例在服务提供方和消费方配置集群模式
<dubbo:service cluster="failsafe" />
或
<dubbo:reference cluster="failsafe" />
```

### dubbo有哪些协议

- dubbo://(推荐)
- rmi://
- hessian://
- http://
- webservice://
- thrift://
- memcached://
- redis://
- rest://
- grpc

Dubbo 自 2.7.5 版本开始支持 gRPC 协议，对于计划使用 HTTP/2 通信，或者想利用 gRPC 带来的 Stream、反压、Reactive 编程等能力的开发者来说， 都可以考虑启用 gRPC 协议。

http://dubbo.apache.org/zh-cn/docs/user/references/protocol/gRPC.html

### dubbo 通信框架

netty

### dubbo默认端口号

- dubbo:20880
- http:80
- hessian:80
- rmi:80

 

### Dubbo默认序列化框架?其他的你还知道？

- dubbo协议缺省为hessian2
- rmi协议缺省为java
- http协议缺省为json

### 一个服务多重实现，如何处理？

可以用group分组，服务提供方和消费放都指定同一个group。

### dubbo调用是阻塞的？还是其它的？

默认是同步等待结果阻塞的，同时也支持异步调用。

Dubbo 是基于 NIO 的非阻塞实现并行调用，客户端不需要启动多线程即可完成并行调用多个远程服务，相对多线程开销较小，异步调用会返回一个 Future 对象。

### dubbo分布式追踪方案？

- Zipkin
- Pinpoint
- SkyWalking

### io线程池大小默认？

cpu个数 + 1

### dubbo协议适用场景

采用单一长链接和NIO异步通讯，适用于小数量大并发的服务调用，以及服务消费者机器数远大于服务提供者机器数的情况。

不适合传送大数据量的服务，比如传文件，传视频等，除非请求量很低。

### 自动剔除什么原理

zookeeper临时节点，会话保持原理。



