package org.xiyou.leetcode.leetcode;

import java.util.LinkedList;
import java.util.Stack;

/**
 * @author xiyou
 * 给你一个整数数组 nums 和一个整数 k。
 * <p>
 * 如果某个 连续 子数组中恰好有 k 个奇数数字，我们就认为这个子数组是「优美子数组」。
 * <p>
 * 请返回这个数组中「优美子数组」的数目。
 * <p>
 *  
 * <p>
 * 示例 1：
 * <p>
 * 输入：nums = [1,1,2,1,1], k = 3
 * 输出：2
 * 解释：包含 3 个奇数的子数组是 [1,1,2,1] 和 [1,2,1,1] 。
 * 示例 2：
 * <p>
 * 输入：nums = [2,4,6], k = 1
 * 输出：0
 * 解释：数列中不包含任何奇数，所以不存在优美子数组。
 * 示例 3：
 * <p>
 * 输入：nums = [2,2,2,1,2,2,1,2,2,2], k = 2
 * 输出：16
 * <p>
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/count-number-of-nice-subarrays
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 */
public class Lc1248 {
    /**
     * 滑动窗口解决
     *
     * @param nums
     * @param k
     * @return
     */
    public static int numberOfSubarrays(int[] nums, int k) {
        int len = nums.length;
        if (k == 0 || len < k) {
            return 0;
        }
        //最后返回的结果
        int total = 0;
        //滑动窗口的左边
        int left = 0;
        //滑动窗口的右边
        int right = 0;
        //窗口中包含多少个奇数
        int jiNum = 0;
        //当最右边还没到边界的情况下
        while (right < len) {
            //判单当前值是不是奇数，不管是不是right都要向右划
            if (nums[right++] % 2 == 1) {
                //是的话，奇数个数+1
                jiNum++;
            }
            //如果奇数的个数是我们要找的个数
            if (jiNum == k) {
                //记录当前节点
                int temp = right;
                //找到下一个节点是奇数的前一个值
                while (right < len && nums[right] % 2 == 0) {
                    right++;
                }
                //所以两个奇数之间有rightHe个偶数
                int rightHe = right - temp;
                //左边两个奇数之间的偶数个数
                int leftHe = 0;
                while (nums[left] % 2 == 0) {
                    left++;
                    leftHe++;
                }
                //和为乘积
                total += (leftHe + 1) * (rightHe + 1);
                left++;
                jiNum--;
            }
        }
        return total;

    }

    public static void main(String[] args) {
        int arr[] = new int[]{1, 1, 1, 1, 1};
        System.out.println(numberOfSubarrays(arr, 1));
    }
}
