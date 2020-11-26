## NLP 学习记录

- Standfordmlp学习参考地址：[李理的博客](https://fancyerii.github.io/books/stanfordnlp/)——**推荐阅读**
- Github地址：[李理的博客Java代码地址](https://github.com/fancyerii/blog-codes/tree/master/stanfordnlp)
- [对话系统NLP](https://kingsea0-0.github.io/categories/NLP/)
- [Nlp download地址](https://stanfordnlp.github.io/CoreNLP/history.html)
- [NLP jar 包下载地址](https://repo1.maven.org/maven2/edu/stanford/nlp/stanford-corenlp/3.9.2/)——maven下载很慢，推荐用这个下载
- [stadforNLP官方 demo](https://stanfordnlp.github.io/CoreNLP/demo.html)
- [NLP 主要工具pipeline](https://stanfordnlp.github.io/CoreNLP/pipeline.html)
- [Stanford在线demo](https://corenlp.run/)
- [NLP parser官网](https://nlp.stanford.edu/software/lex-parser.shtml#Sample)
- [美团NLP相关技术](https://tech.meituan.com/tags/nlp.html)
- [读何晗的自然语言处理——笔记](https://github.com/NLP-LOVE/Introduction-NLP)——**推荐阅读**

### stanfordnlp

#### 主要功能

[词性标注(Part of Speech Tagger](http://nlp.stanford.edu/software/tagger.html)、[命名实体识别(Named Entity Reconizer)](http://nlp.stanford.edu/software/CRF-NER.html)、 [句法分析(parsing)](http://nlp.stanford.edu/software/lex-parser.html)、[指代消解(Coreference Resolution)](http://nlp.stanford.edu/software/dcoref.html)、[情感分析(Sentiment Analysis)](http://nlp.stanford.edu/sentiment/)和[开放关系抽取](https://nlp.stanford.edu/software/openie.html)等等

### 完整的NLP流程

#### 1、tokenize 分词

- Tokenization

#### 2、ssplit 分句

- Sentence Splitting

##### 2.1 英文分词分句demo

```java

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.PTBTokenizer;

/**
 * 将一段文本进行分局或者分词
 */
public class TokenizerDemo {

    public static void main(String[] args) throws IOException {
        String paragraph = "My 1st sentence. “Does it work for questions?” My third sentence.";
        Reader reader = new StringReader(paragraph);
        Reader reader2 = new StringReader(paragraph);

        System.out.println("--------将一段文本变成一句一句的");
        DocumentPreprocessor dp = new DocumentPreprocessor(reader);
        for (List<HasWord> sentence : dp) {
            System.out.println(sentence);
        }
        System.out.println("--------将一段文本分词");
        PTBTokenizer<CoreLabel> ptbt = new PTBTokenizer<>(reader2, new CoreLabelTokenFactory(), "");
        while (ptbt.hasNext()) {
            CoreLabel label = ptbt.next();
            System.out.println(label);
        }

    }
}
/**

 --------将一段文本变成一句一句的
 [My, 1st, sentence, .]
 [``, Does, it, work, for, questions, ?, '']
 [My, third, sentence, .]
 --------将一段文本分词
 My
 1st
 sentence
 .
 ``
 Does
 it
 work
 for
 questions
 ?
 ''
 My
 third
 sentence
 .
 */
```



#### 3、pos词性标注

- Part of Speech Tagger

pos对应POSTaggerAnnotator，它实现词性标注(Part of Speech Tagging)，它使用的是最大熵模型(而不是CRFs)。用于标注名称、形容词、副词、动词等

对于中文使用的是[CTB7的tagset](https://catalog.ldc.upenn.edu/LDC2010T07)。默认使用的模型是edu/stanford/nlp/models/pos-tagger/chinese-distsim/chinese-distsim.tagger，它的准确率是93.99%。详细的CTB7的标注规范可以在[这里](https://catalog.ldc.upenn.edu/docs/LDC2010T07/ctb-posguide.pdf)下载

- 词性标注

```java
//Karma of humans is AI
//结果是：Karma/NN of/IN humans/NNS is/VBZ AI/NNP
```



##### 3.1NLTK 词性标注

感兴趣的可以搜索下NLTK 词性标注，其实就是标示出一个词是：名称、动词、形容词、副词、感叹词等

NLTK一些常见的分类

```json
ROOT :	要处理文本的语句
IP   :	简单从句
NP   :	名词短语
VP   :	动词短语
PU   :	断句符，通常是句号、问号、感叹号等标点符号
LCP  :	方位词短语
PP   :	介词短语
CP   :	由‘的’构成的表示修饰性关系的短语
DNP  :	由‘的’构成的表示所属关系的短语
ADVP :	副词短语
ADJP :	形容词短语
DP   :	限定词短语
QP   :	量词短语
NN   :	常用名词
NT   :	时间名词
PN   :	代词
VV   :	动词
VC   :	是
CC   :	表示连词
VE   :	有
VA   :	表语形容词
VRD  :	动补复合词
CD   :	 表示基数词
DT   :	 determiner 表示限定词
EX   :	 existential there 存在句
FW   :	 foreign word 外来词
IN   :	 preposition or conjunction, subordinating 介词或从属连词
JJ   :	 adjective or numeral, ordinal 形容词或序数词
JJR  :	 adjective, comparative 形容词比较级
JJS  :	 adjective, superlative 形容词最高级
LS   :	 list item marker 列表标识
MD   :	 modal auxiliary 情态助动词
PDT  :	 pre-determiner 前位限定词
POS  :	 genitive marker 所有格标记
PRP  :	 pronoun, personal 人称代词
RB   :	 adverb 副词
RBR  :	 adverb, comparative 副词比较级
RBS  :	 adverb, superlative 副词最高级
RP   :	 particle 小品词
SYM  :	 symbol 符号
TO   :	”to” as preposition or infinitive marker 作为介词或不定式标记
WDT  :	 WH-determiner WH限定词
WP   :	 WH-pronoun WH代词
WP$  :	 WH-pronoun, possessive WH所有格代词
WRB  :	Wh-adverb WH副词
```



#### 4、lemma主干分析

- Morphological Analysis

lemma对应MorphaAnnotator，它的作用是进行词的主干分析，比如把goes还原成go。对于中文来说不需要这个步骤。

#### 5、ner命名实体识别

- Named Entity Recognition

ner对应NERClassifierCombiner，用于实现命名实体识别。默认会使用CRF模型，也可以使用基于规则的算法。如果读者像训练自己的模型，可以参考[这里](https://nlp.stanford.edu/software/crf-faq.html#a)。详细的介绍请参考[这里](https://stanfordnlp.github.io/CoreNLP/ner.html)。

##### 5.1 RegexNER正则命名体识别

除此之外，我们还可以使用[RegexNER](https://nlp.stanford.edu/software/regexner.html)来自己定义识别实体的规则。RegexNer类似与正则表达式，但是它是基于Token(词)而不是字符串的(因此不太适合中文)。

#### 6、parse成分句法分析

- Syntactic Parsing

parse对应的是ParserAnnotator，实现成分句法分析(Constituency Parsing)。有很多的Parsing算法，对应英语来说，CoreNLP默认使用englishPCFG.ser.gz模型，说明它使用的是PCFG算法。除此之外，我们也可以使用速度更快但是准确率稍低一点的[Shift-Reduce Parser](https://nlp.stanford.edu/software/srparser.html)。要切换到SR-Parser，我们只需要配置parse.model为”edu/stanford/nlp/models/srparser/englishSR.ser.gz”就可以了。

英文是使用[Penn Tree Bank](https://catalog.ldc.upenn.edu/docs/LDC99T42/)训练的，而中文是使用[Chinese Tree Bank](https://catalog.ldc.upenn.edu/LDC2010T07)训练的(这些语料库是收费的，因此Stanford Parser值提供训练好的模型，无法提供训练数据)。对于中文来说，有PCFG和Factored两种模型，然后训练语料库包含Xinhua(新华日报)和混合的语料库。因此共有4个模型：xinhuaPCFG.ser.gz、xinhuaFactored.ser.gz、chinesePCFG.ser.gz和chineseFactored.ser.gz。对于大陆的用户来说，推荐使用xinhuaFactored.ser.gz。但CoreNLP中默认使用edu/stanford/nlp/models/srparser/chineseSR.ser.gz，这是速度更快的SR-Parser。更多关于Parser的问题，可以参考[这里](https://nlp.stanford.edu/software/parser-faq.html)。

- 主要作用：自然语言解析器是一种程序，可以计算出**句子**的语法 **结构**，例如，哪些单词组在一起（称为“短语”），哪些单词是动词的**主语**或**宾语**。

##### 举例说明

- 比如对一句话进行分析：《公积金如何申请》会生成一个树的结构

```java
.................句子的解析树如下:
(ROOT
  (IP
    (NP (NN 公积金))
    (VP
      (ADVP (AD 如何))
      (VP (VV 申请)))))
 
```

##### 注意区别：依存语法分析

- 成分语法分析

  - 又称为：句法结构分析

  - 作用：作用是识别出句子中的短语结构以及短语之间的层次句法关系，比如它是形容词或者副词

- 依存语法分析

  - 简称依存分析，作用是识别句子中词汇与词汇之间的相互依存关系。

  - 目的是：分析一个词在一段话中的依赖关系，比如：《公积金》依赖的是《申请》

 #### 7、Dcoref

- Coreference Resolution

##### 7.1 depparse依存语法分析

它对应的是DependencyParseAnnotator，用于实现依存句法分析(Dependency Parsing)。目前默认使用神经网络的模型，基于Shift-Reduce(SR-Parsing)

简称依存分析，作用是识别句子中词汇与词汇之间的相互依存关系。

```java
//比如输入：《公积金如何申请》
.............句子的依存语法分析结果如下：
root(ROOT-0, 申请-3)
nsubj(申请-3, 公积金-1)
advmod(申请-3, 如何-2)
```

##### 7.2依存关系名称说明

```sql
abbrev    :	 abbreviation modifier，缩写
acomp     :	 adjectival complement，形容词的补充；
advcl     :	 adverbial clause modifier，状语从句修饰词
advmod    :	 adverbial modifier状语
agent     :	 agent，代理，一般有by的时候会出现这个
amod      :	 adjectival modifier形容词
appos     :	 appositional modifier,同位词
attr      :	 attributive，属性
aux       :	 auxiliary，非主要动词和助词，如BE,HAVE SHOULD/COULD等到
auxpass   :	 passive auxiliary 被动词
cc        :	 coordination，并列关系，一般取第一个词
ccomp     :	 clausal complement从句补充
complm    :	 complementizer，引导从句的词好重聚中的主要动词
conj      :	 conjunct，连接两个并列的词。
cop       :	 copula。系动词（如be,seem,appear等），（命题主词与谓词间的）连系
csubj     :	 clausal subject，从主关系
csubjpass :	 clausal passive subject 主从被动关系
dep       :	 dependent依赖关系
det       :	 determiner决定词，如冠词等
dobj      :	 direct object直接宾语
expl      :	 expletive，主要是抓取there
infmod    :	 infinitival modifier，动词不定式
iobj      :	 indirect object，非直接宾语，也就是所以的间接宾语；
mark      :	 marker，主要出现在有“that” or “whether”“because”, “when”,
mwe       :	 multi-word expression，多个词的表示
neg       :	 negation modifier否定词
nn        :	 noun compound modifier名词组合形式
npadvmod  :	 noun phrase as adverbial modifier名词作状语
nsubj     :	 nominal subject，名词主语
nsubjpass :	 passive nominal subject，被动的名词主语
num       :	 numeric modifier，数值修饰
number    :	 element of compound number，组合数字
partmod   :	 participial modifier动词形式的修饰
pcomp     :	 prepositional complement，介词补充
pobj      :	 object of a preposition，介词的宾语
poss      :	 possession modifier，所有形式，所有格，所属
possessive:	 possessive modifier，这个表示所有者和那个’S的关系
preconj   :	 preconjunct，常常是出现在 “either”, “both”, “neither”的情况下
predet    :	 predeterminer，前缀决定，常常是表示所有
prep      :	 prepositional modifier
prepc     :	 prepositional clausal modifier
prt       :	 phrasal verb particle，动词短语
punct     :	 punctuation，这个很少见，但是保留下来了，结果当中不会出现这个
purpcl    :	 purpose clause modifier，目的从句
quantmod  :	 quantifier phrase modifier，数量短语
rcmod     :	 relative clause modifier相关关系
ref       :	 referent，指示物，指代
rel       :	 relative
root      :	 root，最重要的词，从它开始，根节点
tmod      :	 temporal modifier
xcomp     :	 open clausal complement
xsubj     :	 controlling subject 掌控者
```



##### 7.2 coref指代消除

coref用于实现指代消解。在一个文本中的可能有多个实体表示的是物理实际中的相同实体，我们需要找出这种关系来。

比如文本”Barack Obama was born in Hawaii. He is the president. Obama was elected in 2008.”，实体**Barack Obama**和**He**以及**Obama**都是指代通一个人。

CoreNLP包含3种算法：

- Deterministic 基于规则的确定的算法，支持英文和中文。
- Statistical 基于统计的方法，只支持英文，而且依赖依存句法分析(depparse)。
- Neural 基于深度学习的算法，支持英文和中文。

##### 7.3 指代消除demo

```java
package com.fancyerii.blog.stanfordnlp;

import java.util.Properties;

import edu.stanford.nlp.coref.CorefCoreAnnotations;
import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.coref.data.Mention;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

/**
 * 指代消除，比如文本出现的Obama 和he其实是一个人
 ---
 coref chains
 CHAIN3-["Barack Obama" in sentence 1, "He" in sentence 2, "Obama" in sentence 3]
 ---
 mentions
 Barack Obama
 Hawaii
 ---
 mentions
 the president
 He
 ---
 mentions
 Obama
 2008
 */
public class CorefExample {
	public static void main(String[] args) throws Exception {
		Annotation document = new Annotation(
				"Barack Obama was born in Hawaii.  He is the president. Obama was elected in 2008.");
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,parse,coref");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		pipeline.annotate(document);
		System.out.println("---");
		System.out.println("coref chains");
		for (CorefChain cc : document.get(CorefCoreAnnotations.CorefChainAnnotation.class).values()) {
			System.out.println("\t" + cc);
		}
		for (CoreMap sentence : document.get(CoreAnnotations.SentencesAnnotation.class)) {
			System.out.println("---");
			System.out.println("mentions");
			for (Mention m : sentence.get(CorefCoreAnnotations.CorefMentionsAnnotation.class)) {
				System.out.println("\t" + m);
			}
		}
	}
}

```



#### 8、sentiment 情感分析

对应SentimentAnnotator，用于情感分类，目前只支持英文。详细算法请参考[这里](https://nlp.stanford.edu/sentiment/),具体情感分为下面5类

```java
"Very negative" = 0
"Negative" = 1 
"Neutral" = 2 
"Positive" = 3
"Very positive" = 4
```



 

#### 一个中文完整的例子

##### java pom

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
		<!-- https://mvnrepository.com/artifact/com.hankcs/hanlp -->
		<dependency>
			<groupId>com.hankcs</groupId>
			<artifactId>hanlp</artifactId>
			<version>portable-1.7.8</version>
		</dependency>
		<!--lombok 依赖引用，需要idea插件支持，可以简化代码get set等基础防范-->
		<dependency>
			<groupId>org.projectlombok</groupId>
			<artifactId>lombok</artifactId>
			<version>1.18.8</version>
			<scope>provided</scope>
		</dependency>

	</dependencies>
	<build>
		<plugins>
			<plugin>
				<groupId>org.apache.maven.plugins</groupId>
				<artifactId>maven-compiler-plugin</artifactId>
				<configuration>
					<source>8</source>
					<target>8</target>
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



##### 代码

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
import lombok.extern.slf4j.Slf4j;

/**
 * Created by sonofelice on 2018/3/27.
 * 进行中文分词、词性标注、命名体识别、句子解析树、句子依赖图和指代关系消除功能的测试
 * -Xms3g -Xmx5g
 */
@Slf4j
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
        System.out.println(".............开始：" + startTime);
        // 创造一个空的Annotation对象
        Annotation document = new Annotation(text);

        // 对文本进行分析
        pipeline.annotate(document);
        System.out.println(".............开始分析：");
        //获取文本处理结果
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
        for (CoreMap sentence : sentences) {
            // 对于每一个分词进行分析它的词性、命名体识别、主干分析
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                // 获取句子的token（可以是作为分词后的词语）
                String word = token.get(CoreAnnotations.TextAnnotation.class);
                //词性标注
                String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
                // 命名实体规范化
                String ne = token.get(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class);
                //命名实体识别
                String ner = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
                // System.out.println(word + " | analysis : {  original : " + ner + "," + " normalized : " + ne + "}");
                //词干化处理——中文不需要这个步骤
                String lema = token.get(CoreAnnotations.LemmaAnnotation.class);
                log.info("分词是：[{}] 它的词性是：[{}]  命名体识别的结果是：  [{}]   ", word, pos, ner);
            }

            // 句子的解析树
            Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
            System.out.println(".................句子的解析树如下:");
            tree.pennPrint();

            // 句子的依赖图
            SemanticGraph graph =
                    sentence.get(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class);
            System.out.println(".............句子的依赖图如下：");
            System.out.println(graph.toString(SemanticGraph.OutputFormat.LIST));

        }

        long endTime = System.currentTimeMillis();
        long time = endTime - startTime;
        System.out.println("分析完成： The analysis lasts " + time + " seconds * 1000");

        // 指代词链
        //每条链保存指代的集合
        // 句子和偏移量都从1开始
        Map<Integer, CorefChain> corefChains = document.get(CorefCoreAnnotations.CorefChainAnnotation.class);
        if (corefChains == null) {
            return;
        }
        for (Map.Entry<Integer, CorefChain> entry : corefChains.entrySet()) {
            System.out.println("下面的指代关系一致，具体如下： ");
            for (CorefChain.CorefMention m : entry.getValue().getMentionsInTextualOrder()) {
                List<CoreLabel> tokens = sentences.get(m.sentNum - 1).get(CoreAnnotations.TokensAnnotation.class);
                log.info("指代关系： [{}]  ,类型是: {} 开始和结束位置是：{  {},{} }", m, m.mentionType.name(), tokens.get(m.startIndex - 1).beginPosition(), tokens.get(m.endIndex - 2).endPosition() + ")");
            }
        }
    }

    public static void main(String[] args) throws Exception {
        TestNLP nlp = new TestNLP();
        nlp.test();
    }
}




```

##### 需要的配置文件

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

##### 分析的结果

```java
2020-11-26 13:07:22.806 [main] INFO  e.s.nlp.pipeline.StanfordCoreNLP - Adding annotator tokenize
2020-11-26 13:07:28.942 [main] INFO  e.s.n.ie.AbstractSequenceClassifier - Loading classifier from edu/stanford/nlp/models/segmenter/chinese/ctb.gz ... done [6.1 sec].
2020-11-26 13:07:28.945 [main] INFO  e.s.nlp.pipeline.StanfordCoreNLP - Adding annotator ssplit
2020-11-26 13:07:28.949 [main] INFO  e.s.nlp.pipeline.StanfordCoreNLP - Adding annotator pos
2020-11-26 13:07:29.529 [main] INFO  e.s.nlp.tagger.maxent.MaxentTagger - Loading POS tagger from edu/stanford/nlp/models/pos-tagger/chinese-distsim/chinese-distsim.tagger ... done [0.6 sec].
2020-11-26 13:07:29.529 [main] INFO  e.s.nlp.pipeline.StanfordCoreNLP - Adding annotator lemma
2020-11-26 13:07:29.530 [main] INFO  e.s.nlp.pipeline.StanfordCoreNLP - Adding annotator ner
2020-11-26 13:07:31.935 [main] INFO  e.s.n.ie.AbstractSequenceClassifier - Loading classifier from edu/stanford/nlp/models/ner/chinese.misc.distsim.crf.ser.gz ... done [2.4 sec].
2020-11-26 13:07:32.132 [main] INFO  e.s.n.p.TokensRegexNERAnnotator - ner.fine.regexner: Read 21238 unique entries out of 21249 from edu/stanford/nlp/models/kbp/chinese/gazetteers/cn_regexner_mapping.tab, 0 TokensRegex patterns.
2020-11-26 13:07:32.493 [main] INFO  e.s.nlp.pipeline.StanfordCoreNLP - Adding annotator parse
2020-11-26 13:07:59.777 [main] INFO  e.s.nlp.parser.common.ParserGrammar - Loading parser from serialized file edu/stanford/nlp/models/srparser/chineseSR.ser.gz ... done [27.3 sec].
2020-11-26 13:07:59.796 [main] INFO  e.s.nlp.pipeline.StanfordCoreNLP - Adding annotator coref
2020-11-26 13:08:11.724 [main] INFO  e.s.n.pipeline.CorefMentionAnnotator - Using mention detector type: rule
.............开始：1606367291727
2020-11-26 13:08:11.770 [main] INFO  e.s.nlp.wordseg.ChineseDictionary - Loading Chinese dictionaries from 1 file:
2020-11-26 13:08:11.770 [main] INFO  e.s.nlp.wordseg.ChineseDictionary -   edu/stanford/nlp/models/segmenter/chinese/dict-chris6.ser.gz
2020-11-26 13:08:13.022 [main] INFO  e.s.nlp.wordseg.ChineseDictionary - Done. Unique words in ChineseDictionary is: 423200.
2020-11-26 13:08:13.360 [main] INFO  edu.stanford.nlp.wordseg.CorpusChar - Loading character dictionary file from edu/stanford/nlp/models/segmenter/chinese/dict/character_list [done].
2020-11-26 13:08:13.380 [main] INFO  e.s.nlp.wordseg.AffixDictionary - Loading affix dictionary from edu/stanford/nlp/models/segmenter/chinese/dict/in.ctb [done].
.............开始分析：
2020-11-26 13:08:17.249 [main] INFO  c.f.b.stanfordnlp.xiyou.test.TestNLP - 分词是：[袁隆平] 它的词性是：[NR]  命名体识别的结果是：  [PERSON]   
2020-11-26 13:08:17.255 [main] INFO  c.f.b.stanfordnlp.xiyou.test.TestNLP - 分词是：[是] 它的词性是：[VC]  命名体识别的结果是：  [O]   
2020-11-26 13:08:17.256 [main] INFO  c.f.b.stanfordnlp.xiyou.test.TestNLP - 分词是：[中国] 它的词性是：[NR]  命名体识别的结果是：  [ORGANIZATION]   
2020-11-26 13:08:17.256 [main] INFO  c.f.b.stanfordnlp.xiyou.test.TestNLP - 分词是：[科学院] 它的词性是：[NN]  命名体识别的结果是：  [ORGANIZATION]   
2020-11-26 13:08:17.257 [main] INFO  c.f.b.stanfordnlp.xiyou.test.TestNLP - 分词是：[的] 它的词性是：[DEG]  命名体识别的结果是：  [O]   
2020-11-26 13:08:17.258 [main] INFO  c.f.b.stanfordnlp.xiyou.test.TestNLP - 分词是：[院士] 它的词性是：[NN]  命名体识别的结果是：  [TITLE]   
2020-11-26 13:08:17.259 [main] INFO  c.f.b.stanfordnlp.xiyou.test.TestNLP - 分词是：[,] 它的词性是：[PU]  命名体识别的结果是：  [O]   
2020-11-26 13:08:17.259 [main] INFO  c.f.b.stanfordnlp.xiyou.test.TestNLP - 分词是：[他] 它的词性是：[PN]  命名体识别的结果是：  [O]   
2020-11-26 13:08:17.259 [main] INFO  c.f.b.stanfordnlp.xiyou.test.TestNLP - 分词是：[于] 它的词性是：[P]  命名体识别的结果是：  [O]   
2020-11-26 13:08:17.259 [main] INFO  c.f.b.stanfordnlp.xiyou.test.TestNLP - 分词是：[2009年] 它的词性是：[NT]  命名体识别的结果是：  [DATE]   
2020-11-26 13:08:17.259 [main] INFO  c.f.b.stanfordnlp.xiyou.test.TestNLP - 分词是：[10月] 它的词性是：[NT]  命名体识别的结果是：  [DATE]   
2020-11-26 13:08:17.260 [main] INFO  c.f.b.stanfordnlp.xiyou.test.TestNLP - 分词是：[到] 它的词性是：[CC]  命名体识别的结果是：  [O]   
2020-11-26 13:08:17.261 [main] INFO  c.f.b.stanfordnlp.xiyou.test.TestNLP - 分词是：[中国] 它的词性是：[NR]  命名体识别的结果是：  [COUNTRY]   
2020-11-26 13:08:17.261 [main] INFO  c.f.b.stanfordnlp.xiyou.test.TestNLP - 分词是：[山东省] 它的词性是：[NR]  命名体识别的结果是：  [STATE_OR_PROVINCE]   
2020-11-26 13:08:17.261 [main] INFO  c.f.b.stanfordnlp.xiyou.test.TestNLP - 分词是：[东营市] 它的词性是：[NR]  命名体识别的结果是：  [CITY]   
2020-11-26 13:08:17.261 [main] INFO  c.f.b.stanfordnlp.xiyou.test.TestNLP - 分词是：[东营区] 它的词性是：[NR]  命名体识别的结果是：  [FACILITY]   
2020-11-26 13:08:17.262 [main] INFO  c.f.b.stanfordnlp.xiyou.test.TestNLP - 分词是：[永乐] 它的词性是：[NR]  命名体识别的结果是：  [FACILITY]   
2020-11-26 13:08:17.262 [main] INFO  c.f.b.stanfordnlp.xiyou.test.TestNLP - 分词是：[机场] 它的词性是：[NN]  命名体识别的结果是：  [FACILITY]   
2020-11-26 13:08:17.262 [main] INFO  c.f.b.stanfordnlp.xiyou.test.TestNLP - 分词是：[附近] 它的词性是：[LC]  命名体识别的结果是：  [O]   
2020-11-26 13:08:17.262 [main] INFO  c.f.b.stanfordnlp.xiyou.test.TestNLP - 分词是：[承包] 它的词性是：[VV]  命名体识别的结果是：  [O]   
2020-11-26 13:08:17.262 [main] INFO  c.f.b.stanfordnlp.xiyou.test.TestNLP - 分词是：[了] 它的词性是：[AS]  命名体识别的结果是：  [O]   
2020-11-26 13:08:17.262 [main] INFO  c.f.b.stanfordnlp.xiyou.test.TestNLP - 分词是：[一千] 它的词性是：[CD]  命名体识别的结果是：  [NUMBER]   
2020-11-26 13:08:17.262 [main] INFO  c.f.b.stanfordnlp.xiyou.test.TestNLP - 分词是：[亩] 它的词性是：[M]  命名体识别的结果是：  [O]   
2020-11-26 13:08:17.262 [main] INFO  c.f.b.stanfordnlp.xiyou.test.TestNLP - 分词是：[盐] 它的词性是：[NN]  命名体识别的结果是：  [O]   
2020-11-26 13:08:17.263 [main] INFO  c.f.b.stanfordnlp.xiyou.test.TestNLP - 分词是：[碱地] 它的词性是：[NN]  命名体识别的结果是：  [O]   
2020-11-26 13:08:17.263 [main] INFO  c.f.b.stanfordnlp.xiyou.test.TestNLP - 分词是：[,] 它的词性是：[PU]  命名体识别的结果是：  [O]   
2020-11-26 13:08:17.263 [main] INFO  c.f.b.stanfordnlp.xiyou.test.TestNLP - 分词是：[开始] 它的词性是：[VV]  命名体识别的结果是：  [O]   
2020-11-26 13:08:17.263 [main] INFO  c.f.b.stanfordnlp.xiyou.test.TestNLP - 分词是：[种植] 它的词性是：[VV]  命名体识别的结果是：  [O]   
2020-11-26 13:08:17.263 [main] INFO  c.f.b.stanfordnlp.xiyou.test.TestNLP - 分词是：[棉花] 它的词性是：[NN]  命名体识别的结果是：  [O]   
2020-11-26 13:08:17.263 [main] INFO  c.f.b.stanfordnlp.xiyou.test.TestNLP - 分词是：[,] 它的词性是：[PU]  命名体识别的结果是：  [O]   
2020-11-26 13:08:17.263 [main] INFO  c.f.b.stanfordnlp.xiyou.test.TestNLP - 分词是：[年产量] 它的词性是：[NN]  命名体识别的结果是：  [O]   
2020-11-26 13:08:17.263 [main] INFO  c.f.b.stanfordnlp.xiyou.test.TestNLP - 分词是：[达到] 它的词性是：[VV]  命名体识别的结果是：  [O]   
2020-11-26 13:08:17.283 [main] INFO  c.f.b.stanfordnlp.xiyou.test.TestNLP - 分词是：[一万] 它的词性是：[CD]  命名体识别的结果是：  [NUMBER]   
2020-11-26 13:08:17.283 [main] INFO  c.f.b.stanfordnlp.xiyou.test.TestNLP - 分词是：[吨] 它的词性是：[M]  命名体识别的结果是：  [O]   
2020-11-26 13:08:17.284 [main] INFO  c.f.b.stanfordnlp.xiyou.test.TestNLP - 分词是：[,] 它的词性是：[PU]  命名体识别的结果是：  [O]   
2020-11-26 13:08:17.284 [main] INFO  c.f.b.stanfordnlp.xiyou.test.TestNLP - 分词是：[哈哈] 它的词性是：[IJ]  命名体识别的结果是：  [O]   
2020-11-26 13:08:17.286 [main] INFO  c.f.b.stanfordnlp.xiyou.test.TestNLP - 分词是：[,] 它的词性是：[PU]  命名体识别的结果是：  [O]   
2020-11-26 13:08:17.286 [main] INFO  c.f.b.stanfordnlp.xiyou.test.TestNLP - 分词是：[反正] 它的词性是：[AD]  命名体识别的结果是：  [O]   
2020-11-26 13:08:17.286 [main] INFO  c.f.b.stanfordnlp.xiyou.test.TestNLP - 分词是：[棣琦] 它的词性是：[NR]  命名体识别的结果是：  [PERSON]   
2020-11-26 13:08:17.286 [main] INFO  c.f.b.stanfordnlp.xiyou.test.TestNLP - 分词是：[说] 它的词性是：[VV]  命名体识别的结果是：  [O]   
2020-11-26 13:08:17.286 [main] INFO  c.f.b.stanfordnlp.xiyou.test.TestNLP - 分词是：[的] 它的词性是：[DEC]  命名体识别的结果是：  [O]   
2020-11-26 13:08:17.286 [main] INFO  c.f.b.stanfordnlp.xiyou.test.TestNLP - 分词是：[是] 它的词性是：[VC]  命名体识别的结果是：  [O]   
2020-11-26 13:08:17.286 [main] INFO  c.f.b.stanfordnlp.xiyou.test.TestNLP - 分词是：[假] 它的词性是：[JJ]  命名体识别的结果是：  [O]   
2020-11-26 13:08:17.286 [main] INFO  c.f.b.stanfordnlp.xiyou.test.TestNLP - 分词是：[的] 它的词性是：[DEG]  命名体识别的结果是：  [O]   
2020-11-26 13:08:17.286 [main] INFO  c.f.b.stanfordnlp.xiyou.test.TestNLP - 分词是：[,] 它的词性是：[PU]  命名体识别的结果是：  [O]   
2020-11-26 13:08:17.286 [main] INFO  c.f.b.stanfordnlp.xiyou.test.TestNLP - 分词是：[逗] 它的词性是：[VV]  命名体识别的结果是：  [O]   
2020-11-26 13:08:17.286 [main] INFO  c.f.b.stanfordnlp.xiyou.test.TestNLP - 分词是：[你] 它的词性是：[PN]  命名体识别的结果是：  [O]   
2020-11-26 13:08:17.286 [main] INFO  c.f.b.stanfordnlp.xiyou.test.TestNLP - 分词是：[玩儿] 它的词性是：[VV]  命名体识别的结果是：  [O]   
2020-11-26 13:08:17.286 [main] INFO  c.f.b.stanfordnlp.xiyou.test.TestNLP - 分词是：[,] 它的词性是：[PU]  命名体识别的结果是：  [O]   
2020-11-26 13:08:17.286 [main] INFO  c.f.b.stanfordnlp.xiyou.test.TestNLP - 分词是：[明天] 它的词性是：[NT]  命名体识别的结果是：  [DATE]   
2020-11-26 13:08:17.288 [main] INFO  c.f.b.stanfordnlp.xiyou.test.TestNLP - 分词是：[下午] 它的词性是：[NT]  命名体识别的结果是：  [TIME]   
2020-11-26 13:08:17.290 [main] INFO  c.f.b.stanfordnlp.xiyou.test.TestNLP - 分词是：[2点] 它的词性是：[NT]  命名体识别的结果是：  [TIME]   
2020-11-26 13:08:17.290 [main] INFO  c.f.b.stanfordnlp.xiyou.test.TestNLP - 分词是：[来] 它的词性是：[VV]  命名体识别的结果是：  [O]   
2020-11-26 13:08:17.290 [main] INFO  c.f.b.stanfordnlp.xiyou.test.TestNLP - 分词是：[我] 它的词性是：[PN]  命名体识别的结果是：  [O]   
2020-11-26 13:08:17.291 [main] INFO  c.f.b.stanfordnlp.xiyou.test.TestNLP - 分词是：[家] 它的词性是：[NN]  命名体识别的结果是：  [O]   
2020-11-26 13:08:17.291 [main] INFO  c.f.b.stanfordnlp.xiyou.test.TestNLP - 分词是：[吃饭] 它的词性是：[VV]  命名体识别的结果是：  [O]   
2020-11-26 13:08:17.291 [main] INFO  c.f.b.stanfordnlp.xiyou.test.TestNLP - 分词是：[吧] 它的词性是：[SP]  命名体识别的结果是：  [O]   
2020-11-26 13:08:17.291 [main] INFO  c.f.b.stanfordnlp.xiyou.test.TestNLP - 分词是：[。] 它的词性是：[PU]  命名体识别的结果是：  [O]   
2020-11-26 13:08:17.291 [main] INFO  c.f.b.stanfordnlp.xiyou.test.TestNLP - 分词是：[棣琦] 它的词性是：[NR]  命名体识别的结果是：  [PERSON]   
2020-11-26 13:08:17.291 [main] INFO  c.f.b.stanfordnlp.xiyou.test.TestNLP - 分词是：[是] 它的词性是：[VC]  命名体识别的结果是：  [O]   
2020-11-26 13:08:17.291 [main] INFO  c.f.b.stanfordnlp.xiyou.test.TestNLP - 分词是：[山东] 它的词性是：[NR]  命名体识别的结果是：  [ORGANIZATION]   
2020-11-26 13:08:17.291 [main] INFO  c.f.b.stanfordnlp.xiyou.test.TestNLP - 分词是：[大学] 它的词性是：[NN]  命名体识别的结果是：  [ORGANIZATION]   
2020-11-26 13:08:17.291 [main] INFO  c.f.b.stanfordnlp.xiyou.test.TestNLP - 分词是：[毕业] 它的词性是：[VV]  命名体识别的结果是：  [O]   
2020-11-26 13:08:17.291 [main] INFO  c.f.b.stanfordnlp.xiyou.test.TestNLP - 分词是：[的] 它的词性是：[DEC]  命名体识别的结果是：  [O]   
2020-11-26 13:08:17.292 [main] INFO  c.f.b.stanfordnlp.xiyou.test.TestNLP - 分词是：[,] 它的词性是：[PU]  命名体识别的结果是：  [O]   
2020-11-26 13:08:17.292 [main] INFO  c.f.b.stanfordnlp.xiyou.test.TestNLP - 分词是：[目前] 它的词性是：[NT]  命名体识别的结果是：  [DATE]   
2020-11-26 13:08:17.292 [main] INFO  c.f.b.stanfordnlp.xiyou.test.TestNLP - 分词是：[在] 它的词性是：[P]  命名体识别的结果是：  [O]   
2020-11-26 13:08:17.292 [main] INFO  c.f.b.stanfordnlp.xiyou.test.TestNLP - 分词是：[百度] 它的词性是：[NR]  命名体识别的结果是：  [ORGANIZATION]   
2020-11-26 13:08:17.292 [main] INFO  c.f.b.stanfordnlp.xiyou.test.TestNLP - 分词是：[做] 它的词性是：[VV]  命名体识别的结果是：  [O]   
2020-11-26 13:08:17.292 [main] INFO  c.f.b.stanfordnlp.xiyou.test.TestNLP - 分词是：[java] 它的词性是：[NN]  命名体识别的结果是：  [O]   
2020-11-26 13:08:17.292 [main] INFO  c.f.b.stanfordnlp.xiyou.test.TestNLP - 分词是：[开发] 它的词性是：[NN]  命名体识别的结果是：  [O]   
2020-11-26 13:08:17.292 [main] INFO  c.f.b.stanfordnlp.xiyou.test.TestNLP - 分词是：[,] 它的词性是：[PU]  命名体识别的结果是：  [O]   
2020-11-26 13:08:17.292 [main] INFO  c.f.b.stanfordnlp.xiyou.test.TestNLP - 分词是：[位置] 它的词性是：[NN]  命名体识别的结果是：  [O]   
2020-11-26 13:08:17.292 [main] INFO  c.f.b.stanfordnlp.xiyou.test.TestNLP - 分词是：[是] 它的词性是：[VC]  命名体识别的结果是：  [O]   
2020-11-26 13:08:17.292 [main] INFO  c.f.b.stanfordnlp.xiyou.test.TestNLP - 分词是：[东北] 它的词性是：[NN]  命名体识别的结果是：  [LOCATION]   
2020-11-26 13:08:17.293 [main] INFO  c.f.b.stanfordnlp.xiyou.test.TestNLP - 分词是：[旺] 它的词性是：[VV]  命名体识别的结果是：  [O]   
2020-11-26 13:08:17.294 [main] INFO  c.f.b.stanfordnlp.xiyou.test.TestNLP - 分词是：[东路] 它的词性是：[NR]  命名体识别的结果是：  [O]   
2020-11-26 13:08:17.294 [main] INFO  c.f.b.stanfordnlp.xiyou.test.TestNLP - 分词是：[102] 它的词性是：[CD]  命名体识别的结果是：  [NUMBER]   
2020-11-26 13:08:17.294 [main] INFO  c.f.b.stanfordnlp.xiyou.test.TestNLP - 分词是：[号院] 它的词性是：[NN]  命名体识别的结果是：  [O]   
2020-11-26 13:08:17.294 [main] INFO  c.f.b.stanfordnlp.xiyou.test.TestNLP - 分词是：[,] 它的词性是：[PU]  命名体识别的结果是：  [O]   
2020-11-26 13:08:17.294 [main] INFO  c.f.b.stanfordnlp.xiyou.test.TestNLP - 分词是：[手机号] 它的词性是：[NN]  命名体识别的结果是：  [O]   
2020-11-26 13:08:17.294 [main] INFO  c.f.b.stanfordnlp.xiyou.test.TestNLP - 分词是：[143667788] 它的词性是：[CD]  命名体识别的结果是：  [NUMBER]   
2020-11-26 13:08:17.294 [main] INFO  c.f.b.stanfordnlp.xiyou.test.TestNLP - 分词是：[90] 它的词性是：[CD]  命名体识别的结果是：  [NUMBER]   
.................句子的解析树如下:
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
.............句子的依赖图如下：
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

分析完成： The analysis lasts 5579 seconds * 1000
下面的指代关系一致，具体如下： 
2020-11-26 13:08:17.306 [main] INFO  c.f.b.stanfordnlp.xiyou.test.TestNLP - 指代关系： ["棣琦" in sentence 1]  ,类型是: PROPER 开始和结束位置是：{  75,77) }
2020-11-26 13:08:17.306 [main] INFO  c.f.b.stanfordnlp.xiyou.test.TestNLP - 指代关系： ["棣琦" in sentence 1]  ,类型是: PROPER 开始和结束位置是：{  101,103) }
下面的指代关系一致，具体如下： 
2020-11-26 13:08:17.306 [main] INFO  c.f.b.stanfordnlp.xiyou.test.TestNLP - 指代关系： ["中国 科学院 的 院士" in sentence 1]  ,类型是: NOMINAL 开始和结束位置是：{  4,12) }
2020-11-26 13:08:17.306 [main] INFO  c.f.b.stanfordnlp.xiyou.test.TestNLP - 指代关系： ["他" in sentence 1]  ,类型是: PRONOMINAL 开始和结束位置是：{  13,14) }

```





## Hanlp

- [github_hanlp](https://github.com/hankcs/HanLP/tree/1.x)

HanLP是一系列模型与算法组成的NLP工具包，目标是普及自然语言处理在生产环境中的应用,主要解决的就是中文NLP的一些列问题

HanLP 提供的功能：[功能地址](https://github.com/hankcs/HanLP/tree/1.x)

HanlLP提供的功能很多，这里我们只测试下它的依存语法分析，如下：

#### 依存语法关系

```java
CoNLLSentence sentence = HanLP.parseDependency("公积金如何申请");
// 可以方便地遍历它
for (CoNLLWord word : sentence)
{
  System.out.printf("%s --(%s)--> %s\n", word.LEMMA, word.DEPREL, word.HEAD.LEMMA);
}
```

```java
1	公积金	公积金	n	n	_	3	主谓关系	_	_
2	如何	如何	r	r	_	3	状中结构	_	_
3	申请	申请	v	v	_	0	核心关系	_	_

公积金 --(主谓关系)--> 申请
如何 --(状中结构)--> 申请
申请 --(核心关系)--> ##核心##
申请 --(核心关系)--> ##核心##
如何 --(状中结构)--> 申请
公积金 --(主谓关系)--> 申请
```

再看下刚才stanforNLP 分析的依存语法关系

```java
//比如输入：《公积金如何申请》
.............句子的依存语法分析结果如下：
root(ROOT-0, 申请-3)
nsubj(申请-3, 公积金-1)
advmod(申请-3, 如何-2)
```

- 相似之处

  - 都对一句话进行语法分析，都要根据特点的规则进行区分和提取

  - 核心都是《申请》

  - 名词主语都是：《申请——公积金》
  - 状语都是：《如何——申请》

- 不同之处
  - hanlp不愧是对汉语做了最强的适配，直接给出了汉语的语法接口介绍



## 如何落地

