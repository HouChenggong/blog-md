package org.xiyou.nlp;

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
public class ACTemplateResult {

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

    /**
     * 匹配到的标准模版
     */
    private String standardTemplate;

    /**
     * 是否成功匹配，因为有的时候匹配的模版不正确
     */
    private Boolean match;
}
