## NLP 学习记录

- Standfordmlp学习参考地址：[李理的博客](https://fancyerii.github.io/books/stanfordnlp/)
- Github地址：[李理的博客Java代码地址](https://github.com/fancyerii/blog-codes/tree/master/stanfordnlp)
- Nlp download地址：https://stanfordnlp.github.io/CoreNLP/history.html
- NLP jar 包下载地址。https://repo1.maven.org/maven2/edu/stanford/nlp/stanford-corenlp/3.9.2/
- [NLP demo](https://stanfordnlp.github.io/CoreNLP/demo.html)
- [NLP pipeline](https://stanfordnlp.github.io/CoreNLP/pipeline.html)
- 

### Java maven环境

```java
<project xmlns="http://maven.apache.org/POM/4.0.0"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<groupId>com.fancyerii.blog</groupId>
	<artifactId>stanfordnlp</artifactId>
	<packaging>jar</packaging>
	<version>1.0-SNAPSHOT</version>
	<name>stanfordnlp</name>
	<url>http://maven.apache.org</url>
	<properties>
		<project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
		<java.version>1.8</java.version>
	</properties>
	<dependencies>
		<dependency>
			<groupId>ch.qos.logback</groupId>
			<artifactId>logback-classic</artifactId>
			<version>1.2.3</version>
		</dependency>
		<dependency>
			<groupId>junit</groupId>
			<artifactId>junit</artifactId>
			<version>4.11</version>
			<scope>test</scope>
		</dependency>
		<dependency>
			<groupId>edu.stanford.nlp</groupId>
			<artifactId>stanford-corenlp</artifactId>
			<version>3.9.2</version>
		</dependency>
		<dependency>
			<groupId>edu.stanford.nlp</groupId>
			<artifactId>stanford-corenlp</artifactId>
			<version>3.9.2</version>
			<classifier>models</classifier>
		</dependency>
		<dependency>
			<groupId>edu.stanford.nlp</groupId>
			<artifactId>stanford-corenlp</artifactId>
			<version>3.9.2</version>
			<classifier>models-chinese</classifier>
		</dependency>
		<dependency>
			<groupId>commons-io</groupId>
			<artifactId>commons-io</artifactId>
			<version>2.6</version>
		</dependency>

	</dependencies>
	<build>
		<plugins>
			<plugin>
				<groupId>org.apache.maven.plugins</groupId>
				<artifactId>maven-compiler-plugin</artifactId>
				<configuration>
					<source>${java.version}</source>
					<target>${java.version}</target>
					<showDeprecation>true</showDeprecation>
					<showWarnings>true</showWarnings>
					<executable>${env.JAVA_HOME}/bin/javac</executable>
					<fork>true</fork>
				</configuration>
			</plugin>
			<plugin>
				<artifactId>maven-assembly-plugin</artifactId>
				<configuration>
					<descriptorRefs>
						<descriptorRef>jar-with-dependencies</descriptorRef>
					</descriptorRefs>
				</configuration>
			</plugin>
		</plugins>
	</build>
</project>

```

### standfordnlp

### 常见名词

##### Annotation注释

Annotation可以看成一个Map，Value是一个String，表示输入的文本

##### Annotator注释器

##### tokenize

##### ssplit 分句

##### pos词性标注

##### ner用于实现命名实体识别,比如地名、时间年月日等词

##### egexNER  

我们还可以使用[RegexNER](https://nlp.stanford.edu/software/regexner.html)来自己定义识别实体的规则。RegexNer类似与正则表达式，但是它是基于Token(词)而不是字符串的(因此不太适合中文)。

##### Parser 实现成分句法分析。

##### depparse 用于实现依存句法分析

##### coref 用于实现指代消解

在一个文本中的可能有多个实体表示的是物理实际中的相同实体，我们需要找出这种关系来。

比如文本”Barack Obama was born in Hawaii. He is the president. Obama was elected in 2008.”，实体**Barack Obama**和**He**以及**Obama**都是指代通一个人。

##### sentiment用于情感分析——ZH不支持



#### 英文分词

- [参考地址](https://fancyerii.github.io/books/stanfordnlp/)

```java
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.util.*;

import java.util.*;

public class PipelineDemo {

    public static void main(String[] args) {
        //代码首先构造Properties对象，
        Properties props = new Properties();

        // 下面的代码是有问题的。
        // props.setProperty("annotators", "ssplit,tokenize");
        //然后设置使用的annotators为”tokenize,ssplit”，表示只分词和分句。
        props.setProperty("annotators", "tokenize,ssplit");

        // 构造Pipeline
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        // 构造一个CoreDocument对象，传入要分析的文本
        CoreDocument exampleDocument = new CoreDocument("Here is the text to tokenize.");
        // annotate document 然后使用pipeline.annotate方法就可以了
        pipeline.annotate(exampleDocument);
        // exampleDocument.sentences()可以拿到所有的句子，我们这里只有一个句子，因此可以再用get(0)拿到第一个句子的处理结果
        List<CoreLabel> firstSentenceTokens = exampleDocument.sentences().get(0).tokens();
        // 一个句子又有很多Token，因此调用tokens()方法拿到多个Token(List)。
        for (CoreLabel token : firstSentenceTokens) {
            //我们可以从中拿到词(token.word())、开始下标(token.beginPosition())和结束下标(token.endPosition())。
            System.out.println(token.word() + "\t" + token.beginPosition() + "\t" + token.endPosition());
        }

    }
}
```

##### ssplit

ssplit对应的是WordsToSentenceAnnotator，它实现的是分句的功能。前面我们其实已经用到了分句，我们在下面的代码里指定了我们的PipeLine是先分词后分句：

```java
props.setProperty("annotators", "tokenize,ssplit");
```

如果换成

```java
props.setProperty("annotators", "ssplit,tokenize");
```

则会抛出异常，提示annotator “ssplit” requires annotation “TextAnnotation”。我们知道TextAnnotation是tokenize的结果之一(还有token的位置等其它结果)，因此分句是依赖分词的。

有些读者可能奇怪，为什么分词在分句之前呢？对于英文来说，分句主要依赖一些标点符号，比如”,.!?”等。但是英文有些单词比如”Mr. St.”是包含以英文句号的，如果直接用标点分句会有问题，因此Stanford CoreNLP首先分词，分完词后就可以用标点分句。但是这对中文并不合适，因为英文的分词是基于规则(状态机)，它的时间复杂度是O(n)的。而中文是使用CRFs模型，它在预测是时使用动态规划来解码，其复杂度是𝑂(𝑛2)O(n2)。中文我们更适合先分句在分词，因此对于中文，我们通常先自行分句，然后用Pipeline来处理每一个句子。为了避免CoreNLP再分句，我们可以在Properties里设置ssplit.isOneSentence为True，则它就把输入的文本当成一个句子处理。

- 简单的英文分词

```java
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;

public class TokenizerDemo {

    public static void main(String[] args) throws IOException {
        String paragraph = "My 1st sentence. “Does it work for questions?” My third sentence.";
        Reader reader = new StringReader(paragraph);
        // option #2: By token
        PTBTokenizer<CoreLabel> ptbt = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "");
        while (ptbt.hasNext()) {
            CoreLabel label = ptbt.next();
            System.out.println(label);
        }

    }
}
```



#### 中文分词

[中文分词参考地址](https://blog.csdn.net/macanv/article/details/72993873)

#### POS 词性标注

```java

import java.io.InputStream;
import java.io.StringReader;
import java.util.List;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.SentenceUtils;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.tagger.maxent.MaxentTagger; 

public class TaggerDemo {

	private TaggerDemo() {
	}

	public static void main(String[] args) throws Exception { 
		InputStream input = TaggerDemo.class.getResourceAsStream("/"+MaxentTagger.DEFAULT_JAR_PATH);
 
		MaxentTagger tagger = new MaxentTagger(input);
		
		List<List<HasWord>> sentences = MaxentTagger.tokenizeText(new StringReader("Karma of humans is AI"));

		for (List<HasWord> sentence : sentences) {

			List<TaggedWord> tSentence = tagger.tagSentence(sentence);

			System.out.println(SentenceUtils.listToString(tSentence, false));

		}

	}

}
//Karma of humans is AI 结果是：
//Karma/NN of/IN humans/NNS is/VBZ AI/NNP
```

##### NLTK 词性标注

感兴趣的可以搜索下NLTK 词性标注，其实就是标示出一个词是：名称、动词、形容词、副词、感叹词等

[NLTK一些常见的分类](https://www.cnblogs.com/elpsycongroo/p/9369111.html)

- NN 常用名词 单数形式

- NNP专有名词

- IN 介词或从属连词

- NNS 常用名称，复数形式

- VBZ 动词第三人称单数

  

#### parser成分语法分析

```java

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.trees.*;

import java.util.*;

public class ConstituentExample {

	public static void main(String[] args) {
		// set up pipeline properties
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,parse");
		// use faster shift reduce parser
		//props.setProperty("parse.model", "edu/stanford/nlp/models/srparser/englishSR.ser.gz");
		props.setProperty("parse.maxlen", "100");
		// set up Stanford CoreNLP pipeline
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		// build annotation for a review
		Annotation annotation = new Annotation("The small red car turned very quickly around the corner.");
		// annotate
		pipeline.annotate(annotation);
		// get tree
		Tree tree = annotation.get(CoreAnnotations.SentencesAnnotation.class).get(0)
				.get(TreeCoreAnnotations.TreeAnnotation.class);
		System.out.println(tree);
    //接着使用”Set treeConstituents = tree.constituents(new LabeledScoredConstituentFactory());
    //"来得到句法树的每一个成分，然后寻找VP和NP。
		Set<Constituent> treeConstituents = tree.constituents(new LabeledScoredConstituentFactory());
		for (Constituent constituent : treeConstituents) {
			if (constituent.label() != null
					&& (constituent.label().toString().equals("VP") || constituent.label().toString().equals("NP"))) {
				System.err.println("found constituent: " + constituent.toString());
				System.err.println(tree.getLeaves().subList(constituent.start(), constituent.end() + 1));
			}
		}
	}
}
```

- 输出的结果是：可以参考这个地址的图[tree图](http://fancyerii.github.io/books/stanfordnlp/#java-client)

```java
(ROOT (S (NP (DT The) (JJ small) (JJ red) (NN car)) (VP (VBD turned) (ADVP (RB very) (RB quickly)) (PP (IN around) (NP (DT the) (NN corner)))) (. .)))
found constituent: NP(0,3)
[The, small, red, car]
found constituent: NP(8,9)
[the, corner]
found constituent: VP(4,9)
[turned, very, quickly, around, the, corner]
```

#### 获取英文中的名词

```java

import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.trees.EnglishGrammaticalStructure;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TypedDependency;

import java.util.Collection;

// 利用stanford parse进行处理，获取名词集合
public class NounAnalyzer {

    private static String model = "edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz";
    
    public String analyze(String temple, String model) {
        LexicalizedParser parser = LexicalizedParser.loadModel(model);
        // 利用parser获取词性并输出temple中的名词
        // 用逗号分割
        StringBuffer sb = new StringBuffer();

        Tree t = parser.parse(temple);
        //英语语法结构
        EnglishGrammaticalStructure es = new EnglishGrammaticalStructure(t);
        //依存关系,获取td关系组合
        Collection<TypedDependency> tdl = es.typedDependencies();
        for (int i = 0; i < tdl.size(); i++) {
            TypedDependency td = (TypedDependency) tdl.toArray()[i];
            String word = td.dep().word();
            String tag = td.dep().tag();
            if (tag.contains("NN")) {
                //名词
                sb.append(word).append(",");
            }
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        NounAnalyzer analyzer = new NounAnalyzer();
        String msg = "this is a apple and its name is xiaopingguo";
        String result = analyzer.analyze(msg, model);
        System.out.println(result);
    }
}
//apple,name,xiaopingguo,
```



#### 将文本变成多个句子

```java
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.process.DocumentPreprocessor;

public class TokenizerDemo {

    public static void main(String[] args) throws IOException {
        String paragraph = "My 1st sentence. “Does it work for questions?” My third sentence.";
        Reader reader = new StringReader(paragraph);
        Reader reader2 = new StringReader(paragraph);

        // option #1: By sentence.
        DocumentPreprocessor dp = new DocumentPreprocessor(reader);
        for (List<HasWord> sentence : dp) {
            System.out.println(sentence);
        }
    }

}
```

- 结果是：

```java
[My, 1st, sentence, .]
[``, Does, it, work, for, questions, ?, '']
[My, third, sentence, .]
```

#### lexparser

```java

import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.trees.EnglishGrammaticalStructure;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TypedDependency;
import edu.stanford.nlp.trees.international.pennchinese.ChineseGrammaticalStructure;

import java.util.Collection;

// 利用stanford parse进行处理，获取名词集合
public class NounAnalyzer {


    public String analyEnglish(String content) {
        String model = "edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz";
        LexicalizedParser parser = LexicalizedParser.loadModel(model);
        // 利用parser获取词性并输出temple中的名词
        // 用逗号分割
        StringBuffer sb = new StringBuffer();
        Tree t = parser.parse(content);
        //英语语法结构
        EnglishGrammaticalStructure es = new EnglishGrammaticalStructure(t);
        //依存关系,获取td关系组合
        Collection<TypedDependency> tdl = es.typedDependencies();
        System.out.println(tdl.toString());
        for (int i = 0; i < tdl.size(); i++) {
            TypedDependency td = (TypedDependency) tdl.toArray()[i];
            String word = td.dep().word();
            String tag = td.dep().tag();
            if (tag.contains("NN")) {
                //名词
                sb.append(word).append(",");
            }
        }
        return sb.toString();
    }

    public String analyChinese(String content) {
        String model = "edu/stanford/nlp/models/lexparser/chinesePCFG.ser.gz";
        LexicalizedParser parser = LexicalizedParser.loadModel(model);
        Tree t = parser.parse(content);
        ChineseGrammaticalStructure gs = new ChineseGrammaticalStructure(t);
        Collection<TypedDependency> tdl = gs.typedDependenciesCCprocessed();
        return tdl.toString();
    }

    public static void main(String[] args) {
        NounAnalyzer analyzer = new NounAnalyzer();
        String msg = "this is a apple and its name is xiaopingguo";
        String result = analyzer.analyEnglish(msg);
        System.out.println(result);
        // 已经分好词了
        String msgZh = "统计 还 显示 ， 台商 投资 祖国 大陆 正 趋向 大型化 。";
        String resultZh = analyzer.analyChinese(msgZh);
        System.out.println(resultZh);
    }
}


```

- 上诉的结果

```java
[nsubj(is-2, this-1), root(ROOT-0, is-2), det(apple-4, a-3), nsubj(xiaopingguo-9, apple-4), cc(apple-4, and-5), poss(name-7, its-6), conj(apple-4, name-7), cop(xiaopingguo-9, is-8), ccomp(is-2, xiaopingguo-9)]
apple,name,xiaopingguo,
2020-11-20 19:36:24.562 [main] INFO  e.s.n.p.lexparser.LexicalizedParser - Loading parser from serialized file edu/stanford/nlp/models/lexparser/chinesePCFG.ser.gz ... done [0.8 sec].
[nsubj(显示-3, 统计-1), advmod(显示-3, 还-2), root(ROOT-0, 显示-3), nn(大陆-8, 台商-5), nn(大陆-8, 投资-6), nn(大陆-8, 祖国-7), nsubj(趋向-10, 大陆-8), advmod(趋向-10, 正-9), conj(显示-3, 趋向-10), dobj(趋向-10, 大型化-11)]

```



#### 中文命名体识别

```java

import java.util.List;
import java.util.Map;
import java.util.Properties;

import edu.stanford.nlp.coref.CorefCoreAnnotations;
import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;

/**
 * Created by sonofelice on 2018/3/27.
 * 进行中文命名实体识别(NER)
 */
public class TestNLP {
    public void test() throws Exception {
        //构造一个StanfordCoreNLP对象，配置NLP的功能，如lemma是词干化，ner是命名实体识别等
        Properties props = new Properties();
        props.load(this.getClass().getResourceAsStream("/StanfordCoreNLP-chinese.properties"));
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        String text = "袁隆平是中国科学院的院士,他于2009年10月到中国山东省东营市东营区永乐机场附近承包了一千亩盐碱地,"
                + "开始种植棉花, 年产量达到一万吨, 哈哈, 反正棣琦说的是假的,逗你玩儿,明天下午2点来我家吃饭吧。"
                + "棣琦是山东大学毕业的,目前在百度做java开发,位置是东北旺东路102号院,手机号14366778890";

        long startTime = System.currentTimeMillis();
        System.out.println(".............开始："+startTime);
        // 创造一个空的Annotation对象
        Annotation document = new Annotation(text);

        // 对文本进行分析
        pipeline.annotate(document);
        System.out.println(".............开始分析：");
        //获取文本处理结果
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
        for (CoreMap sentence : sentences) {
            // traversing the words in the current sentence
            // a CoreLabel is a CoreMap with additional token-specific methods
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                //                // 获取句子的token（可以是作为分词后的词语）
                String word = token.get(CoreAnnotations.TextAnnotation.class);
                System.out.println(word);
                //词性标注
                String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
                System.out.println(pos);
                // 命名实体识别
                String ne = token.get(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class);
                String ner = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
                System.out.println(word + " | analysis : {  original : " + ner + "," + " normalized : "
                        + ne + "}");
                //词干化处理
                String lema = token.get(CoreAnnotations.LemmaAnnotation.class);
                System.out.println(lema);
            }

            // 句子的解析树
            Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
            System.out.println("句子的解析树:");
            tree.pennPrint();

            // 句子的依赖图
            SemanticGraph graph =
                    sentence.get(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class);
            System.out.println("句子的依赖图");
            System.out.println(graph.toString(SemanticGraph.OutputFormat.LIST));

        }

        long endTime = System.currentTimeMillis();
        long time = endTime - startTime;
        System.out.println("The analysis lasts " + time + " seconds * 1000");

        // 指代词链
        //每条链保存指代的集合
        // 句子和偏移量都从1开始
        Map<Integer, CorefChain> corefChains = document.get(CorefCoreAnnotations.CorefChainAnnotation.class);
        if (corefChains == null) {
            return;
        }
        for (Map.Entry<Integer, CorefChain> entry : corefChains.entrySet()) {
            System.out.println("Chain " + entry.getKey() + " ");
            for (CorefChain.CorefMention m : entry.getValue().getMentionsInTextualOrder()) {
                // We need to subtract one since the indices count from 1 but the Lists start from 0
                List<CoreLabel> tokens = sentences.get(m.sentNum - 1).get(CoreAnnotations.TokensAnnotation.class);
                // We subtract two for end: one for 0-based indexing, and one because we want last token of mention
                // not one following.
                System.out.println(
                        "  " + m + ", i.e., 0-based character offsets [" + tokens.get(m.startIndex - 1).beginPosition()
                                +
                                ", " + tokens.get(m.endIndex - 2).endPosition() + ")");
            }
        }
    }

    public static void main(String[] args) throws Exception {
        TestNLP nlp = new TestNLP();
        nlp.test();
    }
}
```

- 需要在springboot. resource 目录下添加的文件

  StanfordCoreNLP-chinese.properties

```properties
# Pipeline options - lemma is no-op for Chinese but currently needed because coref demands it (bad old requirements system)
annotators = tokenize, ssplit, pos, lemma, ner, parse, coref

# segment
tokenize.language = zh
segment.model = edu/stanford/nlp/models/segmenter/chinese/ctb.gz
segment.sighanCorporaDict = edu/stanford/nlp/models/segmenter/chinese
segment.serDictionary = edu/stanford/nlp/models/segmenter/chinese/dict-chris6.ser.gz
segment.sighanPostProcessing = true

# sentence split
ssplit.boundaryTokenRegex = [.。]|[!?！？]+

# pos
pos.model = edu/stanford/nlp/models/pos-tagger/chinese-distsim/chinese-distsim.tagger

# ner 此处设定了ner使用的语言、模型（crf），目前SUTime只支持英文，不支持中文，所以设置为false。
ner.language = chinese
ner.model = edu/stanford/nlp/models/ner/chinese.misc.distsim.crf.ser.gz
ner.applyNumericClassifiers = true
ner.useSUTime = false

# regexner
ner.fine.regexner.mapping = edu/stanford/nlp/models/kbp/chinese/gazetteers/cn_regexner_mapping.tab
ner.fine.regexner.noDefaultOverwriteLabels = CITY,COUNTRY,STATE_OR_PROVINCE

# parse
parse.model = edu/stanford/nlp/models/srparser/chineseSR.ser.gz

# depparse
depparse.model    = edu/stanford/nlp/models/parser/nndep/UD_Chinese.gz
depparse.language = chinese

# coref
coref.sieves = ChineseHeadMatch, ExactStringMatch, PreciseConstructs, StrictHeadMatch1, StrictHeadMatch2, StrictHeadMatch3, StrictHeadMatch4, PronounMatch
coref.input.type = raw
coref.postprocessing = true
coref.calculateFeatureImportance = false
coref.useConstituencyTree = true
coref.useSemantics = false
coref.algorithm = hybrid
coref.path.word2vec =
coref.language = zh
coref.defaultPronounAgreement = true
coref.zh.dict = edu/stanford/nlp/models/dcoref/zh-attributes.txt.gz
coref.print.md.log = false
coref.md.type = RULE
coref.md.liberalChineseMD = false

# kbp
kbp.semgrex = edu/stanford/nlp/models/kbp/chinese/semgrex
kbp.tokensregex = edu/stanford/nlp/models/kbp/chinese/tokensregex
kbp.language = zh
kbp.model = none

# entitylink
entitylink.wikidict = edu/stanford/nlp/models/kbp/chinese/wikidict_chinese.tsv.gz
```

```java
 
.............开始：1605864807982
2020-11-20 17:33:28.013 [main] INFO  e.s.nlp.wordseg.ChineseDictionary - Loading Chinese dictionaries from 1 file:
2020-11-20 17:33:28.014 [main] INFO  e.s.nlp.wordseg.ChineseDictionary -   edu/stanford/nlp/models/segmenter/chinese/dict-chris6.ser.gz
2020-11-20 17:33:28.306 [main] INFO  e.s.nlp.wordseg.ChineseDictionary - Done. Unique words in ChineseDictionary is: 423200.
2020-11-20 17:33:28.428 [main] INFO  edu.stanford.nlp.wordseg.CorpusChar - Loading character dictionary file from edu/stanford/nlp/models/segmenter/chinese/dict/character_list [done].
2020-11-20 17:33:28.430 [main] INFO  e.s.nlp.wordseg.AffixDictionary - Loading affix dictionary from edu/stanford/nlp/models/segmenter/chinese/dict/in.ctb [done].
.............开始分析：
袁隆平
NR
袁隆平 | analysis : {  original : PERSON, normalized : null}
袁隆平
是
VC
是 | analysis : {  original : O, normalized : null}
是
中国
NR
中国 | analysis : {  original : ORGANIZATION, normalized : null}
中国
科学院
NN
科学院 | analysis : {  original : ORGANIZATION, normalized : null}
科学院
的
DEG
的 | analysis : {  original : O, normalized : null}
的
院士
NN
院士 | analysis : {  original : TITLE, normalized : null}
院士
,
PU
, | analysis : {  original : O, normalized : null}
,
他
PN
他 | analysis : {  original : O, normalized : null}
他
于
P
于 | analysis : {  original : O, normalized : null}
于
2009年
NT
2009年 | analysis : {  original : DATE, normalized : 2009-10-XX}
2009年
10月
NT
10月 | analysis : {  original : DATE, normalized : 2009-10-XX}
10月
到
CC
到 | analysis : {  original : O, normalized : null}
到
中国
NR
中国 | analysis : {  original : COUNTRY, normalized : null}
中国
山东省
NR
山东省 | analysis : {  original : STATE_OR_PROVINCE, normalized : null}
山东省
东营市
NR
东营市 | analysis : {  original : CITY, normalized : null}
东营市
东营区
NR
东营区 | analysis : {  original : FACILITY, normalized : null}
东营区
永乐
NR
永乐 | analysis : {  original : FACILITY, normalized : null}
永乐
机场
NN
机场 | analysis : {  original : FACILITY, normalized : null}
机场
附近
LC
附近 | analysis : {  original : O, normalized : null}
附近
承包
VV
承包 | analysis : {  original : O, normalized : null}
承包
了
AS
了 | analysis : {  original : O, normalized : null}
了
一千
CD
一千 | analysis : {  original : NUMBER, normalized : 1000}
一千
亩
M
亩 | analysis : {  original : O, normalized : null}
亩
盐
NN
盐 | analysis : {  original : O, normalized : null}
盐
碱地
NN
碱地 | analysis : {  original : O, normalized : null}
碱地
,
PU
, | analysis : {  original : O, normalized : null}
,
开始
VV
开始 | analysis : {  original : O, normalized : null}
开始
种植
VV
种植 | analysis : {  original : O, normalized : null}
种植
棉花
NN
棉花 | analysis : {  original : O, normalized : null}
棉花
,
PU
, | analysis : {  original : O, normalized : null}
,
年产量
NN
年产量 | analysis : {  original : O, normalized : null}
年产量
达到
VV
达到 | analysis : {  original : O, normalized : null}
达到
一万
CD
一万 | analysis : {  original : NUMBER, normalized : 10000}
一万
吨
M
吨 | analysis : {  original : O, normalized : null}
吨
,
PU
, | analysis : {  original : O, normalized : null}
,
哈哈
IJ
哈哈 | analysis : {  original : O, normalized : null}
哈哈
,
PU
, | analysis : {  original : O, normalized : null}
,
反正
AD
反正 | analysis : {  original : O, normalized : null}
反正
棣琦
NR
棣琦 | analysis : {  original : PERSON, normalized : null}
棣琦
说
VV
说 | analysis : {  original : O, normalized : null}
说
的
DEC
的 | analysis : {  original : O, normalized : null}
的
是
VC
是 | analysis : {  original : O, normalized : null}
是
假
JJ
假 | analysis : {  original : O, normalized : null}
假
的
DEG
的 | analysis : {  original : O, normalized : null}
的
,
PU
, | analysis : {  original : O, normalized : null}
,
逗
VV
逗 | analysis : {  original : O, normalized : null}
逗
你
PN
你 | analysis : {  original : O, normalized : null}
你
玩儿
VV
玩儿 | analysis : {  original : O, normalized : null}
玩儿
,
PU
, | analysis : {  original : O, normalized : null}
,
明天
NT
明天 | analysis : {  original : DATE, normalized : XXXX-XX-XX}
明天
下午
NT
下午 | analysis : {  original : TIME, normalized : null}
下午
2点
NT
2点 | analysis : {  original : TIME, normalized : null}
2点
来
VV
来 | analysis : {  original : O, normalized : null}
来
我
PN
我 | analysis : {  original : O, normalized : null}
我
家
NN
家 | analysis : {  original : O, normalized : null}
家
吃饭
VV
吃饭 | analysis : {  original : O, normalized : null}
吃饭
吧
SP
吧 | analysis : {  original : O, normalized : null}
吧
。
PU
。 | analysis : {  original : O, normalized : null}
。
棣琦
NR
棣琦 | analysis : {  original : PERSON, normalized : null}
棣琦
是
VC
是 | analysis : {  original : O, normalized : null}
是
山东
NR
山东 | analysis : {  original : ORGANIZATION, normalized : null}
山东
大学
NN
大学 | analysis : {  original : ORGANIZATION, normalized : null}
大学
毕业
VV
毕业 | analysis : {  original : O, normalized : null}
毕业
的
DEC
的 | analysis : {  original : O, normalized : null}
的
,
PU
, | analysis : {  original : O, normalized : null}
,
目前
NT
目前 | analysis : {  original : DATE, normalized : null}
目前
在
P
在 | analysis : {  original : O, normalized : null}
在
百度
NR
百度 | analysis : {  original : ORGANIZATION, normalized : null}
百度
做
VV
做 | analysis : {  original : O, normalized : null}
做
java
NN
java | analysis : {  original : O, normalized : null}
java
开发
NN
开发 | analysis : {  original : O, normalized : null}
开发
,
PU
, | analysis : {  original : O, normalized : null}
,
位置
NN
位置 | analysis : {  original : O, normalized : null}
位置
是
VC
是 | analysis : {  original : O, normalized : null}
是
东北
NN
东北 | analysis : {  original : LOCATION, normalized : null}
东北
旺
VV
旺 | analysis : {  original : O, normalized : null}
旺
东路
NR
东路 | analysis : {  original : O, normalized : null}
东路
102
CD
102 | analysis : {  original : NUMBER, normalized : 102}
102
号院
NN
号院 | analysis : {  original : O, normalized : null}
号院
,
PU
, | analysis : {  original : O, normalized : null}
,
手机号
NN
手机号 | analysis : {  original : O, normalized : null}
手机号
143667788
CD
143667788 | analysis : {  original : NUMBER, normalized : 14366778890}
143667788
90
CD
90 | analysis : {  original : NUMBER, normalized : 14366778890}
90
句子的解析树:
(ROOT
  (NP
    (NP (NR 袁隆平))
    (PRN
      (IP
        (VP (VC 是)
          (NP
            (DNP
              (NP (NR 中国) (NN 科学院))
              (DEG 的))
            (NP (NN 院士))))
        (PU ,)
        (IP
          (NP (PN 他))
          (VP
            (PP (P 于)
              (LCP
                (NP (NT 2009年) (NT 10月)
                  (CC 到)
                  (NP (NR 中国) (NR 山东省) (NR 东营市) (NR 东营区) (NR 永乐) (NN 机场)))
                (LC 附近)))
            (VP (VV 承包) (AS 了)
              (IP
                (NP
                  (QP (CD 一千)
                    (CLP (M 亩)))
                  (NP (NN 盐) (NN 碱地)))
                (PU ,)
                (VP (VV 开始)
                  (VP (VV 种植)
                    (IP
                      (IP
                        (NP (NN 棉花))
                        (PU ,)
                        (IP
                          (NP (NN 年产量))
                          (VP (VV 达到)
                            (QP (CD 一万)
                              (CLP (M 吨)))))
                        (PU ,)
                        (FLR (IJ 哈哈))
                        (PU ,)
                        (IP
                          (NP
                            (CP
                              (IP
                                (ADVP (AD 反正))
                                (NP (NR 棣琦))
                                (VP (VV 说)))
                              (DEC 的)))
                          (VP (VC 是)
                            (NP
                              (DNP
                                (ADJP (JJ 假))
                                (DEG 的)))))
                        (PU ,)
                        (IP
                          (VP (VV 逗)
                            (NP (PN 你))
                            (IP
                              (VP (VV 玩儿)))))
                        (PU ,)
                        (CP
                          (IP
                            (NP (NT 明天) (NT 下午) (NT 2点))
                            (VP
                              (VP (VV 来)
                                (NP (PN 我) (NN 家)))
                              (VP (VV 吃饭))))
                          (SP 吧))
                        (PU 。))
                      (NP (NR 棣琦))
                      (VP (VC 是)
                        (NP
                          (CP
                            (IP
                              (VP
                                (NP (NR 山东) (NN 大学))
                                (VP (VV 毕业))))
                            (DEC 的))
                          (PU ,)
                          (PRN
                            (IP
                              (IP
                                (VP
                                  (NP (NT 目前))
                                  (PP (P 在)
                                    (NP (NR 百度)))
                                  (VP (VV 做)
                                    (NP (NN java) (NN 开发)))))
                              (PU ,)
                              (NP (NN 位置))
                              (VP (VC 是)
                                (IP
                                  (NP (NN 东北))
                                  (VP (VV 旺)
                                    (NP
                                      (NP (NR 东路))
                                      (QP (CD 102))
                                      (NP (NN 号院)))))))))))))))))
        (PU ,)
        (IP
          (IP
            (NP (NN 手机号))
            (VP
              (QP (CD 143667788))))
          (QP (CD 90)))))))
句子的依赖图
root(ROOT-0, 袁隆平-1)
cop(院士-6, 是-2)
compound:nn(科学院-4, 中国-3)
nmod:assmod(院士-6, 科学院-4)
case(科学院-4, 的-5)
parataxis:prnmod(袁隆平-1, 院士-6)
punct(院士-6, ,-7)
nsubj(承包-20, 他-8)
case(10月-11, 于-9)
compound:nn(10月-11, 2009年-10)
nmod:prep(承包-20, 10月-11)
cc(10月-11, 到-12)
compound:nn(机场-18, 中国-13)
compound:nn(机场-18, 山东省-14)
compound:nn(机场-18, 东营市-15)
compound:nn(机场-18, 东营区-16)
compound:nn(机场-18, 永乐-17)
dep(10月-11, 机场-18)
case(10月-11, 附近-19)
conj(院士-6, 承包-20)
aux:asp(承包-20, 了-21)
nummod(碱地-25, 一千-22)
mark:clf(一千-22, 亩-23)
compound:nn(碱地-25, 盐-24)
nsubj(种植-28, 碱地-25)
punct(种植-28, ,-26)
xcomp(种植-28, 开始-27)
ccomp(承包-20, 种植-28)
nsubj(达到-32, 棉花-29)
punct(达到-32, ,-30)
nsubj(达到-32, 年产量-31)
conj(毕业-63, 达到-32)
nmod:range(达到-32, 一万-33)
mark:clf(一万-33, 吨-34)
punct(达到-32, ,-35)
dep(达到-32, 哈哈-36)
punct(达到-32, ,-37)
advmod(说-40, 反正-38)
nsubj(说-40, 棣琦-39)
nsubj(假-43, 说-40)
mark(说-40, 的-41)
cop(假-43, 是-42)
conj(达到-32, 假-43)
case(假-43, 的-44)
punct(达到-32, ,-45)
conj(达到-32, 逗-46)
dobj(逗-46, 你-47)
ccomp(逗-46, 玩儿-48)
punct(达到-32, ,-49)
compound:nn(2点-52, 明天-50)
compound:nn(2点-52, 下午-51)
nmod:tmod(来-53, 2点-52)
dep(达到-32, 来-53)
nmod:poss(家-55, 我-54)
dobj(来-53, 家-55)
conj(来-53, 吃饭-56)
discourse(来-53, 吧-57)
punct(达到-32, 。-58)
nsubj(毕业-63, 棣琦-59)
cop(毕业-63, 是-60)
compound:nn(大学-62, 山东-61)
dep(毕业-63, 大学-62)
ccomp(种植-28, 毕业-63)
mark(毕业-63, 的-64)
punct(毕业-63, ,-65)
nmod:tmod(做-69, 目前-66)
case(百度-68, 在-67)
nmod:prep(做-69, 百度-68)
dep(旺-76, 做-69)
compound:nn(开发-71, java-70)
dobj(做-69, 开发-71)
punct(旺-76, ,-72)
nsubj(旺-76, 位置-73)
cop(旺-76, 是-74)
nsubj(旺-76, 东北-75)
parataxis:prnmod(毕业-63, 旺-76)
nmod(号院-79, 东路-77)
dep(号院-79, 102-78)
dobj(旺-76, 号院-79)
punct(院士-6, ,-80)
nsubj(143667788-82, 手机号-81)
conj(院士-6, 143667788-82)
dep(143667788-82, 90-83)

The analysis lasts 1959 seconds * 1000
Chain 5 
  "棣琦" in sentence 1, i.e., 0-based character offsets [75, 77)
  "棣琦" in sentence 1, i.e., 0-based character offsets [101, 103)
Chain 11 
  "中国 科学院 的 院士" in sentence 1, i.e., 0-based character offsets [4, 12)
  "他" in sentence 1, i.e., 0-based character offsets [13, 14)

Process finished with exit code 0

```

