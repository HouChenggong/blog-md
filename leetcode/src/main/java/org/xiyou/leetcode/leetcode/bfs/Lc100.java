package org.xiyou.leetcode.leetcode.bfs;

import org.xiyou.leetcode.leetcode.TreeNode;

import javax.print.attribute.standard.NumberUp;
import java.util.*;

public class Lc100 {

    /**
     * 右视图
     *
     * @param root
     * @return
     */
    public static List<Integer> right(TreeNode root) {
        if (root == null) {
            return new ArrayList<>();
        }
        List<Integer> list = new ArrayList<>(10);
        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(root);
        while (queue.size() > 0) {
            int lenOne = queue.size();
            boolean first = false;
            while (lenOne > 0) {
                TreeNode temp = queue.poll();
                if (!first) {
                    list.add(temp.getVal());
                    first = true;
                }
                if (temp.getRight() != null) {
                    queue.add(temp.getRight());
                }
                if (temp.getLeft() != null) {
                    queue.add(temp.getLeft());
                }
                lenOne--;
            }
        }
        return list;
    }

    /**
     * 左视图
     *
     * @param root
     * @return
     */
    public static List<Integer> left(TreeNode root) {
        if (root == null) {
            return new ArrayList<>();
        }
        List<Integer> list = new ArrayList<>(10);
        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(root);
        while (queue.size() > 0) {
            int lenOne = queue.size();
            boolean first = false;
            while (lenOne > 0) {
                TreeNode temp = queue.poll();
                if (!first) {
                    list.add(temp.getVal());
                    first = true;
                }

                if (temp.getLeft() != null) {
                    queue.add(temp.getLeft());
                }
                if (temp.getRight() != null) {
                    queue.add(temp.getRight());
                }
                lenOne--;
            }
        }
        return list;
    }

    public static void main(String[] args) {
        Integer[] arr = new Integer[]{1, 2, 3, null, 5, null, 4};
        TreeNode root = TreeNode.createBinaryTreeByArray(arr, 0);
        System.out.println(right(root));
        System.out.println(left(root));
    }
}
