package org.xiyou.nlp;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * Description
 * <p>
 * </p>
 * DATE 2020/12/2.
 *
 * @author houchenggong.
 * AC自动机中每个词的类型和下一个词的类型
 */
@Data
@AllArgsConstructor
public class ACTemplateChild {

    /**
     * 当前词类型
     */
    private String type;


    /**
     * 当前词的下一个词的类型，可以有多种
     */
    private List<String> nextType;

    /**
     * 当前词，可以是一类词，也就是批量加入
     */
    List<String> wordList;


}
