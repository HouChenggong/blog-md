package org.xiyou.leetcode.design.zeren.bucun;

/**
 * @author xiyouyan
 * @date 2020-07-15 10:11
 * @description
 */
public class BuCunTest {
    public static void main(String[] args) {

        AbstractParamHandler name = new NameHandler();
        AbstractParamHandler age = new AgeHandler();
        AbstractParamHandler high = new HighHandler();
        name.setNextHandler(age);
        age.setNextHandler(high);

        //成功案例
        Person person = new Person("huyanshi", 23, 172);
        System.out.println(name.handlerRequest(person));

//        //名字太长案例
//        Person person1 = new Person("huyanshihuyanshi",22 , 122);
//        System.out.println(name.handlerRequest(person1));
//
//        //年龄太小
//        Person person2 = new Person("huyanshi",-10 , 122);
//        System.out.println(name.handlerRequest(person2));
    }

}
