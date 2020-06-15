package org.xiyou.leetcode.design.adapter;

/**
 * @author xiyou
 * @version 1.0
 * @date 2020/6/9 22:46
 */
public class TypeCxVga extends Phone implements Vga {
    @Override
    public void vgaInterface() {
        typecPhone();
        System.out.println("接收到Type-c口信息，信息转换成VGA接口中...");
        System.out.println("信息已转换成VGA接口，显示屏可以对接。");
    }

    public static void main(String[] args) {
        //第一种适配器用法
        System.out.println("-------------第一种适配器------------");
        Vga vga = new TypeCxVga();
        vga.vgaInterface();//适配器将typec转换成vga
        System.out.println("显示屏对接适配器，手机成功投影到显示屏!");
    }
}
