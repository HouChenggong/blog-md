package org.xiyou.search.strdig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * @author xiyouyan
 * @date 2020-07-09 23:22
 * @description
 */
public class Lc8 {
    public int myAtoi(String str) {
        char[] chars = str.toCharArray();
        int n = chars.length;
        int idx = 0;
        while (idx < n && chars[idx] == ' ') {
            // 去掉前导空格
            idx++;
        }
        if (idx == n) {
            //去掉前导空格以后到了末尾了
            return 0;
        }
        boolean negative = false;
        if (chars[idx] == '-') {
            //遇到负号
            negative = true;
            idx++;
        } else if (chars[idx] == '+') {
            // 遇到正号
            idx++;
        } else if (!Character.isDigit(chars[idx])) {
            // 其他符号
            return 0;
        }
        int ans = 0;
        while (idx < n && Character.isDigit(chars[idx])) {
            int digit = chars[idx] - '0';
            if (ans > (Integer.MAX_VALUE - digit) / 10) {
                // 本来应该是 ans * 10 + digit > Integer.MAX_VALUE
                // 但是 *10 和 + digit 都有可能越界，所有都移动到右边去就可以了。
                return negative ? Integer.MIN_VALUE : Integer.MAX_VALUE;
            }
            ans = ans * 10 + digit;
            idx++;
        }
        return negative ? -ans : ans;
    }

    public int[] twoSum(int[] nums, int target) {
        Arrays.sort(nums);
        HashMap<Integer, Integer> set = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            if (set.containsKey(target - nums[i])) {
                return new int[]{i, set.get(target - nums[i])};
            } else {
                set.put(nums[i], i);
            }
        }
        return new int[]{};
    }

    public static List<List<Integer>> threeSum(int[] nums) {
        List<List<Integer>> all = new ArrayList<>();
        Arrays.sort(nums);
        for (int i = 0; i < nums.length; i++) {
            int temp = -nums[i];
             if(i<nums.length-1 && nums[i+1]==nums[i]){
                 continue;
             }
            HashMap<Integer, Integer> set = new HashMap<>();
            for (int j = i; j < nums.length; j++) {
                if (set.containsKey(temp - nums[j])) {
                    List<Integer> list = new ArrayList<>(10);
                    list.add(nums[i]);
                    list.add(nums[j]);
                    list.add((temp - nums[j]));
                    all.add(list);
                } else {
                    set.put(nums[j], j);
                }
            }
            while (i<nums.length-1 && nums[i+1]==nums[i]){
                i++;
            }
        }
        return all;
    }

    public static void main(String[] args) {
        int arr[]=new int[]{-1,0,1,2,-1,-4};
        System.out.println(threeSum(arr));
    }
}
