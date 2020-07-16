package org.xiyou.leetcode.design.zeren;

/**
 * @author xiyouyan
 * @date 2020-07-15 09:53
 * @description
 */
public class ZenTest {
    public static void main(String [] args ){
        //log信息
        LogInfo info = new LogInfo();
        info.content = "这是一条WARN测试log";
        info.levelEnum = LevelEnum.WARN;
        //第二条log信息
        LogInfo info1 = new LogInfo();
        info1.levelEnum = LevelEnum.ERROR;
        info1.content = "我是一条严重ERROR的LOG";


        //定义责任链
        AbstractLogHandler debugLog = new DebugLogHandler();
        AbstractLogHandler infoLog = new InfoLogHandler();
        AbstractLogHandler warnLog = new WarnLogHandler();
        AbstractLogHandler errorLog = new ErrorLogHandler();

        debugLog.setNextHandler(infoLog);
        infoLog.setNextHandler(warnLog);
        warnLog.setNextHandler(errorLog);

        debugLog.handlerRequest(info);
        debugLog.handlerRequest(info1);


    }

}
