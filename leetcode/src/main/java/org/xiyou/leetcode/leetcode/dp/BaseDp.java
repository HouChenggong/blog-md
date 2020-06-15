package org.xiyou.leetcode.leetcode.dp;

import java.util.Arrays;

/**
 * @author xiyou
 * @version 1.0
 * xiyou-todo 动态规划
 * @date 2020/6/9 9:08
 */
public class BaseDp {

    /**
     * 递归
     *
     * @param n
     * @return
     */
    public static int fib(int n) {
        if (n == 1 || n == 2) {
            return n;
        }
        return fib(n - 1) + fib(n - 2);
    }

    public static int fib2(int n) {
        int arr[] = new int[n + 1];
        return fib21(n, arr);
    }

    public static int fib21(int n, int arr[]) {
        if (n == 1 || n == 2) {
            return n;
        }
        if (arr[n] != 0) {
            return arr[n];
        }
        arr[n] = fib21(n - 1, arr) + fib21(n - 2, arr);
        return arr[n];
    }

    public static int fibDp(int n) {
        int dp[] = new int[n + 1];
        dp[0] = 1;
        dp[1] = 1;
        for (int i = 2; i < n + 1; i++) {
            dp[i] = dp[i - 1] + dp[i - 2];
        }
        return dp[n];
    }

    public static int fibDp2(int n) {
        if (n == 1 || n == 2) {
            return 1;
        }
        int x = 1;
        int y = 1;
        int result = 0;
        for (int i = 2; i < n + 1; i++) {
            result = x + y;
            y = x;
            x = result;
        }
        return result;
    }




    public static int coinChangeDp(int[] coins, int amount) {
        int max = amount + 1;
        int[] dp = new int[amount + 1];
        Arrays.fill(dp, max);
        dp[0] = 0;
        for (int i = 1; i <= amount; i++) {
            for (int j = 0; j < coins.length; j++) {
                if (coins[j] <= i) {
                    dp[i] = Math.min(dp[i], dp[i - coins[j]] + 1);
                }
            }
        }
        return dp[amount] > amount ? -1 : dp[amount];
    }
    public static int translateNum(int num) {
        if (num < 10) {
            //个位数,只可能有一种翻译法
            return 1;
        }
        char[] nums = String.valueOf(num).toCharArray();
        //dp[i]代表前i-1个数总共有多少种翻译方法
        int[] dp = new int[nums.length];
        dp[0] = 1;
        int n = (nums[0] - '0') * 10 + (nums[1] - '0');
        //计算初始值,第二位数和第一位数组成的数字介于(9,26)之间,有两种翻译
        //若组成的数是0~9或大于25则只能有一种翻译
        dp[1] = n > 9 && n < 26 ? 2 : 1;

        for (int i = 2; i < nums.length; i++) {
            //计算当前数和前一个数组成的数值大小,如1225的下标3的数和它前一个数组成的值为25
            n = (nums[i - 1] - '0') * 10 + (nums[i] - '0');
            if (n > 9 && n < 26) {
                //组成数值处于(9,26)范围内,则可翻译的方法数为前两个数的翻译总和
                dp[i] = dp[i - 1] + dp[i - 2];
            } else {
                //组成数值不在(9,26)范围内，则只能算一种翻译,和前一个数能翻译的方法数一样
                dp[i] = dp[i - 1];
            }
        }
        return dp[nums.length - 1];
    }


    public static void main(String[] args) {
//        System.out.println(fib(10));
//        System.out.println(fib2(10));
//        System.out.println(fibDp2(10));
//        System.out.println(fibDp(10));
//        System.out.println(translateNum(12258));
        int arr[]=new int[]{1,2,5};
        System.out.println(coinChangeDp(arr,101));
    }

}
