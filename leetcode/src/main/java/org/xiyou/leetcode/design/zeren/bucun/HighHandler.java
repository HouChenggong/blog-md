package org.xiyou.leetcode.design.zeren.bucun;

/**
 * @author xiyouyan
 * @date 2020-07-15 10:11
 * @description
 */

/**
 * created by huyanshi on 2019/3/19
 */
public class HighHandler extends AbstractParamHandler {

    @Override
    public BaseResponse doCheck(Person person) {
        System.out.println("high处理");
        if (person.high < 40) {
            return new BaseResponse(BaseError.HIGH_TOO_LOW);
        } else if (person.high > 236) {
            return new BaseResponse(BaseError.HIGH_TOO_HIGH);
        }
        return null;
    }
}

