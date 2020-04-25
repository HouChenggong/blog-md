package org.xiyou.leetcode.leetcode.bfs;

public class CoinCount {
    public static int waysToChange(int n) {
        int res = 0;
        for (int n25 = 0; n25 <= n / 25; n25++)  //一个一个试每一种可能的n25数。
        {
            int temp1 = n - n25 * 25;
            for (int n10 = 0; n10 <= temp1 / 10; n10++) {
                res += (temp1 - n10 * 10) / 5 + 1;
            }
        }
        return res;
    }

    public static int waysToChange2(int n) {
        long total = 0;
        //一个一个试每一种可能的n25数。
        for (int i = 0; i <= n / 25; i++) {
            //用了i个25之后的结果
            int temp = n - i * 25;
            //项数
            int tempNum = temp / 10 + 1;
            //首项即0个10的情况
            int headNum = temp / 5 + 1;
            //能用10都用10的情况
            int tailNum = (temp % 10) / 5 + 1;
            long oneMax = ((long) tempNum * (tailNum + headNum) / 2) % 1000000007;
            total = (oneMax + total) % 1000000007;


        }
        return (int)total;
    }

    public static void main(String[] args) {
        System.out.println(waysToChange(51));
        System.out.println(waysToChange2(51));
        System.out.println(waysToChange(900750));
        System.out.println(waysToChange2(900750));
    }

}
