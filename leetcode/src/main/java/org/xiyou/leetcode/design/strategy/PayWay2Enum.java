package org.xiyou.leetcode.design.strategy;

/**
 * @author xiyou
 * @version 1.0
 * xiyou-todo XXX功能
 * @date 2020/6/5 12:08
 */

public enum PayWay2Enum {

    /**
     * 微信支付
     */
    WEICHAT_PAY("微信支付", 101, new WeChatPayStrategy()),
    /**
     * 支付宝支付
     */
    AL_PAY("支付宝支付", 100, new ALPayStrategy()),
    /**
     * 银行卡支付
     */
    CARD_PAY("银行卡支付", 102, new CardPayStrategy()),
    /**
     * 点券支付
     */
    PONIT_COUPON_PAY("点券支付", 103, new PointCouponPayStrategy());

    private String msg;
    private Integer code;
    private IPayStrategy strategy;

    PayWay2Enum(String msg, Integer code, IPayStrategy strategy) {
        this.msg = msg;
        this.code = code;
        this.strategy = strategy;
    }

    public String getMsg() {
        return msg;
    }

    public Integer getCode() {
        return code;
    }

    public IPayStrategy getStrategy() {
        return strategy;
    }

    public static IPayStrategy getStrategyByType(int type) {
        for (PayWay2Enum one : values()) {
            if (type == one.getCode()) {
                return one.getStrategy();
            }
        }
        return null;
    }
}


