package org.xiyou.nlp;

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
public class ACAutoMachineAnalyzer {
    public static void buildTree(ACAutoMachineNode node, List<String> word, List<String> word2, List<String> word3, List<String> word4) {
        word.forEach(one -> {
            add(node, one, "a", "b");
        });
        word2.forEach(one -> {
            add(node, one, "b", "c");
        });
        word3.forEach(one -> {
            add(node, one, "c", "d");
        });
        word3.forEach(one -> {
            add(node, one, "d", "");
        });
        makeFail(node);
    }

    private static void add(ACAutoMachineNode root, String word, String type, String nextType) {
        ACAutoMachineNode tmpNode = root;
        for (int i = 0; i < word.length(); i++) {
            String curChar = String.valueOf(word.charAt(i));
            if (!tmpNode.getNext().containsKey(curChar)) {
                tmpNode.getNext().put(curChar, new ACAutoMachineNode());
            }
            tmpNode = tmpNode.getNext().get(curChar);
        }
        tmpNode.setEnd(true);
        HashSet<String> typeList = tmpNode.getType();
        typeList.add(type);
        HashSet<String> nextTypeList = tmpNode.getNextType();
        nextTypeList.add(nextType);
        tmpNode.setType(typeList);
        tmpNode.setNextType(nextTypeList);
        tmpNode.setWord(word);
    }

    private static void makeFail(ACAutoMachineNode root) {
        Queue<ACAutoMachineNode> queue = new LinkedBlockingDeque<>();
        queue.add(root);
        while (queue.size() > 0) {
            ACAutoMachineNode tmp = queue.remove();
            for (Map.Entry<String, ACAutoMachineNode> entry : tmp.getNext().entrySet()) {
                String k = entry.getKey();
                ACAutoMachineNode v = entry.getValue();
                if (tmp.equals(root)) {
                    v.setFail(root);
                } else {
                    ACAutoMachineNode f = tmp.getFail();
                    while (f != null) {
                        if (f.getNext().containsKey(k)) {
                            ACAutoMachineNode tmpFail = tmp.getNext().get(k);
                            tmpFail.setFail(f.getNext().get(k));
                            tmp.getNext().put(k, tmpFail);
                        }
                        f = f.getFail();

                    }
                    ACAutoMachineNode n = tmp.getNext().get(k);
                    n.setFail(root);
                    tmp.getNext().put(k, n);
                }
                queue.add(tmp.getNext().get(k));
            }
        }
    }

    public ACAutoMachineAnalyzer() {
    }

    public static ACResult search(ACAutoMachineNode root, String content) {
        ACAutoMachineNode tmpNode = root;
        ACResult result = null;
        String type = "";
        int start = 0;
        int end = 0;
        for (int i = 0; i < content.length(); i++) {
            if (tmpNode == null) {
                return result;
            }
            String curChar = String.valueOf(content.charAt(i));
            while (!tmpNode.getNext().containsKey(curChar) && tmpNode != root && !typeExist(result.getType(), tmpNode.getType())) {
                tmpNode = tmpNode.getFail();
            }
            tmpNode = tmpNode.getNext().get(curChar);
            if (tmpNode != null && tmpNode.getEnd()) {
                end = i;
                result = new ACResult(tmpNode.getWord(), tmpNode.getType(), tmpNode.getNextType(), start, end);
                if (tmpNode.getFail() != null && tmpNode.getFail().getEnd()) {
                    result = (new ACResult(tmpNode.getFail().getWord(), tmpNode.getFail().getType(), tmpNode.getFail().getNextType(), start, end));
                }
            }
        }
        return result;
    }

    public static ACResult searchWithType(ACAutoMachineNode root, String content, HashSet<String> type) {
        if(content.equals("飞机")){
            System.out.println("....");
        }
        ACAutoMachineNode tmpNode = root;
        ACResult result = null;
        int end = 0;
        for (int i = 0; i < content.length(); i++) {
            if (tmpNode == null) {
                return result;
            }
            String curChar = String.valueOf(content.charAt(i));
            while (!tmpNode.getNext().containsKey(curChar) && tmpNode != root && !typeExist(type, tmpNode.getType())) {
                tmpNode = tmpNode.getFail();
            }
            tmpNode = tmpNode.getNext().get(curChar);

            if (tmpNode != null && tmpNode.getEnd()) {
                if (typeExist(type, tmpNode.getType())) {
                    end = i;
                    result = (new ACResult(tmpNode.getWord(), tmpNode.getType(), tmpNode.getNextType(),0,end));

                }
                if (tmpNode.getFail() != null && tmpNode.getFail().getEnd()) {
                    if (tmpNode.getType().retainAll(type)) {
                        result = (new ACResult(tmpNode.getFail().getWord(), tmpNode.getFail().getType(), tmpNode.getFail().getNextType()));
                    }
                }
            }
        }
        return result;
    }

    public static boolean typeExist(HashSet<String> one, HashSet<String> two) {

        if (CollectionUtils.isEmpty(one) || CollectionUtils.isEmpty(two)) {
            return false;
        }
        HashSet<String> result = new HashSet<>();
        result.addAll(one);
        result.retainAll(two);
        if (result.size() > 0) {
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        List<String> templateList = Arrays.asList("北京", "河北", "山东", "北平", "北京车站");
        List<String> templateList3 = Arrays.asList("美国", "日本", "韩国", "英国", "北京车站");
        List<String> list2 = Arrays.asList("到", "飞到", "至", "达到");
        List<String> templateList4 = Arrays.asList("飞机", "火车", "轮船", "汽车");
        String str = "01234567";
        System.out.println(str.substring(4));
        ACAutoMachineNode node = new ACAutoMachineNode();
        ACAutoMachineAnalyzer.buildTree(node, templateList, list2, templateList3,templateList4);
        String content = "北京车站到美国飞机";
        ACResult list = ACAutoMachineAnalyzer.search(node, content);
        System.out.println(list.toString());
        content = content.substring(list.getEnd() + 1);
        System.out.println(content);
        ACResult one = ACAutoMachineAnalyzer.searchWithType(node, content, list.getNextType());
        System.out.println(one.toString());
        while (one != null) {
            content = content.substring(one.getEnd() + 1);
            System.out.println(content);
            one = ACAutoMachineAnalyzer.searchWithType(node, content, one.getNextType());
            System.out.println(one.toString());
        }


    }
}
