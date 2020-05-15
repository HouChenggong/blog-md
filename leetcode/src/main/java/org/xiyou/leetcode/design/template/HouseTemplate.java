package org.xiyou.leetcode.design.template;

/**
 * @author xiyou
 * @version 1.0
 * xiyou-todo XXX功能
 * @date 2020/5/14 22:25
 */
public abstract class HouseTemplate {

    protected HouseTemplate(String name){
        this.name = name;
    }

    protected String name;

    protected abstract void buildDoor();

    protected abstract void buildWindow();

    protected abstract void buildWall();

    protected abstract void buildBase();

    protected abstract void buildToilet();

    //钩子方法
    protected boolean isBuildToilet(){
        return true;
    }

    //公共逻辑
    public final void buildHouse(){

        buildBase();
        buildWall();
        buildDoor();
        buildWindow();
        if(isBuildToilet()){
            buildToilet();
        }
    }

}
