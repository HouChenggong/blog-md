package org.xiyou.leetcode.xx;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Description
 * <p>
 * </p>
 * DATE 2020/12/2.
 *
 * @author houchenggong.
 */
@Data
@AllArgsConstructor
public class TemplateResult {

    /**
     * 输入的词
     */
    private String q;

    /**
     * 匹配到的词
     */
    private String matchQ;


    /**
     * 匹配到的模版
     */
    private String matchTemplate;

}
