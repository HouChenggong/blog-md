package org.xiyou.leetcode.design.factory;

import org.springframework.stereotype.Service;

/**
 * @author xiyou
 * @version 1.0
 * @date 2020/5/10 11:39
 */
@Service
public class PhoneSendImpl implements SendMessageInterface {
    @Override
    public void send() {
        System.out.println("phone send");
    }
}
