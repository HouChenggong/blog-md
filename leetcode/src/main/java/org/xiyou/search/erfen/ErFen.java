package org.xiyou.search.erfen;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xiyouyan
 * @date 2020-07-10 16:24
 * @description
 */
public class ErFen {

    /**
     * 二分求数，求不到为-1
     *
     * @param nums
     * @param target
     * @return
     */
    public static int search(int[] nums, int target) {
        if (nums.length == 0 || nums[0] > target) {
            return -1;
        }
        if (nums[0] == target) {
            return 0;
        }
        int left = 0;
        int right = nums.length - 1;
        while (left < right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] == target) {
                return mid;
            } else if (nums[mid] > target) {
                right = mid;
            } else {
                left = mid + 1;
            }
        }
        if (nums[left] == target) {
            return left;
        } else {
            return -1;
        }
    }


    /**
     * 查询一个元素在数组中的起始和结束位置
     * 没有的话返回-
     *
     * @param nums
     * @param target
     * @return
     */
    public static int[] searchRange(int[] nums, int target) {
        if (nums.length == 0 || nums[0] > target) {
            return new int[]{-1, -1};
        }
        int index = -1;
        int left = 0;
        int right = nums.length - 1;
        while (left < right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] == target) {
                index = mid;
                break;
            } else if (nums[mid] > target) {
                right = mid;
            } else {
                left = mid + 1;
            }
        }
        if (index == -1) {
            if (nums[left] == target) {
                index = left;
            } else {
                return new int[]{-1, -1};
            }
        }

        int start = index;
        int end = index;
        while (start > 0) {
            if (nums[start - 1] == target) {
                start--;
            } else {
                break;
            }
        }
        while (end < nums.length - 1) {
            if (nums[end + 1] == target) {
                end++;
            } else {
                break;
            }

        }
        return new int[]{start, end};
    }

    /**
     * 旋转数据，求target的位置，如果不存在返回-1
     *
     * @param nums
     * @param target
     * @return
     */
    public static int searchXuan(int[] nums, int target) {
        if (nums.length == 0) {
            return -1;
        }
        if (nums.length == 1 && nums[0] != target) {
            return -1;
        }
        int left = 0;
        int right = nums.length - 1;
        while (left < right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] == target) {
                return mid;
            }
            if (nums[mid] > nums[right]) {
                //说明左边是有序的
                if (nums[mid] > target && nums[left] <= target) {
                    right = mid;
                } else {
                    left = mid + 1;
                }
            } else {
                //说明右边是有序的
                if (nums[mid] < target && nums[right] >= target) {
                    left = mid + 1;
                } else {
                    right = mid;
                }
            }

        }
        if (nums[left] == target) {
            return left;
        } else {
            return -1;
        }
    }

    /**
     * 旋转数据，求target的位置，如果不存在返回-1
     *
     * @param arr
     * @param target
     * @return
     */
    public static boolean searchXuan2(int[] arr, int target) {
        if (arr.length == 0) {
            return false;
        }
        if (arr.length == 1 && arr[0] != target) {
            return false;
        }
        List<Integer> list = new ArrayList<>();
        int i = 0;
        int j = 0;
        while (j < arr.length - 1) {
            while (j < arr.length && arr[j] == arr[i]) {
                j++;
            }
            list.add(arr[j - 1]);
            i = j;
        }
        if (arr[0] != arr[arr.length - 1]) {
            list.add(arr[arr.length - 1]);
        }
        int nums[] = new int[list.size()];
        for (int x = 0; x < nums.length; x++) {
            nums[x] = list.get(x);
        }

        int left = 0;
        int right = nums.length - 1;
        while (left < right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] == target) {
                return true;
            }
            if (nums[mid] > nums[right]) {
                //说明左边是有序的
                if (nums[mid] > target && nums[left] <= target) {
                    right = mid;
                } else {
                    left = mid + 1;
                }
            } else {
                //说明右边是有序的
                if (nums[mid] < target && nums[right] >= target) {
                    left = mid + 1;
                } else {
                    right = mid;
                }
            }

        }
        if (nums[left] == target) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * 查找一个排序数组的最小值，但是这个排序数组是经过一次旋转的
     *
     * @param nums
     * @return
     */
    public static int findMin(int[] nums) {
        int len = nums.length;
        if (len == 1) {
            return nums[0];
        } else if (len == 2) {
            return Math.min(nums[0], nums[1]);
        }
        int left = 0;
        int right = len - 1;
        int min = Integer.MAX_VALUE;
        while (left < right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] > nums[right]) {
                //说明，左边的有序的，右边无序，而且最小值在右边,左边的最小值是nums[0]
                min = Math.min(min, nums[left]);
                left = mid + 1;

            } else {
                //说明右边有序，但最小值在左边
                right = mid;
                min = Math.min(min, nums[mid]);
            }
        }
        //可能是min，但也可能是nums[left]，因为【2，3，1】的情况，最小值就是1
        return min > nums[left] ? nums[left] : min;
    }

    /**
     * 搜索排序数组的最小值，有重复元素
     *
     * @param nums
     * @return
     */
    public int findMinChong(int[] nums) {
        int low = 0, high = nums.length - 1;

        while (low < high) {
            int pivot = low + (high - low) / 2;
            if (nums[pivot] < nums[high])
                //在左区间
                high = pivot;
            else if (nums[pivot] > nums[high])
                //在右区间
                low = pivot + 1;
            else
                //换个值，重试
                high -= 1;
        }
        return nums[low];
    }

    public static void main(String[] args) {
        int arr[] = new int[]{2, 3, 1};
        System.out.println(findMin(arr));
        /**
         * 给定一个排序数组和一个目标值，在数组中找到目标值，并返回其索引。
         * 如果目标值不存在于数组中，返回它将会被按顺序插入的位置。
         *
         * 你可以假设数组中无重复元素。
         */
    }
}
