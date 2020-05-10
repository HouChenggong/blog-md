package org.xiyou.leetcode.design.absfactory.cl;

import org.xiyou.leetcode.design.factory.SendMessageInterface;

/**
 * @author xiyou
 * @version 1.0
 * xiyou-todo 抽象工厂类
 * @date 2020/5/10 12:13
 */
public abstract class AbstractFactoryClass {
    /**
     * 抽象工厂类
     *
     * @return
     */
    public abstract SendMessageInterface absFactoryProduce();
}
