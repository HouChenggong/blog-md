package org.xiyou.leetcode.leetcode.stack;

/**
 * @author xiyou
 * @version 1.0
 * xiyou-todo XXX功能
 * @date 2020/5/12 17:41
 */
class MinStack2 {

    private Node head;

    public MinStack2() {}

    public void push(int x) {
        // 新建节点插入链表头部，作为新的头节点，存储当前的元素值和最小值，并且指向之前的头节点。
        if (head == null) {
            head = new Node(x, x);
        } else {
            head = new Node(x, Math.min(x, head.min), head);
        }
    }

    public void pop() {
        // 删除链表头节点
        head = head.next;
    }

    public int top() {
        // 返回头节点中存储的元素值
        return head.val;
    }

    public int getMin() {
        // 返回头节点中存储的最小值
        return head.min;
    }


    class Node {
        int val;
        int min ;
        Node next;

        public Node(int val, int min) {
            this.val = val;
            this.min = min;
        }

        public Node(int val, int min, Node next) {
            this.val = val;
            this.min = min;
            this.next = next;
        }
    }
}

