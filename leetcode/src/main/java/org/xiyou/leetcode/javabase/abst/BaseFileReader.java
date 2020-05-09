package org.xiyou.leetcode.javabase.abst;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xiyou
 * @version 1.0
 * xiyou-todo 用抽象类实现大小写读取的方法
 * @date 2020/5/9 9:34
 */
public abstract class BaseFileReader {
    //filePath 为文件路径，使用 protected 修饰，表明该成员变量可以在需要时被子类访问。
    protected Path filePath;

    /**
     * 构造函数
     *
     * @param filePath
     */
    protected BaseFileReader(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * readFile() 方法用来读取文件，方法体里面调用了抽象方法 mapFileLine()——需要子类扩展实现大小写的方式。
     *
     * @return
     * @throws IOException
     */
    public List<String> readFile() throws IOException {
        return Files.lines(filePath)
                .map(this::mapFileLine).collect(Collectors.toList());
    }

    /**
     * 需要字类扩展的方法
     *
     * @param line
     * @return
     */
    protected abstract String mapFileLine(String line);
}
