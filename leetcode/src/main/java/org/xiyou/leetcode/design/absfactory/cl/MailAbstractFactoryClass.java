package org.xiyou.leetcode.design.absfactory.cl;

import org.xiyou.leetcode.design.absfactory.AbstractFactoryInterface;
import org.xiyou.leetcode.design.factory.MailSendImpl;
import org.xiyou.leetcode.design.factory.SendMessageInterface;

/**
 * @author xiyou
 * @version 1.0
 * xiyou-todo mail抽象实现
 * @date 2020/5/10 11:59
 */
public class MailAbstractFactoryClass  extends AbstractFactoryClass {
    @Override
    public SendMessageInterface absFactoryProduce() {
        return new MailSendImpl();
    }
}
