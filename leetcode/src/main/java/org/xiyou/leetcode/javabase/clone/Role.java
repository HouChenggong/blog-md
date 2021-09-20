package org.xiyou.leetcode.javabase.clone;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.xiyou.leetcode.leetcode.TreeNode;
import org.xiyou.leetcode.leetcode.bfs.TreeSerial;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author xiyouyan
 * @date 2020-07-07 09:19
 * @description
 */
@Getter
@Setter
public class Role implements Serializable {

    private static final long serialVersionUID = -107683888515578557L;
    private String id;

    private String name;

    public Role(String id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return "Role{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

}
