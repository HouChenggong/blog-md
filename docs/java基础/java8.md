## lambda表达式

Lambda 表达式是一个匿名函数，Lambda表达式基于数学中的λ演算得名，直接对应于其中的lambda抽象，是一个匿名函数，即没有函数名的函数。Lambda表达式可以表示闭包。

```java
new Thread(new Runnable() {
    @Override
    public void run() {
        System.out.println("快速新建并启动一个线程");
    }
}).run();
```

等价于

```java
new Thread(()->{
    System.out.println("快速新建并启动一个线程");
}).run();
```

Lambda 表达式简化了匿名内部类的形式，可以达到同样的效果，但是 Lambda 要优雅的多。虽然最终达到的目的是一样的，但其实内部的实现原理却不相同。

匿名内部类在编译之后会创建一个新的匿名内部类出来，而 Lambda 是调用 JVM `invokedynamic`指令实现的，并不会产生新类。

### 过滤、排序限制、map取值

```java
 List<Goods> list = Arrays.asList(
                new Goods(1, "苹果", 15),
                new Goods(2, "香蕉", 20),
                new Goods(3, "橘子", 10),
                null,
                new Goods(5,"",null));
        list.forEach(e-> System.out.println(e));
        System.out.println("*****************************");
        
        //过滤空数据
        list=list.stream().filter(Objects::nonNull).filter(p->Objects.nonNull(p.getPrice())).collect(Collectors.toList());
        list.forEach(e-> System.out.println(e));
        System.out.println("*****************************");
        
        //过滤、排序、限制返回数量
        List<Goods> collect = list.stream().filter(g -> g.getPrice() > 10).sorted((g2, g1) -> g1.getPrice() - g2.getPrice())
//                .forEach(System.out::println);
                .limit(1)
                .collect(Collectors.toList());
        System.out.println(collect);
        System.out.println("****************************");
        
        //最大值最小值
        Goods goods = list.stream().max((p1, p2) -> p1.getPrice() - p2.getPrice()).get();
        System.out.println("max:"+goods);
        Goods min = list.stream().min((p1, p2) -> p1.getPrice() - p2.getPrice()).get();
        System.out.println("min:"+min);
        System.out.println("******************************");
        
        //把流转为数据
        String collect1 = list.stream().map(Goods::getName).collect(Collectors.joining("-"));
        System.out.println(collect1);
        System.out.println("******************************");
        
        //数据统计
        IntSummaryStatistics ss = list.stream().mapToInt(Goods::getPrice).summaryStatistics();
        System.out.println("sum:"+ss.getSum());
        System.out.println("avg:"+ss.getAverage());
        System.out.println("count:"+ss.getCount());
        System.out.println("max:"+ss.getMax());
        System.out.println("min:"+ss.getMin());
```

## list排序

```java
 onePackList.sort(Comparator.comparing(CodeRulePackage::getOrder)
                  .thenComparing(CodeRulePackage::getNodeName));
```

### reduce

[一个简单的介绍](https://blog.csdn.net/zhang89xiao/article/details/77164866)

```java
List<Integer> list = Arrays.asList(1,2,3,4,5,6,7,8,9,10);
// T reduce(T identity, BinaryOperator<T> accumulator);
//0是identity参数，用来指定Stream循环的初始值。如果Stream为空，就直接返回该值。
Integer sum = list.stream().reduce(0, (x,y) -> x+y);
System.out.println(sum);//55
```

### Optional

```java
@Test
public void whenEmptyValue_thenReturnDefault() {
    User user = null;
    User user2 = new User("anna@gmail.com", "1234");
  //如果user为空，则返回user2
    User result = Optional.ofNullable(user).orElse(user2);

    assertEquals(user2.getEmail(), result.getEmail());
}
//不为空直接返回user
User result = Optional.ofNullable(user).orElseGet( () -> user2);
```

两个 *Optional*  对象都包含非空值，两个方法都会返回对应的非空值。不过，*orElse()* 方法仍然创建了 *User* 对象。**与之相反，\*orElseGet()\* 方法不创建 \**\*User\**\*** **对象。**

在执行较密集的调用时，比如调用 Web 服务或数据查询，**这个差异会对性能产生重大影响**。

### 

```
@Test
public void givenPresentValue_whenCompare_thenOk() {
    User user = new User("john@gmail.com", "1234");
    logger.info("Using orElse");
    User result = Optional.ofNullable(user).orElse(createNewUser());
    logger.info("Using orElseGet");
    User
```

### list转换为string

String  str=String.join(";",listOrSet);

