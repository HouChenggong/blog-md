package org.xiyou.leetcode.leetcode.qianzui;

import java.util.*;

/**
 * @author xiyou
 * @version 1.0
 * xiyou-todo XXX功能
 * @date 2020/5/27 20:58
 */
public class Lc560 {
    public static int subarraySum(int[] nums, int k) {
        int count = 0;

        for (int start = 0; start < nums.length; ++start) {
            int sum = 0;
            List<Integer> oneArr = new ArrayList<>(8);
            for (int end = start; end >= 0; --end) {
                sum += nums[end];
                oneArr.add(nums[end]);
                if (sum == k) {
                    count++;
                }
            }
            System.out.println(oneArr.toString());
        }
        return count;
    }

    public static int subarraySum2(int[] nums, int k) {
        int count = 0;
        for (int i = 0; i < nums.length; i++) {
            int sum = 0;
            for (int j = i; j < nums.length; j++) {
                sum += nums[j];
                if (sum == k) {
                    count++;
                }
            }
        }
        return count;
    }

    public static int subarraySum3(int[] nums, int k) {
        int count = 0, pre = 0;
        HashMap< Integer, Integer > mp = new HashMap < > ();
        mp.put(0, 1);
        for (int i = 0; i < nums.length; i++) {
            pre += nums[i];
            if (mp.containsKey(pre - k)) {
                count += mp.get(pre - k);
            }
            mp.put(pre, mp.getOrDefault(pre, 0) + 1);
        }
        return count;
    }
    public static int subarraysDivByK(int[] A, int K) {
        Map<Integer, Integer> record = new HashMap<>();
        record.put(0, 1);
        int sum = 0, ans = 0;
        for (int elem: A) {
            sum += elem;
            // 注意 Java 取模的特殊性，当被除数为负数时取模结果为负数，需要纠正
//            int modulus = (sum % K + K) % K;
            int modulus=sum%K;
            modulus=modulus+K;
            modulus=modulus%K;
            int same = record.getOrDefault(modulus, 0);
            ans += same;
            record.put(modulus, same + 1);
        }
        return ans;
    }

    public static int largestRectangleArea(int[] heights) {
        // 这里为了代码简便，在柱体数组的头和尾加了两个高度为 0 的柱体。
        int[] tmp = new int[heights.length + 2];
        System.arraycopy(heights, 0, tmp, 1, heights.length);

        Deque<Integer> stack = new ArrayDeque<>();
        int area = 0;
        for (int i = 0; i < tmp.length; i++) {
            // 对栈中柱体来说，栈中的下一个柱体就是其「左边第一个小于自身的柱体」；
            // 若当前柱体 i 的高度小于栈顶柱体的高度，说明 i 是栈顶柱体的「右边第一个小于栈顶柱体的柱体」。
            // 因此以栈顶柱体为高的矩形的左右宽度边界就确定了，可以计算面积🌶️ ～
            while (!stack.isEmpty() && tmp[i] < tmp[stack.peek()]) {
                int h = tmp[stack.pop()];
                System.out.println(i+"   "+stack.peek()+",,,,"+h);
                area = Math.max(area, (i - stack.peek() - 1) * h);
            }
            stack.push(i);
        }

        return area;
    }

    public static void main(String[] args) {
        int arr[] = new int[]{2,1,5,6,2,3};
//        int arr[] = new int[]{1,1,1};
//        System.out.println(subarraySum(arr, 7));
//        System.out.println(subarraySum2(arr, 7));
//        System.out.println(subarraysDivByK(arr,5));
        System.out.println(largestRectangleArea(arr));
    }
}
