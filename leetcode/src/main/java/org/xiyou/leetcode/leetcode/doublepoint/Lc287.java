package org.xiyou.leetcode.leetcode.doublepoint;

import org.xiyou.leetcode.leetcode.ListNode;

/**
 * @author xiyou
 * @version 1.0
 * xiyou-todo XXX功能
 * @date 2020/5/26 21:47
 */
public class Lc287 {
    public static int findDuplicate(int[] nums) {
        int head = 0;
        int next = 1;
        int len = nums.length;
        do {
            head++;
            next += 2;
            if (head > len) {
                head = head - len;
            }
            if (next > len) {
                next = next - len;
            }
        } while (head != next);
        return nums[head];
    }

    public boolean hasCycle(ListNode head) {
        if (head == null || head.next == null) {
            return false;
        }
        ListNode fast = head.next.next;
        ListNode slow = head.next;
        while (fast != slow) {
            if (fast == null || fast.next == null) {
                return false;
            }
            fast = fast.next.next;
            slow = slow.next;
        }
        return true;
    }

    public ListNode detectCycle(ListNode head) {
        if (head == null || head.next == null) {
            return null;
        }


        ListNode fast = head.next.next;
        ListNode slow = head.next;
        while (fast != slow) {
            if (fast == null || fast.next == null) {
                return null;
            }
            slow = slow.next;
            fast = fast.next.next;
        }

        ListNode t = head;
        while (t != slow) {
            t = t.next;
            slow = slow.next;
        }
        return slow;
    }


    public static void main(String[] args) {
        int arr[] = new int[]{1, 2, 2, 4, 5};
        System.out.println(findDuplicate(arr));
    }

}
