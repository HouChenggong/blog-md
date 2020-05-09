package org.xiyou.leetcode.javabase;

/**
 * @author xiyou
 * @version 1.0
 * xiyou-todo 引用类型的final
 * @date 2020/5/9 9:17
 */
public class FinalReferenceExample {
    final int[] intArray;                //final 是引用类型
    static FinalReferenceExample obj;

    public FinalReferenceExample () {    // 构造函数
        intArray = new int[1];             //1
        intArray[0] = 1;                   //2
    }

    public static void writerOne () {      // 写线程 A 执行
        obj = new FinalReferenceExample ();  //3
    }

    public static void writerTwo () {          // 写线程 B 执行
        obj.intArray[0] = 2;                 //4
        System.out.println("2");
    }

    public static void reader () {         // 读线程 C 执行
        if (obj != null) {                    //5
            int temp1 = obj.intArray[0];       //6
        }
        System.out.println("1");
    }

    public static void main(String[] args) {
        reader();
        writerOne();
        writerTwo();
    }
}
