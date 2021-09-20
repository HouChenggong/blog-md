package org.xiyou.leetcode.design.absfactory.cl;

import org.xiyou.leetcode.design.absfactory.AbstractFactoryInterface;
import org.xiyou.leetcode.design.absfactory.MailAbstractFactory;
import org.xiyou.leetcode.design.absfactory.PhoneAbstractFractory;
import org.xiyou.leetcode.design.factory.SendMessageInterface;

/**
 * @author xiyou
 * @version 1.0
 * @date 2020/5/10 12:01
 */
public class TestAbstractFactoryClass {
    public static void main(String[] args) {

        MailAbstractFactoryClass mail = new MailAbstractFactoryClass();

        MailAbstractFactoryClass phone = new MailAbstractFactoryClass();

        mail.absFactoryProduce().send();
        phone.absFactoryProduce().send();
    }
}


