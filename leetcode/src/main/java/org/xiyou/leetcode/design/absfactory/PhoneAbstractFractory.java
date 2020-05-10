package org.xiyou.leetcode.design.absfactory;

import org.xiyou.leetcode.design.factory.PhoneSendImpl;
import org.xiyou.leetcode.design.factory.SendMessageInterface;

/**
 * @author xiyou
 * @version 1.0
 * xiyou-todo phone抽象实现
 * @date 2020/5/10 11:59
 */
public class PhoneAbstractFractory implements AbstractFactoryInterface {
    @Override
    public SendMessageInterface absFactoryProduce() {
        return new PhoneSendImpl();
    }
}
