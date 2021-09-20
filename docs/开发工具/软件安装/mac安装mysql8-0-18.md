### 安装

本文记录了MacOS下安装MySQL8.0.21，并成功命令行操作，供大家参考，具体内容如下

- ①下载MySQL8.0.18

下载网址：[点击查看](https://dev.mysql.com/downloads/mysql/)

- 下载后(dmg)进行安装。
  - 如果有问题，请在安全与隐私里面，把允许从以下位置下载的app，那个地方打开
  - 安装的时候需要输入Mac开机的密码
  - 安装的时候需要指定root的密码，而且至少8位，我们设定12345678
- 查看mysql服务，在系统设置里面找到mysql图标就能看到，可以参考[传送门](https://www.jb51.net/article/173126.htm)

- 里面的配置基本无需更改，但是如果想在命令行直接看mysql，还需执行下面的命令

```java
③配置环境变量

进入终端（这里用的是vim）：

vim ~/.bash_profile
在文件最后加入：

PATH=$PATH:/usr/local/mysql/bin
保存并退出（ESC-> : -> wq）。

让配置生效：

source ~/.bash_profile
推出终端重新进入。

mysql --version
出现版本号代表成功：
```

### 运行

```
mysql -u root -p 
然后输入密码进入mysql，执行quit退出

```

### 启动和停止

可以直接在设置找到mysql，进行服务启动和停止