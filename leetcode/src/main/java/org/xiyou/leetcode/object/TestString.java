package org.xiyou.leetcode.object;


import org.xiyou.leetcode.leetcode.ListNode;

import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;

public class TestString {
    public static void main(String[] args) throws InterruptedException {
        BlockingQueue<String> strs = new SynchronousQueue<>();

//        new Thread(()->{
//            try {
//                System.out.println(strs.take());
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }).start();

        strs.put("aaa"); //阻塞等待消费者消费
        //strs.put("bbb");
        //strs.add("aaa");
        System.out.println(strs.size());
    }
    public static double ramainder(double da, double xiao) {
        return da - da / xiao * xiao;
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
    private void print(TestString thisTest) {
        System.out.println("print " +thisTest);
    }
    public TestString() {
        print(this);
    }


}
