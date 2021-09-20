package org.xiyou.nlp;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Description
 * <p>
 * </p>
 * DATE 2020/12/1.
 *
 * @author houchenggong.
 */
@Data
public class PreTreeNode {


    //是否最后一个字
    private boolean isKeyWordsEnd = false;

    //子节点
    private Map<Character, PreTreeNode> subNodes = new HashMap<>();

    public void addSubNode(Character key, PreTreeNode node) {
        subNodes.put(key, node);
    }

    public PreTreeNode getSubNode(Character key) {
        return subNodes.get(key);
    }

    public boolean isKeyWordsEnd() {
        return isKeyWordsEnd;
    }

    public void setKeyWordsEnd(Boolean end) {
        isKeyWordsEnd = end;
    }

    public void addSensitiveWord(String words, PreTreeNode rootNode) {
        PreTreeNode tempNode = rootNode;

        for (int i = 0; i < words.length(); i++) {
            Character c = words.charAt(i);
            PreTreeNode node = tempNode.getSubNode(c);
            if (node == null) {
                node = new PreTreeNode();
                tempNode.addSubNode(c, node);
            }
            // 指针移动
            tempNode = node;
            //如果到了最后一个字符
            if (i == words.length() - 1) {
                tempNode.setKeyWordsEnd(true);
            }
        }
    }

    public String filter(String text, PreTreeNode rootNode) {

        if (StringUtils.isEmpty(text)) {
            return text;
        }

        String sensitiveWords = "***";
        StringBuilder result = new StringBuilder();

        PreTreeNode tempNode = rootNode;
        int begin = 0;
        int position = 0;

        while (position < text.length()) {
            Character c = text.charAt(position);
            tempNode = tempNode.getSubNode(c);
            //如果匹配失败
            if (tempNode == null) {
                //说明以begin起头的那一段不存在非法词汇
                result.append(text.charAt(begin));
                begin++;
                position = begin;
                tempNode = rootNode;
                continue;
            } else if (tempNode.isKeyWordsEnd()) {
                //替换敏感词
                result.append(sensitiveWords);

                position++;
                begin = position;
                tempNode = rootNode;
            } else {
                position++;
            }

        }
        //把剩下的动加入合法集
        result.append(text.substring(begin));

        return result.toString();
    }

    public static void main(String[] args) {
        PreTreeNode root = new PreTreeNode();
        root.addSensitiveWord("王八", root);
        String result = root.filter("大哈哈王八蹄子", root);
        System.out.println(result);


    }
}
 



