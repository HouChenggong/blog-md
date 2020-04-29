package org.xiyou.leetcode.leetcode;

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


    public static ListNode initListNode(int arr[]) {
        ListNode root =null;
         for(int i=arr.length-1;i>=0;i--){
             ListNode pre=new ListNode(arr[i]);
             pre.next=root;
             root=pre;
         }
        return root;
    }

    public static List<Integer> toNodeString(ListNode root) {
        List<Integer> list = new ArrayList<>(10);
        while (root != null) {
            list.add(root.val);
            root = root.next;
        }
        return list;
    }
}
