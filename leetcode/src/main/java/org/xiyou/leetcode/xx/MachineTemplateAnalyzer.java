package org.xiyou.leetcode.xx;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * 描述用途
 * <p>
 * </p>
 * DATE 2020/2/21.
 *
 * @author zhangjunbo.
 */
@Data
@Slf4j
public class MachineTemplateAnalyzer {


    /**
     * 添加数据词
     *
     * @param root     根节点
     * @param word     词
     * @param slotType 当前词的词槽类型
     */
    private static synchronized void add(MachineTemplateNode root, String word, String slotType) {
        MachineTemplateNode tmpNode = root;
        for (int i = 0; i < word.length(); i++) {
            String curChar = String.valueOf(word.charAt(i));
            if (!tmpNode.getNext().containsKey(curChar)) {
                tmpNode.getNext().put(curChar, new MachineTemplateNode());
            }
            tmpNode = tmpNode.getNext().get(curChar);
        }
        tmpNode.setEnd(true);
        HashSet<String> typeList = tmpNode.getSlotTypeSet();
        if (typeList == null) {
            typeList = new HashSet<>();
        }
        typeList.add(slotType);
        tmpNode.setSlotTypeSet(typeList);
        tmpNode.setWord(word);
    }

    /**
     * 添加模版词
     *
     * @param root
     * @param word
     * @param slotType
     * @param templateName
     */
    private static synchronized void addTemplate(MachineTemplateNode root, String word, String slotType, String templateName) {
        MachineTemplateNode tmpNode = root;
        for (int i = 0; i < word.length(); i++) {
            String curChar = String.valueOf(word.charAt(i));
            if (!tmpNode.getNext().containsKey(curChar)) {
                tmpNode.getNext().put(curChar, new MachineTemplateNode());
            }
            tmpNode = tmpNode.getNext().get(curChar);
        }
        tmpNode.setEnd(true);
        HashSet<String> typeList = tmpNode.getSlotTypeSet();
        if (typeList == null) {
            typeList = new HashSet<>();
        }
        typeList.add(slotType);
        tmpNode.setSlotTypeSet(typeList);
        tmpNode.setWord(word);
        HashSet<String> templateNameSet = tmpNode.getTemplateNameSet();
        if (templateNameSet == null) {
            templateNameSet = new HashSet<>();
        }
        templateNameSet.add(templateName);
        // 模版节点
        tmpNode.setTemplateNode(true);
        // 模版名称
        tmpNode.setTemplateNameSet(templateNameSet);
    }


    /**
     * 查询是否匹配到了模版
     *
     * @param root
     * @param content
     * @return
     */
    public static synchronized MatchNodeResult searchTemplate(MachineTemplateNode root, String content) {
        MachineTemplateNode tmpNode = root;
        MatchNodeResult result = null;
        int end = 0;
        for (int i = 0; i < content.length(); i++) {
            if (tmpNode == null) {
                return result;
            }
            String curChar = String.valueOf(content.charAt(i));
            while (!tmpNode.getNext().containsKey(curChar)) {
                // 如果下一个词不包含了，直接退出
                return result;
            }
            tmpNode = tmpNode.getNext().get(curChar);
            // 当前节点是模版节点
            if (tmpNode != null && tmpNode.getTemplateNode() != null && tmpNode.getTemplateNode() == true) {
                // 匹配到一个并不终止，继续匹配更接近的词，比如《北京》《北京东站》都属于地点，当用户输入《北京东站》的时候，保留《北京东站》
                end = i;
                result = new MatchNodeResult(tmpNode.getWord(), tmpNode.getSlotTypeSet(), tmpNode.getTemplateNameSet(), end);
            }
        }
        return result;
    }


    /**
     * 处理《的问题》《问题》对应a_Ignore,  和b的方案
     *
     * @param root
     * @param content
     * @param slotIgnoreType
     * @param nextType
     * @return
     */
    public static synchronized MatchNodeResult searchNode(MachineTemplateNode root, String content, String slotIgnoreType, String nextType) {
        HashSet<String> allType = new HashSet<>();
        allType.add(slotIgnoreType);
        allType.add(nextType);
        MachineTemplateNode tmpNode = root;
        MatchNodeResult result = null;
        int end = 0;
        boolean ignore = true;
        for (int i = 0; i < content.length(); i++) {
            if (tmpNode == null) {
                return result;
            }
            String curChar = String.valueOf(content.charAt(i));
            while (!tmpNode.getNext().containsKey(curChar)) {
                // 如果下一个词不包含了，直接退出
                if (StringUtils.isNotBlank(slotIgnoreType) && tmpNode.getSlotTypeSet().contains(slotIgnoreType) && ignore == true) {
                    tmpNode = root;
                    // ignore词只能用一次
                    ignore = false;
                } else {
                    return result;
                }

            }
            tmpNode = tmpNode.getNext().get(curChar);
            // 当前节点是模版节点
            if (tmpNode != null && tmpNode.getEnd() != null && tmpNode.getEnd()) {
                // 匹配到一个并不终止，继续匹配更接近的词，比如《北京》《北京东站》都属于地点，当用户输入《北京东站》的时候，保留《北京东站》
                if (existCommoneType(tmpNode.getSlotTypeSet(), allType)) {
                    end = i;
                    result = new MatchNodeResult(tmpNode.getWord(), tmpNode.getSlotTypeSet(), tmpNode.getTemplateNameSet(), end);
                } else {
                    return result;
                }

            }
        }
        return result;
    }

