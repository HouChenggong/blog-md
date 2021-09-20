package org.xiyou.leetcode.design.factory;

/**
 * @author xiyou
 * @version 1.0
 * xiyou-todo 简单工厂模式工厂
 * @date 2020/5/10 11:39
 */
public class MySimpleFactory {
    public SendMessageInterface produce(String mes) {
        if ("mail".equals(mes)) {
            return new MailSendImpl();
        } else if ("phone".equals(mes)) {
            return new PhoneSendImpl();
        } else {
            System.out.println("请输入正确的类型");
            return null;
        }
    }

    public static void main(String[] args) {
        MySimpleFactory factory = new MySimpleFactory();
        SendMessageInterface sendMessageInterface = factory.produce("mail");
        sendMessageInterface.send(); //    这里是邮件发送中心
        SendMessageInterface sendMessageInterface2 = factory.produce("phone");
        sendMessageInterface2.send();    //这里是手机短信发送中心

    }
}
