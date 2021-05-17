### Kafka zookeeper

Kafka 使用 ZooKeeper 来保存与分区和代理相关的元数据，并选举出一个代理作为集群控制器。不过，Kafka 开发团队想要消除对 Zookeeper 的依赖，这样就可以以更可伸缩和更健壮的方式来管理元数据，从而支持更多的分区，还能够简化 Kafka 的部署和配置。



所以 Apache Kafka 为什么要移除 Zookeeper 的依赖？Zookeeper 有什么问题？实际上，问题不在于 ZooKeeper 本身，而在于外部元数据管理的概念。