nginx 
 


## mac开发nginx配置

# nginx相关

```java
/usr/local/Cellar/nginx/1.19.0

到位置后启动的方式



 ./bin/nginx -c /usr/local/etc/nginx/nginx.conf



或者直接启动

/usr/local/Cellar/nginx/1.19.0/bin/nginx -c /usr/local/etc/nginx/nginx.conf





nginx Mac config的位置



/usr/local/etc/nginx







终端运行：

nginx 直接启动

sudo nginx -s reload // **修改配置后重新加载生效**

sudo nginx -s reopen  // **重新打开日志文件**

sudo nginx -s stop // **快速停止nginx**

sudo nginx -s quit // **完整有序的停止nginx/优雅关闭（先服务完已打开的连接）**

 **4.判断配置文件是否有问题**：

终端运行：sudo nginx -t 

 

**停止nginx服务的其他的方法：**

查看nginx进程，进程号，停止的话可以杀进程

查看nginx进程：ps -ef|grep nginx

sudo kill -QUIT //主进程号

sudo kill -TERM //主进程号


下面是我的Mac启动前端项目的流程


//拉去代码
cd /Users/xiyouyan/code/BdSoft/cobot-FE


//把本地抛弃
git checkout .


//拉取代码
git pull 


//停止本地nginx
sudo nginx -s stop
//删除原来的文件
rm -rf /usr/local/Cellar/nginx/1.19.0/html  

//到目录

cd /usr/local/Cellar/nginx/1.19.0/


//创建目录
mkdir html

 
//复制
cp -r /Users/xiyouyan/code/BdSoft/cobot-FE/build/* /usr/local/Cellar/nginx/1.19.0/html/


//启动
nginx
```

