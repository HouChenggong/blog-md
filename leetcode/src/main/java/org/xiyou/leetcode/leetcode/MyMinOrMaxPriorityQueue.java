package org.xiyou.leetcode.leetcode;

import java.util.Arrays;

/**
 * @author xiyouyan
 * @date 2020-06-24 14:27
 * @description
 */
public class MyMinOrMaxPriorityQueue {

    private Integer[] arr;

    //当前容量
    private int size;

    //容量
    private int capacity;

    private boolean min;

    //设置最大容量是100
    private int maxCapacity = 100;

    private void doCap(int capSize) {
        capacity = capSize + 1;
    }


    /**
     * 初始化
     *
     * @param target
     * @param min
     */
    public MyMinOrMaxPriorityQueue(int target[], boolean min) {
        int len = target.length + 1;
        arr = new Integer[len];
        capacity = len;
        this.min = min;
        for (int one : target) {
            add(one);
        }

    }

    /**
     * 添加
     *
     * @param value
     */
    private void add(Integer value) {
        if (size == maxCapacity) {
            throw new RuntimeException("列队只能容纳:" + maxCapacity);
        }
        int index = size + 1;
        if (index == capacity) {
            System.out.println("容量满了，当前容量是：" + (capacity - 1));
            int newCapacity = 0;
            if (index * 2 >= maxCapacity) {
                arr = Arrays.copyOf(arr, maxCapacity);
                newCapacity = maxCapacity;
            } else {
                newCapacity = (capacity - 1) * 2 + 1;
                arr = Arrays.copyOf(arr, newCapacity);
            }
            capacity = newCapacity;
            System.out.println("扩容后大小是原来的2倍，变成：" + (newCapacity - 1));
        }
        if (size == 0) {
            arr[index] = value;
        } else {
            arr[index] = value;
            siftUp(index);
        }
        size++;
    }

    /**
     * 删除堆顶元素
     */
    public void poll() {
        int val = arr[size];
        arr[1] = val;
        arr[size] = null;
        size--;
        int index = 1;
        //比如当前节点是1，size是2，则是可以调整的
        while (index * 2 <= size) {
            //交换的节点默认是：当前节点的左子节点
            int swapIndex = index * 2;
            //比如当前节点是1，但是此时size是3，说明当前节点有右子节点
            if (index * 2 + 1 <= size) {
                if (min) {
                    if (arr[index * 2] > arr[index * 2 + 1]) {
                        swapIndex = index * 2 + 1;
                    }
                } else {
                    if (arr[index * 2] < arr[index * 2 + 1]) {
                        swapIndex = index * 2 + 1;
                    }
                }

            }
            if (min) {
                if (arr[index] > arr[swapIndex]) {
                    swap(index, swapIndex);
                }
            } else {
                if (arr[index] < arr[swapIndex]) {
                    swap(index, swapIndex);
                }
            }

            index = swapIndex;
        }
    }

    /**
     * 向上寻找
     *
     * @param index
     */
    private void siftUp(Integer index) {
        while (index / 2 > 0) {
            if (min) {
                if (arr[index] < arr[index / 2]) {
                    swap(index, index / 2);
                }
            } else {
                if (arr[index] > arr[index / 2]) {
                    swap(index, index / 2);
                }
            }

            index = index / 2;
        }
    }

    /**
     * 交换
     *
     * @param index
     * @param parent
     */
    private void swap(int index, int parent) {
        int temp = arr[parent];
        arr[parent] = arr[index];
        arr[index] = temp;
    }

    @Override
    public String toString() {

        return "MyPriorityQueue{" +
                "arr=" + Arrays.toString(arr) +
                ", size=" + size +
                '}';
    }

    public static void main(String[] args) {
        int arr[] = new int[]{7, 6};
        MyMinOrMaxPriorityQueue min = new MyMinOrMaxPriorityQueue(arr, false);
        min.add(9);
        min.add(4);
        min.add(4);
        min.add(6);
        min.add(8);
        min.add(11);
        min.add(12);
        System.out.println(min.size);
        System.out.println(min.toString());
        while (min.size > 0) {
            min.poll();
            System.out.println(min.toString());
        }
    }
}