    /**
     * 构建树
     *
     * @param node
     * @param childList
     */
    public static void buildTree(MachineTemplateNode node, List<TemplateChild> childList) {
        if (CollectionUtils.isEmpty(childList)) {
            return;
        }
        childList.forEach(childTemplate -> {
            List<String> words = childTemplate.getWordList();
            String templateNmae = childTemplate.getTemplateName();
            words.forEach(oneWord -> {
                if (StringUtils.isBlank(templateNmae)) {
                    add(node, oneWord, childTemplate.getSloTtype());
                } else {
                    addTemplate(node, oneWord, childTemplate.getSloTtype(), childTemplate.getTemplateName());
                }
            });
        });
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

    public static List<TemplateChild> init() {
        List<TemplateChild> list = new LinkedList<>();
        // 北京气温
        String weatherTemplate1 = "sys_city##weather";
        TemplateChild child = new TemplateChild("sys_city", Arrays.asList("北京", "北京东站", "天津"), weatherTemplate1);
        TemplateChild weather = new TemplateChild("weather", Arrays.asList("天气", "气温", "温度"), "");
        list.add(child);
        list.add(weather);
        // 北京的气温是多少
        String weatherTemplate2 = "sys_city##city_IGNORE##weather##duo_shao_IGNORE";
        TemplateChild child2 = new TemplateChild("sys_city_2", Arrays.asList("北京", "北京东站", "天津"), weatherTemplate2);
        TemplateChild cityIgnore = new TemplateChild("city_IGNORE", Arrays.asList("de", "地", "的"), "");
        TemplateChild cityDuo = new TemplateChild("duo_shao_IGNORE", Arrays.asList("是多少", "是什么", "怎么样"), "");
        list.add(cityDuo);
        list.add(cityIgnore);
        list.add(child2);
        return list;
    }

    public static List<TemplateChild> init2() {
        List<TemplateChild> list = new LinkedList<>();
        // 北京气温
        String weatherTemplate1 = "sys_city##city_IGNORE##weather##duo_shao_IGNORE";
        TemplateChild child = new TemplateChild("sys_city", Arrays.asList("山东", "山东廊坊", "河北", "韩国"), weatherTemplate1);
        list.add(child);
        return list;
    }

    public static void main(String[] args) {
        MachineTemplateNode root = new MachineTemplateNode();
        buildTree(root, init());
        buildTree(root, init2());
        String q = "北京的气温是多少";
        log.info("搜索词是：[{}].....开始搜索", q);
        List<TemplateResult> result = search(root, q);
        log.info("模版匹配结果是：[{}]", result);

    }

    private static List<TemplateResult> search(MachineTemplateNode root, String q) {
        List<TemplateResult> templateResultList = new LinkedList<>();
        MatchNodeResult matchNodeResult = searchTemplate(root, q);
        if (matchNodeResult != null) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(matchNodeResult.getWord());
            log.info("匹配到内容是：[{}] 模版有：[{}]", matchNodeResult.getWord(), matchNodeResult.getTemplateNameSet());
            matchNodeResult.getTemplateNameSet().forEach(oneTemplate -> {
                String[] arr = oneTemplate.split("##");
                String nodeQ = q.substring(matchNodeResult.getEnd() + 1);
                List<String> wordSlotList = Arrays.asList(arr);
                boolean flag = false;
                for (int i = 1; i < wordSlotList.size(); i++) {
                    String nextSlotType = wordSlotList.get(i);
                    String ignoreType = "";
                    if (nextSlotType.contains("IGNORE")) {
                        ignoreType = nextSlotType;
                        i++;
                        if (i == wordSlotList.size()) {
                            // 如果最后一个是IGNORE，不用再进行匹配，说明匹配到了
                            log.info("最后一个词槽类型是：[{}]可以忽略，模版: [{}]  匹配成功", ignoreType, oneTemplate);
                            flag = true;
                            break;
                        } else if (i < wordSlotList.size()) {
                            nextSlotType = wordSlotList.get(i);
                        }
                    }
                    log.info("开始匹配模版：[{}] 要搜索的词是[{}] 下一个可忽略的词槽[{}] 下一个词槽[{}]", oneTemplate, nodeQ, ignoreType, nextSlotType);
                    MatchNodeResult childResult = searchNode(root, nodeQ, ignoreType, nextSlotType);
                    if (childResult != null) {
                        stringBuffer.append(childResult.getWord());
                        if (i == wordSlotList.size()) {
                            flag = true;
                        }
                        nodeQ = nodeQ.substring(childResult.getEnd() + 1);
                    } else {
                        break;
                    }

                }
                String matchQ = stringBuffer.toString();
                if (flag) {
                    templateResultList.add(new TemplateResult(q, matchQ, oneTemplate));
                }
                log.info("[{}]模版匹配结束..............匹配结果是：[{}] 匹配字符串是：[{}]", oneTemplate, flag, matchQ);
            });
        }
        return templateResultList;
    }
}
