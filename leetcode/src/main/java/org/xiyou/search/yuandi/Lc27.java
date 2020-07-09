package org.xiyou.search.yuandi;

/**
 * @author xiyouyan
 * @date 2020-07-09 16:23
 * @description 原地算法，移除元素，把元素移动到数组尾部
 */
public class Lc27 {
    public static int removeElement(int[] nums, int val) {
        int tail = nums.length - 1;
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] == val && i < tail) {
                while (nums[i] == nums[tail] && i < tail) {
                    tail--;
                }
                int temp = val;
                nums[i] = nums[tail];
                nums[tail] = temp;
                tail--;
            }
        }
        int count = 0;
        for (int i = nums.length - 1; i >= 0; i--) {
            if (nums[i] == val) {
                count++;
            } else {
                break;
            }

        }
        return count;
    }

    public static void main(String[] args) {
        int arr[] = new int[]{0, 1, 2, 2, 3, 0, 4, 2};
        System.out.println(removeElement(arr, 2));
    }
}
