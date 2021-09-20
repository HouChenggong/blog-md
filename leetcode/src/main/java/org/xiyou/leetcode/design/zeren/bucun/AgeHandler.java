package org.xiyou.leetcode.design.zeren.bucun;

/**
 * @author xiyouyan
 * @date 2020-07-15 10:10
 * @description
 */

/**
 * created by huyanshi on 2019/3/19
 */
public class AgeHandler extends AbstractParamHandler {

    @Override
    public BaseResponse doCheck(Person person) {
        System.out.println("age处理");
        if (person.age < -0.9) {
            return new BaseResponse(BaseError.AGE_TOO_SMALL);
        } else if (person.age > 1000) {
            return new BaseResponse(BaseError.AGE_TOO_BIG);
        }
        return null;
    }
}

