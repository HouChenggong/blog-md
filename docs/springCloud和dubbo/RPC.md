## socket+JDKproxy实现的RPC通信demo
###  客户端如何调用不知道具体实现的接口

在我们之前一个服务器里面方法的调用，其实可以理解为API 、service、Client在一起，也就是可以理解为Client在maven中引入了API和service的包

但是RPC不一样，我们只引入API，不引入service，也就是只知道接口名称，不知道具体的service，这个时候我们怎么调用service服务，而且又像在本地一样调用呢？

答案是：找到一个中间人：代理对象，客户端Client只需要告诉Proxy对象它要访问的接口名称是什么，然后代理对象帮你生成一个你要的对象，其实就是动态代理的$Proxy0.class对象

### RPC必须要用动态代理吗？

回顾代理模式的优点

- **不改变别人代码前提下，实现自己要添加的功能**
- **可以实现一套通用的逻辑，避免重复操作**

所以RPC也一样，没用必须。只是RPC要实现的逻辑有一部分是相同的，就是连接服务端，序列化发送服务，然后再接受服务端返回的数据，所以用了动态代理，这一套逻辑我们可以通用化，不需要每个接口都去实现

### RPC客户端代理对象的实现

但是注意，这个生成的代理对象并没有实现相应的接口，也就是它其实并不知道要实现的具体方法是什么，它只是把方法抛给了你实现的InvocationHandler，比如sayHello方法，其实就是反射调用你的InvocationHandler里面的invoke方法

```java
 public final String sayHello(String var1) throws  {
            return (String)super.h.invoke(this, m3, new Object[]{var1});
    }
```

所以RPC动态代理生成的Proxy0对象和动态代理生成的对象没用区别，生成的代理对象如下：

```java
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sun.proxy;

import cn.net.health.user.aop.dynamic.DataChangeListener;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.UndeclaredThrowableException;

public final class $Proxy0 extends Proxy implements DataChangeListener {
    //下面的几个方法分别对应hash toString equals
    //还有你自己实现的方法sayHello listener
    private static Method m1;
    private static Method m3;
    private static Method m4;
    private static Method m2;
    private static Method m0;

    public $Proxy0(InvocationHandler var1) throws  {
        super(var1);
    }


    public final String sayHello(String var1) throws  {
        try {
            return (String)super.h.invoke(this, m3, new Object[]{var1});
        } catch (RuntimeException | Error var3) {
            throw var3;
        } catch (Throwable var4) {
            throw new UndeclaredThrowableException(var4);
        }
    }
 
 

    public final void listener(Object var1) throws  {
        try {
            super.h.invoke(this, m4, new Object[]{var1});
        } catch (RuntimeException | Error var3) {
            throw var3;
        } catch (Throwable var4) {
            throw new UndeclaredThrowableException(var4);
        }
    }
 

    static {
        try {
            m1 = Class.forName("java.lang.Object").getMethod("equals", Class.forName("java.lang.Object"));
            m3 = Class.forName("cn.net.health.user.aop.dynamic.DataChangeListener").getMethod("sayHello", Class.forName("java.lang.String"));
            m2 = Class.forName("java.lang.Object").getMethod("toString");
            m4 = Class.forName("cn.net.health.user.aop.dynamic.DataChangeListener").getMethod("listener", Class.forName("java.lang.Object"));
            m0 = Class.forName("java.lang.Object").getMethod("hashCode");
        } catch (NoSuchMethodException var2) {
            throw new NoSuchMethodError(var2.getMessage());
        } catch (ClassNotFoundException var3) {
            throw new NoClassDefFoundError(var3.getMessage());
        }
    }
}

```

既然生成的代理对象没用区别，那RPC是如何实现调用不同服务下的接口服务的呢？

答案是：代理对象的invoke方法不同

### RPC和动态代理的Invoke方法的区别

- 这时我们之前动态代理的实现，里面的invoke方法其实就是反射调用被代理的真实对象的方法，但是有一个**前提是我们需要传入我们真实的被代理对象subject**

