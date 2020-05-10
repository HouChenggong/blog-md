package org.xiyou.leetcode.design.absfactory;

import org.xiyou.leetcode.design.factory.SendMessageInterface;

/**
 * @author xiyou
 * @version 1.0
 * xiyou-todo 抽象工厂模式核心工厂接口
 * @date 2020/5/10 11:56
 */
public interface AbstractFactoryInterface {
    /**
     * 抽象工厂模式核心工厂接口
     *
     * @return
     */
    SendMessageInterface absFactoryProduce();
}
