package org.xiyou.nlp;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
public class ACNodeResult {

    /**
     * 当前词
     */
    private String word;
    /**
     * 当前词的类型
     */
    private HashSet<String> type;


    /**
     * 匹配到的类型
     */
    private HashSet<String> matchType;
    /**
     * 下一个词的类型
     */
    private HashSet<String> nextType;

    /**
     * 当前词在句子中的位置
     */
    private Integer start;

    /**
     * 当前词在句子中结束的位置
     */
    private Integer end;

    public ACNodeResult(String word, HashSet<String> type, HashSet<String> nextType) {
        this.word = word;
        this.type = type;
        this.nextType = nextType;
    }

    public ACNodeResult(String word, HashSet<String> type, HashSet<String> nextType, Integer start, Integer end) {
        this.word = word;
        this.type = type;
        this.nextType = nextType;
        this.start = start;
        this.end = end;
    }
    public ACNodeResult(String word, HashSet<String> type, HashSet<String> nextType,HashSet<String> matchType, Integer start, Integer end) {
        this.word = word;
        this.type = type;
        this.nextType = nextType;
        this.start = start;
        this.end = end;
        this.matchType=matchType;
    }
}
