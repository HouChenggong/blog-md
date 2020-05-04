package org.xiyou.leetcode.tx;

import java.util.Arrays;
import java.util.HashMap;

/**
 * @author xiyou
 */
public class FenGe {

    public static boolean canFenGe(int arr[], int k) {
        int len = arr.length;
        if (len % k != 0) {
            return false;
        }
        //排序
        Arrays.asList(arr);
        //存放每个元素出现的次数的map
        HashMap<Integer, Integer> map = new HashMap<>(8);
        int countArr[][] = new int[arr[len-1]+1][2];
        //存放次数
        for (int a : arr) {
            map.put(a, map.getOrDefault(a, 0) + 1);
            countArr[a][0] = a;
            countArr[a][1] += 1;
        }
        for (int i = 0; i < len; i++) {
            //判断取出来的K个数是不是连续的
            for (int one = i + 1; one < i + k; one++) {
                int a=arr[one];
                int b =arr[one-1];
                if (countArr[a][0] != countArr[b][0] + 1) {
                    return false;
                }
            }
            //如果取出来的K个数是连续的，则把他们的map对应数据减一
            for (int one = i; one < i + k; one++) {
                int oneVal = countArr[one][1];
                if (oneVal <= 0) {
                    return false;
                }
                countArr[one][1] = countArr[one][1] - 1;
            }
            //把i重置到i+k-1
            i = i + k - 1;
        }
        return true;
    }

    public static void main(String[] args) {
//        int arr[] = new int[]{1, 2, 3, 3, 4, 5, 4, 5, 6, 11, 12, 13};
        int arr[] = new int[]{1, 2, 3, 3, 4, 4, 5, 6};
        System.out.println(canFenGe(arr, 4));
        int arr2[] = new int[2];
        for (int i : arr2) {
            System.out.println(i);
        }
    }
}
