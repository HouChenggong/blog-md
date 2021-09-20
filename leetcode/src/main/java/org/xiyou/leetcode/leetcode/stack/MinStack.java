package org.xiyou.leetcode.leetcode.stack;

import java.util.Stack;

/**
 * @author xiyou
 * @version 1.0
 * xiyou-todo 最小栈的实现
 * @date 2020/5/12 15:30
 */
class MinStack {

    Stack<Integer> origin = new Stack<>();
    Stack<Integer> min = new Stack<>();

    /**
     * initialize your data structure here.
     */
    public MinStack() {

    }

    public void push(int x) {
        origin.push(x);
        if (min.size() == 0) {
            min.push(x);
        } else {
            if (x <= min.peek()) {
                min.push(x);
            }
        }
    }

    public void pop() {
        if (origin.size() > 0) {
            if ( origin.peek().intValue()==min.peek().intValue()) {
                min.pop();
            }
            origin.pop();
        }
    }

    public int top() {
        if (origin.size() > 0) {
            return origin.peek();
        } else {
            throw new RuntimeException();
        }
    }

    public int getMin() {
        if (origin.size() > 0) {
            return min.peek();
        } else {
            throw new RuntimeException();
        }
    }

    public static void main(String[] args) {

        MinStack minStack = new MinStack();
        minStack.push(-2);
        minStack.push(0);
        minStack.push(-3);
    int a   = minStack.getMin();   //--> 返回 -3.
        minStack.pop();
      a=  minStack.top();     // --> 返回 0.
      a=  minStack.getMin();  // --> 返回 -2.


    }
}
