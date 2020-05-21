### Spring框架的七大模块

Spring Core：框架的最基础部分，提供 IoC 容器，对 bean 进行管理。

Spring Context：继承BeanFactory，提供上下文信息，扩展出JNDI、EJB、电子邮件、国际化等功能。

Spring DAO：提供了JDBC的抽象层，还提供了声明性事务管理方法。

Spring ORM：提供了JPA、JDO、Hibernate、MyBatis 等ORM映射层.

Spring AOP：集成了所有AOP功能

Spring Web：提供了基础的 Web 开发的上下文信息，现有的Web框架，如JSF、Tapestry、Structs等，提供了集成

Spring Web MVC：提供了 Web 应用的 Model-View-Controller 全功能实现

### Bean定义5种作用域

singleton（单例） prototype（原型） request session global session

 ### springboot内置tomcat

SpringBoot 的启动是通过 new SpringApplication() 实例来启动的，启动过程主要做如下几件事情：> 1. 配置属性 > 2. 获取监听器，发布应用开始启动事件 > 3. 初始化输入参数 > 4. 配置环境，输出banner > 5. 创建上下文 > 6. 预处理上下文 > 7. 刷新上下文 > 8. 再刷新上下文 > 9. 发布应用已经启动事件 > 10. 发布应用启动完成事件 

而启动 Tomcat 就是在第7步中“刷新上下文”；Tomcat 的启动主要是初始化 2 个核心组件，连接器(Connector)和容器（Container），一个 Tomcat 实例就是一个 Server，一个 Server 包含多个 Service，也就是多个应用程序，每个 Service 包含多个连接器（Connetor）和一个容器（Container),而容器下又有多个子容器，按照父子关系分别为：Engine,Host,Context,Wrapper，其中除了 Engine 外，其余的容器都是可以有多个