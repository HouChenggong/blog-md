/*
 * <summary></summary>
 * <author>He Han</author>
 * <email>me@hankcs.com</email>
 * <create-date>16/3/14 AM11:49</create-date>
 *
 * <copyright file="DemoCustomNature.java" company="码农场">
 * Copyright (c) 2008-2016, 码农场. All Right Reserved, http://www.hankcs.com/
 * This source is subject to Hankcs. Please contact Hankcs to get more information.
 * </copyright>
 */
package org.xiyou.hanlp;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.corpus.tag.Nature;
import com.hankcs.hanlp.dictionary.CoreDictionary;
import com.hankcs.hanlp.dictionary.CustomDictionary;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.utility.LexiconUtility;

import java.io.File;
import java.util.List;

import static com.hankcs.hanlp.corpus.tag.Nature.*;

/**
 * 演示自定义词性,以及往词典中插入自定义词性的词语
 *
 * @author hankcs
 */
public class DemoCustomNature {


    public static void main(String[] args) {


        String[] pathArray = new String[]{"现代汉语补充词库.txt","全国地名大全.txt"};
        String prePath = "";

        for (int i = 0; i < pathArray.length; ++i) {
            if (pathArray[i].startsWith(" ")) {
                pathArray[i] = prePath + pathArray[i].trim();
            } else {
                pathArray[i] = pathArray[i];
                int lastSplash = pathArray[i].lastIndexOf('/');
                if (lastSplash != -1) {
                    prePath = pathArray[i].substring(0, lastSplash + 1);
                }
            }
        }
    String path="/Users/xiyouyan/vipkid/code/blog-md/leetcode/src/main/resources/data/dictionary/custom/CustomDictionary.txt";
             // /Users/xiyouyan/vipkid/code/cattle-nlp/hanlp/data/dictionary/custom/CustomDictionary.txt
    File f=new File(path);
        System.out.println(f.exists());

        CustomDictionary.loadMainDictionary(path, null, CustomDictionary.dat, true);
        HanLP.Config.enableDebug();

    }
}
