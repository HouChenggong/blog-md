package org.xiyou.leetcode.leetcode.bfs;

import org.xiyou.leetcode.leetcode.ListNode;

public class Lc1 {

    private static ListNode merge2Lists(ListNode l1, ListNode l2) {
        ListNode dummyHead = new ListNode(0);
        ListNode tail = dummyHead;
        while (l1 != null && l2 != null) {
            if (l1.val < l2.val) {
                tail.next = l1;
                l1 = l1.next;
            } else {
                tail.next = l2;
                l2 = l2.next;
            }
            tail = tail.next;
        }
        tail.next = l1 == null ? l2 : l1;

        return dummyHead.next;
    }

    private ListNode dfsMerge2Lists(ListNode l1, ListNode l2) {
        if (l1 == null) {
            return l2;
        }
        if (l2 == null) {
            return l1;
        }
        if (l1.val < l2.val) {
            l1.next = dfsMerge2Lists(l1.next, l2);
            return l1;
        } else {
            l2.next = dfsMerge2Lists(l1, l2.next);
            return l2;
        }


    }

    public static void main(String[] args) {
        int arr[] = new int[]{1, 3, 5, 7};
        int arr2[] = new int[]{2, 4, 6, 8};
        ListNode one = ListNode.initListNode(arr);
        System.out.println(ListNode.toNodeString(one));
        ListNode two = ListNode.initListNode(arr2);
//        System.out.println(ListNode.toNodeString(merge2Lists(one, two)));
        Lc1 lc1 = new Lc1();
        ListNode res = lc1.dfsMerge2Lists(one, two);
        System.out.println(ListNode.toNodeString(res));
    }
}
