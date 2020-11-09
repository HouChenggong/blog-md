### ES ContextSuggest根据不同类别进行自动补全

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



#### 测试自动补全基于类型的查询

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

