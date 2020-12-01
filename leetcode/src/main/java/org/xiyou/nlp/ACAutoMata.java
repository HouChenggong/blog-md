package org.xiyou.nlp;

/**
 * Description
 * <p>
 * </p>
 * DATE 2020/12/1.
 *
 * @author houchenggong.
 */

import java.util.*;

/**
 * 36-1 AC 自动机
 *
 * <p>描述：AC 自动机是一种基于 Trie 树改造的多模式串匹配算法的数据结构。它与 Trie 树查找匹配字符串的关系， 类似于单模式串中的 BF 算法与 KMP 算法的关系。
 *
 * <p>特点：
 *
 * <p>1. 多模式串匹配
 *
 * <p>2. 匹配效率高
 *
 * <p>构建实现：
 *
 * <p>1. 将多模式串构建成 Trie 树
 *
 * <p>2. 构建各个节点的失败指针
 *
 * <p>适用场景：敏感词过滤系统
 */
public class ACAutoMata {

    /**
     * 根节点
     */
    private final AcNode root = new AcNode('/');

    public static void main(String[] args) {
        String[] patterns = {"at", "art", "oars", "soar"};
        // String text = "soarsoars";
        String text = "soarat123ddddeedfefoarsoars";
        char[] chars = text.toCharArray();
        System.out.println("原始字符串：" + text);
        System.out.println(match(chars, patterns));
        System.out.println("替换后的字符串：" + String.valueOf(chars));

        // String[] patterns2 = {"Fxtec Pro1", "谷歌Pixel"};
        // String text2
        //   = "一家总部位于伦敦的公司Fxtex在MWC上就推出了一款名为Fxtec Pro1的手机，该机最大的亮点就是采用了侧滑式全键盘设计。DxOMark年度总榜发布 华为P20 Pro/谷歌Pixel 3争冠";
        // System.out.println(match(text2, patterns2));
    }

    public static boolean match(char[] text, String[] patterns) {
        ACAutoMata acAutoMata = new ACAutoMata();
        // 构建 Trie 树
        for (String pattern : patterns) {
            acAutoMata.insert(pattern.toCharArray());
        }
        // 构建失败指针
        acAutoMata.buildFailurePointer();

        return acAutoMata.match(text);
    }

    /**
     * 匹配字符串
     *
     * @param text 主串
     */
    public boolean match(char[] text) {
        // 主串从索引 0 开始遍历
        int n = text.length;
        // Trie 树重根节点开始查找
        boolean result = false;

        HashMap<Integer, Integer> matchedMap = new HashMap<>();

        AcNode p = root;
        for (int i = 0; i < n; i++) {
            while (p.getChild(text[i]) == null && p != root) {
                // 失败指针发挥作用的地方
                // 说明，Trie 树中的一个模式串已经发生了不匹配，从而提前结束，快速移动到下一个模式串中
                p = p.fail;
            }

            p = p.getChild(text[i]);
            if (p == null) {
                // 没有匹配，从 root 开始重新匹配
                p = root;
            }

            AcNode tmp = p;
            // 打印匹配到的字符
            while (tmp != root) {
                // 失败指针是皆为字符串,说明是这个模式串从
                if (tmp.isEndingChar) {
                    // 当前索引减去匹配的模式串的长度
                    int pos = i - tmp.length + 1;
                    System.out.println("匹配的起始下标 " + pos + "；长度" + tmp.length + "，字符串为：" + String
                            .valueOf(text, pos, tmp.length));
                    matchedMap.put(pos, tmp.length);
                    if (!result) {
                        result = true;
                    }
                }
                tmp = tmp.fail;
            }

        }

        // 替换字符串为 *** 号
        for (Map.Entry<Integer, Integer> entry : matchedMap.entrySet()) {
            for (int i1 = entry.getKey(); i1 < entry.getValue() + entry.getKey(); i1++) {
                text[i1] = '*';
            }
        }

        return result;
    }

    /**
     * 构建所有节点的失败指针
     */
    public void buildFailurePointer() {
        // 借助队列，广度优先搜索，按照层遍历 Trie
        Queue<AcNode> queue = new LinkedList<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            AcNode p = queue.poll();
            // 遍历 p 节点的每个子节点
            for (AcNode pc : p.getChildren()) {
                if (pc == null) {
                    continue;
                }
                // 寻找 p 节点的失败指针 q
                if (p == root) {
                    pc.fail = root;
                } else {
                    AcNode q = p.fail;
                    // 获取子节点的失败指针
                    while (q != null && q.getChild(pc.data) == null) {
                        q = q.fail;
                    }
                    if (q == null) {
                        pc.fail = root;
                    } else {
                        pc.fail = q.getChild(pc.data);
                    }
                }
                // 将子节点添加进队列
                queue.add(pc);
            }

        }

    }

    /**
     * 构建 Trie 树——插入模式串
     *
     * @param word 模式串
     */
    public void insert(char[] word) {
        // 遍历模式串的每个字符，从根节点开始寻找，将其插入到 Trie 树中合适的位置
        AcNode p = root;
        for (char c : word) {
            AcNode child = p.getChild(c);
            if (child == null) {
                p.addChildren(c);
            }
            p = p.getChild(c);
        }
        p.isEndingChar = true;
        p.length = word.length;
    }

    /**
     * AC 自动机节点
     */
    public static class AcNode {

        /**
         * 数据
         */
        char data;

        /**
         * 该节点是否为叶子节点
         */
        boolean isEndingChar = false;

        /**
         * 当 isEndingChar == true 时，记录模式串的长度
         */
        int length = -1;

        // 使用散列表存储子节点
        HashMap<String, AcNode> children = new HashMap<>();

        /**
         * 失败指针
         */
        AcNode fail;

        public AcNode(char data) {
            this.data = data;
        }

        public void addChildren(char c) {
            children.put(String.valueOf(c), new AcNode(c));
        }

        public AcNode getChild(char c) {
            return children.get(String.valueOf(c));
        }

        public Collection<AcNode> getChildren() {
            return children.values();
        }

    }

}
