package org.xiyou.nlp;

import org.apache.commons.lang3.StringUtils;
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
public class ACAutoMachineTemplateAnalyzer {


    /**
     * 生成一个模版
     *
     * @param node
     * @param childList
     */
    public static void buildTree(ACAutoMachineTemplateNode node, List<ACTemplateChild> childList) {
        if (CollectionUtils.isEmpty(childList)) {
            return;
        }
        childList.forEach(childTemplate -> {
            List<String> words = childTemplate.getWordList();
            words.forEach(oneWord -> {
                add(node, oneWord, childTemplate.getType(), childTemplate.getNextType());
            });
        });
        makeFail(node);
    }

    /**
     * 添加一个词和它的相关类型
     *
     * @param root
     * @param word
     * @param type
     * @param nextType
     */
    private static void add(ACAutoMachineTemplateNode root, String word, String type, List<String> nextType) {
        ACAutoMachineTemplateNode tmpNode = root;
        for (int i = 0; i < word.length(); i++) {
            String curChar = String.valueOf(word.charAt(i));
            if (!tmpNode.getNext().containsKey(curChar)) {
                tmpNode.getNext().put(curChar, new ACAutoMachineTemplateNode());
            }
            tmpNode = tmpNode.getNext().get(curChar);
        }
        tmpNode.setEnd(true);
        HashSet<String> typeList = tmpNode.getType();
        typeList.add(type);
        HashSet<String> nextTypeList = tmpNode.getNextType();
        nextTypeList.addAll(nextType);
        tmpNode.setType(typeList);
        tmpNode.setNextType(nextTypeList);
        tmpNode.setWord(word);
    }


    /**
     * 构建失败回路
     *
     * @param root
     */
    private static void makeFail(ACAutoMachineTemplateNode root) {
        Queue<ACAutoMachineTemplateNode> queue = new LinkedBlockingDeque<>();
        queue.add(root);
        while (queue.size() > 0) {
            ACAutoMachineTemplateNode tmp = queue.remove();
            for (Map.Entry<String, ACAutoMachineTemplateNode> entry : tmp.getNext().entrySet()) {
                String k = entry.getKey();
                ACAutoMachineTemplateNode v = entry.getValue();
                if (tmp.equals(root)) {
                    v.setFail(root);
                } else {
                    ACAutoMachineTemplateNode f = tmp.getFail();
                    while (f != null) {
                        if (f.getNext().containsKey(k)) {
                            ACAutoMachineTemplateNode tmpFail = tmp.getNext().get(k);
                            tmpFail.setFail(f.getNext().get(k));
                            tmp.getNext().put(k, tmpFail);
                        }
                        f = f.getFail();

                    }
                    ACAutoMachineTemplateNode n = tmp.getNext().get(k);
                    n.setFail(root);
                    tmp.getNext().put(k, n);
                }
                queue.add(tmp.getNext().get(k));
            }
        }
    }


    /**
     * 搜索匹配到的第一个模版
     *
     * @param root
     * @param content
     * @return
     */
    public static ACNodeResult search(ACAutoMachineTemplateNode root, String content) {
        ACAutoMachineTemplateNode tmpNode = root;
        ACNodeResult result = null;
        int end = 0;
        for (int i = 0; i < content.length(); i++) {
            if (tmpNode == null) {
                return result;
            }
            String curChar = String.valueOf(content.charAt(i));
            /**
             * existCommoneType 判断已经匹配到的词和当前值有没有公共类型，如果有公共类型则不调用fail,因为调用之后tmpNode就会变为root,然后就要重新查询了
             * 比如已经匹配了《北京》，下一个词是《的》，我们加入existCommoneType之后的效果是：tmpNode=null，直接退出
             */
            while (!tmpNode.getNext().containsKey(curChar) && tmpNode != root && existCommoneType(result.getType(), tmpNode.getType()) == false) {
                tmpNode = tmpNode.getFail();
            }
            tmpNode = tmpNode.getNext().get(curChar);
            if (tmpNode != null && tmpNode.getEnd()) {
                end = i;
                result = new ACNodeResult(tmpNode.getWord(), tmpNode.getType(), tmpNode.getNextType(), 0, end);
                if (tmpNode.getFail() != null && tmpNode.getFail().getEnd()) {
                    result = (new ACNodeResult(tmpNode.getFail().getWord(), tmpNode.getFail().getType(), tmpNode.getFail().getNextType(), 0, end));
                }
            }
        }
        return result;
    }