```java
public class MyInvocationHandlerImpl implements InvocationHandler {

    /**
     * 这个就是我们要代理的真实对象
     */
    private Object subject;

    /**
     * 构造方法，给我们要代理的真实对象赋初值
     *
     * @param subject
     */
    public MyInvocationHandlerImpl(Object subject) {
        this.subject = subject;
    }

    @Override
    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
        //在代理真实对象前我们可以添加一些自己的操作
        System.out.println("在调用之前..........head，我要干点啥呢？"+method.getName());
        //当代理对象调用真实对象的方法时，其会自动的跳转到代理对象关联的handler对象的invoke方法来进行调用
        Object returnValue = method.invoke(subject, objects);
        //在代理真实对象后我们也可以添加一些自己的操作
        System.out.println("在调用之后...........end，我要干点啥呢？"+method.getName());
        return returnValue;
    }
}
```

- 但是**我们的RPC客户端的代理类根本不知道真实的被代理对象是谁**，因为不在一起，怎么办呢？下面我们看下RPC动态代理的具体实现
  - 可以看到这里不需要再传入真实的被代理对象，而是传入了IP和端口
  - 而且invoke方法也不再是反射调用被代理对象的方法
  - 而是把请求的数据封装成对象，然后序列化发送给服务端

```java
public class RpcClientProxy implements InvocationHandler {
    private String host;
    private int port;

    public RpcClientProxy(String host, int port) {
        this.host = host;
        this.port = port;
    }
    @SuppressWarnings("unchecked")
    public <T>T getProxy(Class<T> clazz){
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, RpcClientProxy.this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        RpcRequest rpcRequest = RpcRequest.builder().methodName(method.getName())
                .parameters(args)
                .interfaceName(method.getDeclaringClass().getName())
                .paramTypes(method.getParameterTypes())
                .build();
        RpcClient rpcClient = new RpcClient();
        return rpcClient.sendRpcRequest(rpcRequest,host,port);
    }
}
```

- 具体的`rpcClient.sendRpcRequest(rpcRequest,host,port);`实现如下：

```java
public class RpcClient {
    public static final Logger logger = LoggerFactory.getLogger(RpcClient.class);

    public Object sendRpcRequest(RpcRequest rpcRequest, String host, int port) {
        //1.建立一个socket对象
        try (Socket socket = new Socket(host, port)) {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            //2.把我们的要传输对象传进去
            objectOutputStream.writeObject(rpcRequest);

            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            return objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            logger.error("occur exception:", e);
        }
        return null;
    }
}
```



### 客户端代理对象序列化发送数据并接收数据

1. 客户端如果想调用某个接口的服务，比如Hellow接口
2. 首先根据上面的介绍就是它要本地实现动态代理创建一个Proxy0对象，为啥非要用动态代理，没说必须要用，只是用了会非常方便，而且是通用逻辑

```java
    public static void main(String[] args) {
        //选择一个代理对象
        RpcClientProxy rpcClientProxy = new RpcClientProxy("127.0.0.1", 9999);
        //用代理对象生成要访问的方法
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        //调用代理对象生成的对象的方法
        String hello = helloService.hello(new Hello("111", "222"));
        System.out.println(hello);
    }
```

生成的代理对象要做的就是发送数据，并且接受返回的数据，所以代理对象的invoke方法如下：

```java
@Override
public Object invoke(Object proxy, Method method, Object[] args) {
    //接受请求的数据
    RpcRequest rpcRequest = RpcRequest.builder().methodName(method.getName())
            .parameters(args)
            .interfaceName(method.getDeclaringClass().getName())
            .paramTypes(method.getParameterTypes())
            .build();
    //生成一个客户端传输对象
    RpcClient rpcClient = new RpcClient();
    return rpcClient.sendRpcRequest(rpcRequest,host,port);
}
```

- 客户端传输对象如下：

其实就是建立一个socket对象，把要传输对象传进去，如果拿到了结果，就反序列化读出来

```java
public class RpcClient {
    public static final Logger logger = LoggerFactory.getLogger(RpcClient.class);

    public Object sendRpcRequest(RpcRequest rpcRequest, String host, int port) {
        //1.建立一个socket对象
        try (Socket socket = new Socket(host, port)) {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            //2.把我们的要传输对象传进去
            objectOutputStream.writeObject(rpcRequest);
			//3.进行反序列化读取结果
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            return objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            logger.error("occur exception:", e);
        }
        return null;
    }
}
```



socket可以使一个应用从网络中读取和写入数据，不同计算机上的两个应用可以通过连接发送和接受字节流，当发送消息时，你需要知道对方的ip和端口，在java中，socket指的是java.net.Socket类。 

### 服务端注册自己的服务，并接受请求，返回数据

