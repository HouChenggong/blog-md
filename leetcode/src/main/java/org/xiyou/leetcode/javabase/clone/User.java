package org.xiyou.leetcode.javabase.clone;

import lombok.*;
import org.apache.commons.lang3.SerializationUtils;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;

/**
 * @author xiyouyan
 * @date 2020-07-07 09:19
 * @description
 */
@Getter
@Setter
public class User implements Serializable {


    private static final long serialVersionUID = 4956990649938926570L;
    private String name;

    private String id;

    private Role role;

    public User(String name, String id, Role role) {
        this.name = name;
        this.id = id;
        this.role = role;
    }

    public User() {
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", role=" + role.toString() +
                '}';
    }

    public static void main(String[] args) {
//        Role role = new Role("r1", "管理员");
//        User user = new User("u1", "xiyou", role);
//        user.setRole(role);
//        User copyUser = new User();
//        BeanUtils.copyProperties(user, copyUser);
//        System.out.println(user.toString());
//        System.out.println(copyUser.toString());
//        System.out.println(copyUser == user);
//        copyUser.setName("xiyouV2");
//        System.out.println(user.toString());
//        role.setName("管理员V2");
//        System.out.println(user.toString());
//        System.out.println(copyUser.toString());
//        System.out.println("________________________");

        Role role2 = new Role("r2", "管理员2");
        User user2 = new User("u2", "xiyou2", role2);
        user2.setRole(role2);
        User copyUser2 = SerializationUtils.clone(user2);

        System.out.println(user2.toString());
        System.out.println(copyUser2.toString());
        System.out.println(copyUser2 == user2);
        copyUser2.setName("xiyouV2");
        System.out.println(user2.toString());
        role2.setName("管理员V2");
        System.out.println(user2.toString());
        System.out.println(copyUser2.toString());
    }
}
