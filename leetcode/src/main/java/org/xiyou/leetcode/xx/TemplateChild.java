package org.xiyou.leetcode.xx;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
@NoArgsConstructor
public class TemplateChild {

    /**
     * 当前词类型
     */
    private String sloTtype;



    /**
     * 当前词，可以是一类词，也就是批量加入
     */
    List<String> wordList;


    private String templateName;

}
