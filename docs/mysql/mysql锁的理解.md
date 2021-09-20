# MySQL锁的理解

![](.\img\MySQL索引.png)

### 共享、排他锁

- 共享锁：S Lock
  - 不同的事物可以兼容，也就是可以共同读一行数据
- 排他锁：X Lock
  - 不兼容
- 但是两者都属于行锁，兼容指的是对同一行记录的兼容情况

|      | X      | S      |
| ---- | ------ | ------ |
| X    | 不兼容 | 不兼容 |
| S    | 不兼容 | 兼容   |

### 意向锁

为了兼容同一个事物对行锁和表锁的兼容能力，Innodb引入意向锁（Intention Lock）。意向锁将锁的对象分为多个层次，意味着事物希望在更细的维度上进行加锁。

如果把上锁的对象看作一棵树，如果我们对一行记录加锁，其实需要对数据库A，表B，页C添加意向锁IX，最后对记录添加X锁

Innodb支持的意向锁比较简单：只有表级别的意向锁

- 意向共享锁：IS Lock 事物想要获取一张表中某几行的共享锁
- 意向排他锁：IX Lock事物想要糊的一张表中某几行的排他锁

由于InnoDB支持的是行级别的锁，因此意向锁其实不会阻塞全表扫描之外的任何请求，故锁兼容性如下：

|      | IS   | IX   | S    | X    |
| ---- | ---- | ---- | ---- | ---- |
| IS   | 兼容 | 兼容 | 兼容 |      |
| IX   | 兼容 | 兼容 |      |      |
| S    | 兼容 |      | 兼容 |      |
| X    |      |      |      |      |

- X锁和任何锁都不兼容
- S锁和IS、IS兼容
- IX和IS、IX兼容，和S、X不兼容
- IS和IS、IX、S兼容，但是不和X兼容

```mysql
## 查看当前锁请求的信息
show engine innodb status\G;
```

| 字段名                | 说明                                         | 举例                                                         |
| --------------------- | -------------------------------------------- | ------------------------------------------------------------ |
| trx_id                | InnoDB存储的唯一事物ID                       |                                                              |
| trx_state             | 当前事物的状态                               |                                                              |
| trx_started           | 事物开始的时间                               |                                                              |
| trx_requested_lock_id | 等待事物的锁ID。                             | 比如trx_state的状态是Lock_wait,那么表示当前事物等待之前事物占用锁资源的ID。<br />如果trx_state的状态不是Lock_wait，则当前值为空. |
| trx_wait_started      | 事物等待开始的时间                           |                                                              |
| trx_weight            | 事物的权重，反映了一个事物修改和锁住的行数。 | 当InnoDB发生死锁需要回滚的时候，InnoDB存储引擎会选择该值最小的进行回滚 |
| trx_mysql_thread_id   | mysql的线程ID，show process显示的结果        |                                                              |
| trx_query             | 事物运行的sql                                |                                                              |

