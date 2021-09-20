package org.xiyou.leetcode.design.absfactory.builder;

/**
 * @author xiyou
 * @version 1.0
 * xiyou-todo 建造者模式
 * @date 2020/6/5 17:53
 */
public class Computer {
    private String cpu;//必须
    private String ram;//必须
    private int usbCount;//可选
    private String keyboard;//可选
    private String display;//可选

    //第二步
    private Computer(Builder builder){
        this.cpu=builder.cpu;
        this.ram=builder.ram;
        this.usbCount=builder.usbCount;
        this.keyboard=builder.keyboard;
        this.display=builder.display;
    }
    //第一步
    public static class Builder{
        private String cpu;//必须
        private String ram;//必须
        private int usbCount;//可选
        private String keyboard;//可选
        private String display;//可选

        //第三步
        public Builder(String cup,String ram){
            this.cpu=cup;
            this.ram=ram;
        }
        //第四步
        public Builder setUsbCount(int usbCount) {
            this.usbCount = usbCount;
            return this;
        }
        //第四步
        public Builder setKeyboard(String keyboard) {
            this.keyboard = keyboard;
            return this;
        }
        //第四步
        public Builder setDisplay(String display) {
            this.display = display;
            return this;
        }
        //第五步（最后一步）
        public Computer build(){
            return new Computer(this);
        }
    }
}

