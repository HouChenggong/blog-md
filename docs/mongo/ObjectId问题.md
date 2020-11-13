### 我们的痛点是：

1、mongo 插入数据数据的时候ID是我们不用去处理的，只要调用mongoTemplate.save（）方法，ID问题mongo会自动插入

2、但是我们想要在数据插入之前就手动生成mongo的ID，这个时候我们可以用UUID或者雪花ID

3、如果是UUID或者其它ID，在我们用ID排序的时候会报错，报错信息如下

```
ObjectId id = new ObjectId(xxxId); query.addCriteria("id").lt(id));  //invalid hexadecimal representation of an objectid
```

意思就是如果我们用UUID，就不能用范围查询

### 2、invalid hexadecimal representation of an objectid

我们想用ID做范围查询，我们又想在数据插入之前获取ID，这个时候怎么办呢？难道想让我监听mongo提前获取ID吗？这下吗有一篇这个文章

https://blog.csdn.net/qq_16313365/article/details/72781469

但是太麻烦，我们下面用一个简单的方式解决

## 3、解决方案

```
String questionId = ObjectId.get().toString();
然后我们使用的时候直接使用即可
ObjectId id = new ObjectId(questionId);
query.addCriteria("id").lt(id));
```



### Java中指定索引

```java
@Document(collection = "user")
@Data
@CompoundIndexes({
        @CompoundIndex(name = "idx_app_id_username_chat_time", def = "{'app_id': 1, 'user_name': 1,'chat_time':1}", unique = true)
})
public class User {

    @Id
    private String id;

    @Field("user_name")
    private String userName;
  
    @Field("app_id")
    private Long appId;

    @Field("chat_time")
    private Long chatTime;
```

