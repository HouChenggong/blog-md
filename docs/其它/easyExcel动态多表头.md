# 需求

 ![](.\img\excel.png)

比如我们的需求是这样的，

- 我要实现一个excel里面有多个轮子，也就是图片最下面的那几个
- 一个轮子里面有多个表头，而且每一个不一样
- 字体大小、颜色，宽高等

## 难点

其实上面的除了一个轮子里面有多个不一样的表头，其它都能在官网的API中找到，附带官方地址

```java
//github仓库
https://github.com/alibaba/easyexcel
//官网API地址
https://www.yuque.com/easyexcel/doc/write
```

下面说下怎么动态表头

```java
ExcelWriter excelWriter = EasyExcel.write("test2.xlsx").build();
WriteSheet writeSheet = EasyExcel.writerSheet("模板").needHead(Boolean.FALSE).build();
WriteTable writeTable0 = EasyExcel.writerTable(0).head(Object1.class).needHead(Boolean.TRUE).build();
WriteTable writeTable1 = EasyExcel.writerTable(1).head(Object2.class).needHead(Boolean.TRUE).build();
excelWriter.write(Object1List, writeSheet, writeTable0);
excelWriter.write(Object2List, writeSheet, writeTable1);
// 千万别忘记finish 会帮忙关闭流
excelWriter.finish();
```

效果如下：

![](.\img\excel2.png)









