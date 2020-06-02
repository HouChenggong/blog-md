package org.xiyou.leetcode.redis;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author xiyou
 * @version 1.0
 * xiyou-todo 手写LRU算法
 * @date 2020/6/2 21:00
 */
public class LRUCache<K, V> extends LinkedHashMap<K, V> {
    private int cacheSize;

    public LRUCache(int cacheSize) {
        //构造函数一定要放在第一行
        //那个f如果不加  就是double类型，然后该构造没有该类型的入参。 然后最为关键的就是那个入参 true
        super(16, 0.75f, true);
        this.cacheSize = cacheSize;
    }

    @Override
    //重写LinkedHashMap原方法
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        //临界条件不能有等于，否则会让缓存尺寸小1
        return size() > cacheSize;
    }

}
