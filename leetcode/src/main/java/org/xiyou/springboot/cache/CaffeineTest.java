package org.xiyou.springboot.cache;

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.cache.RemovalListener;
import lombok.SneakyThrows;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.concurrent.TimeUnit;

/**
 * @author xiyouyan
 * @date 2020-06-22 23:46
 * @description
 */
public class CaffeineTest {
    @SneakyThrows
    public static void main(String[] args) {
        Cache<String, String> cache = Caffeine.newBuilder()
                // 数量上限
                .maximumSize(1024)
                // 过期机制
                .expireAfterWrite(5, TimeUnit.MINUTES)
                // 弱引用key
                .weakKeys()
                // 弱引用value
                .weakValues()
                // 剔除监听
//                .removalListener((RemovalListener<String, String>) (key, value) ->
//                        System.out.println("key:" + key + ", value:" + value + ", 删除原因:"  ))
                .build();
// 将数据放入本地缓存中
        cache.put("username", "afei");
        cache.put("password", "123456");
// 从本地缓存中取出数据
        System.out.println(cache.getIfPresent("username"));
        System.out.println(cache.getIfPresent("password"));
        System.out.println(cache.get("blog", key -> {
            // 本地缓存没有的话，从数据库或者Redis中获取
            return "not find";
        }));

        AsyncLoadingCache<String, String> cache2 = Caffeine.newBuilder()
                .maximumSize(2)
                .expireAfterWrite(5,TimeUnit.MINUTES)
                .refreshAfterWrite(1,TimeUnit.MINUTES)
                .buildAsync(new CacheLoader<String, String>() {
                    @Nullable
                    @Override
                    public String load(@NonNull String s) throws Exception {
                        return "success";
                    }
                });
        System.out.println(cache2.get("username").get());
        System.out.println(cache2.get("password").get(10, TimeUnit.MINUTES));
        System.out.println(cache2.get("username").get(10, TimeUnit.MINUTES));
        System.out.println(cache2.get("blog").get());
    }

}
