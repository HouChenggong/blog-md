package org.xiyou.leetcode.tx;

import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;

class MachineTemplateNode {
    /**
     * 词
     */
    private String word;

    /**
     * 当前词的类型，只有当词是结束的时候才会有slotType
     */
    private HashSet<String> slotTypeSet;

    /**
     * 当前词的下一个词
     */
    private Map<String, MachineTemplateNode> next;

    /**
     * 是否是一个词结束
     */
    private Boolean end;

    public MachineTemplateNode() {
        next = new HashMap<>();
        end = false;
        slotTypeSet = new HashSet<>();

    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public HashSet<String> getSlotTypeSet() {
        return slotTypeSet;
    }

    public void setSlotTypeSet(HashSet<String> slotTypeSet) {
        this.slotTypeSet = slotTypeSet;
    }

    public Map<String, MachineTemplateNode> getNext() {
        return next;
    }

    public void setNext(Map<String, MachineTemplateNode> next) {
        this.next = next;
    }

    public Boolean getEnd() {
        return end;
    }

    public void setEnd(Boolean end) {
        this.end = end;
    }


    public MachineTemplateNode(String word, HashSet<String> slotTypeSet, Map<String, MachineTemplateNode> next, Boolean end) {
        this.word = word;
        this.slotTypeSet = slotTypeSet;
        this.next = next;
        this.end = end;
    }
}
public class PrefixTree{
    public static void add(MachineTemplateNode root, String type, String word) {
        MachineTemplateNode tmpNode = root;
        for (int i = 0; i < word.length(); i++) {
            String curChar = String.valueOf(word.charAt(i));
            if (!tmpNode.getNext().containsKey(curChar)) {
                tmpNode.getNext().put(curChar, new MachineTemplateNode());
            }
            tmpNode = tmpNode.getNext().get(curChar);
        }
        tmpNode.setEnd(true);
        HashSet<String> typeList = tmpNode.getSlotTypeSet();
        if (typeList == null) {
            typeList = new HashSet<>();
        }
        typeList.add(type);
        tmpNode.setSlotTypeSet(typeList);
        tmpNode.setWord(word);
    }
    public static boolean del(MachineTemplateNode root, String type, String word) {
        if(root == null){
            return false;
        }
        if(word.isEmpty()){
            //如果删除的是最后一个字，需要判断类型是否符合
            HashSet<String> slotTypeSet = root.getSlotTypeSet();
            if(slotTypeSet.contains(type)) {
                slotTypeSet.remove(type);
            }
            return root.getNext().isEmpty() && slotTypeSet.isEmpty();
        }else{
            String head = word.substring(0, 1);
            Map<String, MachineTemplateNode> children = root.getNext();
            if(children.containsKey(head)){
                if(del(children.get(head), type, word.substring(1))){
                    if (children.get(head).getEnd()) {
                        return false;
                    }
                    children.remove(head);
                    return true;
                }

            }
            return false;
        }
    }
    public static void print_buff(MachineTemplateNode root, String s){
        if(root.getEnd()){
            for(String type: root.getSlotTypeSet()){
                System.out.println(s + " ---- " + type);
            }
        }
        Map<String, MachineTemplateNode> children = root.getNext();
        for(String word: children.keySet()){
            print_buff(children.get(word), s+word);
        }
    }
    public static void print_prefix_tree(MachineTemplateNode root, String s){
        System.out.println();
        System.out.println("-------------------------------");;
        print_buff(root, s);
    }
    public static void main(String[] args) {
        MachineTemplateNode root = new MachineTemplateNode();
        add(root, "phone", "苹果");
        print_prefix_tree(root, "");

        add(root, "phone", "苹果手机");
        print_prefix_tree(root, "");
        add(root, "mac", "苹果电脑");
        print_prefix_tree(root, "");
        add(root, "shuiguo", "苹果");
        print_prefix_tree(root, "");
        // 类型正确，能删除
        del(root, "mac", "苹果电脑");
        print_prefix_tree(root, "");
//         类型错误，不能删除
        del(root, "mac", "苹果手机");
        print_prefix_tree(root, "");

//         类型正确，但是还有苹果手机在引用不删除，只需要把type清即可
        del(root, "phone", "苹果");
        print_prefix_tree(root, "");
    }
}
