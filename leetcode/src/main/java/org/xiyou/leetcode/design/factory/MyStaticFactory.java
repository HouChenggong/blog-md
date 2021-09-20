package org.xiyou.leetcode.design.factory;

/**
 * @author xiyou
 * @version 1.0
 * xiyou-todo 静态工厂模式核心工厂
 * @date 2020/5/10 11:51
 */
public class MyStaticFactory {
    public static MailSendImpl produceMail() {
        return new MailSendImpl();
    }

    public static PhoneSendImpl producePhone() {
        return new PhoneSendImpl();
    }

    public static void main(String[] args) {
        MailSendImpl mailSend = MyStaticFactory.produceMail();
        PhoneSendImpl phoneSend = MyStaticFactory.producePhone();
        mailSend.send();
        phoneSend.send();
    }
}
