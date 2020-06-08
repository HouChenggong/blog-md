package org.xiyou.leetcode.design.pubsub;

/**
 * @author xiyou
 * @version 1.0
 * xiyou-todo XXX功能
 * @date 2020/6/8 21:49
 */
public class OberserTest {
    public static void main(String[] args) {
        NewPaper newPaper1 = new NewPaper("1月份报纸", "内容是111111111");
        Person1Observer p1 = new Person1Observer();
        Person2Observer p2 = new Person2Observer();
        newPaper1.addObserver(p1);
        newPaper1.addObserver(p2);
        newPaper1.notifyObservers();
        newPaper1.setNewMsg("1月份报纸2", "美国战火四起");

    }


}
