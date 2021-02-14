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

### 并行流

[Java8并行流](https://mp.weixin.qq.com/s/Jm1OQauP1wedc8Vx_nEpnw)

```java
List<Apple> appleList = new ArrayList<>(); // 假装数据是从库里查出来的

for (Apple apple : appleList) {
    apple.setPrice(5.0 * apple.getWeight() / 1000);
}
```

修改为并行流的格式：

```java
appleList.parallelStream().forEach(apple -> apple.setPrice(5.0 * apple.getWeight() / 1000))
```

#### 并行流真的快吗？

**什么是并行流：** 并行流就是将一个流的内容分成多个数据块，并用不同的线程分别处理每个不同数据块的流。并行流内部使用了默认的 ForkJoinPool 线程池。**默认的线程数量就是处理器的核心数**

- 并行流使用的时候如果涉及拆箱和装箱问题，不一定快，尽量使用
- 尽量使用 LongStream / IntStream / DoubleStream 等原始数据流代替 Stream 来处理数字，以避免频繁拆装箱带来的额外开销

- 对于较少的数据量，不建议使用并行流



以下是一些常见的集合框架对应流的可拆分性能表

| 源              | 可拆分性 |
| :-------------- | :------- |
| ArrayList       | 极佳     |
| LinkedList      | 差       |
| IntStream.range | 极佳     |
| Stream.iterate  | 差       |
| HashSet         | 好       |
| TreeSet         | 好       |

#### 并行流线程安全吗？

运行下面的代码，发现并不安全

```java
public static void main(String[] args) {
//        System.out.println(sideEffectParallelSum(100));
        System.out.println(sideEffectSum(100));
    }
    public static long sideEffectSum(long n) {
        Accumulator accumulator = new Accumulator();
        LongStream.rangeClosed(1, n).forEach(accumulator::add);
        return accumulator.total;
    }

    public static long sideEffectParallelSum(long n) {
        Accumulator accumulator = new Accumulator();
        LongStream.rangeClosed(1, n).parallel().forEach(accumulator::add);
        return accumulator.total;
    }

    public static class Accumulator {
        private long total = 0;

        public void add(long value) {
            total += value;
        }
    }
```

### [函数式编程](https://mp.weixin.qq.com/s/Ko41OG9yFAZZMEi6-C9kBQ)

实现面向对象编程不一定非得使用面向对象编程语言，同理，实现函数式编程也不一定非得使用函数式编程语言。现在，很多面向对象编程语言，也提供了相应的语法、类库来支持函数式编程。

Java这种面向对象编程语言，对函数式编程的支持可以通过一个例子来描述：

```java

public class Demo {
  public static void main(String[] args) {
    Optional<Integer> result = Stream.of("a", "be", "hello")
            .map(s -> s.length())
            .filter(l -> l <= 3)
            .max((o1, o2) -> o1-o2);
    System.out.println(result.get()); // 输出2
  }
}
```

Java为函数式编程引入了三个新的语法概念：Stream类、Lambda表达式和函数接口（Functional Inteface）

#### 函数接口@FunctionalInterface

函数式接口也是Java interface的一种，但还需要满足：

- 一个函数式接口只有一个抽象方法(single abstract method)；

- Object类中的public abstract method不会被视为单一的抽象方法；

- 函数式接口可以有默认方法和静态方法；

- 函数式接口可以用@FunctionalInterface注解进行修饰。

#### 函数式接口的作用

函数式接口有什么用呢？一句话，函数式接口带给我们最大的好处就是：可以使用极简的lambda表达式实例化接口。为什么这么说呢？我们或多或少使用过一些只有一个抽象方法的接口，比如Runnable、ActionListener、Comparator等等，比如我们要用Comparator实现排序算法，我们的处理方式通常无外乎两种：

- 规规矩矩的写一个实现了Comparator接口的Java类去封装排序逻辑。若业务需要多种排序方式，那就得写多个类提供多种实现，而这些实现往往只需使用一次。

- 另外一种聪明一些的做法无外乎就是在需要的地方搞个匿名内部类，比如：

```java

public class Test { 
    public static void main(String args[]) { 
        List<Person> persons = new ArrayList<Person>();
        Collections.sort(persons, new Comparator<Person>(){
            @Override
            public int compare(Person o1, Person o2) {
                return Integer.compareTo(o1.getAge(), o2.getAge());
            }
        });
    } 
}
```

而使用函数式接口的lambda快速实现上面的逻辑代码如下：

```java
Comparator<Person> comparator = (p1, p2) -> Integer.compareTo(p1.getAge(), p2.getAge());
```

#### @FunctionalInterface注解使用场景

我们知道，一个接口只要满足只有一个抽象方法的条件，即可以当成函数式接口使用，有没有 @FunctionalInterface 都无所谓。但是jdk定义了这个注解肯定是有原因的，对于开发者，该注解的使用一定要三思而后续行。

如果使用了此注解，再往接口中新增抽象方法，编译器就会报错，编译不通过。换句话说，@FunctionalInterface 就是一个承诺，承诺该接口世世代代都只会存在这一个抽象方法。因此，凡是使用了这个注解的接口，开发者可放心大胆的使用Lambda来实例化。当然误用 @FunctionalInterface 带来的后果也是极其惨重的：如果哪天你把这个注解去掉，再加一个抽象方法，则所有使用Lambda实例化该接口的客户端代码将全部编译错误。

特别地，当某接口只有一个抽象方法，但没有用 @FunctionalInterface 注解修饰时，则代表别人没有承诺该接口未来不增加抽象方法，所以建议不要用Lambda来实例化，还是老老实实的用以前的方式比较稳妥。

