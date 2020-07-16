package org.xiyou.leetcode.design.zeren.bucun;

/**
 * @author xiyouyan
 * @date 2020-07-15 10:10
 * @description
 */

/**
 * created by huyanshi on 2019/3/19
 */
public abstract class AbstractParamHandler {

    private AbstractParamHandler nextHandler;

    public void setNextHandler(
            AbstractParamHandler nextHandler) {
        this.nextHandler = nextHandler;
    }

    public BaseResponse handlerRequest(Person person) {
        BaseResponse response = doCheck(person);
        if (null == response) {
            if (this.nextHandler != null) {
                return this.nextHandler.handlerRequest(person);
            } else {
                return new BaseResponse(BaseError.SUCCESS);
            }
        }
        return response;
    }

    public abstract BaseResponse doCheck(Person person);
}


