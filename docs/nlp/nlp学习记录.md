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

#### 中文分词

[中文分词参考地址](https://blog.csdn.net/macanv/article/details/72993873)




