### ArrayList

ArrayList本身线程不安全，具体哪里不安全呢？参考：[arrayList不安全示例](https://blog.csdn.net/u010416101/article/details/88720974)

```java
    public boolean add(E e) {
    	// 确保ArrayList的长度足够
        ensureCapacityInternal(size + 1);  // Increments modCount!!
        // ArrayList加入
        elementData[size++] = e;
        return true;
    }
```

1. 增加元素会发生数据覆盖的问题 2. 扩充数组长度的时候会发生数组越界的问题;

增加元素过程中较为容易出现问题的部分在于`elementData[size++] = e;`.赋值的过程可以分为两个步骤`elementData[size] = e;size++;`

### LinkedList

- linkedList其实就有两个节点，一个头，一个尾节点

```
    
    public class LinkedList<E>
    
    transient Node<E> first;
 
    transient Node<E> last;
```

- linkedList内部节点

```java
 private static class Node<E> {
        E item;
        Node<E> next;
        Node<E> prev;

        Node(Node<E> prev, E element, Node<E> next) {
            this.item = element;
            this.next = next;
            this.prev = prev;
        }
    }
```

- addLast(e)

```java
    /**
     * Links e as last element.
     */
    void linkLast(E e) {
        final Node<E> l = last;
        final Node<E> newNode = new Node<>(l, e, null);
        last = newNode;
        if (l == null)
            first = newNode;
        else
            l.next = newNode;
        size++;
        modCount++;
    }
```

- addFirst(e)

```java
 private void linkFirst(E e) {
        final Node<E> f = first;
        final Node<E> newNode = new Node<>(null, e, f);
        first = newNode;
        if (f == null)
            last = newNode;
        else
            f.prev = newNode;
        size++;
        modCount++;
    }
```

#### linkedList哪里线程不安全

在尾部添加节点的时候，会发生数据覆盖的问题

### linkedHashMap

- 所以对于linkedHashMap也是一样的，因为它的组成和LinkedList一样

```java
    /**
     * The head (eldest) of the doubly linked list.
     */
    transient LinkedHashMap.Entry<K,V> head;

    /**
     * The tail (youngest) of the doubly linked list.
     */
    transient LinkedHashMap.Entry<K,V> tail;
```

- 只不过它的node节点要复杂些,它的node 继承了hashMap的node

```java
    static class Entry<K,V> extends HashMap.Node<K,V> {
        Entry<K,V> before, after;
        Entry(int hash, K key, V value, Node<K,V> next) {
            super(hash, key, value, next);
        }
    }
```

- 它的关联到尾节点的方法如下：

```java
    // link at the end of list
    private void linkNodeLast(LinkedHashMap.Entry<K,V> p) {
        LinkedHashMap.Entry<K,V> last = tail;
        tail = p;
        if (last == null)
            head = p;
        else {
            p.before = last;
            last.after = p;
        }
    }
```

- 它的实际get操作

```java
    public V get(Object key) {
        Node<K,V> e;
        if ((e = getNode(hash(key), key)) == null)
            return null;
        if (accessOrder)
            afterNodeAccess(e);
        return e.value;
    }
```

- get之后会调用一个afterNodeAccess方法

```java
 void afterNodeAccess(Node<K,V> e) { // move node to last
        LinkedHashMap.Entry<K,V> last;
        if (accessOrder && (last = tail) != e) {
            LinkedHashMap.Entry<K,V> p =
                (LinkedHashMap.Entry<K,V>)e, b = p.before, a = p.after;
            p.after = null;
            if (b == null)
                head = a;
            else
                b.after = a;
            if (a != null)
                a.before = b;
            else
                last = b;
            if (last == null)
                head = p;
            else {
                p.before = last;
                last.after = p;
            }
            tail = p;
            ++modCount;
        }
    }
```

### CopyOnWriteArrayList

**CopyOnWriteArrayList**是Java并发包中提供的一个并发容器，它是个**线程安全且读操作无锁的ArrayList**，写操作则通过创建底层数组的新副本来实现，是一种**读写分离**的并发策略，我们也可以称这种容器为"写时复制器"，Java并发包中类似的容器还有CopyOnWriteSet

- 原理：
  - 读不加锁，直接在原数组上读
  - 写的话copy一份数据出来，在新的副本上执行，同时加上线程同步机制，保证写安全，同时读操作还是在原来的上面进行
  - 写完之后把原容器指向新副本，切换的过程中用volatile保证对读线程立即可见
- 优点：
  - 读操作性能很高，因为无需任何同步措施，比较适用于**读多写少**的并发场景。
  - Java的list在遍历时，若中途有别的线程对list容器进行修改，则会抛出**ConcurrentModificationException**异常。
  - 而CopyOnWriteArrayList由于其"读写分离"的思想，遍历和修改操作分别作用在不同的list容器，所以在使用迭代器进行遍历时候，也就不会抛出ConcurrentModificationException异常了
- 缺点
  - 浪费内存，可能会导致频繁GC
  - 实时性较差，因为读不加锁，写的过程中还是读的老容器

#### 写ReentrantLock加锁

```java
public boolean add(E e) {
        //ReentrantLock加锁，保证线程安全
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            Object[] elements = getArray();
            int len = elements.length;
            //拷贝原容器，长度为原容器长度加一
            Object[] newElements = Arrays.copyOf(elements, len + 1);
            //在新副本上执行添加操作
            newElements[len] = e;
            //将原容器引用指向新副本
            setArray(newElements);
            return true;
        } finally {
            //解锁
            lock.unlock();
        }
    }
```

#### Volatile

```java
public class CopyOnWriteArrayList<E>{
    /** The lock protecting all mutators */
    final transient ReentrantLock lock = new ReentrantLock();

    /** The array, accessed only via getArray/setArray. */
    private transient volatile Object[] array;

    /**
     * Gets the array.  Non-private so as to also be accessible
     * from CopyOnWriteArraySet class.
     */
    final Object[] getArray() {
        return array;
    }

    /**
     * Sets the array.
     */
    final void setArray(Object[] a) {
        array = a;
    }
```

