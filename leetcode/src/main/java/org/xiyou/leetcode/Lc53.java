package org.xiyou.leetcode;

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

    public static void main(String[] args) {
        int arr []=new int[]{1,-2,3,-4,2};
        System.out.println(maxSubArray(arr));
    }
}
