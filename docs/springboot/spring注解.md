## 注解

- spring相关注解、springCloud相关注解、dubbo相关注解

```
@FeignClient 一般关注name、value、url 、configuration、rollback
```



- mybatis、JPA、mongo、ES等数据库相关注解

- 配置文件相关注解，如：

```
@ConditionalOnProperty 读取特定前缀的配置文件的注解
@Value
@RefreshScope　如果代码中需要动态刷新配置，在需要的类上加上该注解就行
```

- MQ 相关注解，如rocketMQ、Kafka相关的各种注解

```
@Consumer
```

- Hystrix、Sentinel相关注解

```
@SentinelResource
@HystrixCommand
```



### spring常用注解

```java
@Bean 用在方法上，告诉Spring容器，你可以从下面这个方法中拿到一个Bean

@Configuration把一个类作为一个IoC容器，它的某个方法头上如果注册了@Bean，就会作为这个Spring容器中的Bean。

@Scope注解 作用域

@Lazy(true) 表示延迟初始化

@Service用于标注业务层组件、

@Controller用于标注控制层组件@Repository用于标注数据访问组件，即DAO组件。

@Component泛指组件，当组件不好归类的时候，我们可以使用这个注解进行标注。

@Scope用于指定scope作用域的（用在类上）

@PostConstruct用于指定初始化方法（用在方法上）

@PreDestory用于指定销毁方法（用在方法上）

@DependsOn：定义Bean初始化及销毁时的顺序

@Primary：自动装配时当出现多个Bean候选者时，被注解为@Primary的Bean将作为首选者，否则将抛出异常

@Autowired 默认按类型装配，如果我们想使用按名称装配，可以结合@Qualifier注解一起使用。如下：

@Autowired @Qualifier("personDaoBean") 存在多个实例配合使用

@Resource默认按名称装配，当找不到与名称匹配的bean才会按类型装配。

@PostConstruct 初始化注解

@PreDestroy 摧毁注解 默认 单例 启动就加载
```

#### 其它使用比较多的注解

```
@ConditionalOnProperty 读取特定前缀的配置文件的注解
@Consumer 消费者注解
@Value注解，注入配置文件字段
@FeignClient注解

@EnableHystrix 支持Hystrix
@HystrixCommand 方法级别的配置
@RefreshScope　如果代码中需要动态刷新配置，在需要的类上加上该注解就行
```



###  Spring中的这几个注解有什么区别
@Component 、@Repository、@Service、 controller

1. @Component指的是组件，

@Controller，@Repository和@Service 注解都被@Component修饰，用于代码中区分表现层，持久层和业务层的组件，代码中组件不好归类时可以使用@Component来标注

比如controller注解

```java
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Controller {
    @AliasFor(
        annotation = Component.class
    )
    String value() default "";
}
```

## 注解元属性

@Documented –注解是否将包含在JavaDoc中

@Retention –什么时候使用该注解

@Target? –注解用于什么地方

@Inherited – 是否允许子类继承该注解

### @Documented定义注解是否将包含在JavaDoc中

@Documented–一个简单的Annotations标记注解，表示是否将注解信息添加在java文档中。
### @Retention定义该注解的生命周期。
@Retention– 定义该注解的生命周期。

RetentionPolicy.SOURCE – 在编译阶段丢弃。这些注解在编译结束之后就不再有任何意义，所以它们不会写入字节码。@Override, @SuppressWarnings都属于这类注解。

RetentionPolicy.CLASS – 在类加载的时候丢弃。在字节码文件的处理中有用。注解默认使用这种方式。

RetentionPolicy.RUNTIME– 始终不会丢弃，运行期也保留该注解，因此可以使用反射机制读取该注解的信息。我们自定义的注解通常使用这种方式。



### @Target表示该注解用于什么地方

@Target – 表示该注解用于什么地方。如果不明确指出，该注解可以放在任何地方。以下是一些可用的参数。需要说明的是：属性的注解是兼容的，如果你想给7个属性都添加注解，仅仅排除一个属性，那么你需要在定义target包含所有的属性。

