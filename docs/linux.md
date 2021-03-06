## linux命令

#### top文件相关

- df -h 查询磁盘占用情况

```java
df -h 
文件系统        容量  已用  可用 已用% 挂载点
/dev/vda1        99G   84G  9.8G   90% /
devtmpfs        7.8G     0  7.8G    0% /dev
tmpfs           7.8G   24K  7.8G    1% /dev/shm
tmpfs           7.8G  452K  7.8G    1% /run
tmpfs           7.8G     0  7.8G    0% /sys/fs/cgroup
tmpfs           1.6G     0  1.6G    0% /run/user/0
tmpfs           1.6G     0  1.6G    0% /run/user/10000
tmpfs           1.6G     0  1.6G    0% /run/user/19481
tmpfs           1.6G     0  1.6G    0% /run/user/19685
```

- du -h --max-depth=1

```java
du -h --max-depth=1

查询当前目录文件信息

# du -h --max-depth=1
9.0M    ./modules
74G ./data
28M ./lib
8.2M    ./config
1.9G    ./logs
9.3M    ./plugins
276K    ./bin
76G .
```



## 文件操作

- mkdir
- vim 
- find . name "xxxx" 递归查询所有的xxx文件

- l s - al查询所有文件，包括隐藏文件

- ```
  cp -r sourceFolder targetFolder 递归复制整个文件夹
  ```

- ```
  cp source dest 复制文件
  ```

### 日志操作

- ```java
  head -n 10 example.txt
  tail -n 10 example.txt
    tail -f xxx.txt自动显示新增内容，默认10行，但是可以设置
  ```

- ```java
  // 查询日志中带有xxxText的,注意两个''
  tail -f xxx.log | grep 'xxxTex'
  // 一样查询
  cat xxx.log | grep 'xxxText'
  
  ```
```
  

#### 日志切分

- head 命令是用来获取文本文件的开始n行。

​```java
head -10000 java.log > javaHead.log
```

- tail 命令是用来获取文本最后行。

```java
tail -10000 java.log > javaTail.log
```

- sed 命令可以从第N行截取到第M行。（ N > 0 , M < FileLineNumber ）

```java
sed -n '1,50000p' java.log > javaRange.log
```

- Split 按照行切分–verbose 显示切分进度

```java
split -l 300 java.txt javaLog --verbose
```

- 按照文件大小切分，10M

```java
split -d 10m java.txt javaLog --verbose
```

#### 日志搜索

```java
less xxx.log 
然后输入《/》直接复制进去错误的日志，比如《手机离职审批接口失败》
然后它自己就会跳转到相关的错误日志
```

```java
//直接搜索相关错误日志
grep -C 20 "手机离职审批接口失败" eim-ad-inventory.log
```



### sort命令

[sort命令](https://blog.csdn.net/u010003835/article/details/106806413)

### 查看nginx相关的进程

```shell
ps -ef |grep nginx
```

### 权限相关

- R100(4)	 	w010(2)		x 001(1)

- Rw- 4+2=6

- Rwxr -XR -x 755

- 1.chmod 755 文件或文件夹名字

  2.chmod a=rwx 1.txt=chmod 777 1.txt

```
chmod 777 file.java
//file.java的权限-rwxrwxrwx，r表示读、w表示写、x表示可执行
```



```
//给用户是qianxun的人而去用户组是qianxun的，手语/DATA/目录的读写权限
chown -R qianxun:qianxun /DATA/
```



### kill -9（强制）  -15(默认)



```java

//强制杀死进程
kill -9 xxxid
//默认是kill -15
kill -15代表的信号为SIGTERM，这是告诉进程你需要被关闭，请自行停止运行并退出；

程序立刻停止

当程序释放相应资源后再停止

程序可能仍然继续运行

大部分程序接收到SIGTERM信号后，会先释放自己的资源，然后再停止。
但是也有程序可能接收信号后，做一些其他的事情（如果程序正在等待IO，可能就不会立马做出响应，
我在使用wkhtmltopdf转pdf的项目中遇到这现象），也就是说，SIGTERM多半是会被阻塞的。
```

### 查看端口情况

```
netstat -tln | grep 8080

lsof -i:8000
```

## java常用

先jps看运行服务的pi d

如果是CPU100% 则用to p命令看pi d

拿到pi d之后，用jstack -l 19466 >>/users/xiyouyan/xx1.txt 导出线程dump

```
jstat
jmap
jstack
```

#### jstack打印堆栈信息分析CPU



- Top -h 

```
https://mp.weixin.qq.com/s/HvRJkzkKPzzvf0TozjaCcg
```

1. 接着用top -H -p pid来找到 CPU 使用率比较高的一些线程，比如pid=66

```java
top -H -p pid
```

2. 然后将占用最高的 pid 转换为 16 进制printf '%x\n' pid得到 nid,比如是：42

```java
printf '%x\n' 66
```

3. 接着直接在 jstack 中找到相应的堆栈信息jstack pid |grep 'nid' -C5 –color

```
jstack pid |grep '0x42' -C5 –color
```

可以看到我们已经找到了 nid 为 0x42 的堆栈信息



[线上问题](https://mp.weixin.qq.com/s/Lyca7d1WYOi3eegIAI2WRQ)

#### JMAP 定位代码内存泄漏

如果是OOM相关的，则使用JMAPjmap -dump:format=b,file=filename pid来导出 dump 文件



另一方面，我们可以在启动参数中指定-XX:+HeapDumpOnOutOfMemoryError来保存 OOM 时的 dump 文件。