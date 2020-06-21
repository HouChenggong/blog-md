package org.xiyou.leetcode.leetcode.dfs;

import org.xiyou.leetcode.leetcode.TreeNode;

/**
 * @author xiyouyan
 * @date 2020-06-20 23:04
 * @description
 */
public class Parttn {



    int maxSum = Integer.MIN_VALUE;

    public int maxPathSum(TreeNode root) {
        maxGain(root);
        return maxSum;
    }

    public int maxGain(TreeNode node) {
        if (node == null) {
            return 0;
        }

        // 递归计算左右子节点的最大贡献值
        // 只有在最大贡献值大于 0 时，才会选取对应子节点
        int leftGain = Math.max(maxGain(node.left), 0);
        int rightGain = Math.max(maxGain(node.right), 0);

        // 节点的最大路径和取决于该节点的值与该节点的左右子节点的最大贡献值
        int priceNewpath = node.val + leftGain + rightGain;
        System.out.println(priceNewpath+"----"+node.val);
        // 更新答案
        maxSum = Math.max(maxSum, priceNewpath);

        // 返回节点的最大贡献值
        return node.val + Math.max(leftGain, rightGain);
    }


    public static boolean isMatch(String s, String p) {
        //如果正则串p为空字符串s也为空这匹配成功，如果正则串p为空但是s不是空则说明匹配失败
        if (p.isEmpty()) {
            return s.isEmpty();
        }
        //判断s和p的首字符是否匹配，注意要先判断s不为空
        boolean headMatched = !s.isEmpty() && (s.charAt(0) == p.charAt(0) || p.charAt(0) == '.');
        if (p.length() >= 2 && p.charAt(1) == '*') {//如果p的第一个元素的下一个元素是*
            //则分别对两种情况进行判断
            return isMatch(s, p.substring(2)) || (headMatched && isMatch(s.substring(1), p));
        }
        if (headMatched) {//否则，如果s和p的首字符相等
            return isMatch(s.substring(1), p.substring(1));
        }
        return false;
    }


    class Solution {
        public boolean isMatch(String s, String p) {
            int m = s.length();
            int n = p.length();

            boolean[][] f = new boolean[m + 1][n + 1];
            f[0][0] = true;
            for (int i = 0; i <= m; ++i) {
                for (int j = 1; j <= n; ++j) {
                    if (p.charAt(j - 1) == '*') {
                        f[i][j] = f[i][j - 2];
                        if (matches(s, p, i, j - 1)) {
                            f[i][j] = f[i][j] || f[i - 1][j];
                        }
                    } else {
                        if (matches(s, p, i, j)) {
                            f[i][j] = f[i - 1][j - 1];
                        }
                    }
                }
            }
            return f[m][n];
        }

        public boolean matches(String s, String p, int i, int j) {
            if (i == 0) {
                return false;
            }
            if (p.charAt(j - 1) == '.') {
                return true;
            }
            return s.charAt(i - 1) == p.charAt(j - 1);
        }
    }

    public static void main(String[] args) {
        Integer arr[] = new Integer[]{
                -10, 9, 20, null, null, 15, 7};
        TreeNode root = TreeNode.createBinaryTreeByArray(arr, 0);
        Parttn parttn = new Parttn();
        int max = parttn.maxPathSum(root);
        System.out.println(max);


    }

}
