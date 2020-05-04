package org.xiyou.leetcode.leetcode.erfen;


/**
 * @author xiyou
 * 求山峰数组的最高点
 */
public class Lc852 {

    public static int getMaxIndex(int arr[]) {
        int len = arr.length;
        int left = 0;
        int right = len - 1;
        while (left < right) {
            int mid = left + (right - left) / 2;
            if (arr[mid] > arr[mid - 1] && arr[mid] > arr[mid + 1]) {
                return mid;
            }else if(arr[mid]>arr[mid-1]){
                left=mid+1;
            }else if(arr[mid]<arr[mid-1]){
                right=mid;
            }else {
               // System.out.println("这个根本不会进入");
            }
        }
        //这里随便return，因为肯定会返回
        return -1;
    }


    public static void main(String[] args) {
        int arr[]=new int[]{3,4,5,1};
        System.out.println(getMaxIndex(arr));
    }
}
