

## MySQL MVCC
- [mvcc详解](./docs/mysql/Mysql关于MVCC.md)


## mysql写一个死锁

- [mvcc](./docs/mysql/写一个MySQL死锁的代码.md)



### MySql的一些规定

- 单行长度不能超过65535

### select * 的问题

- 增加网络开销和传输时间
  - 内容大了，自然开销也就大，传输时间也可能变大
- 如果是大型的varchar、text等会增加IO操作
  - 准确来说，长度超过 728 字节的时候，会先把超出的数据序列化到另外一个地方，因此读取这条记录会增加一次 io 操作。（MySQL InnoDB）

- 不能进行覆盖索引
  - 比如只需要索引字段和主键，则不需要回表
  - 如果是* ，则可能要进行回表查询