下面两个注解最常使用

> ```java
> ElementType.TYPE: 用于描述类、接口或enum声明
> 
> ElementType.METHOD用于描述方法。
> ```



```java
ElementType.FIELD:用于描述实例变量/字段、枚举的常量
ElementType.PARAMETER//方法参数

ElementType.CONSTRUCTOR//构造函数

ElementType.LOCAL_VARIABLE//局部变量

ElementType.ANNOTATION_TYPE 另一个注释

ElementType.PACKAGE 用于记录java文件的package信息
```
### @Inherited 定义该注释和子类的关系
@Inherited – 定义该注释和子类的关系

那么，注解的内部到底是如何定义的呢？Annotations只支持基本类型、String及枚举类型。注释中所有的属性被定义成方法，并允许提供默认值。

## 自定义注解

### 如何定义方法注解？

- 比如我们下面的注解就只能作用于方法上，怎么作用到类呢？

```java
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface AnRateLimiter {

    //以每秒固定数值往令牌桶添加令牌
    double permitsPerSecond();

    //获取令牌最大等待时间
    long timeout();

    // 单位(例:分钟/秒/毫秒) 默认:毫秒
    TimeUnit timeunit() default TimeUnit.MILLISECONDS;

    // 无法获取令牌返回提示信息 默认值可以自行修改
    String msg() default "系统繁忙,请稍后再试.";
}
```

### 如何定义方法和类通用注解？

把上面的@Target改成下面的即可

```java
@Target({ElementType.TYPE, ElementType.METHOD})
//ElementType.TYPE: 用于描述类、接口或enum声明
```

## AOP相关注解
```java
@Aspect:作用是把当前类标识为一个切面供容器读取
 
@Pointcut：Pointcut是植入Advice的触发条件。每个Pointcut的定义包括2部分
    一是表达式
    二是方法签名。
    方法签名必须是 public及void型。可以将Pointcut中的方法看作是一个被Advice引用的助记符，因为表达式不直观，因此我们可以通过方法签名的方式为 此表达式命名
    因此Pointcut中的方法只需要方法签名，而不需要在方法体内编写实际代码。
@Around：环绕增强，相当于MethodInterceptor
@AfterReturning：后置增强，相当于AfterReturningAdvice，方法正常退出时执行
@Before：标识一个前置增强方法，相当于BeforeAdvice的功能，相似功能的还有
@AfterThrowing：异常抛出增强，相当于ThrowsAdvice
@After: final增强，不管是抛出异常或者正常退出都会执行
```



### 如何把切点定义到类和方法上？

一般如果我们使用的是方法注解，只需要下面的配置就行

```java
    @Pointcut("@annotation(cn.net.health.user.config.AnRateLimiter)")
    public void anRateLimiter() {
    }
```

下面这样写就行了

`@within((cn.net.health.user.config.AnRateLimiter))`代表有当前注解的所有类的方法

如果类上没用注解，方法上单独使用也是可以的

`@annotation(cn.net.health.user.config.AnRateLimiter)`

所以要想把切点定义到使用切点的类，或者某个方法上就可以变成下面的形式

```java
@Pointcut("@within((cn.net.health.user.config.AnRateLimiter)) || @annotation(cn.net.health.user.config.AnRateLimiter)")
public void anRateLimiter() {
}
```

### 类和方法的优先级如何设置呢？

方法上的注解优先级高于类上面的注解

```
@Before("pointcut()")
public void doBefore(JoinPoint joinPoint) {
	Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
	//获取方法上的注解
	DataSourceSelector annotationClass = method.getAnnotation(DataSourceSelector.class);
	if (annotationClass == null) {
		//获取类上面的注解
		annotationClass = joinPoint.getTarget().getClass().getAnnotation(DataSourceSelector.class);
		//如果类也没有注解返回
		if (annotationClass == null) return;
	 }
....逻辑
}
```



####  execution

通常用于包或者某个具体的类

