package org.xiyou.leetcode.leetcode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author xiyou
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TreeNode {
    public int val;

    public TreeNode left;

    public TreeNode right;

    public TreeNode(Integer val) {
        this.val = val;
    }



    public   static   TreeNode createBinaryTreeByArray(Integer []array, int index)
    {
        TreeNode tn = null;
        if (index<array.length) {
            Integer value = array[index];
            if (value == null) {
                return null;
            }
            tn = new TreeNode(value);
            tn.left = createBinaryTreeByArray(array, 2*index+1);
            tn.right = createBinaryTreeByArray(array, 2*index+2);
            return tn;
        }
        return tn;
    }
}
