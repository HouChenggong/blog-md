package org.xiyou.leetcode.design.strategy;

/**
 * @author xiyou
 * @version 1.0
 * @date 2020/6/5 11:29
 */
public class CardPayStrategy implements IPayStrategy {
    @Override
    public String selectPayWay(Integer paycode) {
        //do something
        return "银行卡支付成功"+paycode;
    }
}
