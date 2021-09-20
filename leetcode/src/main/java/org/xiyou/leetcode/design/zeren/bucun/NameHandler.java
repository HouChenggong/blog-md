package org.xiyou.leetcode.design.zeren.bucun;

/**
 * @author xiyouyan
 * @date 2020-07-15 10:10
 * @description
 */

/**
 * created by huyanshi on 2019/3/19
 */
public class NameHandler extends AbstractParamHandler {

    @Override
    public BaseResponse doCheck(Person person) {
        System.out.println("name处理");
        if (person.name == null || person.name.length() < 1) {
            return new BaseResponse(BaseError.NAME_TOO_SHORT);
        } else if (person.name.length() > 10) {
            return new BaseResponse(BaseError.NAME_TOO_LONG);
        }
        return null;
    }
}


