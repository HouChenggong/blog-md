package org.xiyou.leetcode.design.factory;

import org.springframework.stereotype.Service;

/**
 * @author xiyou
 * @version 1.0
 * @date 2020/5/10 11:38
 */
@Service
public class  MailSendImpl implements SendMessageInterface {
    @Override
    public void send() {
        System.out.println("mail send");
    }
}
