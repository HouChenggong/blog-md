package org.xiyou.search.search;

/**
 * @author xiyouyan
 * @date 2020-07-09 13:52
 * @description 快排
 */
public class QuickSort {
    public static void quickSortRightMax(int[] arr, int low, int high) {
        if (low > high) {
            return;
        }
        int i = low;
        int j = high;
        int key = arr[i];
        int temp = 0;
        while (i < j) {

            /**
             * 注意：一定要先找后面的，再找前面的，不然结果肯定是错误的
             */

            while ((arr[j] >= key) && (j > i)) {
            /*从后往前比较
                如果没有比关键key小的，比较下一个，直到找到比关键key小的
            */
                j--;
            }


            while ((arr[i] <= key) && (j > i)) {
            /*从往前往后比较
                如果没有比关键key大的，比较下一个，直到找到比关键key大的
            */
                i++;
            }

            if (i < j) {
                //找到了比关键key大的值，和比Key小的值，调换位置
                temp = arr[j];
                arr[j] = arr[i];
                arr[i] = temp;
            }

        }
        // 此时哨兵i和哨兵j相遇了，哨兵i和哨兵j都走到key面前。
        // 说明此时“探测”结束。我们将基准数arr[low]也就是Key所代表的值和arr[i]进行交换。
        arr[low] = arr[i];
        arr[i] = key;

        // 交换完了之后，就是以基准Key分成了2部分，左边是小于key的，右边是大于key的
        // 比如： int arr2[] = new int[]{6,1,2,7,9,3,4,5,10,8};
        // 现在变成了 3，1，2，5，4，6，9，7，10，8左边是3,1,2,5,4 右边是9,7,10,8
        // 接下来还需要分别处理这两个序列。
        // 左边的序列是“3 1 2 5 4”。
        // 请将这个序列以3为基准数进行调整，使得3左边的数都小于等于3，3右边的数都大于等于3。

        System.out.println(i == j);
        System.out.println("===============");
        //递归调用左半数组,因为此时i=j，所以用i j都行
        quickSortRightMax(arr, low, j - 1);
        //递归调用左半数组
        quickSortRightMax(arr, j + 1, high);
    }

    public static int[] quickSort(int arr[]) {
        quickSortRightMax(arr, 0, arr.length - 1);
        return arr;
    }


    public static void main(String[] args) {
        int arr2[] = new int[]{6, 1, 2, 7, 9, 3, 4, 5, 10, 8};
        quickSortRightMax(arr2, 0, arr2.length - 1);
        System.out.println(arr2);
    }
}
