package org.xiyou.leetcode.xx;

import lombok.Data;

import java.util.HashSet;

/**
 * Description
 * <p>
 * </p>
 * DATE 2020/12/1.
 *
 * @author houchenggong.
 * 在AC自动机中找到的节点数据
 */
@Data
public class MatchNodeResult {

    /**
     * 当前词
     */
    private String word;
    /**
     * 当前词的类型
     */
    private HashSet<String> slotType;

    /**
     * 当前词的对应模版的列表
     */
    private HashSet<String> templateNameSet;

    /**
     * 当前词在句子中结束的位置
     */
    private Integer end;


    public MatchNodeResult(String word, HashSet<String> slotType, HashSet<String> templateNameSet,Integer end) {
        this.word = word;
        this.slotType = slotType;
        this.templateNameSet = templateNameSet;
        this.end=end;
    }
}
