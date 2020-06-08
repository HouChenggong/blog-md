package org.xiyou.leetcode.design.pubsub;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xiyou
 * @version 1.0
 * xiyou-todo XXX功能
 * @date 2020/6/8 21:42
 */
@Getter
@Setter
public class NewPaper implements IObserverSubject {
    private String name;

    private String data;

    private List<IOberser> list = new ArrayList<>(10);

    public NewPaper(String name, String data) {
        this.name = name;
        this.data = data;
    }
    public void  setNewMsg(String name,String data){
        System.out.println("更新消息:"+name+"   内容是： "+data);
        notifyObservers();
    }

    @Override
    public void addObserver(IOberser observer) {
        list.add(observer);
    }

    @Override
    public void deleteObserver(IOberser observer) {
        list.remove(observer);
    }

    @Override
    public void notifyObservers() {
        for (IOberser oberser : list) {
            oberser.update(name, data);
        }
    }
}
