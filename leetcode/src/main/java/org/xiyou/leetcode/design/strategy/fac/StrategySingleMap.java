package org.xiyou.leetcode.design.strategy.fac;

/**
 * @author xiyou
 * @version 1.0
 * xiyou-todo XXX功能
 * @date 2020/6/5 12:29
 */

import org.xiyou.leetcode.design.strategy.*;

import java.util.HashMap;
import java.util.Map;

public class StrategySingleMap {
    //恶汉单例模式
    private static StrategySingleMap factory = new StrategySingleMap();

    private StrategySingleMap() {
    }
    public static StrategySingleMap getInstance() {
        return factory;
    }

    private static Map<Integer, IPayStrategy> strategyMap = new HashMap<>(16);

    static {
        strategyMap.put(PayWayEnum.AL_PAY.getCode(), new ALPayStrategy());
        strategyMap.put(PayWayEnum.WEICHAT_PAY.getCode(), new WeChatPayStrategy());
        strategyMap.put(PayWayEnum.CARD_PAY.getCode(), new CardPayStrategy());
        strategyMap.put(PayWayEnum.PONIT_COUPON_PAY.getCode(), new PointCouponPayStrategy());
    }

    public IPayStrategy getMapStrategy(Integer payCode) {
        return strategyMap.get(payCode);
    }
}


