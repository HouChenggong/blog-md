package org.xiyou.search.queue;

import java.util.*;

/**
 * @author xiyouyan
 * @date 2020-07-09 14:27
 * @description 优先队列
 */
public class YouXianQueue {

    /**
     * 优先队列解决TOP K问题
     * top k大就用，最小堆，topk 小，就用最大堆
     *
     * @param nums
     * @param k
     * @return
     */
    public int findKthLargest(int[] nums, int k) {

        PriorityQueue<Integer> queue = new PriorityQueue<Integer>(k + 1, (o1, o2) -> o1 - o2);
        for (int i = 0; i < nums.length; i++) {
            //注意这里一定要先添加，不能后添加
            queue.add(nums[i]);
            if (queue.size() > k) {
                queue.poll();
            }

        }
        return queue.poll();
    }


    /**
     * 每个窗口的最大值
     * LC239 https://leetcode-cn.com/problems/sliding-window-maximum/
     *
     * @param nums
     * @param k
     * @return
     */
    public static int[] maxSlidingWindow(int[] nums, int k) {
        //双向队列
        LinkedList<Integer> list = new LinkedList<>();
        //最后结果
        int res[] = new int[nums.length - k + 1];
        for (int i = 0; i < nums.length; i++) {
            if (i >= k) {
                //比如1，2，3，4，5，当i=3的时候，要把列队头节点删除
                list.peekFirst();
            }
            while (list.size() > 0 && list.peekFirst() < nums[i]) {
                list.pollFirst();
            }
            list.addLast(nums[i]);
            if (i >= k - 1) {
                res[i - k + 1] = list.peekFirst();
            }

        }
        return res;
    }


    public void bfs(char[][] arr, int x, int y) {
        Queue<int[]> list = new LinkedList<>();
        list.add(new int[]{x, y});
        while (list.size() > 0) {
            int one[] = list.poll();
            if (x >= 0 && y >= 0 && x < arr.length && y < arr[0].length && arr[x][y] == '1') {
                int i = one[0];
                int j = one[1];
                arr[x][y] = '0';
                list.add(new int[]{i + 1, j});
                list.add(new int[]{i - 1, j});
                list.add(new int[]{i, j + 1});
                list.add(new int[]{i, j - 1});
            }
        }
    }

    class Solution {
        public int lengthOfLIS(int[] nums) {
            int[] res = new int[nums.length];
            int len = 0;
            for (int num : nums) {
                int idx = Arrays.binarySearch(res, 0, len, num);
                idx = idx < 0 ? -idx - 1 : idx;
                res[idx] = num;
                if (idx == len) {
                    len++;
                }
            }
            return len;
        }
    }

    /**
     * LC435 无重叠区间
     *
     * @param intervals
     * @return
     */
    public  static int eraseOverlapIntervals(int[][] intervals) {
        if (intervals.length == 0) {
            return 0;
        }
        Arrays.sort(intervals,(o1, o2) -> o1[1]-o2[1]);
        int count = 1;    //最多能组成的不重叠区间个数
        int end = intervals[0][1];
        for (int i = 0; i < intervals.length; i++) {
            if (intervals[i][0] < end) {
                continue;
            }
            end = intervals[i][1];
            count++;
        }
        return intervals.length - count;
    }

    public static void main(String[] args) {
        int arr[] []= new int[][]{{1,2},{2,3},{1,3},{3,4},{1,4}} ;

        System.out.println(eraseOverlapIntervals(arr));
    }
}
