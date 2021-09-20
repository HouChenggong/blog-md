package org.xiyou.leetcode.javabase;

import lombok.Data;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * @author xiyouyan
 * @date 2020-06-23 09:14
 * @description
 */
public class StrTest {
    static String newLine = System.getProperty("line.separator");

    public static void main(String[] args) {
        str2();
        Set<String> set = new HashSet<>();
        set.add("java");
        set.add("c");
        set.add("javaSricpt");
        String s = String.join(",", set);
        System.out.println(s);
    }

    @Data
    class User {
        private String name;
        private Integer age;
    }


    private static void str2() {
        String str1 = "";
        String str2 = null;
        System.out.println(Optional.ofNullable(str1).get());
        System.out.println(Optional.ofNullable(str2).orElse("xxx"));
    }


    private static void str1() {
        String mutiLine = "xx"
                .concat(newLine)
                .concat("1")
                .concat(newLine)
                .concat("2？")
                .concat(newLine)
                .concat("3？");
        System.out.println(mutiLine);

        String mutiXi[] = new String[]{"1", "2", "3"};
        String str2 = String.join("-", mutiXi);
        System.out.println(str2);
        int length = 6;
        boolean useLetters = true;
        // 不使用数字
        boolean useNumbers = false;
        String generatedString = RandomStringUtils.random(length, useLetters, useNumbers);
        StringUtils.isBlank(" ");
        String all = "";
        all = all.replaceAll("=", "x");
        System.out.println(all);
        System.out.println(generatedString);
        String s = "xxx12";
//        System.out.println(s.substring(0, s.length() - 1));
        s = StringUtils.substring(s, 0, s.length() - 1);
        s = StringUtils.chop(s);
        System.out.println(s);
        String s1 = "";
        Optional.ofNullable(s1);
        String result1 = Optional.ofNullable(s1)
                .map(str -> str.replaceAll(".$", ""))
                .orElse(s);
        System.out.println(s1 + "----");
    }
}
