package org.nlp;

import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Description
 * <p>
 * </p>
 * DATE 2020/12/1.
 *
 * @author houchenggong.
 */
public class ACTreeUtil {


    /**
     * 生成一个模版
     *
     * @param node
     * @param words
     */
    public static void buildTree(ACTree node, List<String> words) {
        if (CollectionUtils.isEmpty(words)) {
            return;
        }
        node.setWord("root");

        words.forEach(oneWord -> {
            add(node, oneWord);
        });
        makeFail(node);
    }

    /**
     * 添加一个词和它的相关类型
     *
     * @param root
     * @param word
     */
    private static void add(ACTree root, String word) {
        ACTree tmpNode = root;
        for (int i = 0; i < word.length(); i++) {
            String curChar = String.valueOf(word.charAt(i));
            if (!tmpNode.getNext().containsKey(curChar)) {
                tmpNode.getNext().put(curChar, new ACTree());
            }
            tmpNode = tmpNode.getNext().get(curChar);
        }
        tmpNode.setEnd(true);
        tmpNode.setWord(word);
    }

    private static void makeFailOld(ACTree root) {
        Queue<ACTree> queue = new LinkedBlockingDeque<>();
        queue.add(root);
        while (queue.size() > 0) {
            ACTree tmp = queue.remove();
            for (Map.Entry<String, ACTree> entry : tmp.getNext().entrySet()) {
                String k = entry.getKey();
                ACTree v = entry.getValue();
                if (tmp.equals(root)) {
                    v.setFail(root);
                } else {
                    ACTree f = tmp.getFail();
                    while (f != null) {
                        if (f.getNext().containsKey(k)) {
                            ACTree tmpFail = tmp.getNext().get(k);
                            tmpFail.setFail(f.getNext().get(k));
                            tmp.getNext().put(k, tmpFail);
                        }
                        f = f.getFail();

                    }
                    ACTree n = tmp.getNext().get(k);
                    n.setFail(root);
                    tmp.getNext().put(k, n);
                }
                queue.add(tmp.getNext().get(k));
            }
        }
    }

    /**
     * 构建失败回路
     *
     * @param root
     */
    private static void makeFail(ACTree root) {
        Queue<ACTree> queue = new LinkedBlockingDeque<>();
        queue.add(root);
        while (queue.size() > 0) {
            // 广度优先遍历
            ACTree parent = queue.remove();
            ACTree parentFail;
            for (Map.Entry<String, ACTree> entry : parent.getNext().entrySet()) {
                String k = entry.getKey();
                ACTree v = entry.getValue();
                if (parent.equals(root)) {
                    // 第一层的fail总是指向root
                    v.setFail(root);
                } else {
                    parentFail = parent.getFail();
                    // 是否进行了转移
                    boolean transfer = false;
                    while (parentFail != null && transfer == false) {
                        // 遍历它的孩子，看它们有没与当前孩子相同字符的节点
                        if (parentFail.getNext().containsKey(k)) {
                            // 找到可以转移的节点
                            ACTree tmpFail = parent.getNext().get(k);
                            tmpFail.setFail(parentFail.getNext().get(k));
                            // 重新放当前转移的节点
                            parent.getNext().put(k, tmpFail);
                            transfer = true;
                        } else {
                            // 如果没有找到继续查找
                            parentFail = parentFail.getFail();
                        }
                    }
                    if (transfer == false) {
                        // 如果最后发现还是没找到可以转移的节点，则设置fail为root
                        ACTree tmpFail = parent.getNext().get(k);
                        tmpFail.setFail(root);
                        parent.getNext().put(k, tmpFail);
                    }
                }
                queue.add(parent.getNext().get(k));
            }
        }
    }

    public static List<String> search(ACTree root, String q) {
        ACTree tmpNode = root;
        List<String> result = new LinkedList<>();
        for (int i = 0; i < q.length(); i++) {
            if (tmpNode == null) {
                return result;
            }
            String curChar = String.valueOf(q.charAt(i));
            // 如果当前节点下不包含这个字符，而且当前node不是root，就转移成为fail节点
            while (!tmpNode.getNext().containsKey(curChar) && tmpNode != root) {
                tmpNode = tmpNode.getFail();
            }
            tmpNode = tmpNode.getNext().get(curChar);
            if (tmpNode != null && tmpNode.getEnd()) {
                // 如果词是end，加入结果集
                result.add(tmpNode.getWord());
                if (tmpNode.getFail() != null && tmpNode.getFail().getEnd()) {
                    // 如果它的fail也是end也加入结果集
                    result.add(tmpNode.getFail().getWord());
                }
            }
        }
        return result;
    }

    public static void main(String[] args) {
        ACTree root = new ACTree();
        buildTree(root, Arrays.asList("he", "hers", "his", "she","sheh"));
        makeFail(root);
        System.out.println(root);
        System.out.println(search(root, "hishers").toString());
    }
}
