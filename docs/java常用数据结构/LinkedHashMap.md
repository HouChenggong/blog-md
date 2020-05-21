### 访问

这里accessOrder设置为false，表示不是访问顺序而是插入顺序存储的，这也是默认值

true 表示是以：访问顺序存储

```dart
 Map<String, String> linkedHashMap = new LinkedHashMap<>(16, 0.75f, false);
```

### 简单的LRU

```
//map的最大容量是10
final int maxSize = 10;
Map<Integer, Integer> map = new LinkedHashMap<Integer, Integer>(0, 0.75f, true) {
  @Override
  protected boolean removeEldestEntry(Map.Entry<Integer, Integer> eldest) {
      return size() > maxSize;
  }
};
```



### 用LinkedHashMap写一个LRU缓存系统



```
public class LruCache<K,V> {
    private LinkedHashMap<K,V> map;
    //最大缓存容量
    private int maxSize;
    //当前容量
    private int size;


    public LruCache(int maxSize){
        this.maxSize = maxSize;
        map = new LinkedHashMap<>(0, 0.75f, true);
    }

    /**
     * 插入一条数据，更新当前容量大小，并检测是否已超出容量
     * @param key
     * @param value
     */
    public void put(K key, V value){
        size += sizeOf(key, value);
        map.put(key, value);
        trimSize(maxSize);
    }

    /**
     * 获取一条数据
     * @param key
     * @return
     */
    public V get(K key){
        V v;
        synchronized (this){
            v = map.get(key);
        }
        return v;
    }

    /**
     * 删除一条数据，并更新当前容量
     * @param key
     * @return
     */
    public V remote(K key){
        V v = map.remove(key);
        size -= sizeOf(key, v);
        return v;
    }

    /**
     * 检测当前容量是否已经超过最大容量，如果超过就开始清除数据，知道size小于maxSize为止。
     * @param maxSize
     */
    public void trimSize(int maxSize){
        while (true) {
            K key;
            V value;
            synchronized (this) {
                if (size < 0 || (map.isEmpty() && size != 0)) {
                    throw new IllegalStateException(getClass().getName()
                            + ".sizeOf() is reporting inconsistent results!");
                }

                if (size <= maxSize || map.isEmpty()) {
                    break;
                }

                Map.Entry<K, V> toEvict = map.entrySet().iterator().next();
                key = toEvict.getKey();
                value = toEvict.getValue();
                map.remove(key);
                size -= sizeOf(key, value);
            }
        }
    }

    /**
     * 重新调整缓存总大小
     * @param maxSize
     */
    public void resize(int maxSize){
        if (maxSize <= 0){
            throw new IllegalArgumentException("MaxSize 不能小于等于0！");
        }
        synchronized (this){
            this.maxSize = maxSize;
        }
        resize(maxSize);
    }

    /**
     * 数据大小.默认为1，想要修改数据大小，还需要子类实现
     * @param key
     * @param value
     * @return
     */
    protected int sizeOf(K key, V value) {
        return 1;
    }

    /**
     * 清除
     * @return
     */
    public void clear(){
        synchronized (this){
            map.clear();
        }
    }

    @Override
    public String toString() {
        return "size:"+size+";maxSize:"+maxSize+" "+map.toString();
    }
}


```

