package org.xiyou.leetcode.leetcode.hs;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author xiyou
 * 力扣全排列问题
 */
public class QuanPaiLie {

    public List<List<Integer>> permute(int[] nums) {
        int len = nums.length;
        List<List<Integer>> res = new ArrayList<>(factorial(len));
        if (len == 0) {
            return res;
        }
        Deque<Integer> path = new ArrayDeque<>(len);
        dfs(nums, len, 0, path, res);
        System.out.println(res.toArray());
        return res;
    }

    //计算全排列总数据量
    private int factorial(int n) {
        int res = 1;
        for (int i = 2; i <= n; i++) {
            res *= i;
        }
        return res;
    }

    private void dfs(int[] nums, int len, int depth,
                     Deque<Integer> path,
                     List<List<Integer>> res) {
        if (depth == len) {
            res.add(new ArrayList<>(path));
            return;
        }

        for (int i = 0; i < len; i++) {
            //排除不合法的请求
            if (!path.contains(nums[i])) {
                //一条路径里添加当前值
                path.addLast(nums[i]);
                //进入下一层决策树
                //递归调用剩下的组合，这个和二叉树差不多都是前序遍历，会先遍历完一条路径
                dfs(nums, len, depth + 1, path, res);

                path.removeLast();

            }
        }
    }

    public static void main(String[] args) {
        int arr[] = new int[]{1, 2, 3, 4, 5};
        QuanPaiLie solution = new QuanPaiLie();
        System.out.println(solution.permute(arr));
    }
}

