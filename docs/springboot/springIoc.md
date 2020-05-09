# springIOC

inversion of control  控制反转

## 1. 依赖倒置思想

### 牵一发而动全身的设计

比如我们要设计一款车，刚开始设计螺丝，根据螺丝设计轮子，再根据轮子设计地盘，然后组成车

这样往往牵一发而动全身，为啥？因为如果我想改车的轮子了，我发现轮子改了车地盘就得改，地盘改了车就要改这完全废了啊

### 顶层建筑决定底层实现

上面的思路不行，所以我们反过来，先设计车，再设计地盘，再根据车地盘设计轮子，再根据轮子设计螺丝，这样如果我们要改车的轮子了，我们只需要动车的轮子，而不需要动车地盘

**高层建筑决定需要什么，底层去实现这样的需求，但是高层并不用管底层是怎么实现的。**这样就不会出现前面的“牵一发动全身”的情况。

## 2. 控制反转实现是依赖注入DI

其实依赖倒置原则的一种代码设计的思路。具体采用的方法就是所谓的依赖注入（Dependency Injection

即上层控制下层，而不是下层控制着上层。我们用依赖注入（Dependency Injection）这种方式来实现控制反转。所谓依赖注入，就是把底层类作为参数传入上层类，实现上层类对下层类的“控制”。

### 2.1 控制反转容器IOC

ioc Container

因为采用了依赖注入，在初始化的过程中就不可避免的会写大量的new。这里IoC容器就解决了这个问题。这个容器可以自动对你的代码进行初始化，你只需要维护一个Configuration（可以是xml可以是一段代码），而不用每次初始化一辆车都要亲手去写那一大段初始化的代码。这是引入IoC Container的第一个好处。

IoC Container的第二个好处是：我们在创建实例的时候不需要了解其中的细节。在上面的例子中，我们自己手动创建一个车instance时候，是从底层往上层new的：

springIOC 是管理bean的容器，默认情况下bean都是以单例存在的,它要求所有ioc容器都要去实现BeanFactory接口，而BeanFactory接口中有根据bean名称或者类型返回bean的方法，这也为依赖注入DI提供了可能

### 2.2 依赖注入DI 实现的方式

#### @Bean+@Configration实现注入

@Configration代表这是一个配置文件

这种方式比较复杂

####  @ComponentScan或者@Component实现注入

@ComponentScan：是以何种策略扫描装配bean

@Component是标记当前类进入IOC容器

比如我们常见的@Service注解，内部实现就是用@Component

```
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Service {}
```

### 2.3自定义第三方Bean

比如我们常见的MYSQL或者mongo等数据库，我们常常会自己定义一个bean引入进来，当然不自己定义也可以，因为springboot默认实现了，但是如果比如我们开发多数据源的时候就要用到了

### 2.4@Autowired注解获取bean

根据类型获取bean

@Primary作用是如果发现有多个同样类型的bean时，优先选择哪个

## bean 生命周期

1. 通过我们的配置@ComponentScan定义去扫描带有@Component的类
2. 找到这些类之后，开始解析，并且把定义的信息保存起来，但是没有进行bean初始化，也没有bean的实例，仅仅是bean的定义

将所有的 Bean 定义保存到 BeanDefinition 的实例中 

1. 发布bean到IOC容器中，此时IOC里面也仅仅只有Bean的定义，还是没有bean的实例
2. 实例化，创建bean的实例对象
3. 依赖注入DI（例如@Autowired）



## Spring的IOC注入方式

构造器注入 

 setter方法注入

 注解注入

 接口注入

## spring循环依赖

**springBean 定义5种作用域**

主要就是单例和原型（当然还有request、Session等范围），单例表示在整个应用上下文中只会存在一个Bean实例，而原型正好相反，可以存在多个Bean实例，每次调用getBean的时候都会新建一个新的bean实例。

```java
singleton（单例） prototype（原型） request session global session
```

先说明前提：**原型**(Prototype)的场景是**不支持**循环依赖的，通常会走到`AbstractBeanFactory`类中下面的判断，抛出异常。

