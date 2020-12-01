package org.xiyou.nlp;

import lombok.Data;

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
public class ACAutoMachineNode {
    private String word;
    private Boolean end;
    private ACAutoMachineNode fail;
    private Map<String, ACAutoMachineNode> next;

    private HashSet<String> type;

    private HashSet<String> nextType;

    public ACAutoMachineNode() {
        next = new HashMap<>();
        end = false;
        word = "";
        type = new HashSet<>();
        nextType = new HashSet<>();
    }

    @Override
    public String toString() {
        return word;
    }
}
