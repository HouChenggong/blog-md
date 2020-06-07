package org.xiyou.leetcode.design.strategy;

import org.xiyou.leetcode.design.strategy.fac.StrategySingleMap;

/**
 * @author xiyou
 * @version 1.0
 * xiyou-todo 策略模式上下文切换
 * @date 2020/6/5 11:30
 */
public class PayContextSimple {

    //一个策略
    private IPayStrategy payStrategy;

    public PayContextSimple(IPayStrategy payStrategy) {
        this.payStrategy = payStrategy;
    }

    public String getPayStrategy(Integer code) {
        return payStrategy.selectPayWay(code);
    }

    public static String doPay(int type, int money) {
        IPayStrategy xx = null;
        if (type == 100) {
            xx = new ALPayStrategy();
        } else if (type == 101) {
            xx = new WeChatPayStrategy();
        } else {
            //....其它策略或者不存在丢弃
        }
        PayContextSimple xxContent = new PayContextSimple(xx);
        return xxContent.getPayStrategy(money);

    }

    public static String doPay2(int type, int money) {
        IPayStrategy xx = StrategySingleMap.getInstance().getMapStrategy(type);
        PayContextSimple xxContent = new PayContextSimple(xx);
        return xxContent.getPayStrategy(money);
    }

    public static String doPay3(int type, int money) {
        IPayStrategy xx = PayWay2Enum.getStrategyByType(type);
        if (xx == null) {
            return "不存在的类型";
        }
        PayContextSimple xxContent = new PayContextSimple(xx);
        return xxContent.getPayStrategy(money);
    }

    public static void main(String[] args) {
        IPayStrategy ali = new ALPayStrategy();
        PayContextSimple contextSimple = new PayContextSimple(ali);
        String aliPrice = contextSimple.getPayStrategy(11);
        System.out.println(aliPrice);


        IPayStrategy we = new WeChatPayStrategy();
        PayContextSimple weContent = new PayContextSimple(we);
        String wePrice = weContent.getPayStrategy(13);
        System.out.println(wePrice);

        System.out.println(PayContextSimple.doPay(100, 66));
        System.out.println(PayContextSimple.doPay2(100, 88));
        System.out.println(PayContextSimple.doPay3(102, 55));
        System.out.println(PayContextSimple.doPay3(103, 44));
        System.out.println(PayContextSimple.doPay3(22, 44));

    }
}