```mysql

mysql> show engine innodb status\G;
*************************** 1. row ***************************
  Type: InnoDB
  Name: 
Status: 
=====================================
2021-03-01 20:47:36 0x70000b3f7000 INNODB MONITOR OUTPUT
=====================================
Per second averages calculated from the last 23 seconds
-----------------
BACKGROUND THREAD
-----------------
srv_master_thread loops: 5 srv_active, 0 srv_shutdown, 474 srv_idle
srv_master_thread log flush and writes: 479
----------
SEMAPHORES
----------
OS WAIT ARRAY INFO: reservation count 5
OS WAIT ARRAY INFO: signal count 5
RW-shared spins 0, rounds 9, OS waits 2
RW-excl spins 0, rounds 0, OS waits 0
RW-sx spins 0, rounds 0, OS waits 0
Spin rounds per wait: 9.00 RW-shared, 0.00 RW-excl, 0.00 RW-sx
------------
TRANSACTIONS
------------
Trx id counter 1810
Purge done for trx's n:o < 1806 undo n:o < 0 state: running but idle
History list length 2
LIST OF TRANSACTIONS FOR EACH SESSION:
---TRANSACTION 281479499138384, not started
0 lock struct(s), heap size 1136, 0 row lock(s)
---TRANSACTION 281479499137480, not started
0 lock struct(s), heap size 1136, 0 row lock(s)
---TRANSACTION 281479499135672, not started
0 lock struct(s), heap size 1136, 0 row lock(s)
---TRANSACTION 281479499134768, not started
0 lock struct(s), heap size 1136, 0 row lock(s)
---TRANSACTION 1809, ACTIVE 108 sec
2 lock struct(s), heap size 1136, 1 row lock(s)
MySQL thread id 125, OS thread handle 123145491853312, query id 472 localhost 127.0.0.1 root
--------
FILE I/O
--------
I/O thread 0 state: waiting for i/o request (insert buffer thread)
I/O thread 1 state: waiting for i/o request (log thread)
I/O thread 2 state: waiting for i/o request (read thread)
I/O thread 3 state: waiting for i/o request (read thread)
I/O thread 4 state: waiting for i/o request (read thread)
I/O thread 5 state: waiting for i/o request (read thread)
I/O thread 6 state: waiting for i/o request (write thread)
I/O thread 7 state: waiting for i/o request (write thread)
I/O thread 8 state: waiting for i/o request (write thread)
I/O thread 9 state: waiting for i/o request (write thread)
Pending normal aio reads: [0, 0, 0, 0] , aio writes: [0, 0, 0, 0] ,
 ibuf aio reads:, log i/o's:, sync i/o's:
Pending flushes (fsync) log: 0; buffer pool: 0
284 OS file reads, 157 OS file writes, 36 OS fsyncs
0.00 reads/s, 0 avg bytes/read, 0.00 writes/s, 0.00 fsyncs/s
-------------------------------------
INSERT BUFFER AND ADAPTIVE HASH INDEX
-------------------------------------
Ibuf: size 1, free list len 0, seg size 2, 0 merges
merged operations:
 insert 0, delete mark 0, delete 0
discarded operations:
 insert 0, delete mark 0, delete 0
Hash table size 34673, node heap has 0 buffer(s)
Hash table size 34673, node heap has 0 buffer(s)
Hash table size 34673, node heap has 0 buffer(s)
Hash table size 34673, node heap has 0 buffer(s)
Hash table size 34673, node heap has 0 buffer(s)
Hash table size 34673, node heap has 0 buffer(s)
Hash table size 34673, node heap has 0 buffer(s)
Hash table size 34673, node heap has 0 buffer(s)
0.00 hash searches/s, 0.00 non-hash searches/s
---
LOG
---
Log sequence number 2573975
Log flushed up to   2573975
Pages flushed up to 2573975
Last checkpoint at  2573966
0 pending log flushes, 0 pending chkp writes
27 log i/o's done, 0.00 log i/o's/second
----------------------
BUFFER POOL AND MEMORY
----------------------
Total large memory allocated 137428992
Dictionary memory allocated 148936
Buffer pool size   8191
Free buffers       7881
Database pages     310
Old database pages 0
Modified db pages  0
Pending reads      0
Pending writes: LRU 0, flush list 0, single page 0
Pages made young 0, not young 0
0.00 youngs/s, 0.00 non-youngs/s
Pages read 251, created 59, written 117
0.00 reads/s, 0.00 creates/s, 0.00 writes/s
No buffer pool page gets since the last printout
Pages read ahead 0.00/s, evicted without access 0.00/s, Random read ahead 0.00/s
LRU len: 310, unzip_LRU len: 0
I/O sum[0]:cur[0], unzip sum[0]:cur[0]
--------------
ROW OPERATIONS
--------------
0 queries inside InnoDB, 0 queries in queue
0 read views open inside InnoDB
Process ID=17570, Main thread ID=123145485631488, state: sleeping
Number of rows inserted 876, updated 0, deleted 0, read 1494
0.00 inserts/s, 0.00 updates/s, 0.00 deletes/s, 0.00 reads/s
----------------------------
END OF INNODB MONITOR OUTPUT
============================

1 row in set (0.00 sec)

ERROR: 
No query specified
```



```mysql
mysql> select * from information_schema.INNODB_TRX\G;
*************************** 1. row ***************************
                    trx_id: 1817
                 trx_state: LOCK WAIT
               trx_started: 2021-03-01 20:53:14
     trx_requested_lock_id: 1817:24:3:2
          trx_wait_started: 2021-03-01 20:53:14
                trx_weight: 2
       trx_mysql_thread_id: 241
                 trx_query: /* ApplicationName=DataGrip 2020.3.2 */ select  * from user where id =1 for update
       trx_operation_state: starting index read
         trx_tables_in_use: 1
         trx_tables_locked: 1
          trx_lock_structs: 2
     trx_lock_memory_bytes: 1136
           trx_rows_locked: 1
         trx_rows_modified: 0
   trx_concurrency_tickets: 0
       trx_isolation_level: REPEATABLE READ
         trx_unique_checks: 1
    trx_foreign_key_checks: 1
trx_last_foreign_key_error: NULL
 trx_adaptive_hash_latched: 0
 trx_adaptive_hash_timeout: 0
          trx_is_read_only: 0
trx_autocommit_non_locking: 0
*************************** 2. row ***************************
                    trx_id: 1816
                 trx_state: RUNNING
               trx_started: 2021-03-01 20:52:10
     trx_requested_lock_id: NULL
          trx_wait_started: NULL
                trx_weight: 2
       trx_mysql_thread_id: 125
                 trx_query: NULL
       trx_operation_state: NULL
         trx_tables_in_use: 0
         trx_tables_locked: 1
          trx_lock_structs: 2
     trx_lock_memory_bytes: 1136
           trx_rows_locked: 1
         trx_rows_modified: 0
   trx_concurrency_tickets: 0
       trx_isolation_level: REPEATABLE READ
         trx_unique_checks: 1
    trx_foreign_key_checks: 1
trx_last_foreign_key_error: NULL
 trx_adaptive_hash_latched: 0
 trx_adaptive_hash_timeout: 0
          trx_is_read_only: 0
trx_autocommit_non_locking: 0
2 rows in set (0.00 sec)

ERROR: 
No query specified
```

在了解 InnoDB 的加锁原理前，需要对其存储结构有一定的了解。InnoDB 是聚簇索引，也就是 B+树的叶节点既存储了主键索引也存储了数据行。而 InnoDB 的二级索引的叶节点存储的则是主键值，所以通过二级索引查询数据时，还需要拿对应的主键去聚簇索引中再次进行查询。

- 下面以两条 SQL 的执行为例，讲解一下 InnoDB 对于单行数据的加锁原理。

```
update user  set  age  =   10   where  id  =   49 ;
update user  set  age  =   10   where  name  = 'tom';
```

