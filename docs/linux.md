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

- ```
  // 查询日志中带有xxxText的
  tail -f xxx.log | grep xxxText
  // 一样查询
  cat xxx.log | grep xxxText
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

- Top -h 