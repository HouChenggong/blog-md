

## 继承封装和多态

在 Java 中有两种形式可以实现多态：继承（多个子类对同一方法的重写）和接口（实现接口并覆盖接口中同一方法）。

为了实现运行期的多态，或者说是动态绑定，需要满足三个条件。

即有类继承或者接口实现、子类要重写父类的方法、父类的引用指向子类的对象。

- 比如继承的多态

```java
   Parent p = new Son(); //3.父类的引用指向子类的对象
        Parent p1 = new Daughter(); //3.父类的引用指向子类的对象
```

- 比如接口覆盖的多态

```java
  interface PlayService{
	    void play();
}
//玩游戏
class GamesPlayServiceImpl implements PlayService{
		public void play(){
			System.out.println("games play");
		}
}
//和朋友一起玩
class PersonPlayServiceImpl implements PlayService{
	public void play(){
		System.out.println("Person play");
	}
}
 //但是注入的时候，要指定是哪个
    @Qualifier("PersonPlayServiceImpl")
    @Autowired
    private PlayService playService;
//或者通过@Resource注解，因为@Resouce注解默认用的是名称，而@Autowired用的是类型
    @Resource(name = "GamesPlayServiceImpl")
    private PlayService playService2;
```



### 重载和重写的区别

#### 重载

发生在同一个类中，方法名必须相同，参数类型不同、个数不同、顺序不同，方法返回值和访问修饰符可以不同。

下面是《Java 核心技术》对重载这个概念的介绍：

