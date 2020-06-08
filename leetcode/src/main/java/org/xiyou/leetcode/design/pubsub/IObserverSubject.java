package org.xiyou.leetcode.design.pubsub;

/**
 * @author xiyou
 * @version 1.0
 * xiyou-todo 观察者模式
 * @date 2020/6/8 21:39
 */
public interface IObserverSubject  {
    /**
     * 添加观察者
     * @param observer
     */
      void addObserver(IOberser observer);

    /**
     * 删除指定观察者
     * @param observer
     */
      void deleteObserver(IOberser observer);

    /**
     * 通知所有观察者
     */
      void notifyObservers();

}
