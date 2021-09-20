package org.xiyou.leetcode.leetcode;

/**
 * @author xiyou
 * @version 1.0
 * xiyou-todo XXX功能
 * @date 2020/5/16 12:54
 */
public class Lc25 {

    public static ListNode reverseList(ListNode head) {
        ListNode root = head;
        ListNode pre = null;
        while (root != null) {
            ListNode temp = root;
            root = root.next;
            temp.next = pre;
            pre = temp;
        }
        return pre;
    }

    public static ListNode reverseList(ListNode head, ListNode tail) {
        ListNode root = head;
        ListNode pre = null;
        while (root != null && !root.equals(tail)) {
            ListNode temp = root;
            root = root.next;
            temp.next = pre;
            pre = temp;
        }
        return pre;
    }

    public static ListNode reverseListNoChange(ListNode start, ListNode end) {
        ListNode curr = start.next;
        ListNode prev = start;
        ListNode first = curr;
        while (curr != end){
            ListNode temp = curr.next;
            curr.next = prev;
            prev = curr;
            curr = temp;
        }
        start.next = prev;
        first.next = curr;
        return first;


    }

    public static ListNode reverseListWithTail(ListNode head, ListNode tail) {
        ListNode root = head;
        ListNode pre = null;
        while (root != null && !root.equals(tail)) {
            ListNode temp = root;
            root = root.next;
            temp.next = pre;
            pre = temp;
        }
        head.next=tail;
        return pre;
    }

    public static ListNode reverseKGroup(ListNode head, int k) {
        ListNode dump = new ListNode(0);
        dump.next = head;
        // 代表待翻转链表的前驱
        ListNode start = dump;
        // 代表待翻转链表的末尾
        ListNode end = head;
        int count = 0;
        while (end != null) {
            count++;
            if (count % k == 0) {
               start = reverseListWithTail(start, end.next);
            }
                end = end.next;



        }
        return dump.next;
    }


    public static void main(String[] args) {
        int arr[] = new int[]{1, 2, 3, 4, 5, 6, 7};
        ListNode root1 = ListNode.initNodeByArrAsc(arr);
        ListNode.toNodeString(root1);
        ListNode root2 = ListNode.initNodeByArrDesc(arr);
        ListNode.toNodeString(root2);
        ListNode root3 = ListNode.initReverseNodeByArrAsc(arr);
        ListNode.toNodeString(root3);
        int arr2[] = new int[]{5, 6, 7};
        ListNode root4 = ListNode.initNodeByArrAsc(arr);
        ListNode root42 = ListNode.initNodeByArrAsc(arr2);
        ListNode rrr = reverseKGroup(root4, 3);
//       ListNode  res=reverseListWithTail(root4,root42);
//       ListNode res2=reverse(root4,root42);
         ListNode.toNodeString(rrr);
//         ListNode.toNodeString(res2);
    }
}

