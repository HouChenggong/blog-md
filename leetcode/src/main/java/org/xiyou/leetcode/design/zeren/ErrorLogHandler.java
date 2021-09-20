package org.xiyou.leetcode.design.zeren;

/**
 * @author xiyouyan
 * @date 2020-07-15 09:51
 * @description
 */
public class ErrorLogHandler extends AbstractLogHandler {
    @Override
    void consumeLog(String content) {
        System.out.println("error");
        System.out.println("error而且还发送邮件");
    }
    public ErrorLogHandler(){
        this.levelEnum=LevelEnum.ERROR;
    }
}