![](https://my-blog-to-use.oss-cn-beijing.aliyuncs.com/bg/desktopjava核心技术-重载.jpg)

#### 重写

重写是子类对父类的允许访问的方法的实现过程进行重新编写,发生在子类中，方法名、参数列表必须相同，返回值范围小于等于父类，抛出的异常范围小于等于父类，访问修饰符范围大于等于父类。另外，如果父类方法访问修饰符为 private 则子类就不能重写该方法。**也就是说方法提供的行为改变，而方法的外貌并没有改变。**

### 构造函数能否被重写？重载？
不能被重写，可以被重载，也就是为什么有那么多构造函数的原因，都是重载的


## Object类

```java
public final native Class<?> getClass();
//本地方法
public native int hashCode();
//比较内存地址
  public boolean equals(Object obj) {
        return (this == obj);
    }
//克隆
protected native Object clone() throws CloneNotSupportedException;    
//toString
 public String toString() {
        return getClass().getName() + "@" + Integer.toHexString(hashCode());
    }  
//随机唤醒一个线程
public final native void notify();
//唤醒所有的线程
public final native void notifyAll();
//等待并释放锁，至于为啥抛出异常，是为了防止
public final native void wait(long timeout) throws InterruptedException;

//当垃圾回收器确定不存在对该对象的更多引用时，由对象的垃圾回收器调用此方法。
protected void finalize() throws Throwable { }
```

### 使用注意

wait, notify/notifyAll 要在 synchronized 内部被使用，并且，如果锁的对象是this，就要 this.wait，this.notify/this.notifyAll , 否则JVM就会抛出 java.lang.IllegalMonitorStateException 的。

```java
synchronized (this){
	obj.notify();
}
//这样是会报错的，因为选错了对象
```



### 1.为什么 wait，notify 和 notifyAll 是在 Object 类中定义的而不是在 Thread 类中定义?

1. 实际上实现的时线程之间的通信机制，用来通知线程的阻塞与唤醒。

2. 为了每个对象都可上锁。由于wait，notify和notifyAll都是锁级别的操作，所以把他们定义在Object类中因为锁属于对象。

3. 线程A并不知道当前对象被哪个线程占用着，它只知道当前对象肯定是被某个线程占用着，所以他（线程A）是被通知的一方，也就是等对象通知他什么时候可以区获取锁。

就比如说，10个顾客（线程）去预约一个餐厅（对象），假设他们都会占用该餐厅的所有资源，肯定是餐厅通知他们什么时候可以来，而不是一个顾客吃完去通知下一个顾客

###  2.notify()和notifyAll ()执行完会释放锁吗？

不会

直到执行完synchronized 代码块的代码或是中途遇到wait() ，再次释放锁。

也就是说，notify/notifyAll() 的执行只是唤醒沉睡的线程，而不会立即释放锁，锁的释放要看代码块的具体执行情况

### 3.为什么wait()、notify()和notifyAll ()必须在同步方法或者同步块中被调用？

比如说下面的代码：

```java
    public static void main(String[] args) throws InterruptedException {
        Object o=new Object();
     	o.wait();
     }
//Exception in thread "main" java.lang.IllegalMonitorStateException
```

下面的代码不会抛出异常

```java
 public static void main(String[] args) throws InterruptedException {
        Object o=new Object();
        synchronized (o){
            o.wait(1000);
        }
        System.out.println("end");
    }
    //end
```



0. JDK要求对object.wait()和object().notify方法必须在synchronized代码块内部使用，否则运行时会抛出IllegalMonitorStateException异常。
1. 这三个方法想要运行的前提是他们有当前对象的锁，然后再执行相关的方法
2. 想要获取锁，就会出现线程安全的问题，一个对象不能让两个线程获取锁，所以需要同步机制，所以他们只能在同步方法或者同步块中被调用。

### 4. 什么时候用notify和notifyAll

先说两个概念：锁池和等待池

- 锁池:假设线程A已经拥有了某个对象(注意:不是类)的锁，而其它的线程想要调用这个对象的某个synchronized方法(或者synchronized块)，由于这些线程在进入对象的synchronized方法之前必须先获得该对象的锁的拥有权，但是该对象的锁目前正被线程A拥有，所以这些线程就进入了该对象的锁池中。
- 等待池:(因为wait()方法必须出现在synchronized中，这样自然在执行wait()方法之前线程A就已经拥有了该对象的锁)。假设一个线程A调用了某个对象的wait()方法，线程A就会释放该对象的锁后，进入到了该对象的等待池中。如果另外的一个线程调用了相同对象的notifyAll()方法，那么处于该对象的等待池中的线程就会全部进入该对象的锁池中，准备争夺锁的拥有权。如果另外的一个线程调用了相同对象的notify()方法，那么仅仅有一个处于该对象的等待池中的线程(随机)会进入该对象的锁池.

 然后再来说notify和notifyAll的区别

-  如果线程调用了对象的 wait()方法，那么线程便会处于该对象的**等待池**中，等待池中的线程**不会去竞争该对象的锁**。
- 当有线程调用了对象的 **notifyAll**()方法（唤醒所有 wait 线程）或 **notify**()方法（只随机唤醒一个 wait 线程），被唤醒的的线程便会进入该对象的锁池中，锁池中的线程会去竞争该对象锁。也就是说，调用了notify后只要一个线程会由等待池进入锁池，而notifyAll会将该对象等待池内的所有线程移动到锁池中，等待锁竞争
- 优先级高的线程竞争到对象锁的概率大，假若某线程没有竞争到该对象锁，它**还会留在锁池中**，唯有线程再次调用 wait()方法，它才会重新回到等待池中。而竞争到对象锁的线程则继续往下执行，直到执行完了 synchronized 代码块，它会释放掉该对象锁，这时锁池中的线程会继续竞争该对象锁。

 

### 4.举个例子证明为啥需要同步代码块
- Lost Wake-Up Problem 问题

  一个简单的生产者-消费者模型的实现如下：当count为0的时候，生产者进行生产操作，并将count+1，然后调用notify()方法通知；当count为0时，消费者会调用wait()方法释放锁进行等待。

```java
private int count = 0;
private Object obj;

public void producer(){
    if (count == 0){
        //省略生产者逻辑
        count++;
        obj.notify();
　　}
}

public void consumer(){
    while (count == 0){
　　　　obj.wait();    
    }
    //省略消费逻辑
}
```

  乍一看，通过上述代码实现了生产者-消费者的功能，但是仔细一想，存在问题。假如此时线程T1在执行producer的逻辑，线程T2在执行consumer的逻辑，如果代码的执行顺序变成下面这样，就会有问题：

​    1.线程T2执行while (count == 0)，表达式成立，进入while循环；

​    2.线程T1执行if (count == 0)，表达式成立，进入if消息体；

​    3.线程T1执行if消息体内容，最终调用obj.notify()（注意，此时线程T2未执行obj.wait()，notify()不会唤醒任何线程）；

​    4.线程T2执行obj.wait()进行等待；

  这样执行完之后，count的值为1，生产者不会再进行生产操作（也就不会调用obj.notify()，而此时消费者线程T2处于等待状态（需要obj.notify()来唤醒），消费者线程就永远地死等下去了，这就是多线程编程中臭名昭著的**Lost Wake-Up Problem**问题。

- 改成同步代码块有什么作用？

仔细分析上面的问题，原因很简单，就是对count变量的读写存在竞态条件，举个例子，consumer()方法原本的用意是在执行obj.wait()的时候，count的值必须为0，也就是obj.wait()和count为0是绑定的，但是此时如果有另外一个线程在执行producer()方法，可能就会在执行while (count == 0)到obj.wait()之间对count的值进行修改，从而出现非预期的情况（即在执行obj.wait()方法的时候，count的值不是0）。

```java
private int count = 0;
private Object obj;
 
public void producer(){
    synchonized(obj){
        if (count == 0){
            //省略生产逻辑
            count++;
            obj.notify();
        }
    }
}
 
public void consumer(){
    synchonized(obj){
        while (count == 0){
            obj.wait();    
        }
        //省略消费逻辑
    }
}
```

此时，由于只有线程拿到obj对象的锁才能进入同步代码块，所以能够保证生产者和消费者只有一个线程在执行，也就保证了在执行while (count == 0)到obj.wait()之间count的值不会发生改变，也就是上面1、4步骤之间，不可能会有2、3步骤了。

### 5.解决虚假唤醒问题为啥用while而不用if

运行下面的案例你就明白了

[一个比较好的案例](https://blog.csdn.net/qq_39455116/article/details/87101633)

#### 6. wait()之后的代码会被执行吗？

- 答案是：不能被执行

```java
        public  static  void test(){
        Object o=new Object();
            new Thread(()->{
                synchronized (o){
                    System.out.println("1");
                    try {
                        o.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("2");
                    System.out.println("3");
                }


            },"线程1").start();
        }
```

#### sleep和yield的区别

- 相同：都会释放CPU

- 不同

  - sleep() 方法给其他线程运行机会时不考虑线程的优先级；yield() 方法只会给相同优先级或更高优先级的线程运行的机会

  - 线程执行 sleep() 方法后进入阻塞状态；线程执行 yield() 方法转入就绪状态，可能马上又得得到执行
  - sleep() 方法声明抛出 InterruptedException；yield() 方法没有声明抛出异常
  - sleep() 方法需要指定时间参数；yield() 方法出让 CPU 的执行权时间由 JVM 控制

## String 

### string常见小功能

- 判断是否为空

Apache 的 commons-lang3 包，有各式各样判空的方法。更重要的是，可以省却判 null 的操作，因为 StringUtils 的所有方法都是 null 安全的。

```java
import org.apache.commons.lang3.StringUtils;
StringUtils.isBlank(" ");
StringUtils.isNotBlank(" ");
```

- 裁剪字符串

```java
   public static String removeLastChar(String s) {
        return (s == null || s.length() == 0)
                ? null
                : (s.substring(0, s.length() - 1));
    }
```

- 裁剪推荐写法（安全）

```java
String s = "x x";
StringUtils.substring(s, 0, s.length() - 1);
```

- 删除最后一个字符（安全）

```java
s=   StringUtils.chop(s);
```

- 替换字符串（null非安全）

```java
String all=null;
all=all.replaceAll("=","x");
//所以要判断是否是null
String result= (s == null) ? null : s.replaceAll(".$", "");
```

- 替换字符串推荐写法lambda和Optional

```java
        String s1="";
        String result1 = Optional.ofNullable(s1)
                .map(str -> str.replaceAll(".$", ""))
                .orElse(s);
```

- 统计字符串出现的次数

```java
long count = someString.chars().filter(ch -> ch == 'e').count();
//或者使用工具类
int count2 = StringUtils.countMatches("xxxxeee", "e");
```

- 拆分字符串

```
String[] splitted = "aaa，bbb".split("，");
//但是上面的null不安全的，所有也可以用一些工具类
String[] splitted = StringUtils.split("a a a，vvv", "，");
```



### 随机字符串

```java
int length = 6;
boolean useLetters = true;
// 不使用数字
boolean useNumbers = false;
String generatedString = RandomStringUtils.random(length, useLetters, useNumbers);

System.out.println(generatedString);
```

但是不推荐这样用做系统ID相关的，还是推荐分布式ID

**String 源码**

```java
public final class String
    implements java.io.Serializable, Comparable<String>, CharSequence
private final char value[];
// Default to 0
private int hash;  
//String 重写了它的hash方法
public int hashCode() {
        int h = hash;
        if (h == 0 && value.length > 0) {
            char val[] = value;

            for (int i = 0; i < value.length; i++) {
                h = 31 * h + val[i];
            }
            hash = h;
        }
        return h;
    }
//String 重写了它的equals方法
//其实就是判断每一格字符是否相等
  public boolean equals(Object anObject) {
        if (this == anObject) {
            return true;
        }
        if (anObject instanceof String) {
            String anotherString = (String)anObject;
            int n = value.length;
            if (n == anotherString.value.length) {
                char v1[] = value;
                char v2[] = anotherString.value;
                int i = 0;
                while (n-- != 0) {
                    if (v1[i] != v2[i])
                        return false;
                    i++;
                }
                return true;
            }
        }
        return false;
    }
```

### string的hash值为啥用31做乘子？

**为啥选用质数？**

这个还不是很理解

“100内所有的质数列举如下: 2、3、5、7、11、13、17、19、23、29、31、37、41、43、47、53、59、61、67、71、73、79、83、89、97

**为啥不用偶数？**

也不是很理解

但是用31的确是有优势的

1. 31是一个不大不小的质数，是作为 hashCode 乘子的优选质数之一。另外一些相近的质数，比如37、41、43等等，也都是不错的选择。那么为啥偏偏选中了31呢？请看第二个原因
2. 31可以被 JVM 优化，`31 * i = (i << 5) - i`。
3. 相比37 41等其实hash碰撞的几率差不太多

### string为啥不可变？

可变性**

简单的来说：String 类中使用 final 关键字修饰字符数组来保存字符串，`private final char value[]`，所以 String 对象是不可变的。

> 在 Java 9 之后，String 类的实现改用 byte 数组存储字符串 `private final byte[] value`
>
> 为什么使用byte字节而舍弃了char字符:
>
> 节省内存占用，byte占一个字节(8位)，char占用2个字节（16），相较char节省一半的内存空间。节省gc压力。重构带来的最大好处就是在字符串中所有的字符都小于0xFF的情况下，会节省一半的内存。
>
> 针对初始化的字符，对字符长度进行判断选择不同的编码方式。如果是 LATIN-1 编码，则右移0位，数组长度即为字符串长度。而如果是 UTF16 编码，则右移1位，数组长度的二分之一为字符串长度。
>
> 因为一个英文就占用一个字节，而中文或者其它的语言可能不太一样需要占用2-3个字节，
>
> 老外估计早就想改了，老子可能一辈子做的东西都用不到0xFF之上的字符，却要占我一倍的内存！！哈哈~~
>
> 

```
    //JDK1.9 String 的结构
    private final byte[] value;

    private final byte coder;

    @Native static final byte LATIN1 = 0;
    @Native static final byte UTF16  = 1;
```



### String拼接方法

测试过程，先新建一个类

```
public class TestString {
    public static void main(String[] args) {
        String a = "a";
        a = a + "b";
    }
}
```

然后命令行里面输入：`javac TestString.java`这个时候会出现TestString.class在同级目录中

然后输入: `javap -verbose  TestString` 即可看到反编译的字节码

#### 1. 直接+

```
String a="a"+"b";
//javac 反编译出来是这样的
   String var1 = "ab";
//javap 出来是下面这样的
 // String ab
```



#### 2.在一个对象上+

发现用到了StringBuilder

```java
String a = "a";
a = a + "b"; 
```

相当于

```java
String a="a";
a=StringBuilder.append(a).append(b).toString();
//下面是StringBuiler的toString方法
public String toString() {
  // Create a copy, don't share the array
  return new String(value, 0, count);
}
所以创建了3个对象，因为String a是一个；StringBuilder是一个；toString的时候创建了1个，所以是3个
```



```
String a = "a";
a = a + "b";     
//javac 出来是这样的
String var1 = "a";
var1 = var1 + "b";
//javap 出来是下面这样的
stack=2, locals=2, args_size=1
0: ldc           #2                  // String a
2: astore_1
3: new           #3                  // class java/lang/StringBuilder
6: dup
7: invokespecial #4                  // Method java/lang/StringBuilder."<init>":()V
10: aload_1
11: invokevirtual #5                  // Method java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
14: ldc           #6                  // String b
16: invokevirtual #5                  // Method java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
19: invokevirtual #7                  // Method java/lang/StringBuilder.toString:()Ljava/lang/String;

```

#### 3.String.join()

```java
public static String join(CharSequence delimiter, CharSequence... elements) {
    Objects.requireNonNull(delimiter);
    Objects.requireNonNull(elements);
    // Number of elements not likely worth Arrays.stream overhead.
    StringJoiner joiner = new StringJoiner(delimiter);
    for (CharSequence cs: elements) {
        joiner.add(cs);
    }
    return joiner.toString();
}
```

发现底层用的是StringJoiner对象里面的add()方法

- 实际使用，可以说数组或者说list、set等

```java
String mutiXi[] = new String[]{"1", "2", "3"};
String str2 = String.join("-", mutiXi);
System.out.println(str2);
//1-2-3
```

```java
Set<String> set = new HashSet<>();
set.add("java");
set.add("c");
set.add("javaSricpt");
String s = String.join(",", set);
System.out.println(s);
//c,xjava,javaSricpt
```



#### 4.concat()

这段代码首先创建了一个字符数组，长度是已有字符串和待拼接字符串的长度之和，再把两个字符串的值复制到新的字符数组中，并使用这个字符数组创建一个新的String对象并返回。通过源码我们也可以看到，经过concat方法，其实是new了一个新的String

```java
    public String concat(String str) {
        int otherLen = str.length();
        if (otherLen == 0) {
            return this;
        }
        int len = value.length;
        char buf[] = Arrays.copyOf(value, len + otherLen);
        str.getChars(buf, len);
        return new String(buf, true);
    }
```



#### 5.StringJoiner、StringBuffer、StringBuilder

### StringJoiner

```java
        StringJoiner joiner=new StringJoiner("-");
        joiner.add("1");
        joiner.add("2");
        joiner.add("3");
        System.out.println(joiner);//1-2-3
//如果把上面的换一行代码
   StringJoiner joiner=new StringJoiner("-","[","]");
//结果就是：[1-2-3]
```

StringJoiner源码：内部是用StringBuilder进行拼接的

```java
public final class StringJoiner {
//前缀
private final String prefix;
//分隔符
private final String delimiter;
//后缀
private final String suffix;
//值
private StringBuilder value;
//空值,如果传入的值是null,默认是前缀加后缀
private String emptyValue;
    //只有分隔符的构造函数
    public StringJoiner(CharSequence delimiter) {
        this(delimiter, "", "");
    }
		//构造函数，分隔符、前缀、后缀
        public StringJoiner(CharSequence delimiter,
                        CharSequence prefix,
                        CharSequence suffix) {
        this.prefix = prefix.toString();
        this.delimiter = delimiter.toString();
        this.suffix = suffix.toString();
            //空值为前缀+间隔符
        this.emptyValue = this.prefix + this.suffix;
        }
}
```

add方法

```java
    public StringJoiner add(CharSequence newElement) {
        prepareBuilder().append(newElement);
        return this;
    }
    
private StringBuilder prepareBuilder() {
        if (value != null) {
        //添加分隔符
            value.append(delimiter);
        } else {
        //添加前缀
            value = new StringBuilder().append(prefix);
        }
        return value;
    }
    
     @Override
    public String toString() {
        if (value == null) {
            return emptyValue;
        } else {
            if (suffix.equals("")) {
                return value.toString();
            } else {
                int initialLength = value.length();
                String result = value.append(suffix).toString();
                // reset value to pre-append initialLength
                value.setLength(initialLength);
                return result;
            }
        }
    }
```

#### String.intern()

把结果保存到字符串常量池，只有调用的时候才会去存储

```
String a="xxx";//会存储到字符串常量池
String b=new String("xxx1");//不会存储到字符串常量池，再堆中存储
b.intern();//会把b的string类型存储到字符串常量池
```



### StringBuffer与StringBuilder



而 StringBuilder 与 StringBuffer 都继承自 AbstractStringBuilder 类，在 AbstractStringBuilder 中也是使用字符数组保存字符串`char[]value` 但是没有用 final 关键字修饰，所以这两种对象都是可变的。

StringBuilder 与 StringBuffer 的构造方法都是调用父类构造方法也就是 AbstractStringBuilder 实现的，大家可以自行查阅源码。

`AbstractStringBuilder.java`

```java
abstract class AbstractStringBuilder implements Appendable, CharSequence {
    /**
     * The value is used for character storage.
     */
    char[] value;

    /**
     * The count is the number of characters used.
     */
    int count;

    AbstractStringBuilder(int capacity) {
        value = new char[capacity];
    }
```

**线程安全性**

String 中的对象是不可变的，也就可以理解为常量，线程安全。AbstractStringBuilder 是 StringBuilder 与 StringBuffer 的公共父类，定义了一些字符串的基本操作，如 expandCapacity、append、insert、indexOf 等公共方法。StringBuffer 对方法加了同步锁或者对调用的方法加了同步锁，所以是线程安全的。StringBuilder 并没有对方法进行加同步锁，所以是非线程安全的。

```java
    //StringBuffer源码
    @Override
    public synchronized StringBuffer append(String str) {
        toStringCache = null;
        super.append(str);
        return this;
    }
    //StringBuilder源码
    @Override
    public StringBuilder append(String str) {
        super.append(str);
        return this;
    }
```

**性能**

每次对 String 类型进行改变的时候，都会生成一个新的 String 对象，然后将指针指向新的 String 对象。StringBuffer 每次都会对 StringBuffer 对象本身进行操作，而不是生成新的对象并改变对象引用。相同情况下使用 StringBuilder 相比使用 StringBuffer 仅能获得 10%~15% 左右的性能提升，但却要冒多线程不安全的风险。

**对于三者使用的总结：**

1. 操作少量的数据: 适用 String
2. 单线程操作字符串缓冲区下操作大量数据: 适用 StringBuilder
3. 多线程操作字符串缓冲区下操作大量数据: 适用 StringBuffer



### StringBuffer和StringBuilder的扩容问题？

我相信这个问题很多同学都没有注意到吧，其实 StringBuilder 和 StringBuffer 存在扩容问题，先从 StringBuilder 开始看起

首先先注意一下 StringBuilder 的初始容量

```
public StringBuilder() {
  super(16);
}
```

StringBuilder 的初始容量是 16，当然也可以指定 StringBuilder 的初始容量。

在调用 append 拼接字符串，会调用 AbstractStringBuilder 中的 append 方法

```java
public AbstractStringBuilder append(String str) {
  if (str == null)
    return appendNull();
  int len = str.length();
  ensureCapacityInternal(count + len);
  str.getChars(0, len, value, count);
  count += len;
  return this;
}
```

上面代码中有一个 `ensureCapacityInternal` 方法，这个就是扩容方法，我们跟进去看一下

```java
private void ensureCapacityInternal(int minimumCapacity) {
  // overflow-conscious code
  if (minimumCapacity - value.length > 0) {
    value = Arrays.copyOf(value,
                          newCapacity(minimumCapacity));
  }
}
```

这个方法会进行判断，minimumCapacity 就是字符长度 + 要拼接的字符串长度，如果拼接后的字符串要比当前字符长度大的话，会进行数据的复制，真正扩容的方法是在 `newCapacity` 中

```java
private int newCapacity(int minCapacity) {
  // overflow-conscious code
  int newCapacity = (value.length << 1) + 2;
  if (newCapacity - minCapacity < 0) {
    newCapacity = minCapacity;
  }
  return (newCapacity <= 0 || MAX_ARRAY_SIZE - newCapacity < 0)
    ? hugeCapacity(minCapacity)
    : newCapacity;
}
```

扩容后的字符串长度会是原字符串长度增加一倍 + 2，如果扩容后的长度还比拼接后的字符串长度小的话，那就直接扩容到它需要的长度  newCapacity = minCapacity，然后再进行数组的拷贝。

### 为啥不推荐在循环里面用+

每次都会创建一个 StringBuilder ，并把引用赋给 StringBuilder 对象，因此每个 StringBuilder 对象都是`强引用`， 这样在创建完毕后，内存中就会多了很多 StringBuilder 的无用对象。

## hashcode equals 
### == 与 equals的区别

**==** : 它的作用是判断两个对象的地址是不是相等。即，判断两个对象是不是同一个对象(基本数据类型==比较的是值，引用数据类型==比较的是内存地址)。

**equals()** : 它的作用也是判断两个对象是否相等。但它一般有两种使用情况：

- 情况 1：类没有覆盖 equals() 方法。则通过 equals() 比较该类的两个对象时，等价于通过“==”比较这两个对象。
- 情况 2：类覆盖了 equals() 方法。一般，我们都覆盖 equals() 方法来比较两个对象的内容是否相等；若它们的内容相等，则返回 true (即，认为这两个对象相等)。

**举个例子：**

```java
public class test1 {
    public static void main(String[] args) {
        String a = new String("ab"); // a 为一个引用
        String b = new String("ab"); // b为另一个引用,对象的内容一样
        String aa = "ab"; // 放在常量池中
        String bb = "ab"; // 从常量池中查找
        if (aa == bb) // true
            System.out.println("aa==bb");
        if (a == b) // false，非同一对象
            System.out.println("a==b");
        if (a.equals(b)) // true
            System.out.println("aEQb");
        if (42 == 42.0) { // true
            System.out.println("true");
        }
    }
}
```

**说明：**

- String 中的 equals 方法是被重写过的，因为 object 的 equals 方法是比较的对象的内存地址，而 String 的 equals 方法比较的是对象的值。
- 当创建 String 类型的对象时，虚拟机会在常量池中查找有没有已经存在的值和要创建的值相同的对象，如果有就把它赋给当前引用。如果没有就在常量池中重新创建一个 String 对象。




### hashCode（）介绍

hashCode() 的作用是根据对象内存地址获取哈希码 ；它实际上是返回一个 int 整数 

hashcode有自己的一套算法，当然一个对象无论计算多少次，hashcode都是相同的,当然是在没有重写hashcode的情况下

### 为什么要有 hashCode

我的理解是：如果你没有使用hash类型的数据结构的时候，就没有啥意义，使用hash类型的结构的时候，因为存储的方式是用hashcode来确定列表中的位置

- 那引入为啥使用hashmap和hashset的问题

比如说，你有1万个对象，里面有只有100个id是一样的，你想获取这100个不一样的id，你要怎么办？

这个时候你直接说HashSet，或者数据库去重，但是如果让你不用这些结构，你怎么办？

用一个数组，存储不一样的id，如果存在就不存了，但是每次都要去数组里面从头到尾去对比有没有一样的，如果数组长度小还比较好，如果数组长度是1万个，你怎么办？

其实hashcode就是为了简化我们遍历用的，我们不用每个都去对比，我们只需要对比相同hashcode的对象即可



 这个**哈希码的作用**是确定该对象在哈希表中的索引位置。**`hashCode()`在散列表中才有用，在其它情况下没用**。在散列表中 hashCode() 的作用是获取对象的散列码，进而确定该对象在散列表中的位置。

### hashCode（）与 equals（）的相关规定

1. 如果两个对象相等，则 hashcode 一定也是相同的
2. 两个对象相等,对两个对象分别调用 equals 方法都返回 true
3. 两个对象有相同的 hashcode 值，它们也不一定是相等的
4. **因此，equals 方法被覆盖过，则 hashCode 方法也必须被覆盖**
5. hashCode() 的默认行为是对堆上的对象产生独特值。如果没有重写 hashCode()，则该 class 的两个对象无论如何都不会相等（即使这两个对象指向相同的数据）

### 为啥重写equals必须重写hashcode

不重写就会出现equals相同，但是hashcode不同的情况，我们在用hash类型的数据结构的时候，就达不到我们想要的效果

```java
我们为啥要用hashmap或者hashset？
1. 其实就是简单的去重
2. 或者理解为我们去寻找一个数是否存在的时候，不再遍历全部的数据
3. 为此我们引入hash这个概念，让我们认为的同一个对象每次存储的时候都存放在相同的位置
```



```java
1.如果我们判断对象相等，用的是ID判断
2.那么我们用equals的时候，只要ID相等就可以认为是一样的
3.但是如果我们不重写hashcode，就会出现2个对象的hashcode不相等，导致我们存放数据的时候
4.存储在不同的数组单元格内，这样就违反了我们用hashmap的初衷，我们让我们认为的同一个对象存放了多次
5. 因此失去了hash结构的意义
```




### 自动拆箱与装箱

- **装箱**：将基本类型用它们对应的引用类型包装起来；
- **拆箱**：将包装类型转换为基本数据类型；

### 常量池

**基本类型有**：byte、short、char、int、long、boolean。
**基本类型的包装类分别是**：Byte、Short、Character、Integer、Long、Boolean。

对于int short long  byte 都是-128 到127之间,具体代码在valueOf里面

```java
//low =-128 high=127   
public static Integer valueOf(int i) {
        if (i >= IntegerCache.low && i <= IntegerCache.high)
            return IntegerCache.cache[i + (-IntegerCache.low)];
        return new Integer(i);
    }

// 常量池技术
//从这2段代码可以看出，在通过valueOf方法创建Integer对象的时候，如果数值在[-128,127]之间，
//便返回指向IntegerCache.cache中已经存在的对象的引用；否则创建一个新的Integer对象。


 private static class IntegerCache {
        static final int low = -128;
        static final int high;
        static final Integer cache[];

        static {
            // high value may be configured by property
            int h = 127;
            String integerCacheHighPropValue =
                sun.misc.VM.getSavedProperty("java.lang.Integer.IntegerCache.high");
            if (integerCacheHighPropValue != null) {
                try {
                    int i = parseInt(integerCacheHighPropValue);
                    i = Math.max(i, 127);
                    // Maximum array size is Integer.MAX_VALUE
                    h = Math.min(i, Integer.MAX_VALUE - (-low) -1);
                } catch( NumberFormatException nfe) {
                    // If the property cannot be parsed into an int, ignore it.
                }
            }
            high = h;

            cache = new Integer[(high - low) + 1];
            int j = low;
            for(int k = 0; k < cache.length; k++)
                cache[k] = new Integer(j++);

            // range [-128, 127] must be interned (JLS7 5.1.7)
            assert IntegerCache.high >= 127;
        }

        private IntegerCache() {}
    }
```

但是对于double没有实现常量池技术，为啥double没有实现常量池技术呢，因为Int在一个范围内的数量是有限的，而double在一个范围内的数量是无限的，类似的还有Float

- == 和equal在数字类型的比较
  - 当 "=="运算符的两个操作数都是 包装器类型的引用，则是比较指向的是否是同一个对象，而如果其中有一个操作数是表达式（即包含算术运算）则比较的是数值（即会触发自动拆箱的过程）

```java
        Integer a = 1;
        Integer b = 2;
        Integer c = 3;
        System.out.println(c == (a + b));//有运算符号，==比较的变为值，所以是true
        System.out.println(c.equals(a + b));// true
        Integer d = 3;
        System.out.println(c == d);//常量池相等true
        Integer e = 321;
        Integer f = 321;
        System.out.println(e == f);//超过常量池范围false
        Long g = 3L;
        System.out.println(g == (a + b));//比较的是值，拆箱true
        Long h = 2L;
        //如果数值是int类型的，装箱过程调用的是Integer.valueOf；如果是long类型的，装箱调用的Long.valueOf方法
        //之所以不相等是因为一个是Long 一个是Integer
        System.out.println(g.equals(a + b));//false
        //a+h会变成Long类型，所以相等
        System.out.println(g.equals(a + h));//true

```

### 为什么 Java 中只有值传递？

[为啥只有值传参](<https://blog.csdn.net/qq_39455116/article/details/83617271>)

下面三种种情况的后两种的修改都会影响到真实的数据值，为啥？

```java
    public  static void test(Role role){
        //role的结果不变
      //引用虽然拷贝了，但是它马上把这个引用指向了另一块内存地址，所以不影响原始对象
        role=new Role("222","xxxx");
    }
    public  static void test2(Role role){
      //拷贝的是引用的拷贝（引用的再次拷贝）,指向了同一块内存地址，对user的修改影响实际的值
        Role role1=role;
        role1.setName("xxxxxx");
        //最后role的结果改变了
    }
    public  static void test3(Role role){
        //role的结果改变
      //拷贝的是引用,指向了同一块内存地址，对user的修改影响实际的值
        role.setName("xxxxxx");
    }
```

即：**一个方法的引用传参可以修改原来的内存地址的值，但是不能让它指向一个新的内存地址**

总结：

```java
在Java中所有的参数传递，不管基本类型还是引用类型，
	都是值传递，或者说是副本传递。
	只是在传递过程中：

A:如果是对基本数据类型的数据进行操作，
    由于原始内容和副本都是存储实际值，并且是在不同的栈帧区，
    因此形参的操作，不影响原始内容。

B:如果是对引用类型的数据进行操作，其实拷贝的都是对内存地址的引用，分两种情况，
  一种是拷贝引用，直接在引用上修改，影响原值
  一种是拷贝引用，但是把引用指向了一个新的内存地址，所以不影响原来的值
```

- 特殊情况
  **String, Integer, Double等特殊基础类型的包装类，可以理解为传值**，最后的操作不会修改实参对象。

  **StringBuilder 、StringBuffer  是引用传参**和string完全不同

  **数组类型的也是引用传参**，因为实际数据在堆中保存着


为什么？

```java
1. 当运行一个main()方法的时候，会往虚拟机栈中压入一个栈帧，
    即为当前栈帧（Main栈帧），用来存放main()中的局部变量表
    (包括参数)、操作栈、方法出口等信息 
2.而传入另一个方法中的值是在当前栈帧（main栈帧）中的

3.当执行到myTest（）或者myTest2()方法时，
JVM也会往虚拟机栈栈中压入一个栈，即为myTest()栈帧或者myTest2()栈帧
    这个栈帧用来存储这个方法中的局部变量信息，
    所以，当前栈帧操作的数据是在当前栈帧里面，只是"值"是main栈帧的副本
    不同的是副本是值还是引用地址
```

## 关键字



### final关键字

final 关键字主要用在三个地方：变量、方法、类。

1. 对于一个 final 变量，如果是基本数据类型的变量，则其数值一旦在初始化之后便不能更改；如果是引用类型的变量，则在对其初始化之后便不能再让其指向另一个对象。
2. 当用 final 修饰一个类时，表明这个类不能被继承。final 类中的所有成员方法都会被隐式地指定为 final 方法。
3. 当final修饰方法时，方法不能被重写

### this 关键字

- **作用一、消除歧义**

如果参数名和成员变量的名字相同，就需要使用 this 关键字消除歧义：this.age 是指成员变量，age 是指构造方法的参数。

```java
public class Writer {
    private int age;
    private String name;

    public Writer(int age, String name) {
        this.age = age;
        this.name = name;
    }
}
```

- **作用二、引用当前类的其他构造方法**

```java
public class Writer {
    private int age;
    private String name;

    public Writer(int age, String name) {
        this.age = age;
        this.name = name;
    }
	//在无参构造方法中调用有参构造方法：
    public Writer() {
        this(18, "xiyou");
    }
}
```

在有参构造方法中调用无参构造方法：

```java
public class Writer {
    private int age;
    private String name;

    public Writer(int age, String name) {
        //this() 必须是构造方法中的第一条语句，否则就会报错
        this();
        this.age = age;
        this.name = name;
    }

    public Writer() {
    }
}
```

- **作用三：作为参数传递**

this 就是我们在 `main()` 方法中使用 new 关键字创建的 ThisTest 对象。

```java
public class ThisTest {
    public ThisTest() {
        print(this);
    }

    private void print(ThisTest thisTest) {
        System.out.println("print " +thisTest);
    }

    public static void main(String[] args) {
        ThisTest test = new ThisTest();
        System.out.println("main " + test);
    }
}
```

- 作用四：链式调用（建造者模式）不做解读，可以去原文理解
- 作用五：在内部类访问外部类对象

```java
public class ThisInnerTest {
    private String name;

    class InnerClass {
        //在内部类 InnerClass 的构造方法中，通过外部类.this 可以获取到外部类对象，
        //然后就可以使用外部类的成员变量了，比如说 name。
        public InnerClass() {
            ThisInnerTest thisInnerTest = ThisInnerTest.this;
            String outerName = thisInnerTest.name;
        }
    }
}
```



### 深拷贝 vs 浅拷贝

1. **浅拷贝**：对基本数据类型进行值传递，对引用数据类型进行引用传递般的拷贝，此为浅拷贝。

   - 实现方式：1. 实现Cloneable接口，在bean里面重写clone()方法，权限为public。
   - 拷贝构造函数实现

   ```java
       public Teacher(Age age,String name) {
           this.age=age;
           this.name=name;
       }
   ```

2. **深拷贝**：对基本数据类型进行值传递，对引用数据类型，创建一个新的对象，并复制其内容，此为深拷贝。

   - 方式1：A方案.深复制条件 ---父类和子类（所有涉及到的类）都要实现Cloneable（）接口

   - 方式1 B方案. 父类重写Clone接口的时候，要把子类带上

   - 方式二：涉及到的类都去实现序列化接口

[详细解读](<https://blog.csdn.net/qq_39455116/article/details/82886328>)

#### 浅拷贝方法BeanUtils.copyProperties(origin,newObject);

```java
Role role = new Role("r1", "管理员");
User user = new User("u1", "xiyou", role);
user.setRole(role);
User copyUser = new User();
BeanUtils.copyProperties(user, copyUser);
System.out.println(user.toString());
System.out.println(copyUser.toString());
System.out.println(copyUser == user);
copyUser.setName("xiyouV2");
System.out.println(user.toString());
role.setName("管理员V2");
System.out.println(user.toString());
System.out.println(copyUser.toString());
//结果发现如果只修改copyUser的nam属性，发现user没有改变，只是copyUser改变了
//如果修改role属性，发现user和copyUser都改变了，说明是浅拷贝，没有拷贝内部对象
```

#### 深拷贝方法SerializationUtils.clone（origin）

```java
Role role2 = new Role("r2", "管理员2");
User user2 = new User("u2", "xiyou2", role2);
user2.setRole(role2);
User copyUser2 = SerializationUtils.clone(user2);

System.out.println(user2.toString());
System.out.println(copyUser2.toString());
System.out.println(copyUser2 == user2);
copyUser2.setName("xiyouV2");
System.out.println(user2.toString());
role2.setName("管理员V2");
System.out.println(user2.toString());
System.out.println(copyUser2.toString());
//结果发现如果只修改copyUser的nam属性，发现user没有改变，只是copyUser改变了
//如果修改role属性，发现user改变了，但是copyUser没有改变
```



###  switch 对于string的支持

- **switch对int的判断是直接比较整数的值**
- 对char类型进行比较的时候，实际上比较的是ascii码，编译器会把char型变量转换成对应的int型变量
- 原来字符串的switch是通过`equals()`和`hashCode()`方法来实现的。。**记住，switch中只能使用整型**

**其实switch只支持一种数据类型，那就是整型，其他数据类型都是转换成整型之后在使用switch的。**

为啥不用Long类型比较，因为String在比较的时候计算的是hash值计算的hashcode，如果hashcode一致再比较equals

### 快速失败、快速安全

- fail fast 

其实就是如果遇到错误，直接返回失败

```java
public int divide(int divisor,int dividend){
    if(dividend == 0){
        throw new RuntimeException("dividend can't be null");
    }
    return divisor/dividend;
}
```

- 集和中的快速失败

在Java中， 如果在foreach 循环里对某些集合元素进行元素的 remove/add 操作的时候，就会触发fail-fast机制，进而抛出CMException。

```java
 List<String> userNames = new ArrayList<String>() {{
            add("Hollis");
            add("hollis");
            add("HollisChuang");
            add("H");
        }};

        for (String userName : userNames) {
            if (userName.equals("Hollis")) {
                userNames.remove(userName);
            }
        }

        System.out.println(userNames);
//Exception in thread "main" java.util.ConcurrentModificationException
```

简单总结一下，之所以会抛出CMException异常，是因为我们的代码中使用了增强for循环，而在增强for循环中，集合遍历是通过iterator进行的，但是元素的add/remove却是直接使用的集合类自己的方法。这就导致iterator在遍历的时候，会发现有一个元素在自己不知不觉的情况下就被删除/添加了，就会抛出一个异常，用来提示用户，可能发生了并发修改！

所以，在使用Java的集合类的时候，如果发生CMException，优先考虑fail-fast有关的情况，实际上这里并没有真的发生并发，只是Iterator使用了fail-fast的保护机制，只要他发现有某一次修改是未经过自己进行的，那么就会抛出异常。

- 为什么阿里巴巴禁止在 foreach 循环里进行元素的 remove/add 操作



- fail safe

为了避免触发fail-fast机制，导致异常，我们可以使用Java中提供的一些采用了fail-safe机制的集合类。

这样的集合容器在遍历时不是直接在集合内容上访问的，而是先复制原有集合内容，在拷贝的集合上进行遍历。

java.util.concurrent包下的容器都是fail-safe的，可以在多线程下并发使用，并发修改。同时也可以在foreach中进行add/remove 。

```java
public static void main(String[] args) {
    List<String> userNames = new CopyOnWriteArrayList<String>() {{
        add("Hollis");
        add("hollis");
        add("HollisChuang");
        add("H");
    }};

    userNames.iterator();

    for (String userName : userNames) {
        if (userName.equals("Hollis")) {
            userNames.remove(userName);
        }
    }

    System.out.println(userNames);
}

```

以上代码，使用CopyOnWriteArrayList代替了ArrayList，就不会发生异常。

fail-safe集合的所有对集合的修改都是先拷贝一份副本，然后在副本集合上进行的，并不是直接对原集合进行修改。并且这些修改方法，如add/remove都是通过加锁来控制并发的。

- copyOnWrite思想

**CopyOnWrite容器是一种读写分离的思想，读和写不同的容器。**

opyOnWriteArrayList中add/remove等写方法是需要加锁的，目的是为了避免Copy出N个副本出来，导致并发写



### 枚举

一个普通的枚举

```java
public enum t {
    SPRING,SUMMER;
}
```

反编译后的结果：当我们使用enmu来定义一个枚举类型的时候，编译器会自动帮我们创建一个final类型的类继承Enum类，所以枚举类型不能被继承。

```java
public final class T extends Enum
```

- 枚举为啥线程安全

```java
        public static final T SPRING;
        public static final T SUMMER;

        private static final T ENUM$VALUES[];
        static
        {
            SPRING = new T("SPRING", 0);
            SUMMER = new T("SUMMER", 1);
            ENUM$VALUES = (new T[] {
                SPRING, SUMMER
            });
        }

```



- 枚举序列化线程安全的原因：

当我们使用`enum`来定义一个枚举类型的时候，编译器会自动帮我们创建一个final类型的类继承Enum类,所以枚举类型不能被继承，我们看到这个类中有几个属性和方法。

我们可以看到：都是static类型的，因为static类型的属性会在类被加载之后被初始化，我们在[深度分析Java的ClassLoader机制（源码级别）](https://hollischuang.github.io/archives/199)和[Java类的加载、链接和初始化](https://hollischuang.github.io/archives/201)两个文章中分别介绍过，当一个Java类第一次被真正使用到的时候静态资源被初始化、Java类的加载和初始化过程都是线程安全的。所以， 创建一个enum类型是线程安全的 。

- 枚举序列化线程安全的原因

普通的Java类的反序列化过程中，会通过反射调用类的默认构造函数来初始化对象。所以，即使单例中构造函数是私有的，也会被反射给破坏掉。由于反序列化后的对象是重新new出来的，所以这就破坏了单例。

但是，枚举的反序列化并不是通过反射实现的。所以，也就不会发生由于反序列化导致的单例破坏问题。

why: 为了保证枚举类型像Java规范中所说的那样，每一个枚举类型极其定义的枚举变量在JVM中都是唯一的，在枚举类型的序列化和反序列化上，Java做了特殊的规定 

在序列化的时候Java仅仅是将枚举对象的name属性输出到结果中，反序列化的时候则是通过java.lang.Enum的valueOf方法来根据名字查找枚举对象。同时，编译器是不允许任何对这种序列化机制的定制的，因此禁用了writeObject、readObject、readObjectNoData、writeReplace和readResolve等方法。 

- 枚举比较

java 枚举值比较用 == 和 equals 方法没啥区别，两个随便用都是一样的效果。

因为枚举 Enum 类的 equals 方法默认实现就是通过 == 来比较的；

### 反射破坏单例的原因

通过反射获得单例类的构造函数，由于该构造函数是private的，通过setAccessible(true)指示反射的对象在使用时应该取消 Java 语言访问检查,使得私有的构造函数能够被访问，这样使得单例模式失效。

### 序列化破坏单例以及阻止序列化破坏单例

列化会通过反射调用无参数的构造方法创建一个新的对象。

- 如何阻止序列化破坏单例,添加下面的方法

```java
    private Object readResolve() {
        return singleton;
    }
```

- 为什么能阻止

但是为啥用了readResolve方法就能是单例了，因为在创建的时候，序列化方法做了一层判断，回去判断是否是readResolve，也就是生成策略会发生变化

- 写了readResolve方法的单例能阻止反射破坏单例吗

不能，因为写了readResolve方法，也只是在序列化的时候动态的判断了一下，并不能阻止反射破坏单例模式

### 注解与自定义注解

注解4要素

@Documented –注解是否将包含在JavaDoc中

@Retention –什么时候使用该注解

@Target? –注解用于什么地方

@Inherited – 是否允许子类继承该注解

- 自定义注解

比如接口限流使用令牌桶算法+aop+自定义注解

比如接口防止重复提交用redis+token+aop+自定义注解

- 常用注解

### 泛型与类型擦除

- 什么是泛型

就是声明的时候指定具体的类型，泛型最⼤的好处是可以提⾼代码的复⽤性。 以List接⼜为例，我们可以将String、 Integer等类型放⼊List中， 如不⽤泛型， 存放String类型要写⼀个List接口， 存放Integer要写另外⼀个List接口， 泛型可以很好的解决这个问题。

- 什么是类型擦除

擦除的主要过程如下： 

1. 将所有的泛型参数用其最左边界（最顶级的父类型）类型替换

2. 移除所有的类型参数。
3. 其实就是将泛型java代码转换为普通java代码，只不过编译器更直接点，将泛型java代码直接转换成普通java字节码
4. 也就是说Java中的泛型，只在编译阶段有效。在编译过程中，正确检验泛型结果后，会将泛型的相关信息擦出，并且在对象进入和离开方法的边界处添加类型检查和类型转换的方法。也就是说，泛型信息不会进入到运行时阶段。

比如下面的代码

```java
public static void main(String[] args) {  
    Map<String, String> map = new HashMap<String, String>();  
    map.put("name", "hollis");  
    map.put("age", "22");  
    System.out.println(map.get("name"));  
    System.out.println(map.get("age"));  
}  

```

反编译后的代码

执行命令 javac XX.java 然后查看class文件即可

```java
public static void main(String[] args) {  
    Map map = new HashMap();  
    map.put("name", "hollis");  
    map.put("age", "22"); 
    System.out.println((String) map.get("name"));  
    System.out.println((String) map.get("age"));  
}  

```

? 通配符类型 无边界的通配符(Unbounded Wildcards), 就是<?>, 比如List<?>
　　     无边界的通配符的主要作用就是让泛型能够接受未知类型的数据. 
<? extends T> 表示类型的上界，表示参数化类型的可能是T 或是 T的子类
<? super T> 表示类型下界（Java Core中叫超类型限定），
​	表示参数化类型是此类型的超类型（父类型），直至Object
注意： 你可以为一个泛型指定上边界或下边界, 但是不能同时指定上下边界.

####  泛型中K T V E ？ object等的含义

E - Element (在集合中使用，因为集合中存放的是元素)

T - Type（Java 类）

K - Key（键）

V - Value（值）

N - Number（数值类型）

？ - 表示不确定的java类型（无限制通配符类型）

S、U、V - 2nd、3rd、4th types

Object - 是所有类的根类，任何类的对象都可以设置给该Object引用变量，使用的时候可能需要类型强制转换，但是用使用了泛型T、E等这些标识符后，在实际用之前类型就已经确定了，不需要再进行类型强制转换。

### IO模型

- BIO阻塞io**(Blocking I/O**
- NIO不阻塞io **(Non-blocking/New I/O)**
- AIO异步IO **(Asynchronous I/O)**
- 多路复用IO

### 什么时候用抽象类

与抽象类息息相关的还有一个概念，就是接口，我们留到下一篇文章中详细说，因为要说的知识点还是蛮多的。你现在只需要有这样一个概念就好，接口是对行为的抽象，抽象类是对整个类（包含成员变量和行为）进行抽象。

所以抽象类有什么特性：

```java
1.定义抽象类的时候需要用到关键字 abstract，放在 class 关键字前。
2.抽象类不能被实例化，但可以有子类。
3.如果一个类定义了一个或多个抽象方法，那么这个类必须是抽象类。
4.抽象类可以同时声明抽象方法和具体方法，也可以什么方法都没有，但没必要
5.抽象类派生的子类必须实现父类中定义的抽象方法
```

- 什么时候用抽象类
  - 一、我们希望一些通用的功能被多个子类复用。比如说，AbstractPlayer 抽象类中有一个普通的方法 `sleep()`，表明所有运动员都需要休息，那么这个方法就可以被子类复用。
  - 我们需要在抽象类中定义好 API，然后在子类中扩展实现。比如说，AbstractPlayer  抽象类中有一个抽象方法 `play()`，定义所有运动员都可以从事某项运动，但需要对应子类去扩展实现。
  - 如果父类与子类之间的关系符合 `is-a` 的层次关系，就可以使用抽象类，比如说篮球运动员是运动员，足球运动员是运动员。

```java
public abstract class AbstractPlayer {
    public void sleep() {
        System.out.println("运动员也要休息而不是挑战极限");
    }
}
```

- 具体示例

为了进一步展示抽象类的特性，我们再来看一个具体的示例。假设现在有一个文件，里面的内容非常简单——“Hello World”，现在需要有一个读取器将内容读取出来，最好能按照大写的方式，或者小写的方式。

这时候，最好定义一个抽象类，比如说 BaseFileReader：

```java

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xiyou
 * @version 1.0
 * xiyou-todo 用抽象类实现大小写读取的方法
 * @date 2020/5/9 9:34
 */
public abstract class BaseFileReader {
    //filePath 为文件路径，使用 protected 修饰，表明该成员变量可以在需要时被子类访问。
    protected Path filePath;

    /**
     * 构造函数
     *
     * @param filePath
     */
    protected BaseFileReader(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * readFile() 方法用来读取文件，方法体里面调用了抽象方法 mapFileLine()——需要子类扩展实现大小写的方式。
     *
     * @return
     * @throws IOException
     */
    public List<String> readFile() throws IOException {
        return Files.lines(filePath)
                .map(this::mapFileLine).collect(Collectors.toList());
    }

    /**
     * 需要字类扩展的方法
     *
     * @param line
     * @return
     */
    protected abstract String mapFileLine(String line);
}
```

小写的实现

```java
import java.nio.file.Path;

/**
 * @author xiyou
 * @version 1.0
 * xiyou-todo 小写的实现
 * @date 2020/5/9 9:35
 */
public class LowercaseFileReader extends BaseFileReader {
    /**
     * 读取父类的PATH
     * @param filePath
     */
    protected LowercaseFileReader(Path filePath) {
        super(filePath);
    }

    /**
     * 小写实现
     *
     * @param line
     * @return
     */
    @Override
    protected String mapFileLine(String line) {
        return line.toLowerCase();
    }
}
```

大写的实现

```java
public class UppercaseFileReader extends BaseFileReader {
    /**
     * 读取父类PATH
     * @param filePath
     */
    protected UppercaseFileReader(Path filePath) {
        super(filePath);
    }

    /**
     * 大写的实现
     * @param line
     * @return
     */
    @Override
    protected String mapFileLine(String line) {
        return line.toUpperCase();
    }
}
```

测试，在项目的 resource 目录下有一个文本文件，名字叫 helloworld.txt。里面写入

```java
[hello world]
[HELLO WORLD]
```



```java
    public static void main(String[] args) throws URISyntaxException, IOException {
        URL location = TestAbstractReader.class.getClassLoader().getResource("helloworld.txt");
        Path path = Paths.get(location.toURI());
        BaseFileReader lowercaseFileReader = new LowercaseFileReader(path);
        BaseFileReader uppercaseFileReader = new UppercaseFileReader(path);
        System.out.println(lowercaseFileReader.readFile());
        System.out.println(uppercaseFileReader.readFile());
    }
//结果：
[[hello world], [hello world]]
[[HELLO WORLD], [HELLO WORLD]]
```

### 接口是什么

 Java 中，可以通过两种形式来达到抽象的目的，一种是抽象类，另外一种就是接口。

一个简单的区别是：一个类只能继承一个抽象类，但却可以实现多个接口。

```java
public interface Electronic {
    // 常量
    String LED = "LED";

    // 抽象方法
    int getElectricityUse();

    // 静态方法
    static boolean isEnergyEfficient(String electtronicType) {
        return electtronicType.equals(LED);
    }

    // 默认方法
    default void printDescription() {
        System.out.println("电子");
    }
}
```

上面接口的实现类，我们发现三个二方法我们只需要实现一个即可

```java
@Service
public class IElectronicImpl implements IElectronic{
    @Override
    public int getElectricityUse() {
        return 0;
    }
}
```



- 1)接口中定义的变量会在编译的时候自动加上 `public static final` 修饰符，也就是说 LED 变量其实是一个常量。
- 2)没有使用 `private`、`default` 或者 `static` 关键字修饰的方法是隐式抽象的，在编译的时候会自动加上 `public abstract` 修饰符。也就是说 `getElectricityUse()`其实是一个抽象方法，没有方法体——这是定义接口的本意。
- 3)从 Java 8 开始，接口中允许有静态方法，比如说 `isEnergyEfficient()` 方法。

静态方法无法由（实现了该接口的）类的对象调用，它只能通过接口的名字来调用，比如说 `Electronic.isEnergyEfficient("LED")`。

接口中定义静态方法的目的是为了提供一种简单的机制，使我们不必创建对象就能调用方法，从而提高接口的竞争力。

- 4)接口中允许定义 `default` 方法也是从 Java 8 开始

比如说 `printDescription()`，它始终由一个代码块组成，为实现该接口而不覆盖该方法的类提供默认实现，也就是说，无法直接使用一个“;”号来结束默认方法——编译器会报错的。

 下面是接口的反编译字节码

```java
public interface  IElectronic {
  public static final java.lang.String LED;
  public abstract int getElectricityUse();
  public static boolean isEnergyEfficient(java.lang.String);
  public void printDescription();
}
//接口中定义的所有变量或者方法，都会自动添加上 public 关键字
```

  

### 接口和抽象类的区别是什么

1. 接口的方法默认是 public，所有方法在接口中不能有实现(Java 8 开始接口方法可以有默认实现），而抽象类可以有非抽象的方法。
2. 接口中除了 static、final 变量，不能有其他变量，而抽象类中则不一定。
3. 一个类可以实现多个接口，但只能实现一个抽象类。接口自己本身可以通过 extends 关键字扩展多个接口。
4. 接口方法默认修饰符是 public，抽象方法可以有 public、protected 和 default 这些修饰符（抽象方法就是为了被重写所以不能使用 private 关键字修饰！）。
5. 从设计层面来说，抽象是对类的抽象，是一种模板设计，而接口是对行为的抽象，是一种行为的规范。

> 备注：
>
> 1. 在 JDK8 中，接口也可以定义静态方法，可以直接用接口名调用。实现类和实现是不可以调用的。如果同时实现两个接口，接口中定义了一样的默认方法，则必须重写，不然会报错。(详见 issue:https://github.com/Snailclimb/JavaGuide/issues/146。
> 2. jdk9 的接口被允许定义私有方法 。

总结一下 jdk7~jdk9 Java 中接口概念的变化（[相关阅读](https://www.geeksforgeeks.org/private-methods-java-9-interfaces/)）：

1. 在 jdk 7 或更早版本中，接口里面只能有常量变量和抽象方法。这些接口方法必须由选择实现接口的类实现。
2. jdk8 的时候接口可以有默认方法和静态方法功能。
3. Jdk 9 在接口中引入了私有方法和私有静态方法。

### 枚举为啥线程安全而且序列化、反射安全都是单例

- 随便写个枚举

```java
public enum  AppleEnum {
    RED,BLEACK,WHITE;
}
```

- Javac生成class文件

```java
package org.xiyou.leetcode.thread;

public enum AppleEnum {
    RED,
    BLEACK,
    WHITE;
    private AppleEnum() {
    }
}
```

- javap 反编译class文件

发现下面生成的都是static final修饰的

```java

public final class  AppleEnum extends 
    java.lang.Enum<AppleEnum> {
  public static final  AppleEnum RED;
  public static final  AppleEnum BLEACK;
  public static final  AppleEnum WHITE;
  public static  AppleEnum[] values();
  public static  AppleEnum valueOf(java.lang.String);
  static {};
}

```

- 看枚举父类

```java
public abstract class Enum<E extends Enum<E>>
        implements Comparable<E>, Serializable {

    private final String name;

    public final String name() {
        return name;
    }

    private final int ordinal;

    public final int ordinal() {
        return ordinal;
    }

    protected Enum(String name, int ordinal) {
        this.name = name;
        this.ordinal = ordinal;
    }
    ......
   public final int compareTo(E o) {
        Enum<?> other = (Enum<?>)o;
        Enum<E> self = this;
        if (self.getClass() != other.getClass() && // optimization
            self.getDeclaringClass() != other.getDeclaringClass())
            throw new ClassCastException();
        return self.ordinal - other.ordinal;
    }
    protected final Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }
   private void readObject(ObjectInputStream in) throws IOException,
        ClassNotFoundException {
        throw new InvalidObjectException("can't deserialize enum");
    }

    private void readObjectNoData() throws ObjectStreamException {
        throw new InvalidObjectException("can't deserialize enum");
    }
}
```

从Enum中我们可以看到，每个枚举都定义了两个属性，name和ordinal，name表示枚举变量的名称，而ordinal则是根据变量定义的顺序授予的整型值，从0开始。

而且我们可以从Enum的源码中看到，大部分的方法都是final修饰的，特别是**clone、readObject、writeObject**这三个方法，**保证了枚举类型的不可变性**，不能通过克隆、序列化和反序列化复制枚举，这就保证了枚举变量只是一个实例，即是单例的。

总结一下，其实**枚举本质上也是通过普通的类来实现的**，只是编译器为我们进行了处理。每个枚举类型都继承自**Enum**类，并由**编译器自动添加了values()和valueOf()方法**，**每个枚举变量是一个静态常量字段，由内部类实现**，而这个内部类继承了此枚举类。

**所有的枚举变量都是通过静态代码块进行初始化**，也就是说在类加载期间就实现了。

另外，**通过把clone、readObject、writeObject这三个方法定义为final，保证了每个枚举类型及枚举常量都是不可变的**，也就是说，可以用枚举实现线程安全的单例。

###  在Java的反射中，Class.forName和ClassLoader的区别

**[传送门](https://www.cnblogs.com/jimoer/p/9185662.html)**

在java中Class.forName()和ClassLoader都可以对类进行加载。ClassLoader就是遵循**双亲委派模型**最终调用启动类加载器的类加载器，实现的功能是“通过一个类的全限定名来获取描述此类的二进制字节流”，获取到二进制流后放到JVM中。Class.forName()方法实际上也是调用的CLassLoader来实现的。

```java
  @CallerSensitive
    public static Class<?> forName(String className)
                throws ClassNotFoundException {
        Class<?> caller = Reflection.getCallerClass();
        return forName0(className, true, ClassLoader.getClassLoader(caller), caller);
    }
```

最后调用的方法是forName0这个方法，在这个forName0方法中的第二个参数被默认设置为了true，这个参数代表是否对加载的类进行初始化，设置为true时会类进行初始化，代表会执行类中的静态代码块，以及对静态变量的赋值等操作。

也可以调用Class.forName(String name, boolean initialize,ClassLoader loader)方法来手动选择在加载类的时候是否要对类进行初始化。

```java
public class ClassForName {
    //静态代码块
    static {
        System.out.println("执行了静态代码块");
    }

    //静态变量
    private static String staticFiled = staticMethod();

    //赋值静态变量的静态方法
    public static String staticMethod() {
        System.out.println("执行了静态方法");
        return "给静态字段赋值了";
    }
}
```

- classForName

```java
    public static void test45(){
        try {
            Class.forName("org.xiyou.leetcode.javabase.ClassForName");
            System.out.println("#########-------------结束符------------##########");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    //结果如下：
执行了静态代码块
执行了静态方法
#########-------------结束符------------##########
```

- classLocader

```java
public static void test44(){
        try {
            ClassLoader.getSystemClassLoader().loadClass("org.xiyou.leetcode.javabase.ClassForName");
            System.out.println("#########-------------结束符------------##########");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
//结果如下
#########-------------结束符------------##########
```

- 根据运行结果得出Class.forName加载类时将类进了初始化，而ClassLoader的loadClass并没有对类进行初始化，只是把类加载到了虚拟机中。

在我们熟悉的Spring框架中的IOC的实现就是使用的ClassLoader。

而在我们使用JDBC时通常是使用Class.forName()方法来加载数据库连接驱动。这是因为在JDBC规范中明确要求Driver(数据库驱动)类必须向DriverManager注册自己。

### 类加载机制、类生命周期

- 加载
  - 把类元信息加载到方法区（元空间）
  - 加载方式：根据类的全路径区加载class
  - 加载时间：new、反射调用、调用静态方法或者读区静态字段的时候

- 验证
  - 验证文件格式是否合法、变量名和方法是否重复等安全措施
- 准备
  - 给类的静态变量赋默认的初始值，比如 static int a=123;此阶段会把a的值设置为0
- 解析
  - 把符号引用换成直接引用

比如我们要在内存中找一个类里面的一个叫做show的方法，显然是找不到。但是在解析阶段，
jvm就会把show这个名字转换为指向方法区的的一块内存地址，比如c17164，通过c17164就可以找到show这个方法具体分配在内存的哪一个区域了。
这里show就是符号引用，而c17164就是直接引用。
在解析阶段jvm会将所有的类或接口名、字段名、方法名转换为具体的内存地址。

- 初始化
  - 执行静态变量的初始化和静态Java代码块，比如 static int a=123;此阶段会把a的值从0设置为123
- 使用
- 卸载

### 双亲委派模型

[双亲委派模型的介绍]([https://www.funtl.com/zh/interview/Java-%E9%9D%A2%E8%AF%95%E5%AE%9D%E5%85%B8-%E5%8F%8C%E4%BA%B2%E5%A7%94%E6%B4%BE%E6%A8%A1%E5%9E%8B.html#%E7%B1%BB%E5%8A%A0%E8%BD%BD%E5%99%A8](https://www.funtl.com/zh/interview/Java-面试宝典-双亲委派模型.html#类加载器))

如果一个类加载器收到了类加载请求，它并不会自己先去加载，而是把这个请求委托给父类的加载器去执行，如果父类加载器还存在其父类加载器，则进一步向上委托，依次递归，请求最终将到达顶层的启动类加载器，如果父类加载器可以完成类加载任务，就**成功返回**，倘若父类加载器无法完成此加载任务，**子加载器才会尝试自己去加载**，这就是双亲委派模式

 userClassLoader——>application——> Extension——> BootStrap ClassLoader

​                              classPath  <JAVA_HOME>\lib\ext  <JAVA_HOME>\lib 

优点：

1. 一个可以避免类的重复加载
2. 安全

**像 java.lang.Object 这些存放在 rt.jar 中的类，无论使用哪个类加载器加载，最终都会委派给最顶端的启动类加载器加载，从而使得不同加载器加载的 Object 类都是同一个**。

相反，如果没有使用双亲委派模型，由各个类加载器自行去加载的话，如果用户自己编写了一个称为 java.lang.Object 的类，并放在 classpath 下，那么系统将会出现多个不同的 Object 类，Java 类型体系中最基础的行为也就无法保证。

####  双亲委派模型的系统实现

在 `java.lang.ClassLoader` 的 `loadClass()` 方法中，先检查是否已经被加载过，若没有加载则调用父类加载器的 `loadClass()` 方法，若父加载器为空则默认使用启动类加载器作为父加载器。如果父加载失败，则抛出 `ClassNotFoundException` 异常后，再调用自己的 `findClass()` 方法进行加载。

```java
protected synchronized Class<?> loadClass(String name,boolean resolve)throws ClassNotFoundException{
    //check the class has been loaded or not
    Class c = findLoadedClass(name);
    if(c == null){
        try{
            if(parent != null){
                c = parent.loadClass(name,false);
            }else{
                c = findBootstrapClassOrNull(name);
            }
        }catch(ClassNotFoundException e){
            //if throws the exception ,the father can not complete the load
        }
        if(c == null){
            c = findClass(name);
        }
    }
    if(resolve){
        resolveClass(c);
    }
    return c;
}
```



### 破坏双亲委派模型

  双亲委派模型有一系列的优势，还是需要去破坏双亲委派模型。比如 ：基础类去调用回用户的代码。

[破坏双亲委派模型实现类隔离](https://mp.weixin.qq.com/s/oK6A3viNP3XafEvl7rzjKA)

总结：比如一个项目依赖了多个版本的C，启动的时候未选择理想的那个版本，类隔离技术是为了解决依赖冲突而诞生的，它通过自定义类加载器破坏双亲委派机制，然后利用类加载传导规则实现了不同模块的类隔离。

#### SPI里面的JDBC

为什么必须要破坏?
    DriverManager::getConnection 方法需要根据参数传进来的 url 从所有已经加载过的 Drivers 里找到一个合适的 Driver 实现类去连接数据库.
    Driver 实现类在第三方 jar 里, 要用 AppClassLoader 加载. 而 DriverManager 是 rt.jar 里的类, 被 BootstrapClassLoader 加载, DriverManager 没法用 BootstrapClassLoader 去加载 Driver 实现类（不再lib下）, 所以只能破坏双亲委派模型, 用它下级的 AppClassLoader 去加载 Driver.

JDK 中也有较大规模破坏双亲模型的情况，例如线程上下文类加载器（**Thread Context ClassLoader**）的出现




### 正则表达式
#### 正则预编译

- 在使用正则表达式时，利用好其预编译功能，可以有效加快正则匹配速度。 说明：不要在方法体内定义：Pattern pattern = Pattern.compile(规则);

```java
 public class XxxClass {
        // Use precompile
        private static Pattern NUMBER_PATTERN = Pattern.compile("[0-9]+");
        public Pattern getNumberPattern() {
            // Avoid use Pattern.compile in method body.
            Pattern localPattern = Pattern.compile("[0-9]+");
            return localPattern;
        }
    }
```

#### 正则表达式栈溢出

通过观察，我们知道这是一个虚拟机 **栈溢出** 错误。

Java语言中每个线程（Thread）的栈大小是有限制的，每一次函数调用都会生成一个栈帧（Frame），占用一定的栈空间，当栈空间被消耗完虚拟机就会报出StackOverflowError。

再仔细地观察，发现栈信息中显示调用层次非常多，而且类名与代码行重复出现，这意味着栈帧是由递归调用产生的。

到这里，问题发生的原因己经基本可以确定了。

**在Java中正则表达式的产生了递归调用，而传入的长Json字符串导致函数调用层次太多，超过虚拟机预设的栈空间，因此，出现了StackOverflowError。**

[区分json风格的字符串导致的栈溢出](https://www.jianshu.com/p/87d0175e1aed)
### 线程

线程创建之后它将处于 **NEW（新建）** 状态，调用 `start()` 方法后开始运行，线程这时候处于 **READY（可运行）** 状态。可运行状态的线程获得了 cpu 时间片（timeslice）后就处于 **RUNNING（运行）** 状态。

当线程执行 `wait()`方法之后，线程进入 **WAITING（等待）**状态。进入等待状态的线程需要依靠其他线程的通知才能够返回到运行状态，而 **TIME_WAITING(超时等待)** 状态相当于在等待状态的基础上增加了超时限制，比如通过 `sleep（long millis）`方法或 `wait（long millis）`方法可以将 Java 线程置于 TIMED WAITING 状态。当超时时间到达后 Java 线程将会返回到 RUNNABLE 状态。当线程调用同步方法时，在没有获取到锁的情况下，线程将会进入到 **BLOCKED（阻塞）** 状态。线程在执行 Runnable 的`run()`方法之后将会进入到 **TERMINATED（终止）** 状态。

## java版本变迁

https://mp.weixin.qq.com/s/yOsq6FWCXb22r52oWm7i4w

