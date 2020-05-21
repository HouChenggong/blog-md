package org.xiyou.leetcode.object;


import org.xiyou.leetcode.leetcode.ListNode;

import java.util.HashMap;

public class TestString {
    public static void main(String[] args) {
        String a = "a";
        a = a + "b";
        StringBuffer aa;
        StringBuilder bb;
        HashMap map;
    }
    public ListNode reverseList(ListNode head) {
        ListNode root=head;
        ListNode pre=null;
        while(root!=null){
            ListNode temp= root;
            root=root.next;
            temp.next=pre;
            pre=temp;
        }
        return pre;
    }
}
