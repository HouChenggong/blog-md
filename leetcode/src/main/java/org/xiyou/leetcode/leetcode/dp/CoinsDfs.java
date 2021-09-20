package org.xiyou.leetcode.leetcode.dp;

import java.util.Arrays;

/**
 * @author xiyou
 * @version 1.0
 * xiyou-todo XXX功能
 * @date 2020/6/10 21:04
 */
public class CoinsDfs {

    // coins 中是可选硬币面值，amount 是目标金额
    public static int coinChange(int[] coins, int amount) {
        int amountArr[] = new int[amount + 1];
        Arrays.fill(amountArr, amount + 1);
        return dp(coins, amount, amountArr, amount + 1);

    }

    public static int dp(int[] coins, int amount, int amountArr[], int oneValue) {
        if (amount == 0) {
            return 0;
        }
        if (amountArr[amount] != oneValue) {
            return amountArr[amount];
        }
        for (int oneCoin : coins) {
            if (amount >= oneCoin && amountArr[amount-oneCoin]!=-1) {
                int temp = dp(coins, amount - oneCoin, amountArr, oneValue) + 1;
                if (temp < amountArr[amount]) {
                    amountArr[amount] = temp;
                }
            }

        }

        return amountArr[amount] >= oneValue ? -1 : amountArr[amount];
    }

    public static void main(String[] args) {
        int arr[] = new int[]{186,419,83,408};
        System.out.println(coinChange(arr, 6249));
    }
}
