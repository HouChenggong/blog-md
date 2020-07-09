package org.xiyou.search.queue;

import java.util.PriorityQueue;

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
}
