package org.xiyou.leetcode.design.zeren;

/**
 * @author xiyouyan
 * @date 2020-07-15 09:51
 * @description
 */
public class InfoLogHandler extends AbstractLogHandler {
    @Override
    void consumeLog(String content) {
        System.out.println("info");
    }

    public InfoLogHandler(){
        this.levelEnum=LevelEnum.INFO;
    }
}
