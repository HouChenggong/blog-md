## 关于写这个的目的

- 查漏补缺

主要是收集了网上的一些**自己欠缺**或者**觉得应该要记住**的一些知识要点，原则上只是作为**自己的查漏补缺和复习使用**，不做网络推广，相当于自己的一个记事本。因为网上的相关内容太多，但是自己只需要记录自己需要的即可，这也是我为啥要写的原因，所以对于版权如有冒犯到您，请您谅解，或者与我沟通。

- 作为学习记录

同时我也会把每天所学记录在这里面，比如每日算法，每日学习记录等等，算是作为一个学习记录

- 受到学长指引

哈哈，主要是实习的时候就深深的被学长写博客的习惯打动了，目前小哥也是非常厉害的，他的博客地址：[高达一号](https://blog.csdn.net/u010003835)

## 感谢

**前人种树，后人纳凉，感谢这两个博主，也感谢那些被我引用的某些知识点提供者的博主，谢谢你们！**



## 如何搭建在线文档

这个是我参考github的一个博主搭建，附上源地址连接

[如何使用docsify搭建文档类型的网站](./docs/how-to-use-docsify.md)

这里我在win10安装遇到了各种坑，最后发现docsify在电脑上不能运行

D:\Program Files\nodejs\node_global

这是我的Nodejs全局目录，直接在当前目录运行cmd，然后输入docsify命令

```
D:\Program Files\nodejs\node_global>docsify
Usage: docsify <init|serve> <path>
```

然后执行`docsify init ./blog`这个时候会在当前目录创建一个bolg子目录

然后执行`docsify serve ./blog`

输入http://localhost:3000/#/

即可访问

### 如何加快访问速度

```html
unpkg.com替换为unpkg.zhimg.com
```

这个是非常有必要的，因为unpkg.com是国外的，另一个是国内的镜像

## 在线阅读

- 国内快速阅读地址：https://cnxiyou.gitee.io/blog-md/

- github阅读地址（偏慢）https://houchenggong.github.io/blog-md/

- Github：https://github.com/HouChenggong/blog-md



如果您对本项目中的内容有建议或者意见

如果你对本项目中未完成的章节感兴趣

欢迎提出专业方面的修改建议

请直接在[GitHub](https://github.com/HouChenggong/blog-md)上以issue或者PR的形式提出,如果方便的话也可以添加微信：Yan_Hiiumaa_Eesti
