package org.nlp;

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
public class ACTree {
    private String word;
    private Boolean end;
    private ACTree fail;
    private Map<String, ACTree> next;

    public ACTree() {
        next = new HashMap<>();
        end = false;
        word = "";
    }
    @Override
    public String toString() {
        return word;
    }
}
