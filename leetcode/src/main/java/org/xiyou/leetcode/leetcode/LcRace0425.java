package org.xiyou.leetcode.leetcode;

import java.util.*;

/**
 * @author xiyou
 * https://leetcode-cn.com/contest/season/2020-spring/problems/qi-wang-ge-shu-tong-ji/
 * 其实代码很简单就是去重，但是这道题给它讲的太复杂了，特别是还有期望值的公式，但是简单来说，不管是多少个重复的数，最后取到的期望就是1
 * <p>
 * 比如1的期望是1
 * <p>
 * 1，1的期望还是1，设两位面试者的编号为 0, 1。由于他们的能力值都是 1，小 A 和小 B 的浏览顺序都为从全排列 `[[0,1],[1,0]]` 中等可能地取一个。
 * 如果小 A 和小 B 的浏览顺序都是 `[0,1]` 或者 `[1,0]` ，那么出现在同一位置的简历数为 2 ，否则是 0 。所以 `X` 的期望是 (2+0+2+0) * 1/4 = 1
 */
public class LcRace0425 {
    public int expectNumber(int[] scores) {
        HashSet<Integer> set = new HashSet<>(scores.length);
        for (int one : scores) {
            set.add(one);
        }
        return set.size();
    }

}
