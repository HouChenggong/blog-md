package org.xiyou.leetcode.design.adapter;

/**
 * @author xiyou
 * @version 1.0
 * xiyou-todo XXX功能
 * @date 2020/6/9 23:22
 */
public class TypeCx3Vga  extends TypeC3Impl {

    @Override
    public void typec() {
        System.out.println("信息从Typec口的手机输出。");
    }

    @Override
    public void typec2vga() {
        System.out.println("接收到Type-c口信息，信息转换成VGA接口中...");
        System.out.println("信息已转换成VGA接口，显示屏可以对接。");
    }

    public static void main(String[] args) {
        TypeCx3Vga c3=new TypeCx3Vga();
        c3.typec();
        c3.typec2vga();
        System.out.println("xxxx");
    }
}