    /**
     * 根据匹配到到模版，接着找当前模版下面的子模版
     *
     * @param root
     * @param content
     * @param type
     * @return
     */
    public static ACNodeResult searchWithType(ACAutoMachineTemplateNode root, String content, HashSet<String> type) {
        ACAutoMachineTemplateNode tmpNode = root;
        ACNodeResult result = null;
        int end = 0;
        for (int i = 0; i < content.length(); i++) {
            if (tmpNode == null) {
                return result;
            }
            String curChar = String.valueOf(content.charAt(i));
            while (!tmpNode.getNext().containsKey(curChar) && tmpNode != root && !existCommoneType(result.getType(), tmpNode.getType())) {
                tmpNode = tmpNode.getFail();
            }
            tmpNode = tmpNode.getNext().get(curChar);
            if (tmpNode != null && tmpNode.getEnd()) {
                if (existCommoneType(type, tmpNode.getType())) {
                    end = i;
                    result = (new ACNodeResult(tmpNode.getWord(), tmpNode.getType(), tmpNode.getNextType(), getCommoneType(tmpNode.getType(), type), 0, end));
                }
                if (tmpNode.getFail() != null && tmpNode.getFail().getEnd()) {
                    if (tmpNode.getType().retainAll(type)) {
                        result = (new ACNodeResult(tmpNode.getFail().getWord(), tmpNode.getFail().getType(), tmpNode.getFail().getNextType(), getCommoneType(tmpNode.getType(), type), 0, end));
                    }
                }
            }
        }
        return result;
    }

    /**
     * 判断是否有公共类型
     *
     * @param one
     * @param two
     * @return
     */
    public static boolean existCommoneType(HashSet<String> one, HashSet<String> two) {
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

    public static HashSet<String> getCommoneType(HashSet<String> one, HashSet<String> two) {
        if (CollectionUtils.isEmpty(one) || CollectionUtils.isEmpty(two)) {
            return new HashSet<>();
        }
        HashSet<String> result = new HashSet<>();
        result.addAll(one);
        result.retainAll(two);
        if (result.size() > 0) {
            return result;
        }
        return new HashSet<>();
    }

    public static List<ACTemplateChild> init() {
        List<String> dateList = Arrays.asList("今天", "昨天", "明天", "后天", "这周", "这个月", "这月", "温度计", "北京", "北京东站");
        List<String> weatherType = Arrays.asList("天气", "气温", "温度", "温度");
        ACTemplateChild date = new ACTemplateChild("date", Arrays.asList("cityIgnore", "weather"), dateList);
        ACTemplateChild weather = new ACTemplateChild("weather", new ArrayList<>(), weatherType);

        List<String> city1List = Arrays.asList("北京", "河北", "河北秦皇岛", "秦皇岛", "山东", "北平", "北京车站", "北京东站", "温州");
        List<String> cityToList = Arrays.asList("到", "飞到", "至", "达到");
        List<String> trafficList = Arrays.asList("飞机", "火车", "轮船", "汽车", "温度计","秦皇岛");
        List<String> ignoreList = Arrays.asList("的", "地", "de", "di");
        ACTemplateChild child1 = new ACTemplateChild("city", Arrays.asList("cityTo", "cityIgnore", "traffic"), city1List);
        ACTemplateChild child2 = new ACTemplateChild("cityTo", Arrays.asList("city"), cityToList);
        ACTemplateChild child4 = new ACTemplateChild("traffic", new ArrayList<>(), trafficList);
        ACTemplateChild child5 = new ACTemplateChild("cityIgnore", Arrays.asList("traffic", "weather"), ignoreList);
        List<ACTemplateChild> childList = new LinkedList<>();
        childList.add(child1);
        childList.add(child2);
        childList.add(child4);
        childList.add(child5);
        childList.add(date);
        childList.add(weather);
        return childList;
    }


    public static void main(String[] args) {
        ACAutoMachineTemplateNode root = new ACAutoMachineTemplateNode();
        ACAutoMachineTemplateAnalyzer.buildTree(root, init());
        String q = "北京到秦皇岛的火车";
        ACTemplateResult result = new ACTemplateResult();
        result.setQ(q);
        System.out.println("输入的词是：" + q);
        q = q.replaceAll(" ", "");
        ACNodeResult father = ACAutoMachineTemplateAnalyzer.search(root, q);
        StringBuffer partternStr = new StringBuffer();
        if (father != null) {
            q = searchChild(root, q, father, result);
        }
        System.out.println("根据模版匹配到的句子是：" + q);
    }

    private static String searchChild(ACAutoMachineTemplateNode root, String q, ACNodeResult father, ACTemplateResult result) {
        System.out.println("匹配到的模版开头是：" + father.toString());
        StringBuffer matchQ = new StringBuffer();
        StringBuffer matchTemplate = new StringBuffer();
        matchQ.append(father.getWord());
        matchTemplate.append(father.getType());
        q = q.substring(father.getEnd() + 1);
        while (StringUtils.isNotBlank(q)) {
            ACNodeResult one = ACAutoMachineTemplateAnalyzer.searchWithType(root, q, father.getNextType());
            while (one != null) {
                System.out.println("匹配到的子模版内容是：" + one.toString());
                matchQ.append(one.getWord());
                if (StringUtils.isNotBlank(q)) {
                    q = q.substring(one.getEnd() + 1);
                    one = ACAutoMachineTemplateAnalyzer.searchWithType(root, q, one.getNextType());
                }
            }
            break;
        }
        result.setMatchQ(matchQ.toString());
        result.setMatchTemplate("");
        return matchQ.toString();
    }


}
