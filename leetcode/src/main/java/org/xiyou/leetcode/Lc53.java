package org.xiyou.leetcode;

import java.util.HashMap;

/**
 * @author xiyou
 * @version 1.0
 * xiyou-todo LC53题
 * @date 2020/5/3 21:54
 */
public class Lc53 {
    public  static int maxSubArray(int[] nums) {
        int ans = nums[0];
        int sum = 0;
        for (int num : nums) {
            if (sum > 0) {
                sum += num;
            } else {
                sum = num;
            }
            ans = Math.max(ans, sum);
        }
        return ans;

    }
    public static int[] subarraySum(int[] nums ) {
        int len = nums.length;
        // 计算前缀和数组
        int[] preSum = new int[len + 1];
        preSum[0] = nums[0];
        for (int i = 1; i < len; i++) {
            preSum[i] = preSum[i - 1] + nums[i];
        }
        return preSum;
    }
    public static int[] subarraySum2(int[] nums ) {
        int len = nums.length;
        // 计算前缀和数组
        int[] preSum = new int[len + 1];
        preSum[0] = 0;
        for (int i = 1; i < len; i++) {
            preSum[i+1] = preSum[i ] + nums[i];
        }
        return preSum;
    }

    public static int subarraySum3(int[] nums, int k) {
        int len = nums.length;
        // 计算前缀和数组
        int[] preSum = new int[len + 1];
        preSum[0] = 0;
        for (int i = 0; i < len; i++) {
            preSum[i + 1] = preSum[i] + nums[i];
        }

        int count = 0;
        for (int left = 0; left < len; left++) {
            for (int right = left; right < len; right++) {
                // 区间和 [left..right]，注意下标偏移
                if (preSum[right + 1] - preSum[left] == k) {
                    count++;
                }
            }
        }
        return count;
    }
    public static  int subarraySum4(int[] nums, int k) {
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


    public static void main(String[] args) {
        int arr []=new int[]{1,2,3,4,5,6};
        System.out.println(maxSubArray(arr));
       int arr1[]=subarraySum(arr);
       int arr2[]=subarraySum2(arr);
       int a=subarraySum3(arr,0);
        System.out.println();
    }

}
