package org.xiyou.leetcode.design.zeren;

/**
 * @author xiyouyan
 * @date 2020-07-15 09:51
 * @description
 */
public class WarnLogHandler extends AbstractLogHandler {
    @Override
    void consumeLog(String content) {
        System.out.println("warn ");
    }
    public WarnLogHandler(){
        this.levelEnum=LevelEnum.WARN;
    }
}
