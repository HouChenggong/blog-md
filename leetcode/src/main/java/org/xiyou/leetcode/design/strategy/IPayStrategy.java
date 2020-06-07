package org.xiyou.leetcode.design.strategy;

/**
 * @author xiyou
 * @version 1.0
 * xiyou-todo 策略接口
 * @date 2020/6/5 11:24
 */
public interface IPayStrategy {
    /**
     * 器
     * @param paycode
     * @return
     */
    String selectPayWay(Integer paycode);
}
