package org.xiyou.leetcode.leetcode.erfen;

/**
 * @author xiyou
 * @version 1.0
 * xiyou-todo 求X的平方根
 * @date 2020/5/9 19:43
 */
public class Lc69 {
    public static int searchInsert(int target) {
        if (target == 0) {
            return 0;
        }
        int left = 1;
        int right = target / 2 + 1;
        while (left < right) {
            int mid = left + (right - left) / 2;
            long q = (long) mid * mid;
            long q1 = (long) (mid + 1) * (mid + 1);
            if (q == target || (q < target && q1 > target)) {
                return mid;
            } else if (q > target) {
                right = mid;
            } else if (q < target) {
                left = left + 1;
            }
        }
        return left;
    }

    public static void main(String[] args) {
        System.out.println(searchInsert(2147395599));
        System.out.println(searchInsert(9));
    }
}
