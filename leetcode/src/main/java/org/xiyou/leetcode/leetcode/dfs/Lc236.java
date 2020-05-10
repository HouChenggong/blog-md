package org.xiyou.leetcode.leetcode.dfs;

import org.xiyou.leetcode.leetcode.TreeNode;

/**
 * @author xiyou
 * @version 1.0
 * xiyou-todo 二叉树最近公共节点
 * @date 2020/5/10 10:23
 */
public class Lc236 {
    TreeNode one = null;
    TreeNode two = null;

    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        if (root == null || p == null || q == null) {
            return null;
        }
        if (root.val == (q.val) || root.val == q.val) {
            return root;
        }
        TreeNode l = lowestCommonAncestor(root.left, p, q);
        TreeNode r = lowestCommonAncestor(root.right, p, q);
        if (l != null && r != null) {
            return root;
        } else if (l != null) {
            return l;
        } else {
            return r;
        }

    }
}
