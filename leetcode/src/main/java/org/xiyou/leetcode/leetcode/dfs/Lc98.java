package org.xiyou.leetcode.leetcode.dfs;

import org.xiyou.leetcode.leetcode.TreeNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * @author xiyou
 * @version 1.0
 * xiyou-todo 验证二叉搜索树
 * @date 2020/5/5 20:57
 */
public class Lc98 {

    /**
     * 验证二叉搜索树的栈系列
     *
     * @param root
     * @return
     */
    public boolean isValidBST(TreeNode root) {
        Stack<TreeNode> stack = new Stack();
        double inorder = -Double.MAX_VALUE;

        while (!stack.isEmpty() || root != null) {
            while (root != null) {
                stack.push(root);
                root = root.left;
            }
            root = stack.pop();
            // 如果中序遍历得到的节点的值小于等于前一个 inorder，说明不是二叉搜索树
            if (root.val <= inorder) {
                return false;
            }
            inorder = root.val;
            root = root.right;
        }
        return true;
    }

    /**
     * 中序遍历栈实现
     *
     * @param root
     * @return
     */
    public static List<Integer> midStack(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        if (root == null) {
            return result;
        }
        Stack<TreeNode> stack = new Stack<>();
        // 条件是：或，意思是只要根节点或者stack中有一个数据不为空就继续
        while (stack.isEmpty() == false || root != null) {
            while (root != null) {
                //把当前根节点下面的left节点全都放入到栈中
                stack.add(root);
                root = root.getLeft();
            }
            //根节点等于当前根节点最左下的节点
            root = stack.pop();
            //放入根节点的值
            result.add(root.val);
            //把根节点换成最左下节点的右子节点
            root = root.getRight();

        }
        return result;
    }

    /**
     * 递归实现
     * @param root
     * @param result
     */
    public static void midDiGui(TreeNode root, List<Integer> result) {
        if (root == null) {
            return;
        }

        midDiGui(root.getLeft(), result);
        result.add(root.val);
        midDiGui(root.getRight(), result);
    }
     


}