第一条 SQL 使用主键索引来查询，则只需要在 id = 49 这个主键索引上加上写锁；第二条 SQL 则使用二级索引来查询，则首先在 name = Tom 这个索引上加写锁，然后由于使用 InnoDB 二级索引还需再次根据主键索引查询，所以还需要在 id = 49 这个主键索引上加写锁，如上图所示。

也就是说使用主键索引需要加一把锁，使用二级索引需要在二级索引和主键索引上各加一把锁。

根据索引对单行数据进行更新的加锁原理了解了，那如果更新操作涉及多个行呢，比如下面 SQL 的执行场景。

- 如果涉及多个行的操作呢

```
update user  set  age  =   10   where  id  >   49 ;
```



### 行锁的模式

锁的模式有：读意向锁，写意向锁，读锁，写锁和自增锁(auto_inc)，下面我们依次来看。

#### 读写锁

读锁，又称共享锁（Share locks，简称 S 锁），加了读锁的记录，所有的事务都可以读取，但是不能修改，并且可同时有多个事务对记录加读锁。

写锁，又称排他锁（Exclusive locks，简称 X 锁），或独占锁，对记录加了排他锁之后，只有拥有该锁的事务可以读取和修改，其他事务都不可以读取和修改，并且同一时间只能有一个事务加写锁。

### 行锁的类型

根据锁的粒度可以把锁细分为表锁和行锁，行锁根据场景的不同又可以进一步细分，依次为 Next-Key Lock，Gap Lock 间隙锁，Record Lock 记录锁和插入意向 GAP 锁。

不同的锁锁定的位置是不同的，比如说记录锁只锁住对应的记录，而间隙锁锁住记录和记录之间的间隔，Next-Key Lock 则所属记录和记录之前的间隙。不同类型锁的锁定范围大致如下图所示。

![](.\img\行锁类型.png)

### 记录锁

记录锁是最简单的行锁，并没有什么好说的。上边描述 InnoDB 加锁原理中的锁就是记录锁，只锁住 id = 49 或者 name = 'Tom' 这一条记录。

当 SQL 语句无法使用索引时，会进行全表扫描，这个时候 MySQL 会给整张表的所有数据行加记录锁，再由 MySQL Server 层进行过滤。但是，在 MySQL Server 层进行过滤的时候，如果发现不满足 WHERE 条件，会释放对应记录的锁。这样做，保证了最后只会持有满足条件记录上的锁，但是每条记录的加锁操作还是不能省略的。

所以更新操作必须要根据索引进行操作，没有索引时，不仅会消耗大量的锁资源，增加数据库的开销，还会极大的降低了数据库的并发性能。



### 间隙锁

还是最开始更新用户年龄的例子，如果 id = 49 这条记录不存在，这个 SQL 语句还会加锁吗？答案是可能有，这取决于数据库的隔离级别。这种情况下，在 RC 隔离级别不会加任何锁，在 RR 隔离级别会在 id = 49 前后两个索引之间加上间隙锁。

间隙锁是一种加在两个索引之间的锁，或者加在第一个索引之前，或最后一个索引之后的间隙。这个间隙可以跨一个索引记录，多个索引记录，甚至是空的。使用间隙锁可以防止其他事务在这个范围内插入或修改记录，保证两次读取这个范围内的记录不会变，从而不会出现幻读现象

值得注意的是，间隙锁和间隙锁之间是互不冲突的，间隙锁唯一的作用就是为了防止其他事务的插入，所以加间隙 S 锁和加间隙 X 锁没有任何区别。



### Next-Key 锁

Next-key锁是记录锁和间隙锁的组合，它指的是加在某条记录以及这条记录前面间隙上的锁。假设一个索引包含 15、18、20 ，30，49，50 这几个值，可能的 Next-key 锁如下：

```
(-∞, 15]，(15, 18]，(18, 20]，(20, 30]，(30, 49]，(49, 50]，(50, +∞)
```

通常我们都用这种左开右闭区间来表示 Next-key 锁，其中，圆括号表示不包含该记录，方括号表示包含该记录。前面四个都是 Next-key 锁，最后一个为间隙锁。和间隙锁一样，在 RC 隔离级别下没有 Next-key 锁，只有 RR 隔离级别才有。还是之前的例子，如果deptId不是主键，而是二级索引，且不是唯一索引，那么这个 SQL 在 RR 隔离级别下就会加如下的 Next-key 锁 (30, 49](49, 50)

此时如果插入一条 deptId = 31 的记录将会阻塞住。之所以要把 deptId = 49 前后的间隙都锁住，仍然是为了解决幻读问题，因为 deptId 是非唯一索引，所以 deptId = 49 可能会有多条记录，为了防止再插入一条 deptId = 49 的记录。



### 插入意向锁

插入意向锁是一种特殊的间隙锁（简写成 II GAP）表示插入的意向，只有在 INSERT 的时候才会有这个锁。注意，这个锁虽然也叫意向锁，但是和上面介绍的表级意向锁是两个完全不同的概念，不要搞混了。

插入意向锁和插入意向锁之间互不冲突，所以可以在同一个间隙中有多个事务同时插入不同索引的记录。譬如在上面的例子中，id = 30 和 id = 49 之间如果有两个事务要同时分别插入 id = 32 和 id = 33 是没问题的，虽然两个事务都会在 id = 30 和 id = 50 之间加上插入意向锁，但是不会冲突。

