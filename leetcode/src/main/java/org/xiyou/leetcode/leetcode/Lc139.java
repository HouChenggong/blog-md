package org.xiyou.leetcode.leetcode;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author xiyouyan
 * @date 2020-06-25 16:07
 * @description
 */
public class Lc139 {
    public static boolean wordBreak(String s, List<String> wordDict) {
        Set<String> wordDictSet = new HashSet(wordDict);
        boolean[] dp = new boolean[s.length() + 1];
        dp[0] = true;
        for (int i = 1; i <= s.length(); i++) {
            for (int j = 0; j < i; j++) {

                if (dp[j] && wordDictSet.contains(s.substring(j, i))) {
                    dp[i] = true;
                    System.out.println("i:"+i+""+dp[i]+"--"+ s.substring(j,i));
                    break;
                }
            }
        }
        return dp[s.length()];
    }

    public static void main(String[] args) {
        String s="catsandog";
        List<String> list= Arrays.asList("cats", "dog", "sand", "and", "cat");

        System.out.println(wordBreak(s,list));
    }

}
