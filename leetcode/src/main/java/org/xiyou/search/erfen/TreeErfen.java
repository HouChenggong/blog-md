package org.xiyou.search.erfen;

import java.util.Arrays;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.TreeMap;

/**
 * @author xiyouyan
 * @date 2020-07-10 23:55
 * @description
 */
public class TreeErfen {
    public int[] findRightInterval(int[][] intervals) {
        int len = intervals.length;
        if (len == 0) {
            return new int[0];
        }

        TreeMap<Integer, Integer> treeMap = new TreeMap<>();
        int[] res = new int[len];
        for (int i = 0; i < len; i++) {
            treeMap.put(intervals[i][0], i);
        }
        for (int i = 0; i < len; i++) {
            Map.Entry<Integer, Integer> entry = treeMap.ceilingEntry(intervals[i][1]);
            if (entry == null) {
                res[i] = -1;
            } else {
                res[i] = entry.getValue();
            }
        }
        return res;
    }

    /**
     * 把数组中大于某个元素X的值，替换成X，最接近目标值target，而且数组有序
     * 思路：target-sum=shengxia
     * 如果剩下的值shengxia 除以剩下的长度，大于当前元素的时候，说明当前元素就是临界点，判断它是否大于0。5
     *
     * @param arr
     * @param target
     * @return
     */
    public static int findBestValue(int[] arr, int target) {

        int len = arr.length;
        if (len == 0) {
            return 0;
        }
        Arrays.sort(arr);
        int sum = 0;
        for (int i = 0; i < len; i++) {
            int x = (target - sum) / (len - i);
            if (x < arr[i]) {
                double t = ((double) (target - sum)) / (len - i);
                if (t - x > 0.5) {
                    return x + 1;
                } else {
                    return x;
                }
            }
            sum += arr[i];
        }
        return arr[len - 1];
    }

    /**
     * @param nums1
     * @param nums2
     * @return
     */
    public static double findMedianSortedArrays(int[] nums1, int[] nums2) {
        int n1 = nums1.length;
        int n2 = nums2.length;
        if (n1 > n2) {
            return findMedianSortedArrays(nums2, nums1);
        }
        int k = (n1 + n2 + 1) / 2;
        int left = 0;
        int right = n1;
        while (left < right) {
            int m1 = left + (right - left) / 2;
            int m2 = k - m1;
            if (nums1[m1] < nums2[m2 - 1]) {
                left = m1 + 1;
            } else {
                right = m1;
            }
        }
        int m1 = left;
        int m2 = k - left;
        int c1 = Math.max(m1 <= 0 ? Integer.MIN_VALUE : nums1[m1 - 1],
                m2 <= 0 ? Integer.MIN_VALUE : nums2[m2 - 1]);
        if ((n1 + n2) % 2 == 1) {
            return c1;
        }
        int c2 = Math.min(m1 >= n1 ? Integer.MAX_VALUE : nums1[m1],
                m2 >= n2 ? Integer.MAX_VALUE : nums2[m2]);
        return (c1 + c2) * 0.5;

    }


    public static void main(String[] args) {
        int arr[] = new int[]{1,2,3,4,5};
        int arr2[] = new int[]{3,5};
        System.out.println(findMedianSortedArrays(arr, arr2));
    }
}
