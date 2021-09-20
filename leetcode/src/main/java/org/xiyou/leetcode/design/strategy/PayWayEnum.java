package org.xiyou.leetcode.design.strategy;

/**
 * @author xiyou
 * @version 1.0
 * xiyou-todo XXX功能
 * @date 2020/6/5 12:08
 */

public enum PayWayEnum {

    /**微信支付*/
    WEICHAT_PAY("微信支付",101),
    /**支付宝支付*/
    AL_PAY("支付宝支付",100),
    /**银行卡支付*/
    CARD_PAY("银行卡支付",102),
    /**点券支付*/
    PONIT_COUPON_PAY("点券支付",103);

    private String msg;
    private Integer code;
    private IPayStrategy strategy;

    PayWayEnum(String msg, Integer code) {
        this.msg = msg;
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public Integer getCode() {
        return code;
    }
}