插入意向锁只会和间隙锁或 Next-key 锁冲突，正如上面所说，间隙锁唯一的作用就是防止其他事务插入记录造成幻读，正是由于在执行 INSERT 语句时需要加插入意向锁，而插入意向锁和间隙锁冲突，从而阻止了插入操作的执行











### 自增锁



```
/* 设置自增基数 */
ALTER TABLE stus AUTO_INCREMENT = 10001;
/* 设置自增量 */
ALTER TABLE stus AUTO_INCREMENT_INCREMENT = 1;
```



在InnoDB存储引擎中，对每个含有自增长的表都有一个自增长计数器（auto-increment counter） 当对含有自增长的计数器的表进行插入操作时，这个计数器就会被初始化。而插入操作会依据这个自增长的计数器加1赋值的方式叫：auto-inc Locking

- 获取计数器的值

select max(auto_inc_col) from t for update ;

这种锁其实是一种特殊的表级别的锁，但这个锁不是在一个事物完成后采释放，而是在完成对自增长插入的sql语句后立刻释放，虽然提高了并发效率，但是还是存在一定的性能问题。

- 什么问题？
  - 首先事物必须等待前一个事物的完成，虽然不是前一个事物的完成（啥意思：就是插入不是并发，插入的时候有顺序）
  - 其次，对于INSERT。select 的大数据的插入会影响插入的性能，因为一个另一个事物中的插入会被阻塞



- 5.1.22mysql提供了一个轻量级的互斥量的自增实现机制，从当前版本开始，InnoDB存储了一个innodb_autoinc_lock_mode参数来控制自增长的模式，默认值是：1
  - insert like:指代所有插入语句，比如insert、insert ...select 、replace、replace..select 、load data
  - simple insert 指代插入前九年却带你插入行数的语句ß



#### 查询下一个自增ID

```sql
create table vipkid.user
(
   -- ID自增
    id   int auto_increment
        primary key,
  -- 唯一索引
    name varchar(20) null,
  -- 普通字段
    city int         not null,
    constraint user_name_uindex
        unique (name)
);
```



```sql
SELECT
	AUTO_INCREMENT
FROM
	INFORMATION_SCHEMA. TABLES
WHERE
	TABLE_NAME = 'user' and TABLE_SCHEMA='vipkid'
```

```sql
show table status  where Name='user';
```

AUTO_INC 锁又叫自增锁，它是一种特殊类型的表锁，当插入的表中有自增列（AUTO_INCREMENT）的时候可能会遇到。当插入表中有自增列时，数据库需要自动生成自增值，在生成之前，它会先为该表加 AUTO_INC 表锁，其他事务的插入操作阻塞，这样保证生成的自增值肯定是唯一的。AUTO_INC 锁具有如下特点：

- AUTO_INC 锁互不兼容，也就是说同一张表同时只允许有一个自增锁；
- 自增锁不遵循二段锁协议，它并不是事务结束时释放，而是在 INSERT 语句执行结束时释放，这样可以提高并发插入的性能。
- 自增值一旦分配了就会 +1，如果事务回滚，自增值也不会减回去，所以自增值可能会出现中断的情况。

显然，AUTO_INC 表锁会导致并发插入的效率降低，为了提高插入的并发性，MySQL 从 5.1.22 版本开始，引入了一种可选的轻量级锁（mutex）机制来代替 AUTO_INC 锁，我们可以通过参数 `innodb_autoinc_lock_mode` 控制分配自增值时的并发策略



#### 对于自增ID插入的三种方式