1. 一个服务端要处理客户端的请求，首先要把自己的服务启动并且暴漏出来，并且要做到来一个请求就处理一个请求
2. 来一个请求并处理一个请求ServerSocket可以做到，因为ServerSocket是等待客户端的请求，一旦获得一个连接请求，就创建一个Socket示例来与客户端进行通信。 
3. 但是serverSocket不能同时处理多个请求，所以我们用线程的方式去弥补
4. 当多个请求来的时候，我们开N个线程分别取执行不同的请求



> 代码如下：服务端注册并监听一个端口，打开一个socket，当socket里面接受到数据，
>
> 就新建一个socket并且开一个线程去执行
>
> (Socket socket = server.accept()是从队列里面接受请求,并创建一个socket连接

```java
 public class RpcServer {
   public void register(Object service, int port) {
        try (ServerSocket server = new ServerSocket(port);) {
            logger.info("server starts...");
             
            while ((Socket socket = server.accept()) != null) {
                logger.info("client connected");
                threadPool.execute(new WorkerThread(socket, service));
            }
        } catch (IOException e) {
            logger.error("occur IOException:", e);
        }
    }
    ....
```

5. 我们一个线程内部执行的逻辑肯定是：拿到给自己的socket和服务端注册的service去执行，因为它不能接受别的service，所以一定要有一个service
6. 具体执行的逻辑可以想象，无非就是把socket里面的信息读出来（反序列化）出来，然后反射调用方法，最后再把结果（序列化）传输给客户端

具体的代码如下：

```java
    public void run() {
        // 注意使用 try-with-resources ,因为这样更加优雅
        // 并且,try-with-resources 语句在编写必须关闭资源的代码时会更容易，也不会出错
        try (ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream())) {
            RpcRequest rpcRequest = (RpcRequest) objectInputStream.readObject();
            Method method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
            Object result = method.invoke(service, rpcRequest.getParameters());
            objectOutputStream.writeObject(result);
            objectOutputStream.flush();
        } catch (IOException | ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            logger.error("occur exception:", e);
        }
    }
```

- 所以最后，服务端的代码如下：

```java
    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        RpcServer rpcServer = new RpcServer();
        rpcServer.register(helloService, 9999);
    }
```

###  github地址

https://gitee.com/SnailClimb/guide-rpc-framework/tree/de721cc11c304531b52fec6a000f83a54c854a28

这时guide哥，第一次提交的代码





### 第一步优化

#### 优化服务端

- 我们之前的服务端代码就是上面所写的，但是并没有做判断，比如ServiceA我本地没用实现怎么办？
- 比如能不能把结果统一封装一下
- 如果传过来的方法为空怎么办？

```java
    public void run() {
        // 注意使用 try-with-resources ,因为这样更加优雅
        // 并且,try-with-resources 语句在编写必须关闭资源的代码时会更容易，也不会出错
        try (ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream())) {
            RpcRequest rpcRequest = (RpcRequest) objectInputStream.readObject();
            Method method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
            Object result = method.invoke(service, rpcRequest.getParameters());
            objectOutputStream.writeObject(result);
            objectOutputStream.flush();
        } catch (IOException | ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            logger.error("occur exception:", e);
        }
    }
```

##### 对异常判断，对结果进行封装

```java
@Override
    public void run() {
        // 注意使用 try-with-resources ,因为这样更加优雅
        // 并且,try-with-resources 语句在编写必须关闭资源的代码时会更容易，也不会出错
        try (ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream())) {
            RpcRequest rpcRequest = (RpcRequest) objectInputStream.readObject();
            Object result = invokeTargetMethod(rpcRequest);
            RpcResponse res = (RpcResponse) result;
            objectOutputStream.writeObject(res);
            objectOutputStream.flush();
        } catch (IOException | ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            logger.error("occur exception:", e);
        }
    }

    private Object invokeTargetMethod(RpcRequest rpcRequest) throws NoSuchMethodException, ClassNotFoundException, IllegalAccessException, InvocationTargetException {
        Class<?> cls = Class.forName(rpcRequest.getInterfaceName());
        // 判断类是否实现了对应的接口
        if (!cls.isAssignableFrom(service.getClass())) {
            logger.error(RpcResponseCode.NOT_FOUND_CLASS.getMessage(), service.getClass());
            RpcResponse response = RpcResponse.fail(RpcResponseCode.NOT_FOUND_CLASS);
            return response;
        }
        Method method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
        if (null == method) {
            logger.error(RpcResponseCode.NOT_FOUND_METHOD.getMessage(), service.getClass());
            return RpcResponse.fail(RpcResponseCode.NOT_FOUND_METHOD);
        }
        Object res = method.invoke(service, rpcRequest.getParameters());
        //结果统一封装
        return RpcResponse.success(res);
    }
```

#### 优化客户端

##### 对结果判断，并异常处理

```java
public class RpcClient {
    public static final Logger logger = LoggerFactory.getLogger(RpcClient.class);

    public Object sendRpcRequest(RpcRequest rpcRequest, String host, int port) {
        try (Socket socket = new Socket(host, port)) {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(rpcRequest);
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            RpcResponse rpcResponse = (RpcResponse) objectInputStream.readObject();
            if (rpcResponse == null) {
                logger.error("调用服务失败,serviceName:{} error is [{}]", rpcRequest.getInterfaceName(), RpcErrorMessageEnum.SERVICE_INVOCATION_FAILURE.getMessage());
                throw new RpcException(RpcErrorMessageEnum.SERVICE_INVOCATION_FAILURE, "interfaceName:" + rpcRequest.getInterfaceName());
            }
            if (rpcResponse.getCode() == null || rpcResponse.getCode() != (RpcResponseCode.SUCCESS.getCode())) {
                logger.error("调用服务失败,serviceName :" + rpcRequest.getInterfaceName() + "RpcResponse:" + rpcResponse.getMessage());
                throw new RpcException(RpcErrorMessageEnum.SERVICE_INVOCATION_FAILURE, "interfaceName:" + rpcRequest.getInterfaceName());
            }
            return rpcResponse.getData();
        } catch (IOException | ClassNotFoundException e) {
            throw new RpcException("调用服务失败:", e);
        }
    }
}
```

#### demo测试

- ISayHello注意这个方法服务端里面是没用实现的，所以就会报异常

```java
    public static void main(String[] args) {
        RpcClientProxy rpcClientProxy = new RpcClientProxy("127.0.0.1", 9999);
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        String hello = helloService.hello(new Hello("111", "222"));
        System.out.println(hello);
        ISayHello sayHello = rpcClientProxy.getProxy(ISayHello.class);
        String sayHelloRes = sayHello.sayHello( "I am xiyou");
        System.out.println(sayHelloRes);
    }
```

### 第二步优化

#### 把所有的service和方法缓存起来

- 写一个接口

```java
public interface ServiceRegistry {
    <T> void register(T service);

    Object getService(String serviceName);
}
```

- 写一个默认的实现类

```java
public class DefaultServiceRegistry implements ServiceRegistry {
    private static final Logger logger = LoggerFactory.getLogger(DefaultServiceRegistry.class);

    /**
     * 接口名和服务的对应关系，TODO 处理一个接口被两个实现类实现的情况
     * key:service/interface name
     * value:service
     */
    private final Map<String, Object> serviceMap = new ConcurrentHashMap<>();
    private final Set<String> registeredService = ConcurrentHashMap.newKeySet();

    /**
     * TODO 修改为扫描注解注册
     * 将这个对象所有实现的接口都注册进去
     */
    @Override
    public synchronized <T> void register(T service) {
        String serviceName = service.getClass().getCanonicalName();
        if (registeredService.contains(serviceName)) {
            return;
        }
        registeredService.add(serviceName);
        Class[] interfaces = service.getClass().getInterfaces();
        if (interfaces.length == 0) {
            throw new RpcException(RpcErrorMessageEnum.SERVICE_NOT_IMPLEMENT_ANY_INTERFACE);
        }
        for (Class i : interfaces) {
            serviceMap.put(i.getCanonicalName(), service);
        }
        logger.info("Add service: {} and interfaces:{}", serviceName, service.getClass().getInterfaces());
    }

    @Override
    public synchronized Object getService(String serviceName) {
        Object service = serviceMap.get(serviceName);
        if (null == service) {
            throw new RpcException(RpcErrorMessageEnum.SERVICE_CAN_NOT_BE_FOUND);
        }
        return service;
    }
}
```

#### 修改服务端调用逻辑

- 对服务端代码的修改，主要是对service进行注册和获取逻辑的修改

```java
public class RpcServer {
    /**
     * 线程池参数
     */
    private static final int CORE_POOL_SIZE = 10;
    private static final int MAXIMUM_POOL_SIZE_SIZE = 100;
    private static final int KEEP_ALIVE_TIME = 1;
    private static final int BLOCKING_QUEUE_CAPACITY = 100;
    private ExecutorService threadPool;
    private RpcRequestHandler rpcRequestHandler = new RpcRequestHandler();
    private static final Logger logger = LoggerFactory.getLogger(RpcServer.class);
    private final ServiceRegistry serviceRegistry;

    public RpcServer(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(BLOCKING_QUEUE_CAPACITY);
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        this.threadPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE_SIZE, KEEP_ALIVE_TIME, TimeUnit.MINUTES, workQueue, threadFactory);
    }

    public void start(int port) {

        try (ServerSocket server = new ServerSocket(port);) {
            logger.info("server starts...");
            Socket socket;
            while ((socket = server.accept()) != null) {
                logger.info("client connected");
                threadPool.execute(new RpcRequestHandlerRunnable(socket, rpcRequestHandler, serviceRegistry));
            }
            threadPool.shutdown();
        } catch (IOException e) {
            logger.error("occur IOException:", e);
        }
    }
```

其实主要变得是这个

```java
 threadPool.execute(new RpcRequestHandlerRunnable(socket, rpcRequestHandler, serviceRegistry));
```

对应得线程处理逻辑也改变了

```java
public class RpcRequestHandlerRunnable implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RpcRequestHandlerRunnable.class);
    private Socket socket;
    private RpcRequestHandler rpcRequestHandler;
    private ServiceRegistry serviceRegistry;

    public RpcRequestHandlerRunnable(Socket socket, RpcRequestHandler rpcRequestHandler, ServiceRegistry serviceRegistry) {
        this.socket = socket;
        this.rpcRequestHandler = rpcRequestHandler;
        this.serviceRegistry = serviceRegistry;
    }

    @Override
    public void run() {
        try (ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream())) {
            RpcRequest rpcRequest = (RpcRequest) objectInputStream.readObject();
            String interfaceName = rpcRequest.getInterfaceName();
            Object service = serviceRegistry.getService(interfaceName);
            Object result = rpcRequestHandler.handle(rpcRequest, service);
            objectOutputStream.writeObject(RpcResponse.success(result));
            objectOutputStream.flush();
        } catch (IOException | ClassNotFoundException e) {
            logger.error("occur exception:", e);
        }
    }

}
```

其实也非常要理解，无非就是把注册和获取得接口传入，然后从缓存里面读取方法名称和接口名称，然后把反射调用和异常处理单独拎出来

```java
public class RpcRequestHandler {
    private static final Logger logger = LoggerFactory.getLogger(RpcRequestHandler.class);

    public Object handle(RpcRequest rpcRequest, Object service) {
        Object result = null;
        try {
            result = invokeTargetMethod(rpcRequest, service);
            logger.info("service:{} successful invoke method:{}", rpcRequest.getInterfaceName(), rpcRequest.getMethodName());
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            logger.error("occur exception", e);
        }
        return result;
    }

    private Object invokeTargetMethod(RpcRequest rpcRequest, Object service) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Method method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
        if (null == method) {
            return RpcResponse.fail(RpcResponseCode.NOT_FOUND_METHOD);
        }
        return method.invoke(service, rpcRequest.getParameters());
    }
}
```

- 最后是服务端注册服务

```java
    public static void main(String[] args) {
        HelloServiceImpl helloService = new HelloServiceImpl();
        DefaultServiceRegistry defaultServiceRegistry = new DefaultServiceRegistry();
        // 手动注册
        defaultServiceRegistry.register(helloService);
        RpcServer rpcServer = new RpcServer(defaultServiceRegistry);
        rpcServer.start(9999);
    }
```

- 客户端不变进行调用

```java
    public static void main(String[] args) {
        RpcClientProxy rpcClientProxy = new RpcClientProxy("127.0.0.1", 9999);
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        String hello = helloService.hello(new Hello("111", "222"));
        System.out.println(hello);
        ISayHello sayHello = rpcClientProxy.getProxy(ISayHello.class);
        String sayHelloRes = sayHello.sayHello( "I am xiyou");
        System.out.println(sayHelloRes);
    }
```

##  V2.0 Netty来袭

由于socket的缺点，我们采用Netty来建立我们的客户端

首先引入netty和Kryo进行通信和序列化

### Kryo序列化、编码解码

- 注意所有对象要实现空的构造方法，否则序列化会失败，如果是Lombok

  ```java
  @Data
  @Builder
  //下面两个不能丢
  @AllArgsConstructor
  @NoArgsConstructor
  ```

  

### Netty客户端

### Netty服务端

