



## 手撸AOP

### 先看下动态代理

在我们目录的设计模式里面，有一个代理模式，点击跳转

[代理模式-动态代理](./docs/springboot/动态代理.md)



### 1. 随便写一个接口和实现类

- 接口

```
/**
 * @author xiyou
 * JDK动态代理
 */
public interface DataChangeListener {

    /**
     * 测试JDK动态代理
     *
     * @param name
     * @return
     */
    String sayHello(String name);

}
```

- 实现类

```
/**
 * @author xiyou
 * JDK动态代理监听器
 */
public class DataChangeListerImpl implements DataChangeListener {

    @Override
    public String sayHello(String name) {
        if (name == null) {
            throw new RuntimeException("param is null");
        }
        return name + System.currentTimeMillis();
    }
    }
```

### 2. 开发自己的拦截器和实现类

- 拦截器

```java
public interface XiyouInterceptor {

    /**
     * 事前方法
     *
     * @return
     */
    boolean before();


    /**
     * 事后方法
     */
    void after();

    /**
     * 要环绕的方法
     * @param params
     * @param method
     * @param target
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public Object around(Object[] params, Method method, Object target) throws InvocationTargetException, IllegalAccessException;


    /**
     * 事件无异常的时候执行
     */
    void afterRetruning();

    /**
     * 事件异常的时候执行
     */
    void afterThrowing();

    /**
     * 是否使用around取代原来的方法
     *
     * @return
     */
    boolean userAround();

}
```

- 里面用到的对象

```java
/**
 * @author xiyou
 * 其实就是一个对象
 */
public class XiyouInvocation {


    private Object[] params;

    private Method method;

    private Object target;

    public Object proceed() throws InvocationTargetException, IllegalAccessException {
        return method.invoke(target, params);
    }


    public XiyouInvocation(Object[] params, Method method, Object target) {
        this.params = params;
        this.method = method;
        this.target = target;
    }

    public Object[] getParams() {
        return params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }
}
```

- 拦截器实现类

```java
public class XiyouInterceptorImpl implements XiyouInterceptor {
    @Override
    public boolean before() {
        System.out.println("before ..........");
        return true;
    }

    @Override
    public void after() {
        System.out.println("after ..........");
    }

    @Override
    public Object around(Object[] params, Method method, Object target) throws InvocationTargetException, IllegalAccessException {
        System.out.println("around before ..........");
        Object o = method.invoke(target, params);
        System.out.println("around after ..........");
        return o;
    }

    @Override
    public void afterRetruning() {
        System.out.println("afterRetruning ..........");
    }

    @Override
    public void afterThrowing() {
        System.out.println("afterThrowing ..........");
    }

    @Override
    public boolean userAround() {
        return true;
    }
}
```

### 3. 实现JDK动态代理（核心）

注意里面的参数一个不能错，错了就死循环了，建议直接拷贝，我这里就错了很多次

```java
package cn.net.health.user.aop.my;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author xiyou
 * 真正AOP的核心
 */
public class XiyouProxyBean implements InvocationHandler {

    /**
     * 这个就是我们要代理的真实对象
     */
    private Object target;

    private XiyouInterceptor xiyouInterceptor;

    public XiyouProxyBean(Object myObject, XiyouInterceptor xiyouInterceptor) {
        this.target = myObject;
        this.xiyouInterceptor = xiyouInterceptor;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        boolean excetionFlag = false;
        Object returnObj = null;
        try {
            if (this.xiyouInterceptor.before()) {
                returnObj = this.xiyouInterceptor.around(args,method,target);
            } else {
                returnObj = method.invoke(target, args);
            }
        } catch (Exception e) {
            //产生异常
            excetionFlag = true;
        }
        this.xiyouInterceptor.after();
        if (excetionFlag) {
            this.xiyouInterceptor.afterThrowing();
        } else {
            this.xiyouInterceptor.afterRetruning();
            return returnObj;
        }
        return null;
    }
}

```

### 4. 测试和结果

```java
public class XiyouInterceptorTest {
    public static void main(String[] args) {
        DataChangeListener target = new DataChangeListerImpl();
        XiyouProxyBean proxyBean=new XiyouProxyBean(target,new XiyouInterceptorImpl());
        DataChangeListener proxy =(DataChangeListener) Proxy.newProxyInstance(target.getClass().getClassLoader()
                , target.getClass().getInterfaces()
                , proxyBean);
        proxy.sayHello("11111111111111ll");

    }
}
```

结果如下：

```sql
before ..........
around before ..........
2020-06-04 19:33:39.519--- [main] INFO  c.net.health.user.aop.dynamic.DataChangeListerImpl ---- 11111111111111ll1591270419510
around after ..........
after ..........
afterRetruning ..........
```

### 总结-嵌套代理

其实看下来就是嵌套了一下，比如我们动态代理其实就是为某一个接口（JDK动态代理）、或者某一个类（CGlib）进行了增强，而AOP其实就是增强的时候去实现了一套通用的方法,在通用方法的里面去调用了具体的方法，减少代码量

**下面我们总结下手写AOP的具体逻辑过程：**

- 我们要想把我们的某个方法进行AOP，那么我们要先写一个接口DataChangeListener和实现类DataChangeListerImpl
- 第二步：定义一个进行增强环绕的接口XiyouInterceptor，然后里面的around方法，传入我们想进行环绕的方法，代码如下：

```java
    @Override
    public Object around(Object[] params, Method method, Object target) throws InvocationTargetException, IllegalAccessException {
        System.out.println("around before ..........");
        Object o = method.invoke(target, params);
        System.out.println("around after ..........");
        return o;
    }
```

其实就是反射调用我们传入的方法，只不过在前后添加自己的逻辑

- 第三步，我们创建自定义的IvocationHandler，注意这个时候需要传入两个对象，一个对象是我们要进行环绕的对象DataChangeListener（可以是任意对象），另一个是我们的通用方法对象XiyouInterceptor，代码如下

```java
public class XiyouProxyBean implements InvocationHandler {

    /**
     * 这个就是我们要代理的真实对象
     */
    private Object target;

    private XiyouInterceptor xiyouInterceptor;

    public XiyouProxyBean(Object target, XiyouInterceptor xiyouInterceptor) {
        this.target = target;
        this.xiyouInterceptor = xiyouInterceptor;
    }
}
```

- 第四步，我们在实现自定义的IvocationHandler的时候，可以判断是否需要进行增强，如果不需要，直接调用即可target的方法即可，如果需要增强，调用XiyouInterceptor的around方法，代码如下：

```java
@Override
public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		//如果需要增强
        if (this.xiyouInterceptor.before()) {
        
                returnObj = this.xiyouInterceptor.around(args,method,target);
            } else {
            //不需要增强
                returnObj = method.invoke(target, args);
            }
            ....
 }
```

