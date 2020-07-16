package org.xiyou.leetcode.design.zeren.bucun;

/**
 * @author xiyouyan
 * @date 2020-07-15 10:09
 * @description
 */
public class BaseResponse {

    private int errCode;
    private String errMsg;

    private String content;

    public BaseResponse(BaseError error){
        this.errCode = error.errCode;
        this.errMsg = error.errMsg;
    }

    @Override
    public String toString() {
        return "errCode:" + errCode + ";" + "errMsg" + errMsg;
    }
}

