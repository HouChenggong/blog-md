package org.xiyou.leetcode.xx;

import lombok.Data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * 描述用途
 * <p>
 * </p>
 * DATE 2020/2/21.
 *
 * @author zhangjunbo.
 */
@Data
public class MachineTemplateNode {
    /**
     * 词
     */
    private String word;

    /**
     * 当前词的类型，只有当词是结束的时候才会有slotType
     */
    private HashSet<String> slotTypeSet;


    /**
     * 当前词的下一个词
     */
    private Map<String, MachineTemplateNode> next;


    /**
     * 是否是一个词结束
     */
    private Boolean end;

    /**
     * 是否是模版节点
     */
    private Boolean templateNode;
    /**
     * 不同模版的名称，比如《北京》是模版节点，它下面有两个模版《city_weather》《city_traffic》
     */
    private HashSet<String> templateNameSet;


    public MachineTemplateNode() {
        next = new HashMap<>();
        end = false;
        slotTypeSet = new HashSet<>();

    }


}
