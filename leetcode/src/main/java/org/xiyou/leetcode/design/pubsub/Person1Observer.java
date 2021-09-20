package org.xiyou.leetcode.design.pubsub;

/**
 * @author xiyou
 * @version 1.0
 * xiyou-todo 观察者1号
 * @date 2020/6/8 21:48
 */
public class Person1Observer implements IOberser {
    @Override
    public void update(String name, String data) {
        System.out.println("第一个人 订阅 ： " + name + " 数据是： " + data);
    }
}
