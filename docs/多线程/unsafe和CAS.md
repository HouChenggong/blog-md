## Unsafe

简单讲一下这个类。Java无法直接访问底层操作系统，而是通过本地（native）方法来访问。不过尽管如此，JVM还是开了一个后门，JDK中有一个类Unsafe，它提供了硬件级别的原子操作。

从第一行的描述可以了解到Unsafe提供了硬件级别的操作，比如说

- 获取某个属性在内存中的位置
- 修改对象的字段值，即使它是私有的。

1. 获取给定的paramField的内存地址偏移量，这个值对于给定的field是唯一的且是固定不变的

```java
public native long staticFieldOffset(Field paramField);
```

2. 前一个方法是用来获取数组第一个元素的偏移地址，后一个方法是用来获取数组的转换因子即数组中元素的增量地址的。

```java
public native int arrayBaseOffset(Class paramClass);
public native int arrayIndexScale(Class paramClass);
```

3. 分配内存、扩充内存、释放内存

```java
public native long allocateMemory(long paramLong);
public native long reallocateMemory(long paramLong1, long paramLong2);
public native void freeMemory(long paramLong);
```

## CAS

CAS，Compare and Swap即比较并交换，设计并发算法时常用到的一种技术，java.util.concurrent包全完建立在CAS之上，没有CAS也就没有此包，可见CAS的重要性。

### CAS实现的方式

- 下面是Unsafe下面的三个方法，主要为CAS提供实现

```java
public final native boolean compareAndSwapObject(Object paramObject1, long paramLong, Object paramObject2, Object paramObject3);
 
public final native boolean compareAndSwapInt(Object paramObject, long paramLong, int paramInt1, int paramInt2);
 
public final native boolean compareAndSwapLong(Object paramObject, long paramLong1, long paramLong2, long paramLong3);
```

