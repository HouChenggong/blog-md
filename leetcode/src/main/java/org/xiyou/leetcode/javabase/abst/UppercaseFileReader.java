package org.xiyou.leetcode.javabase.abst;

import java.nio.file.Path;

/**
 * @author xiyou
 * @version 1.0
 * xiyou-todo 大写的实现
 * @date 2020/5/9 9:36
 */
public class UppercaseFileReader extends BaseFileReader {
    /**
     * 读取父类PATH
     * @param filePath
     */
    protected UppercaseFileReader(Path filePath) {
        super(filePath);
    }

    /**
     * 大写的实现
     * @param line
     * @return
     */
    @Override
    protected String mapFileLine(String line) {
        return line.toUpperCase();
    }
}
