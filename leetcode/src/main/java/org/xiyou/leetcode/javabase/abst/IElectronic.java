package org.xiyou.leetcode.javabase.abst;

/**
 * @author xiyou
 * @version 1.0
 * xiyou-todo 测试接口
 * @date 2020/5/14 11:56
 */
public interface IElectronic {
    String LED = "LED";

    int getElectricityUse();

    static boolean isEnergyEfficient(String electtronicType) {
        return electtronicType.equals(LED);
    }

    default void printDescription() {
        System.out.println("电子");
    }
}
