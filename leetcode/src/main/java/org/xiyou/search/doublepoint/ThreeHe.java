package org.xiyou.search.doublepoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author xiyouyan
 * @date 2020-07-10 13:44
 * @description 三数之和
 */
public class ThreeHe {

    /**
     * 三数之和为零
     *
     * @param nums
     * @return
     */
    public static List<List<Integer>> threeSum(int[] nums) {// 总时间复杂度：O(n^2)
        List<List<Integer>> all = new ArrayList<>();
        Arrays.sort(nums);
        int len = nums.length;
        for (int i = 0; i < len; i++) {
            if (nums[i] > 0) {
                return all;
            }
            if (i > 0 && nums[i] == nums[i - 1]) {
                continue;
            }
            int left = i + 1;
            int right = len - 1;
            while (left < right) {
                int sum = nums[i] + nums[left] + nums[right];
                if (sum == 0) {
                    List<Integer> oneList = Arrays.asList(nums[i], nums[left], nums[right]);
                    all.add(oneList);
                    right--;
                    left++;
                    while (nums[left] == nums[left - 1] && left < right) {
                        left++;
                    }
                    while (nums[right] == nums[right + 1] && left < right) {
                        right--;
                    }
                } else if (sum > 0) {
                    right--;
                } else {
                    left++;
                }

            }

        }
        return all;

    }

    /**
     * 最接近三数之和
     *
     * @param nums
     * @param target
     * @return
     */
    public static int threeSum(int[] nums, int target) {// 总时间复杂度：O(n^2)
        int result = Integer.MAX_VALUE;
        boolean dayuTarget = true;
        Arrays.sort(nums);
        int len = nums.length;
        for (int i = 0; i < len; i++) {
            if (i > 0 && nums[i] == nums[i - 1]) {
                continue;
            }
            int left = i + 1;
            int right = len - 1;
            while (left < right) {
                int sum = nums[i] + nums[left] + nums[right];
                if (sum == target) {
                    return target;
                } else if (sum > target) {
                    if (sum - target < result) {
                        result = sum - target;
                        dayuTarget = true;
                    }

                    right--;
                } else {
                    if (target - sum < result) {
                        result = target - sum;
                        dayuTarget = false;
                    }

                    left++;
                }

            }

        }
        if (dayuTarget) {
            return target + result;
        } else {
            return target - result;
        }
    }

    public static void main(String[] args) {
        int arr[] = new int[]{0, 2, 1, -3};
        System.out.println(threeSum(arr, 1));
    }

}
