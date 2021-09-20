package org.xiyou.leetcode.design.strategy;

/**
 * @author xiyou
 * @version 1.0
 * @date 2020/6/5 11:30
 */
public class PointCouponPayStrategy implements IPayStrategy {

    @Override
    public String selectPayWay(Integer paycode) {
        //do something
        return "点券支付成功"+paycode;
    }
}
