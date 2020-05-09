package org.xiyou.leetcode.javabase.abst;

import java.nio.file.Path;

/**
 * @author xiyou
 * @version 1.0
 * xiyou-todo 小写的实现
 * @date 2020/5/9 9:35
 */
public class LowercaseFileReader extends BaseFileReader {
    /**
     * 读取父类的PATH
     * @param filePath
     */
    protected LowercaseFileReader(Path filePath) {
        super(filePath);
    }

    /**
     * 小写实现
     *
     * @param line
     * @return
     */
    @Override
    protected String mapFileLine(String line) {
        return line.toLowerCase();
    }
}
