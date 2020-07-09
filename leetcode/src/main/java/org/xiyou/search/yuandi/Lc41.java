package org.xiyou.search.yuandi;

/**
 * @author xiyouyan
 * @date 2020-07-09 15:06
 * @description 缺失的第一个正数
 * 原地算法解决
 */
public class Lc41 {
    /**
     * 缺失的第一个正数
     *
     * @param nums
     * @return
     */
    public static int firstMissingPositive(int[] nums) {
        int len = nums.length;
        for (int i = 0; i < len; i++) {
            //把3放到位置2
            while (nums[i] > 0 && nums[i] <= len && nums[i] != nums[nums[i] - 1]) {
                swap(nums, i, nums[i] - 1);
            }
        }
        for (int i = 0; i < len; i++) {
            //如果位置0，不为1，则返回1
            if (nums[i] != i + 1) {
                return i + 1;
            }
        }
        return len + 1;
    }

    public static void swap(int arr[], int index1, int index2) {
        int temp = arr[index1];
        arr[index1] = arr[index2];
        arr[index2] = temp;
    }
}
