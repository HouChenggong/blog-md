## JPA 查询

### 1、原生SQL

```mysql
select * from xxx where appId=xxx and status=xxx

select * from xxx where appId=xxx and  status=xxx and name like %xxx%
```



### 2、JPA推荐方式

```java
List<XXX> findByAppIdAndStatusIs(Integer appId,Integer status)
```



### 3、 JAP——Specifications方式

现在统一换成JPA的高级查询,相关官方文档参考：[JPA-Specifications](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#specifications)

我们精简SQL的原理是利用`JpaSpecificationExecutor`进行，

- 如下，我们可以对findOne，分页或者查询List和count进行精简

```java

public interface JpaSpecificationExecutor<T> {
    Optional<T> findOne(@Nullable Specification<T> var1);

    List<T> findAll(@Nullable Specification<T> var1);

    Page<T> findAll(@Nullable Specification<T> var1, Pageable var2);

    List<T> findAll(@Nullable Specification<T> var1, Sort var2);

    long count(@Nullable Specification<T> var1);
}
```

#### 3.1、DAO层改造

为了支持`Specifications`，我们需要改造我们的DAO层

- 比如：原本的DAO是

```java
@Repository public interface UserDAO extends PagingAndSortingRepository<User, Long>
```

- 为了支持Specifications,改造如下

```java
@Repository public interface userDAO extends  PagingAndSortingRepository<User, Long>, JpaSpecificationExecutor<User> {  
```

### 4、SQL精简实战

#### 4.1 findById系列

比如一个查询如下：**select \* from xxx where id=xxx and status=xxx** 

```java
User findByIdAndStatusIs(Long id, Integer status);
```

我们这里有两种改造方案：

方案一、直接用Optional的findById,然后进行条件判断

- 适用场景：条件不是很多的情况下，一般是3个以下

  

```java

Optional<User> optional= userDAO.findById(id);
if(!optional.isPresent()){
    throw new DataNotExistsCodeException();
}
User data=optional.get();

if(BooleanEnum.FALSE.getValue().equals(data.getStatus())){
    throw new DataNotExistsCodeException();
}
```

方案二、用Specifications进行改造



```

private Specification<User> getSearchSpecification( Long id,Integer status) {
    Specification<User> specification = (Specification<User>) (root, cq, cb) -> {
        Predicate idPre = cb.equal(root.get("id").as(Long.class), id);
        Predicate statusPre = cb.equal(root.get("status").as(Integer.class), status);
        List<Predicate> predicatesList = new ArrayList<>();
        predicatesList.add(idPre);
        predicatesList.add(statusPre);
        Predicate[] predicates = new Predicate[predicatesList.size()];
        cq.where(cb.and(predicatesList.toArray(predicates)));
        return cq.getRestriction();
    };
    return specification;
}

```



#### 4.2 查询Page或者List系列

- 原本的SQL

```
 select * from user where user_name=xxx and status= xxx
```

改造查询

利用`List<T> findAll(@Nullable Specification<T> var1);`传入一个Specification

如果是查询列表则利用Page<T> findAll(@Nullable Specification<T> var1, Pageable var2);

下面对一个查询列表的进行改造

```
 List<User> list = userDAO.findAll (name, BooleanEnum.TRUE.getValue()));
```

- 具体的Specification改造如下：

```java
    /**
     * 查询
     * where    status=xxx and userName =xxx
     *
     * @param userName
     * @param status
     * @return
     */
    private Specification<User> getSearchSpecification(String userName, Integer status) {
        Specification<User> specification = (Specification<User>) (root, cq, cb) -> {
            Predicate userNamePre = cb.equal(root.get("userName").as(String.class), userName);
            Predicate statusPre = cb.equal(root.get("status").as(Integer.class), status);
            List<Predicate> predicatesList = new ArrayList<>();
            predicatesList.add(userNamePre);
            predicatesList.add(statusPre);
            Predicate[] predicates = new Predicate[predicatesList.size()];
            cq.where(cb.and(predicatesList.toArray(predicates)));
            return cq.getRestriction();
        };
        return specification;
    }
```





#### 4.3 count改造

比如有如下的SQL

```
Long countByAppIdAndStatusIsNot(Long appId, Integer status);
```



我们同样可以参考findById的Specification方案改造，这里不再说明



### 5、总结

这里的Specification改造方案有缺点也有优点

- 优点：精简SQL，而且复用性高
- 缺点：使用者需要查询代码才能看懂这个查询语句代表的SQL意思