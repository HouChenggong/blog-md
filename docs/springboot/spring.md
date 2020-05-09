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