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
 */
@Data
@AllArgsConstructor
public class ACResult {

    private String word;
    private HashSet<String> type;
    private HashSet<String> nextType;

    private Integer start;

    private Integer end;

    public ACResult(String word, HashSet<String> type, HashSet<String> nextType) {
        this.word = word;
        this.type = type;
        this.nextType = nextType;
    }
}
