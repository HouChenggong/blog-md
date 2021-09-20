package org.xiyou.search.stack;

import java.util.Stack;

/**
 * @author xiyouyan
 * @date 2020-07-10 15:44
 * @description
 */
public class YuShui {
    public int trap(int[] height) {
        int arr[] = new int[height.length + 2];
        int start = 0;
        Stack<Integer> end = new Stack<>();
        end.add(0);
        for (int i = height.length - 1; i >= 0; i--) {
            if (height[i] > end.peek()) {
                end.add(height[i]);
            } else {
                end.add(end.peek());
            }
        }
        int maxArea = 0;
        for (int i = 0; i < height.length; i++) {
            int min = Math.min(start, end.pop());
            if (start < height[i]) {
                start = height[i];
            }
            if (height[i] < min) {
                maxArea += min - height[i];
            }
        }
        return maxArea;
    }
}
