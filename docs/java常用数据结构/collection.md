## Collection

![](http://img.blog.csdn.net/20160706172512559?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQv/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/Center)


### Collections 工具类和 Arrays

[详细介绍](https://gitee.com/SnailClimb/JavaGuide/blob/master/docs/java/basic/Arrays,CollectionsCommonMethods.md)

### Collections工具类常用方法

- 排序

```java
void reverse(List list)//反转
void shuffle(List list)//随机排序
void sort(List list)//按自然排序的升序排序
void sort(List list, Comparator c)//定制排序，由Comparator控制排序逻辑
```

- 定制排序

```java
// 定制排序的用法
Collections.sort(arrayList, new Comparator<Integer>() {

@Override
public int compare(Integer o1, Integer o2) {
return o2.compareTo(o1);
}
});
```

- 查询和替换

```java
  int binarySearch(List list, Object key)//对List进行二分查找，返回索引，注意List必须是有序的
  int max(Collection coll)//根据元素的自然顺序，返回最大的元素。 类比int min(Collection coll)
  int max(Collection coll, Comparator c)//根据定制排序，返回最大元素，排序规则由Comparatator类控制。类比int min(Collection coll, Comparator c)
  void fill(List list, Object obj)//用指定的元素代替指定list中的所有元素。
  int frequency(Collection c, Object o)//统计元素出现次数
  int indexOfSubList(List list, List target)//统计target在list中第一次出现的索引，找不到则返回-1，类比int lastIndexOfSubList(List source, list target).
boolean replaceAll(List list, Object oldVal, Object newVal), 用新元素替换旧元素
```



### Arrays常用操作

1. 排序 : `sort()`

   // 排序后再进行二分查找，否则找不到

2. 查找 : `binarySearch()`

```java
// *************查找 binarySearch()****************
char[] e = { 'a', 'f', 'b', 'c', 'e', 'A', 'C', 'B' };
// 排序后再进行二分查找，否则找不到
Arrays.sort(e);
System.out.println("Arrays.sort(e)" + Arrays.toString(e));
System.out.println("Arrays.binarySearch(e, 'c')：");
int s = Arrays.binarySearch(e, 'c');
System.out.println("字符c在数组的位置：" + s);
```

1. 比较: `equals()`

```java
// *************比较 equals****************
char[] e = { 'a', 'f', 'b', 'c', 'e', 'A', 'C', 'B' };
char[] f = { 'a', 'f', 'b', 'c', 'e', 'A', 'C', 'B' };
/*
* 元素数量相同，并且相同位置的元素相同。 另外，如果两个数组引用都是null，则它们被认为是相等的 。
*/
// 输出true
System.out.println("Arrays.equals(e, f):" + Arrays.equals(e, f));
```

1. 填充 : `fill()`

```
int[] g = { 1, 2, 3, 3, 3, 3, 6, 6, 6 };
// 数组中所有元素重新分配值
Arrays.fill(g, 3);
```

1. 转列表: `asList()`

```java
List<String> stooges = Arrays.asList("Larry", "Moe", "Curly");
System.out.println(stooges);
```

2. 转字符串 : `toString()`

```
	char[] k = { 'a', 'f', 'b', 'c', 'e', 'A', 'C', 'B' };
		System.out.println(Arrays.toString(k));// [a, f, b, c, e, A, C, B]
```

1. 复制: `copyOf()`

```java
int[] h = { 1, 2, 3, 3, 3, 3, 6, 6, 6, };
int i[] = Arrays.copyOf(h, 6);
System.out.println("Arrays.copyOf(h, 6);：");
// 输出结果：123333
```




