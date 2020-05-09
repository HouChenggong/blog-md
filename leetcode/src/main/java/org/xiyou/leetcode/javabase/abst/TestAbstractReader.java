package org.xiyou.leetcode.javabase.abst;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author xiyou
 * @version 1.0
 * xiyou-todo 测试抽象类功能
 * @date 2020/5/9 9:38
 */
public class TestAbstractReader {
    public static void main(String[] args) throws URISyntaxException, IOException {
        URL location = TestAbstractReader.class.getClassLoader().getResource("helloworld.txt");
        Path path = Paths.get(location.toURI());
        BaseFileReader lowercaseFileReader = new LowercaseFileReader(path);
        BaseFileReader uppercaseFileReader = new UppercaseFileReader(path);
        System.out.println(lowercaseFileReader.readFile());
        System.out.println(uppercaseFileReader.readFile());
    }
}
