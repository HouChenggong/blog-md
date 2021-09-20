package org.xiyou.leetcode.design.strategy;

/**
 * @author xiyou
 * @version 1.0
 * @date 2020/6/5 11:29
 */
public class WeChatPayStrategy implements IPayStrategy {

    @Override
    public String selectPayWay(Integer paycode) {
        //do something
        return "微信支付成功"+paycode;
    }

}
