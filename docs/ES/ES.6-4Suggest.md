### ES ContextSuggest根据上下文进行自动补全

- 个人ES版本6.4.3



[一个比较详细的讲解](https://www.cnblogs.com/Neeo/articles/10695031.html)

[官网讲解](https://www.elastic.co/guide/en/elasticsearch/reference/7.0/suggester-context.html)

- 看之前要有一些ES-Suggest的基础，传送门：

[es-Suggest](https://www.cnblogs.com/Neeo/articles/10695019.html)

[官网讲解](https://www.elastic.co/guide/en/elasticsearch/reference/current/search-suggesters.html#completion-suggester)



- 看官网的时候注意要切换到你的ES版本

### ContentSuggest存在的意义



1、比如我们有很多问题，统一都用standardQuestion这个字段存储，但是这个问题可能属于不同的应用app，所以对于每一个问题都有一个appId。也就是说同一个问题（字面上一样的）可能在不同的app中都存在，如果直接搜索则召回所有的，显然是不能满足我们根据appId来过滤问题的需求。

2、但是我们知道ES-Suggest是存储在FST中的，FST类似一个Map是没有过滤appId的功能的，根本没有过滤功能，我们怎么办呢？

​	我们可以选择将appID拼到前缀里面，类似xxxAppId_xxxstandardQuestion

​	比如：下面这样，但是这样太麻烦，而且不具备扩展性，如果下次我们要根据appId和userId进行过滤怎么办？

```
45_微信公众号如何申请、46_微信公众号如何申请、47_微信公众号如何申请
```

3、天无绝人之路，ES提供了ContentSuggest的功能，就是在指定索引的时候，设置一个分类，我们查询的时候就可以根据这个分类进行查询了

比如下面的：

#### 设置索引映射关系

```java
PUT question_index
{
  "mappings": {
    "doc":{
      "properties":{
        "standardQuestion":{
          "type":"completion",
          "analyzer":"ik_smart",
          "contexts":[
            {
              "name":"appId",
              "type": "category"
            } 
          ]
        }
      }
    }
  }
}
```

```java
PUT question_index
{
  "mappings": {
    "doc":{
      "properties":{
      // 字段名称
        "standardQuestion":{
        // 只有类型是completion才能使用Suggest功能
          "type":"completion",
          // 指定字段分词器为：中文ik_smart
          "analyzer":"ik_smart",
          "contexts":[
            {
            // 类型的名称
              "name":"appId",
              // 类型，这个固定这个category就好
              "type": "category"
            } 
          ]
        }
      }
    }
  }
}
```

#### 获取索引映射

```java
GET question_index/_mapping
    //下面是执行结果
{
  "question_index": {
    "mappings": {
      "doc": {
        "properties": {
          "standardQuestion": {
            "type": "completion",
            "analyzer": "ik_smart",
            "preserve_separators": true,
            "preserve_position_increments": true,
            "max_input_length": 50,
            "contexts": [
              {
                "name": "appId",
                "type": "CATEGORY"
              }
            ]
          }
        }
      }
    }
  }
}
```

#### 插入基础数据

```java
PUT question_index/doc/1
{
  "standardQuestion":{
    "input":["微信公众号如何申请", "申请微信公众号", "怎么申请微信公共号"],
    "contexts":{
      "appId":["45", "46"] 
    }
  }
}

PUT question_index/doc/2
{
  "standardQuestion":{
    "input":["微信公众号如何申请", "申请微信公众号", "怎么申请微信公共号"],
    "contexts":{
      "appId":["46", "12"] 
    }
  }
}
PUT question_index/doc/3
{
  "standardQuestion":{
    "input":"如何申请公司的微信公众号",
    "contexts":{
      "appId":["46", "12"] 
    }
  }
}
```

#### 查询所有数据

```java
GET question_index/doc/_search
//下面是查询的结果
{
  "took": 4,
  "timed_out": false,
  "_shards": {
    "total": 5,
    "successful": 5,
    "skipped": 0,
    "failed": 0
  },
  "hits": {
    "total": 3,
    "max_score": 1,
    "hits": [
      {
        "_index": "question_index",
        "_type": "doc",
        "_id": "2",
        "_score": 1,
        "_source": {
          "standardQuestion": {
            "input": [
              "微信公众号如何申请",
              "申请微信公众号",
              "怎么申请微信公共号"
            ],
            "contexts": {
              "appId": [
                "46",
                "12"
              ]
            }
          }
        }
      },
      {
        "_index": "question_index",
        "_type": "doc",
        "_id": "1",
        "_score": 1,
        "_source": {
          "standardQuestion": {
            "input": [
              "微信公众号如何申请",
              "申请微信公众号",
              "怎么申请微信公共号"
            ],
            "contexts": {
              "appId": [
                "45",
                "46"
              ]
            }
          }
        }
      },
      {
        "_index": "question_index",
        "_type": "doc",
        "_id": "3",
        "_score": 1,
        "_source": {
          "standardQuestion": {
            "input": "怎么申请公司的微信公众号",
            "contexts": {
              "appId": [
                "46",
                "12"
              ]
            }
          }
        }
      }
    ]
  }
}
```



#### 测试基于上下文的自动补全

```java
POST question_index/_search
{
  "suggest":{
    "place_suggestion":{
      "prefix":"怎么",
      "completion":{
        "field":"standardQuestion",
        "size": 10,
        "contexts":{
          "appId":["12" ]
        }
      }
    }
  }
}
```

 发现确实匹配到了《怎么》开头的，而且appId是12的数据

```
{
  "took": 5,
  "timed_out": false,
  "_shards": {
    "total": 5,
    "successful": 5,
    "skipped": 0,
    "failed": 0
  },
  "hits": {
    "total": 0,
    "max_score": 0,
    "hits": []
  },
  "suggest": {
    "place_suggestion": [
      {
        "text": "怎么",
        "offset": 0,
        "length": 2,
        "options": [
          {
            "text": "怎么申请公司的微信公众号",
            "_index": "question_index",
            "_type": "doc",
            "_id": "3",
            "_score": 1,
            "_source": {
              "standardQuestion": {
                "input": "怎么申请公司的微信公众号",
                "contexts": {
                  "appId": [
                    "46",
                    "12"
                  ]
                }
              }
            },
            "contexts": {
              "appId": [
                "12"
              ]
            }
          },
          {
            "text": "怎么申请微信公共号",
            "_index": "question_index",
            "_type": "doc",
            "_id": "2",
            "_score": 1,
            "_source": {
              "standardQuestion": {
                "input": [
                  "微信公众号如何申请",
                  "申请微信公众号",
                  "怎么申请微信公共号"
                ],
                "contexts": {
                  "appId": [
                    "46",
                    "12"
                  ]
                }
              }
            },
            "contexts": {
              "appId": [
                "12"
              ]
            }
          }
        ]
      }
    ]
  }
}
```

### 基于Java实现ContextSuggest

上面我们测试了基于上下文字段时间的contextSuggest功能，下面我们就结合Java来具体实现下

- 类名

```
//我想在ES-contextSuggest的时候，根据appId进行过滤standardQuestion，那我的mapping该如何写呢
@Data
@Document(indexName = "question_index",
        type = "question_index",
        shards = 5, replicas = 1)
public class QuestionIndex {
    @Id
    @Field(type = FieldType.Keyword)
    private String id;

 
    @Field(type = FieldType.keyword)
    private Long appId;

    @Field(type = FieldType.Text, analyzer = "ik_smart")
    private String standardQuestion;
```





#### contextSuggest 坑点

- contexts里面的字段必须存在，而且类型必须是：keyword或者text否则报错，也就是我们的appId不能说int或者是long 类型，必须是keyword和text的一种
- 由于ES和本地IDEA的字段类型 不一致，如果直接在ES里面设置completion,那么启动就会报错，解决方案是：先启动，然后删除索引，重新构建索引即可

```java
 PUT question_index
{
    "mappings":{
        "knowledge_test":{
            "properties":{
                "id":{
                    "type":"keyword"
                },
                "appId":{
                    "type":"keyword"
                },
                "standardQuestion":{
                    "type":"completion",
                    "analyzer":"ik_smart",
                    "contexts":[
                        {
                            "name":"appId",
                            "type":"category",
                            "path":"appId"
                        }
                    ]
                }
            }
        }
    }
}
```

#### 完整的searchSourceBuilder

```java
    public SearchSourceBuilder getSuggestSourceBuilder(String q, Long appId) {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        // 创建CompletionSuggestionBuilder
        CompletionSuggestionBuilder textBuilder = SuggestBuilders.completionSuggestion("standardQuestion") // 指定字段名
                .size(10) // 设定返回数量
                .skipDuplicates(true); // 去重
        // 配置context
        CategoryQueryContext categoryQueryContext = CategoryQueryContext.builder().setCategory(String.valueOf(appId)).build();
        Map<String, List<? extends ToXContent>> contexts = new HashMap<>();
        List<CategoryQueryContext> list = new ArrayList<>();
        list.add(categoryQueryContext);
        contexts.put("appId", list);
        textBuilder.contexts(contexts); // 设置contexts
        // 创建suggestBuilder并将completionBuilder添加进去
        SuggestBuilder suggestBuilder = new SuggestBuilder();
        suggestBuilder.addSuggestion("standardQuestionSuggest", textBuilder)
                .setGlobalText(q);
        sourceBuilder.suggest(suggestBuilder);
        log.info("knowledge search json:{}", sourceBuilder.toString());
        return sourceBuilder;
    }
```

```
 SearchRequest request = new SearchRequest();
  request.indices("question_index");
                request.types("question_index");
                 request.source(getSuggestSourceBuilder(q,appId));
                 
                 
                 
```

