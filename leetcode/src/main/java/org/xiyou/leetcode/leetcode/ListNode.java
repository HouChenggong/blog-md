package org.xiyou.leetcode.leetcode;

import com.google.common.base.Objects;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xiyou
 * 链表的
 */
@AllArgsConstructor
@NoArgsConstructor
public class ListNode {
    public int val;
    public ListNode next;

    public ListNode(int x) {
        val = x;
    }

    public int getVal() {
        return val;
    }

    public ListNode getNext() {
        return next;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ListNode listNode = (ListNode) o;
        return val == listNode.val &&
                Objects.equal(next, listNode.next);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(val, next);
    }

    /**
     * 按照数组的逆序生成数组，利用的是头插法
     *
     * @param arr
     * @return
     */
    public static ListNode initNodeByArrDesc(int arr[]) {
        ListNode root = null;
        for (int i = arr.length - 1; i >= 0; i--) {
            ListNode pre = new ListNode(arr[i]);
            pre.next = root;
            root = pre;
        }
        return root;
    }


    /**
     * 按照数据的正序生成链表
     * 结果是：1，2，3，4，5
     * 利用的是尾插法
     *
     * @param arr
     * @return
     */
    public static ListNode initNodeByArrAsc(int arr[]) {
        ListNode root = new ListNode(0);
        ListNode pre = root;
        for (int i = 0; i < arr.length; i++) {

            ListNode temp = new ListNode(arr[i]);
            pre.next = temp;
            pre = pre.next;
        }
        return root.next;
    }

    /**
     * 利用头插法，生成一个数组逆序的链表
     *
     * @param arr
     * @return
     */
    public static ListNode initReverseNodeByArrAsc(int arr[]) {
        ListNode root = null;
        for (int i = 0; i < arr.length; i++) {
            ListNode temp = new ListNode(arr[i]);
            temp.next = root;
            root = temp;
        }
        return root;
    }

    /**
     * 给定头节点和尾节点，反转链表
     * 比如头是1234567，尾是567
     * 结果就是4321
     * 但是head的值会发生变化，变成1
     *
     * @param head
     * @param tail
     * @return
     */
    public static ListNode reverseList(ListNode head, ListNode tail) {
        ListNode root = head;
        ListNode pre = null;
        while (root != null && !root.equals(tail)) {
            ListNode temp = root;
            root = root.next;
            temp.next = pre;
            pre = temp;
        }
        toNodeString(head);
        toNodeString(tail);
        return pre;
    }

    /**
     * 给定头节点和尾节点，反转链表
     * 比如头是1234567，尾是567
     * 结果就是4321
     * 但是head的值不会发生变成，还是原来的
     *
     * @param head
     * @param tail
     * @return
     */
    public static ListNode reverseListNoChange(ListNode head, ListNode tail) {
        ListNode root = head;
        ListNode pre = null;
        while (root != null && !root.equals(tail)) {
            ListNode temp = new ListNode(root.val);
            root = root.next;
            temp.next = pre;
            pre = temp;
        }
        return pre;
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

    public ListNode reverseBetween(ListNode head, ListNode tail) {
        ListNode root = head;
        ListNode pre = null;
        while (root != null && !root.equals(tail)) {
            ListNode temp = root;
            root = root.next;
            temp.next = pre;
            pre = temp;
        }
        toNodeString(head);
        toNodeString(tail);
        return pre;
    }

    public static List<Integer> toNodeString(ListNode root) {
        List<Integer> list = new ArrayList<>(10);
        while (root != null) {
            list.add(root.val);
            root = root.next;
        }
        System.out.println(list);
        return list;
    }
}
