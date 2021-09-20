package org.xiyou.leetcode.leetcode.dfs;

import org.xiyou.leetcode.leetcode.TreeNode;

/**
 * @author xiyou
 * @version 1.0
 * xiyou-todo 验证是否是平衡二叉树
 * @date 2020/5/12 21:14
 */
public class AvlTree {
    int pre = Integer.MIN_VALUE;

    public boolean isValidBST(TreeNode root) {
        if (root == null) {
            return true;
        }
        // 访问左子树
        if (!isValidBST(root.left)) {
            return false;
        }
        System.out.println(root.val);
        // 访问当前节点：如果当前节点小于等于中序遍历的前一个节点，说明不满足BST，返回 false；否则继续遍历。
        if (root.val <= pre) {
            return false;
        }
        pre = root.val;
        // 访问右子树
        return isValidBST(root.right);
    }

    public boolean isBalanced(TreeNode root) {
        if (root == null) {
            return true;
        }
        // 访问左子树
        if (!isValidBST(root.left)) {
            return false;
        }
        int cha = maxDepth(root.left) - maxDepth(root.right);
        if (cha > 1 || cha < -1) {
            return false;
        }
        return isValidBST(root.right);
    }

    /**
     * 获取二叉树最大深度
     *
     * @param root
     * @return
     */
    public int maxDepth(TreeNode root) {
        if (root == null) {
            return 0;
        }
        int left = maxDepth(root.left);
        int right = maxDepth(root.right);
        return Math.max(left, right) + 1;
    }

    public static void main(String[] args) {
        Integer arr[] = new Integer[]{3, 9, 20, null, null, 15, 7};
        Integer arr2[] = new Integer[]{5, 1, 4, null, null, 3, 6};
        TreeNode root = TreeNode.createBinaryTreeByArray(arr, 0);
        TreeNode root2 = TreeNode.createBinaryTreeByArray(arr2, 0);
        AvlTree avlTree = new AvlTree();
//        System.out.println(avlTree.isBalanced(root));
        System.out.println(avlTree.isValidBST(root2));
    }

}
