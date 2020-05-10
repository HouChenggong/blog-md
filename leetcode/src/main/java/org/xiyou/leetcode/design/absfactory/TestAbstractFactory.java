package org.xiyou.leetcode.design.absfactory;

import org.xiyou.leetcode.design.factory.SendMessageInterface;

/**
 * @author xiyou
 * @version 1.0
 * @date 2020/5/10 12:01
 */
public class TestAbstractFactory {
    public static void main(String[] args) {
        AbstractFactoryInterface mailAbstractFactory = new MailAbstractFactory();
        AbstractFactoryInterface phoneAbstractFractory = new PhoneAbstractFractory();
        SendMessageInterface mail = mailAbstractFactory.absFactoryProduce();
        mail.send();
        SendMessageInterface phone = phoneAbstractFractory.absFactoryProduce();
        phone.send();
    }
}


