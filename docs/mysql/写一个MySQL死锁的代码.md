# 写一个MySQL锁的代码

### MySQL8.0查看和更改隔离级别

- 查看

```sql
mysql> select @@global.transaction_isolation,@@transaction_isolation;
+--------------------------------+-------------------------+
| @@global.transaction_isolation | @@transaction_isolation |
+--------------------------------+-------------------------+
| REPEATABLE-READ                | REPEATABLE-READ         |
+--------------------------------+-------------------------+
1 row in set (0.00 sec)

```

- 修改

```sql
mysql> set session transaction isolation level read uncommitted;
Query OK, 0 rows affected (0.00 sec)
## 查看
mysql>  select @@global.transaction_isolation,@@transaction_isolation;
+--------------------------------+-------------------------+
| @@global.transaction_isolation | @@transaction_isolation |
+--------------------------------+-------------------------+
| REPEATABLE-READ                | READ-UNCOMMITTED        |
+--------------------------------+-------------------------+
1 row in set (0.00 sec)


```



### 第一种 对于不存在的数据for update导致死锁

比如我们在RR级别下，开启两个session，原来的数据是：

```java
mysql> select * from test;
+----+-----+
| id | num |
+----+-----+
|  1 |   1 |
|  2 |   2 |
|  3 |   3 |
|  4 |   4 |
+----+-----+
4 rows in set (0.04 sec)
```

- sessionA

```java
mysql> start transaction;
Query OK, 0 rows affected (0.01 sec)

mysql> select * from test where id =10 for update ;
Empty set
```

- sessionB

```java
mysql> start transaction;
Query OK, 0 rows affected (0.02 sec)

mysql> select * from test where id=11 for update;
Empty set
```

第二步：对sessionA执行插入记录10操作

```java
insert into test values (10,10);
//发现卡死了,如下：如果等的够久，还会报下面的错误
mysql> insert into test value (10,10);
1205 - Lock wait timeout exceeded; try restarting transaction
```

我们可以理解为sessionA把（4，+无穷）的记录都锁死了，sessionB也一样

这时我们插入记录10，要等待sessionB释放该锁，所以，不管A怎么插入都会阻塞导致失败，**出现死锁**

第三步：我们对sessionB执行它要插入的数据记录11

```java
insert into test values (11,11);
```

发现以及报了死锁错误，说明我们上诉死锁确实存在

- 原因，比如我们上诉只有4个记录，for uptate 10会把4到正无穷都锁住

  同样for uptate11也是

- 如果现在记录变成1，2，3，4，20那么for update 10会锁住（4-20）; for update11也一样

### 第二种，锁住一个范围导致的死锁

比如现在记录有1，2，3，4，9

- sessionA进行如下操作

```java
mysql> start transaction;
Query OK, 0 rows affected (0.01 sec)

mysql> select * from test where id =9 for update ;;
+----+-----+
| id | num |
+----+-----+
|  9 |   9 |
+----+-----+
1 row in set (0.04 sec)
```



- sessionB进行如下操作

```java
mysql> start transaction;
Query OK, 0 rows affected (0.02 sec)
//发现这个卡死了
mysql> select * from test where id<20 for update;
 
```

- 然后再sessionA里面插入记录是8的记录,发现死锁

```sql
mysql> insert into test values (8,8);
1213 - Deadlock found when trying to get lock; try restarting transaction
```

