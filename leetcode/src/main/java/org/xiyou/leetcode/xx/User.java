package org.xiyou.leetcode.xx;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Data
public class User {
    private String name;
    private int age;
    private String sex;


    public static   void  setA(User user){
        User a=user;
        a.setName("xxx");
    }
    public static void main(String[] args) {
        List<User> list = new ArrayList<>();
        User user1 = new User("张三",15,"男");

        User user2 = new User("李四",22,"男");
        User user3 = new User("王五",25,"男");
        User user4 = new User("赵六",30,"男");
        User user5 = new User("李世民",28,"男");
        User user6 = new User("杨贵妃",18,"女");
        User user7 = new User("貂蝉",16,"女");
        list.add(user1);
        list.add(user2);
        list.add(user3);
        list.add(user4);
        list.add(user5);
        list.add(user6);
        list.add(user7);
        List<User> collect = list.stream()
                .filter(user -> !"张三".equals(user.getName()))
                .collect(Collectors.toList());
        System.out.println(list);
        List<User> collect2 = list.stream()
                .filter(user -> user.getAge()<30 && "女".equals(user.getSex()))
                .collect(Collectors.toList());
      long total = list.stream()
                .filter(user -> user.getAge()<30 && "女".equals(user.getSex())).count();

        System.out.println(list);

        User user11 = new User("张三",15,"男");
        setA(user11);
        System.out.println(user11);
    }
}
