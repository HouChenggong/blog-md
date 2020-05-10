package org.xiyou.leetcode.design.factory;

/**
 * @author xiyou
 * @version 1.0
 * xiyou-todo 多个工厂模式核心工厂
 * @date 2020/5/10 11:47
 */
public class MyManyFactory {
    public MailSendImpl produceMail() {
        return new MailSendImpl();
    }

    public PhoneSendImpl producePhone() {
        return new PhoneSendImpl();
    }

    public static void main(String[] args) {
        MyManyFactory myManyFactory = new MyManyFactory();
        MailSendImpl mailSend = myManyFactory.produceMail();
        PhoneSendImpl phoneSend = myManyFactory.producePhone();
        mailSend.send();
        phoneSend.send();
    }
}