基于构造器的循环依赖，就更不用说了，[官方文档](https://links.jianshu.com/go?to=https%3A%2F%2Fdocs.spring.io%2Fspring%2Fdocs%2Fcurrent%2Fspring-framework-reference%2Fcore.html%23beans-dependency-resolution)都摊牌了，你想让构造器注入支持循环依赖，是不存在的，不如把代码改了，怎么改，下面有介绍。

### 为啥spring要支持循环依赖

其实就是为了解决A依赖B，B依赖A的问题

### 怎么解决？问题的本质居然是two sum！

#### twoSum

不知道**two sum**是什么梗的，笔者和你介绍一下：

**two sum**是刷题网站[leetcode](https://links.jianshu.com/go?to=https%3A%2F%2Fleetcode-cn.com%2Fproblems%2Ftwo-sum%2F)序号为1的题，也就是大多人的算法入门的第一题。

问题内容是：给定**一个数组**，给定**一个数字**。返回数组中可以**相加得到指定数字**的两个**索引**。

比如：给定`nums = [2, 7, 11, 15], target = 9`
 那么要返回 `[0, 1]`，因为`2 + 7 = 9`

这道题的优解是，一次遍历+HashMap：

```cpp
class Solution {
    public int[] twoSum(int[] nums, int target) {
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            int complement = target - nums[i];
            if (map.containsKey(complement)) {
                return new int[] { map.get(complement), i };
            }
            map.put(nums[i], i);
        }
        throw new IllegalArgumentException("No two sum solution");
    }
}
```

先去Map中找**需要的数字**，没有就将**当前的数字**保存在Map中，如果找到**需要的数字**，则一起返回。

#### spring版本的twoSum

有了上面的思路，如果你是spring，你想解决A依赖B，B依赖A的问题，你会怎么做？申请Map?

对，不过spring做了更深层次的优化，它用了3个Map,但是原理还是一样的

**原理：spring是先去缓存里找Bean，没有则实例化当前的Bean放到Map，如果有需要依赖当前Bean的，就能从Map取到。**

首先，Spring内部维护了三个**Map**，也就是我们通常说的**三级缓存**。

在Spring的`DefaultSingletonBeanRegistry`类中，你会赫然发现类上方挂着这三个Map：

- *singletonObjects* 它是我们最熟悉的朋友，俗称“**单例池**”“**容器**”，缓存创建完成单例Bean的地方。
- *singletonFactories* 映射创建Bean的原始工厂
- *earlySingletonObjects* 

*earlySingletonObjects* 是映射Bean的**早期**引用，也就是说在这个Map里的Bean不是完整的，甚至还不能称之为“**Bean**”，只是一个**Instance**.早期单例缓存池，用于保存尚未创建完成的用于早期暴露的单例Bean，是Map，它与singletonObjects是互斥的，就是不可能同时保存于两者之中，只能择一而存，保存在该缓存池中的是尚未完成创建，而被注入到其他Bean中的Bean实例，可以说该缓存就是一个中间缓存（或者叫过程缓存），只在当将该BeanName对应的原生Bean（处于创建中池）注入到另一个bean实例中后，将其添加到该缓存中，这个缓存中保存的永远是半成品的bean实例，当Bean实例最终完成创建后会从此缓存中移除，转移到singletonObjects缓存中保存。

后两个Map其实是“**垫脚石**”级别的，只是创建Bean的时候，用来借助了一下，创建完成就清掉了。

#### 解决相互依赖问题具体流程是什么？

要说明流程光靠上面3个Map是不够的，还要引入一个ingletonsCurrentlyInCreation

ingletonsCurrentlyInCreation：创建中池，保存处于创建中的单例bean的BeanName，是Set，在这个bean实例开始创建时添加到池中，而来Bean实例创建完成之后从池中移除。

流程开始：当存在循环依赖的情况时，比如之前的情况：A依赖B，B又依赖A的情况，这种情况下

1. 先要创建A实例，将AbeanName添加到singletonsCurrentlyInCreation池

2. 然后调用A的构造器创建A的原生实例，并将AObjectFactory添加到singletonFactories缓存中

3. 然后处理依赖注入（B实例），发现B实例不存在且也不在singletonsCurrentlyInCreation池中，表示Bean实例尚未进行创建，那么下一步开始创建B实例

4. 将B的beanName添加到singletonsCurrentlyInCreation池，然后调用B的构造器创建A的原生实例，并将B的ObjectFactory添加到singletonFactories缓存中

5. 再然后处理依赖注入（A实例），发现A实例尚未创建完成

6. 但在singletonsCurrentlyInCreation池中发现了A实例的beanName，说明A实例正处于创建中，这时表示出现循环依赖

   **核心来了**

7. 出现循环依赖后，Spring会将singletonFactories缓存中获取对应A的beanName的ObjectFactory中getObject方法返回的Bean实例注入到B中，来完成B实例的创建步骤

8. 同时也会将A的Bean实例添加到earlySingletonObjects缓存中，表示A实例是一个提前暴露的Bean实例

9. B实例创建完毕之后需要将B的原生实例从singletonFactories缓存中移除，并将完整实例添加到SingletonObjects缓存中（当然earlySingletonObjects中也不能存在）并且将其beanName从singletonsCurrentlyInCreation池中移除（表示B实例完全创建完毕）

10. 然后将B实例注入到A实例中来完成A实例的创建，最后同样将A的原生实例从earlySingletonObjects中移除，完整实例添加到SingletonObjects中，并将A的beanName从创建中池中移除。

11. 到此完成A和B两个单例实例的创建。

#### 三级缓存怎么用

1. 先从一级缓存singletonObjects中去获取。（如果获取到就直接return）
2. 如果获取不到或者对象正在创建中（isSingletonCurrentlyInCreation()），那就再从二级缓存earlySingletonObjects中获取。（如果获取到就直接return）
3. 如果还是获取不到，且允许singletonFactories（allowEarlyReference=true）通过getObject()获取。就从三级缓存singletonFactory.getObject()获取。（如果获取到了就从singletonFactories中移除，并且放进earlySingletonObjects。
4. 其实也就是从三级缓存移动（是剪切、不是复制哦~）到了二级缓存）
    

#### 为啥构造器造成的循环依赖spring无法解决？

了解了上面所述的解决方案之后，我们可以明白针对构造器实现依赖注入的Bean发生循环依赖的情况下为什么无法解决。

那就是因为，之前提前暴露的前提是创建好原生的Bean实例，原生的Bean实例就是依靠构造器创建的，如果在构造器创建Bean的时候就需要注入依赖，而依赖又正处于创建中的话，由于无法暴露ObjectFactory，而无法解决循环依赖问题。

另外原型bean的情况，Spring根本就不会对原型的Bean添加缓存，因为添加缓存的目的是为了保证单例Bean的唯一性，但是对于原型，就不能缓存了，如果从缓存获取的Bean实例，那还是原型模式吗？不存在缓存当然也就无法实现上面描述的那一系列操作，也就无法解决循环依赖的问题了。

 

### 构造器注入导致的循环依赖没法解决吗？

下面是会造成循环依赖的构造器注入的方法

```java
@Component
public class CircularDependencyA {
 
    private CircularDependencyB circB;
 
    @Autowired
    public CircularDependencyA(CircularDependencyB circB) {
        this.circB = circB;
    }
}
 
@Component
public class CircularDependencyB {
 
    private CircularDependencyA circA;
 
    @Autowired
    public CircularDependencyB(CircularDependencyA circA) {
        this.circA = circA;
    }
}
```

#### 1. 用@lazy解决

```java
@Component
public class CircularDependencyA {
 
    private CircularDependencyB circB;
 
    @Autowired
    public CircularDependencyA(@Lazy CircularDependencyB circB) {
        this.circB = circB;
    }
 
}
```

#### 2.用setter方式解决

```java
@Component
public class CircularDependencyA {
 
    private CircularDependencyB circB;
 
    @Autowired
    public void setCircB(CircularDependencyB circB) {
        this.circB = circB;
    }
 
    public CircularDependencyB getCircB() {
       return circB;
    }
 
}
 
@Component
public class CircularDependencyB {
 
    private CircularDependencyA circA;
 
    private String message = "Hi!";
 
    @Autowired
    public void setCircA(CircularDependencyA circA) {
        this.circA = circA;
    }
 
    public String getMessage() {
        return message;
    }
 
}
```

#### 3.使用@PostConstruct方式解决

```java
@Component
public class CircularDependencyA {
 
    @Autowired
    private CircularDependencyB circB;
 
    @PostConstruct
    public void init() {
        circB.setCircA(this);
    }
 
    public CircularDependencyB getCircB() {
        return circB;
    }
 
}
 
@Component
public class CircularDependencyB {
 
    private CircularDependencyA circA;
 
    private String message = "Hi!";
 
    public void setCircA(CircularDependencyA circA) {
        this.circA = circA;
    }
 
    public String getMessage() {
        return message;
    }
 
}
```