```
1）execution(* *(..))  
//表示匹配所有方法  
2）execution(public * com. savage.service.UserService.*(..))  
//表示匹配com.savage.server.UserService中所有的公有方法  
3）execution(* com.savage.server..*.*(..))  
//表示匹配com.savage.server包及其子包下的所有方法
```

#### winin

类的所有的方法都会执行(不包含子包) aop方法

必须是在目标对象上声明这个注解， 在接口上声明的对它不起作用

而且不能是其子类型。

> ```java
> pointcutexp包里的任意类.
> within(com.test.spring.aop.pointcutexp.*)
> pointcutexp包和所有子包里的任意类.
> within(com.test.spring.aop.pointcutexp..*)
>  
> 带有@Transactional标注的所有类的任意方法.
> @within(org.springframework.transaction.annotation.Transactional)
> @target(org.springframework.transaction.annotation.Transactional)
> 
> 带有@Transactional标注的任意方法.
> @annotation(org.springframework.transaction.annotation.Transactional)
> 
> @within和@target针对类的注解,@annotation是针对方法的注解
>  
> 参数带有@Transactional标注的方法.
> @args(org.springframework.transaction.annotation.Transactional)
> 参数为String类型(运行是决定)的方法.
> ```

#### target

和within一样作用于类

而且也是必须是在目标对象上声明这个注解，在接口上声明的对它不起作用

 

## 实际注解的相关应用



### 注解+令牌桶算法+aop实现单机限流

[guava+aop+注解实现单机限流](https://mp.weixin.qq.com/s/vyQZvJm5sjnEnrpi1siP2A)

### 注解+redis+aop+ip实现分布式限流

[redis+aop+ip+注解实现分布式限流](https://mp.weixin.qq.com/s/vyQZvJm5sjnEnrpi1siP2A)

目录秒杀相关、限流里面

### 注解+AOP实现动态数据源切换和读写分离

这时网上的一个项目，但是项目并没有实现类注解，就是给一个service直接设置数据源，所以下面我自己实现下

[读写分离项目](https://github.com/Taoxj/mysql-proxy)

- 自定义注解

```java
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataSourceSelector {

  DynamicDataSourceEnum value() default DynamicDataSourceEnum.MASTER;

    boolean clear() default true;

}
```

- 核心逻辑
  - 引入该注解的方法会执行AOP逻辑
  - 引入该注解的类会执行AOP逻辑
  - 如果方法上有注解，优先用方法上的，方法上没用判断类中是否存在注解

```java
    @Pointcut("@within(com.xjt.proxy.dynamicdatasource.DataSourceSelector)|| @annotation(com.xjt.proxy.dynamicdatasource.DataSourceSelector)")
    public void pointcut() {
    }
```



- 全部代码

```java
@Slf4j
@Aspect
@Order(value = 1)
@Component
public class DataSourceContextAop {
    @Pointcut("@within(com.xjt.proxy.dynamicdatasource.DataSourceSelector)|| @annotation(com.xjt.proxy.dynamicdatasource.DataSourceSelector)")
    public void pointcut() {
    }

    @Before("pointcut()")
    public void doBefore(JoinPoint joinPoint) {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        //获取方法上的注解
        DataSourceSelector annotationClass = method.getAnnotation(DataSourceSelector.class);
        if (annotationClass == null) {
            //获取类上面的注解
            annotationClass = joinPoint.getTarget().getClass().getAnnotation(DataSourceSelector.class);
            if (annotationClass == null) return;
        }
        //获取注解上的数据源的值的信息
        String dataSourceKey = annotationClass.value().getDataSourceName();
        if (dataSourceKey != null) {
            //给当前的执行SQL的操作设置特殊的数据源的信息
            DataSourceContextHolder.set(dataSourceKey);
        }
        log.info("AOP动态切换数据源，className" + joinPoint.getTarget().getClass().getName() + "methodName" + method.getName() + ";dataSourceKey:" + dataSourceKey == "" ? "默认数据源" : dataSourceKey);
    }

    @After("pointcut()")
    public void after(JoinPoint point) {
        //清理掉当前设置的数据源，让默认的数据源不受影响
        DataSourceContextHolder.clear();
    }

}
```

