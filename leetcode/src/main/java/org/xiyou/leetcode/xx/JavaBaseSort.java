package org.xiyou.leetcode.xx;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author xiyou
 * @version 1.0
 * xiyou-todo java 基础的算法
 * @date 2020/5/17 16:11
 */
public class JavaBaseSort {
    /**
     * 冒泡排序
     *
     * @param arr
     * @return
     */
    public static int[] maoPao(int[] arr) {
        int len = arr.length;
        for (int i = 0; i < len; i++) {
            for (int j = 0; j < len; j++) {
                if (arr[i] < arr[j]) {
                    int temp = arr[j];
                    arr[j] = arr[i];
                    arr[i] = temp;
                }
            }
        }
        return arr;
    }

    /**
     * 选择排序
     *
     * @param arr
     * @return
     */
    public static int[] xuanZhe(int[] arr) {
        int len = arr.length;
        for (int i = 0; i < len; i++) {
            int index = i;
            for (int j = i; j < len; j++) {
                if (arr[index] > arr[j]) {
                    index = j;
                }
            }
            int temp = arr[index];
            arr[index] = arr[i];
            arr[i] = temp;
        }
        return arr;
    }


    /**
     * 插入排序
     *
     * @param arr
     * @return
     */
    public static int[] chaRu(int arr[]) {
        //插入排序
        for (int i = 1; i < arr.length; i++) {
            //外层循环，从第二个开始比较
            for (int j = i; j > 0; j--) {
                //内存循环，与前面排好序的数据比较，如果后面的数据小于前面的则交换
                if (arr[j] < arr[j - 1]) {
                    int temp = arr[j - 1];
                    arr[j - 1] = arr[j];
                    arr[j] = temp;
                } else {
                    //如果不小于，说明插入完毕，退出内层循环
                    break;
                }
            }
        }
        return arr;
    }

    /**
     * 默认是left=0
     * right =arr[len-1]
     *
     * @param num
     * @param left
     * @param right
     */
    private static void QuickSort(int[] num, int left, int right) {
        //如果left等于right，即数组只有一个元素，直接返回
        if (left >= right) {
            return;
        }
        //设置最左边的元素为基准值
        int key = num[left];
        //数组中比key小的放在左边，比key大的放在右边，key值下标为i
        int i = left;
        int j = right;
        while (i < j) {
            //j向左移，直到遇到比key小的值
            while (num[j] >= key && i < j) {
                j--;
            }
            //i向右移，直到遇到比key大的值
            while (num[i] <= key && i < j) {
                i++;
            }
            //i和j指向的元素交换
            if (i < j) {
                int temp = num[i];
                num[i] = num[j];
                num[j] = temp;
            }
        }
        num[left] = num[i];
        num[i] = key;
        QuickSort(num, left, i - 1);
        QuickSort(num, i + 1, right);
    }

    public static int[] guiBingsort(int[] ins){
        if(ins.length <=1){
            return ins;
        }
        int num = ins.length/2;
        int[] left = guiBingsort(Arrays.copyOfRange(ins, 0, num));
        int[] right = guiBingsort(Arrays.copyOfRange(ins, num, ins.length));
        return guiBingMerge(left,right);
    }
    public static int[] guiBingMerge(int[] left, int[] right){
        int l=0;
        int r=0;
        List<Integer>  list = new ArrayList<Integer>();
        while(l<left.length && r<right.length){
            if(left[l] < right[r]){
                list.add(left[l]);
                l += 1;
            }else{
                list.add(right[r]);
                r += 1;
            }
        }
        if(l>=left.length){
            for(int i=r; i<right.length; i++){
                list.add(right[i]);
            }
        }
        if(r>=right.length){
            for(int i=l; i<left.length; i++){
                list.add(left[i]);
            }
        }
        int[] result = new int[list.size()];
        for(int i=0; i<list.size(); i++){
            result[i] = list.get(i);
        }
        return result;
    }


    public static String toArrString(int[] arr) {
        StringBuffer buffer = new StringBuffer();
        for (int one : arr) {
            buffer.append(one + ",");
        }
        System.out.println(buffer.toString());
        return buffer.toString();
    }

    public static void main(String[] args) {
        int arr[] = new int[]{3, 5, 2, 4, 1, 6};
       toArrString(arr);
        QuickSort(arr,0,5);

        System.out.println(toArrString(arr));

    }
}