- simple insert 插入前就能确认插入行数的语句
  
  - 比如单行和多行 [`INSERT`](https://dev.mysql.com/doc/refman/8.0/en/insert.html)
  
  - 轻量级锁 mutex，只锁住预分配自增值的过程，不锁整张表
  - 比如：insert但是不包含update 、insert on delicate key(存在则更新，不存在则新增)[地址](https://blog.csdn.net/qq_36095679/article/details/100775338)
  
- bulk insert 插入前不能确认要插入行数的语句
  
  - 使用表锁
  - 比如insert select 、load data
  
- mixed insert 插入语句中有部门值是确定ID值，部分是不确定的
  - 轻量级锁 mutex，只锁住预分配自增值的过程，不锁整张表
  - 会直接分析语句，获得最坏情况下需要插入的数量，一次性分配足够的自增值，缺点是会分配过多，导致浪费和空洞。
  - 比如insert into xx values()()()() 和insert on delicate key
  - 另一种类型的“混合模式插入”是 [`INSERT ... ON DUPLICATE KEY UPDATE`](https://dev.mysql.com/doc/refman/5.7/en/insert-on-duplicate.html)，

```
-- 1、指定ID列和值
insert into user(id,name, city) values(12,'2222',22323);

-- 1.1单次插入多个确定的ID和值
insert into user(id,name, city) values(12,'2002',22323), 
(13,'2003',22323), (14,'2004',22323);

-- 1.2、不指定ID列和值
insert into user(name, city) values('2222',22323);
-- 单次插入多条不指定ID列和值
insert into user(name, city) values('20002',22323),('20003',22323),('20004',22323);

-- 2、通过分析不能确认要插入多少记录的语句，如 INSERT INTO table SELECT 或 LOAD DATA 等；

-- 3、语句不能确认是否要分配自增记录
insert into user(id,name, city) values(32,'200002',32), (null, '200003',33), (34,'200004',34);
```

#### 查询mysql 自增ID相关配置

```json
show global variables where Variable_name like '%auto_inc%'
 [
  {
    //表示自增长字段每次递增的量，其默认值是1，取值范围是1 .. 65535
    "Variable_name": "auto_increment_increment",
    "Value": "1"
  },
  {
    // 自增长字段从那个数开始，他的取值范围是1 .. 65535
    "Variable_name": "auto_increment_offset",
    "Value": "1"
  }
]
```

#### 修改自增ID配置

```sql

show variables like '%auto_inc%';           
show session variables like '%auto_inc%';   --  //session会话变量 
show global variables like '%auto_inc%';   --  //全局变量 
SET @auto_increment_increment = 3 ;
SET session auto_invrement_increment=2; 
SET global auto_increment_increment=1;
 
 
 
  第一种和第二种是一样的效果,第三个是修改全局的变量;
```

#### 自增ID模式

```sql
show  global variables where Variable_name= 'innodb_autoinc_lock_mode';

[
  {
    "Variable_name": "innodb_autoinc_lock_mode",
    "Value": "1"
  }
]
```

- 0
  - 5.1.22	之前默认的自增模式，通过表锁Auto-inc的方式
  - 所有的insert语句("insert like") 都要在语句开始的时候得到一个 表级的auto_inc锁，在语句结束的时候才释放这把锁，注意呀，这里说的是语句级而不是事务级的， 一个事务可能包涵有一个或多个语句。
  - 它能保证值分配的可预见性，与连续性，可重复性，这个也就保证了insert语句在复制到slave
         的时候还能生成和master那边一样的值(它保证了基于语句复制的安全)。
  - 由于在这种模式下auto_inc锁一直要保持到语句的结束，所以这个就影响到了并发的插入
  - 目的：提供了一个向后兼容的能力，即兼容之前版本的mysql

  ```
  Tx1: INSERT INTO t1 (c2) SELECT 1000 rows from another table ...
  Tx2: INSERT INTO t1 (c2) VALUES ('xxx');
  ```
  
  其实这块为啥要保持在语句结尾呢？
  
  因为：如果不是语句结尾，而是Tx2在Tx1运行的过程中进行，那么在重放日志的时候保证了安全
  
  如何优化呢？我们可以不要表锁，当判断一个SQL要插入的条目的时候立刻返回，即做到`InnoDB`可以避免对行数已知的“简单插入”语句使用表级`AUTO-INC`锁定 ，并且仍然保留确定性执行和基于语句的复制的安全性。
  
- 1
  - 这一模式下去simple insert 做了优化，由于simple insert一次性插入值的个数可以立马得到
         确定，所以mysql可以一次生成几个连续的值，用于这个insert语句；总的来说这个对复制也是安全的
         (它保证了基于语句复制的安全)
  - 这一模式也是mysql的默认模式，这个模式的好处是auto_inc锁不要一直保持到语句的结束，只要
         语句得到了相应的值后就可以提前释放锁
  - 对于Bulk insert还是进行传统的Auto-inc lock的方式
  - 如果已经使用Auto-inc lock的方式产生自增的值，而这时需要再进行simple inserts的操作时，还是需要等待Auto-inc lock的释放(针对事物还是什么？)其实就是另一个事务持有 `AUTO-INC`锁，则“简单插入”将等待`AUTO-INC` 锁，就好像它是“批量插入”。
  - “混合模式插入” 例外，其中用户为`AUTO_INCREMENT`多行“简单插入”中的某些（但不是全部）行的列提供显式值 。对于此类插入，`InnoDB`分配的自动增量值要大于要插入的行数。但是，所有自动分配的值都是连续生成的（因此高于由最近执行的先前语句生成的自动增量值）。“多余的”数字丢失。
- 这种模式的好处是既平衡了并发性，又能保证同一条 INSERT 语句分配的自增值是连续的。
  
  试想一个场景，批量插入失败了呢？
  
- 2
  
  - 全部都用轻量级锁 mutex，并发性能最高，按顺序依次分配自增值，不会预分配。
  - 主从复制的时候必须用row-base replication
  - 缺点是不能保证同一条 INSERT 语句内的自增值是连续的，这样在复制（replication）时，如果 binlog_format 为 statement-based（基于语句的复制）就会存在问题，因为是来一个分配一个，同一条 INSERT 语句内获得的自增值可能不连续，主从数据集会出现数据不一致。所以在做数据库同步时要特别注意这个配置。



```
在主主同步配置时，需要将两台服务器的auto_increment_increment增长量都配置为2，而要把auto_increment_offset分别配置为1和2.

这样才可以避免两台服务器同时做更新时自增长字段的值之间发生冲突。
```

死锁

https://dev.mysql.com/doc/refman/5.7/en/innodb-deadlock-example.html

#### mysql 官网配置说明

https://dev.mysql.com/doc/refman/5.7/en/innodb-auto-increment-handling.html





死锁文章：https://blog.csdn.net/gb4215287/article/details/108824898

一个解说文章：https://www.cnblogs.com/JiangLe/p/6362770.html

一个优秀的博客：https://www.aneasystone.com/archives/2017/11/solving-dead-locks-two.html

这有一个错误的示例：http://blog.itpub.net/30135314/viewspace-2713757/

这个确实是一个死锁案例：https://blog.csdn.net/CommanderZero/article/details/102823011

自增死锁：https://blog.csdn.net/gb4215287/article/details/108824928

解释的时候带了官网的例子：https://blog.csdn.net/bohu83/article/details/107625096

这个解释的时候带了官网的链接https://blog.csdn.net/jiao_fuyou/article/details/74130902

```sql
他的SQL是：
INSERT INTO p_session(transaction_id,status) SELECT '18','ON' FROM dual WHERE NOT EXISTS (SELECT transaction_id FROM p_session WHERE transaction_id = '18')
```

这也有一个示例：

https://blog.csdn.net/weixin_30808253/article/details/97500521

```
insert into pay_reconcile
( trade_time,
shop_no,
out_trade_no,
out_order_no,
total_fee,
cost,
system_id,
payment_category,
create_time,
direction,
refund_original_order_no )
select '2018-01-17 16:20:13.0',
'XM0003',
'4200000069201801175791162100',
'118301180117162014',
2120,
13,
1,
1,
now(),
1,
null
FROM dual
WHERE NOT EXISTS (
SELECT 1 FROM pay_reconcile where out_trade_no='4200000069201801175791162100'
AND `out_order_no`='118301180117162014'
 
)
```







这个讲解的是load data导致死锁案例

https://www.jb51.net/article/78509.htm



- 模拟死锁，模拟多线程（可能需要mybatis）
  
  - 创建一个1万条记录的表，批量插入的时候用查询插入
  
  先梳理有哪几种插入类型，3类，然后如果你设计一个自增锁怎么设计？
  
- 讲解自增锁3种模式，自增锁的前世今生

- 自增的特殊用法，0，负数，自增用完

- 自增在什么情况下会出现间隙？只有2的情况下才会出现吗？

  - 对于锁定模式1或2，在连续的语句之间可能会出现间隙，因为对于批量插入，可能不知道每个语句所需的自动递增值的确切数量，并且可能会高估。

- 修改配置模拟再次模拟自增锁，看是否还能出现死锁

- 讲解其它死锁案例

- 讲解锁：行锁、记录锁、next-key锁等

- 讲解锁日志

- 归纳总结





```
// 介绍innodb死锁来源
https://blog.csdn.net/weixin_29461699/article/details/113190507
```





#### 思考？

为啥oracle不支持自增长锁？

事物回滚后自增长为啥又变小了？因为是在内存中操作的

mysql自增BUG：https://bugs.mysql.com/bug.php?id=49001



```
- concurrency并发量
- iterations迭代次数
- query 执行的SQL
- number-of-queries=N 总的测试查询次数(并发客户数×每客户查询次数)


./mysqlslap -hlocalhost -uroot -p123456 -P3306  --concurrency=10 --iterations=10000 --create-schema='deadlock' --query="insert into t_inc_template(cookie_unique) select 'lanchun';" --number-of-queries=10

```









```
select count(*) from t_inc;//刚开始有22327098数据——2千万
select count(*) from t_inc_template;大概有3千万数据29383550
如果执行2次数据从template导入inc，此时inc表里面大概有8千万数据
```

```
我们直接开两个session插入，在插入的过程中执行show engine innodb status;
另一个执行show processlist;
insert into t_inc(x) select cookie_unique from t_inc_template;
```

刚开始如下：

```
mysql> show processlist;
+---------+------+-----------------+----------+---------+------+----------+------------------+
| Id      | User | Host            | db       | Command | Time | State    | Info             |
+---------+------+-----------------+----------+---------+------+----------+------------------+
|   75343 | root | localhost:59660 | deadlock | Sleep   | 1822 |          | NULL             |
|   75344 | root | localhost:59671 | deadlock | Sleep   |  159 |          | NULL             |
|   77146 | root | localhost:59910 | deadlock | Sleep   |  348 |          | NULL             |
|   94266 | root | localhost:60020 | deadlock | Sleep   | 2249 |          | NULL             |
|   94268 | root | localhost:60034 | deadlock | Sleep   | 2244 |          | NULL             |
|   94270 | root | localhost:60050 | deadlock | Sleep   | 2241 |          | NULL             |
| 1194552 | root | localhost       | NULL     | Sleep   |  349 |          | NULL             |
| 1194721 | root | localhost       | NULL     | Query   |    0 | starting | show processlist |
+---------+------+-----------------+----------+---------+------+----------+------------------+
```

执行完如下：

```sql
mysql> show processlist;
+---------+------+-----------------+----------+---------+------+--------------+------------------------------------------------------------------------------------------------------+
| Id      | User | Host            | db       | Command | Time | State        | Info                                                                                                 |
+---------+------+-----------------+----------+---------+------+--------------+------------------------------------------------------------------------------------------------------+
|   75343 | root | localhost:59660 | deadlock | Sleep   | 1934 |              | NULL                                                                                                 |
|   75344 | root | localhost:59671 | deadlock | Query   |    4 | Sending data | /* ApplicationName=DataGrip 2020.3.2 */ insert into t_inc(x) select cookie_unique from t_inc_templat |
|   77146 | root | localhost:59910 | deadlock | Query   |    8 | Sending data | /* ApplicationName=DataGrip 2020.3.2 */ insert into t_inc(x) select cookie_unique from t_inc_templat |
|   94266 | root | localhost:60020 | deadlock | Sleep   | 2361 |              | NULL                                                                                                 |
|   94268 | root | localhost:60034 | deadlock | Sleep   | 2356 |              | NULL                                                                                                 |
|   94270 | root | localhost:60050 | deadlock | Sleep   | 2353 |              | NULL                                                                                                 |
| 1194735 | root | localhost       | NULL     | Sleep   |    1 |              | NULL                                                                                                 |
| 1194743 | root | localhost       | NULL     | Query   |    0 | starting     | show processlist                                                                                     |
+---------+------+-----------------+----------+---------+------+--------------+------------------------------------------------------------------------------------------------------+
8 rows in set (0.00 sec)
```

```java
TRANSACTIONS
------------
Trx id counter 1184942
Purge done for trx's n:o < 1184942 undo n:o < 0 state: running but idle
History list length 44
LIST OF TRANSACTIONS FOR EACH SESSION:
---TRANSACTION 281479601934792, not started
0 lock struct(s), heap size 1136, 0 row lock(s)
---TRANSACTION 281479601932080, not started
0 lock struct(s), heap size 1136, 0 row lock(s)
  //注意这里
---TRANSACTION 1184937, ACTIVE 9 sec setting auto-inc lock
mysql tables in use 2, locked 2
LOCK WAIT 3 lock struct(s), heap size 1136, 1 row lock(s)
MySQL thread id 75344, OS thread handle 123145509613568, query id 2357977 localhost 127.0.0.1 root Sending data
/* ApplicationName=DataGrip 2020.3.2 */ insert into t_inc(x) select cookie_unique from t_inc_template
------- TRX HAS BEEN WAITING 9 SEC FOR THIS LOCK TO BE GRANTED:
TABLE LOCK table `deadlock`.`t_inc` trx id 1184937 lock mode AUTO-INC waiting
------------------
---TRANSACTION 1184936, ACTIVE 13 sec inserting
mysql tables in use 2, locked 2
9547 lock struct(s), heap size 1319120, 4218119 row lock(s), undo log entries 4208575
MySQL thread id 77146, OS thread handle 123145504043008, query id 2357966 localhost 127.0.0.1 root Sending data
/* ApplicationName=DataGrip 2020.3.2 */ insert into t_inc(x) select cookie_unique from t_inc_template
--------

```

执行完只有5千万条数据，其中3千万美元被执行为啥？

看执行日志：发现超时，直接回滚了

```jade
deadlock> insert into t_inc(x) select cookie_unique from t_inc_template
Lock wait timeout exceeded; try restarting transaction
```

而另一个执行了一分钟，日志如下：

```
deadlock> insert into t_inc(x) select cookie_unique from t_inc_template
[2021-04-03 23:34:15] 1 m 26 s 268 ms 中有 29,383,550 行受到影响
```

```java
| InnoDB |      | 
=====================================
2021-04-03 23:33:02 0x70000bcab000 INNODB MONITOR OUTPUT
=====================================
Per second averages calculated from the last 32 seconds
-----------------
BACKGROUND THREAD
-----------------
srv_master_thread loops: 478 srv_active, 0 srv_shutdown, 6999 srv_idle
srv_master_thread log flush and writes: 7477
----------
SEMAPHORES
----------
OS WAIT ARRAY INFO: reservation count 49066
OS WAIT ARRAY INFO: signal count 50254
RW-shared spins 0, rounds 12684, OS waits 4197
RW-excl spins 0, rounds 8489, OS waits 89
RW-sx spins 194, rounds 4759, OS waits 126
Spin rounds per wait: 12684.00 RW-shared, 8489.00 RW-excl, 24.53 RW-sx
------------
TRANSACTIONS
------------
Trx id counter 1184942
Purge done for trx's n:o < 1184942 undo n:o < 0 state: running but idle
History list length 44
LIST OF TRANSACTIONS FOR EACH SESSION:
---TRANSACTION 281479601934792, not started
0 lock struct(s), heap size 1136, 0 row lock(s)
---TRANSACTION 281479601932080, not started
0 lock struct(s), heap size 1136, 0 row lock(s)
  注意这里，等待自增锁已经9秒了
---TRANSACTION 1184937, ACTIVE 9 sec setting auto-inc lock
mysql tables in use 2, locked 2
LOCK WAIT 3 lock struct(s), heap size 1136, 1 row lock(s)
MySQL thread id 75344, OS thread handle 123145509613568, query id 2357977 localhost 127.0.0.1 root Sending data
/* ApplicationName=DataGrip 2020.3.2 */ insert into t_inc(x) select cookie_unique from t_inc_template
------- TRX HAS BEEN WAITING 9 SEC FOR THIS LOCK TO BE GRANTED:
TABLE LOCK table `deadlock`.`t_inc` trx id 1184937 lock mode AUTO-INC waiting
------------------
  注意这里,正在执行插入，已经执行13秒了
---TRANSACTION 1184936, ACTIVE 13 sec inserting
mysql tables in use 2, locked 2
9547 lock struct(s), heap size 1319120, 4218119 row lock(s), undo log entries 4208575
MySQL thread id 77146, OS thread handle 123145504043008, query id 2357966 localhost 127.0.0.1 root Sending data
/* ApplicationName=DataGrip 2020.3.2 */ insert into t_inc(x) select cookie_unique from t_inc_template
--------
FILE I/O
--------
I/O thread 0 state: waiting for i/o request (insert buffer thread)
I/O thread 1 state: waiting for i/o request (log thread)
I/O thread 2 state: waiting for i/o request (read thread)
I/O thread 3 state: waiting for i/o request (read thread)
I/O thread 4 state: waiting for i/o request (read thread)
I/O thread 5 state: waiting for i/o request (read thread)
I/O thread 6 state: waiting for i/o request (write thread)
I/O thread 7 state: waiting for i/o request (write thread)
I/O thread 8 state: waiting for i/o request (write thread)
I/O thread 9 state: waiting for i/o request (write thread)
Pending normal aio reads: [0, 0, 0, 0] , aio writes: [0, 0, 0, 0] ,
 ibuf aio reads:, log i/o's:, sync i/o's:
Pending flushes (fsync) log: 0; buffer pool: 0
442160 OS file reads, 937346 OS file writes, 758080 OS fsyncs
280.37 reads/s, 16384 avg bytes/read, 315.65 writes/s, 10.53 fsyncs/s
-------------------------------------
INSERT BUFFER AND ADAPTIVE HASH INDEX
-------------------------------------
Ibuf: size 1, free list len 0, seg size 2, 0 merges
merged operations:
 insert 0, delete mark 0, delete 0
discarded operations:
 insert 0, delete mark 0, delete 0
Hash table size 34673, node heap has 0 buffer(s)
Hash table size 34673, node heap has 1 buffer(s)
Hash table size 34673, node heap has 0 buffer(s)
Hash table size 34673, node heap has 0 buffer(s)
Hash table size 34673, node heap has 1 buffer(s)
Hash table size 34673, node heap has 0 buffer(s)
Hash table size 34673, node heap has 0 buffer(s)
Hash table size 34673, node heap has 0 buffer(s)
130692.07 hash searches/s, 1367.24 non-hash searches/s
---
LOG
---
Log sequence number 5297045243
Log flushed up to   5283724426
Pages flushed up to 5239438979
Last checkpoint at  5238187811
0 pending log flushes, 0 pending chkp writes
752392 log i/o's done, 2.50 log i/o's/second
----------------------
BUFFER POOL AND MEMORY
----------------------
Total large memory allocated 137428992
Dictionary memory allocated 196684
Buffer pool size   8191
Free buffers       0
Database pages     8109
Old database pages 2973
Modified db pages  2826
Pending reads      0
Pending writes: LRU 0, flush list 0, single page 0
Pages made young 9606, not young 65349951
7.16 youngs/s, 115225.90 non-youngs/s
Pages read 442121, created 172998, written 179282
280.37 reads/s, 389.52 creates/s, 303.68 writes/s
Buffer pool hit rate 1000 / 1000, young-making rate 0 / 1000 not 216 / 1000
Pages read ahead 261.99/s, evicted without access 0.00/s, Random read ahead 0.00/s
LRU len: 8109, unzip_LRU len: 0
I/O sum[10430]:cur[6], unzip sum[0]:cur[0]
--------------
ROW OPERATIONS
--------------
0 queries inside InnoDB, 0 queries in queue
0 read views open inside InnoDB
Process ID=18988, Main thread ID=123145493921792, state: sleeping
Number of rows inserted 55919735, updated 0, deleted 0, read 229786067
131514.23 inserts/s, 0.00 updates/s, 0.00 deletes/s, 131514.27 reads/s
----------------------------
END OF INNODB MONITOR OUTPUT
============================

```

```
http://m.mamicode.com/info-detail-1713555.html
https://blog.csdn.net/weixin_34358365/article/details/86416200
https://blog.csdn.net/weixin_39800918/article/details/112731026
```

```
https://blog.csdn.net/weixin_33644009
一个死锁，明天验证下，是2012年的文章
```

## 尾部

/usr/local/mysql-5.7.20-macos10.12-x86_64/bin







#### insert into select 锁表问题



发现了可以使用`insert into select`实现，这样就可以避免使用网络I/O，直接使用SQL依靠数据库I/O完成，这样简直不要太棒了。



```
确认实验条件：为RR

select @@global.tx_isolation,@@session.tx_isolation;
```

1、insert into table1 select * from table2：

直接执行该SQL，使用show engine innodb status;查询锁情况如下

发现是逐步加锁

但是用的是Using where，会从上到下扫描并且加锁，这样就和全表扫描一样了

2、insert into table1 select * from table2 order by id 

直接执行该SQL，使用show engine innodb status;查询锁情况如下

发现是逐步加锁

3、insert into table1 select * from table2 order by 其它非主键非索引

直接执行该SQL，使用show engine innodb status;查询锁情况如下

发现是锁表



4、insert into table1 select * from table2 order by 普通索引

直接执行该SQL，使用show engine innodb status;查询锁情况如下

发现是逐步加锁

- 结论：其实就是根据查询条件加锁

#### insert into select 死锁问题

MySQL中使用insert into select时，事务隔离级别为默认的REPEATABLE-READ时会产生锁，

那么在此时如果使用update对表进行dml操作时，就有可能产生锁等待甚至死锁。





 这里给出一个方案就是讲事务隔离级别改成READ-COMMITTED，设置方法：

SET GLOBAL tx_isolation = 'READ-COMMITTED';

SET SESSION tx_isolation = 'READ-COMMITTED';

此时还应将binlog_format改成ROW或者MIXED

SET GLOBAL binlog_format = 'ROW';

或者修改配置文件

transaction-isolation = READ-COMMITTED
binlog_format = MIXED

#### 其它数据库ID设计

https://blog.csdn.net/youanyyou/article/details/78990037

UUID、雪花ID、中间件Redis、SnowFlake算法、UidGenerator是百度开源的分布式ID生成器，基于于snowflake算法的实现

Leaf：要依赖关系数据库、Zookeeper等中间件

bug修复流程

https://blog.csdn.net/weixin_34358365/article/details/86416200

表级别的会死锁吗