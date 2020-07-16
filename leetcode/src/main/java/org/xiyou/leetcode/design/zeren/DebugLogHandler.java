package org.xiyou.leetcode.design.zeren;

/**
 * @author xiyouyan
 * @date 2020-07-15 09:50
 * @description
 */
public class DebugLogHandler  extends AbstractLogHandler{
    @Override
    void consumeLog(String content) {
        System.out.println("debug");
    }
    public DebugLogHandler() {
        this.levelEnum = LevelEnum.DEBUG;
    }
}
