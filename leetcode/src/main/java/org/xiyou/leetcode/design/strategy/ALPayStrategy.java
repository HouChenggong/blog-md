package org.xiyou.leetcode.design.strategy;

/**
 * @author xiyou
 * @version 1.0
 * @date 2020/6/5 11:28
 */
public class ALPayStrategy implements IPayStrategy {

    @Override
    public String selectPayWay(Integer paycode) {
        //do something
        return "支付宝支付成功"+paycode;
    }
}
