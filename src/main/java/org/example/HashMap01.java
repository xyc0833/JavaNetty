package org.example;

import java.util.HashMap;
import java.util.Map;

public class HashMap01 {
    //统计单词频次
    public static void main(String[] args) {
        //假设我们有全英文的小写文档
        String document  = "this is a simple document for daily use a document . it records basic information and keeps content clear ";

        //创建HashMap来存储对应的单词和数量
        Map<String,Integer> wordCountMap = new HashMap<>();
        //直接以空格分割单词 使用正则表达式分割 除去标点符号和空格
        //String [] words = document.split("\\w+");
        // \\w+ 是匹配单词，用单词去分割，结果就只剩下空格、符号，完全反了！
        //复习一下 \w 表示匹配任意数字字母和下划线 + 表示匹配一次或多次

        String[] words = document.split("\\s+"); // 按空格分割

        //遍历单词数组，统计每个单词出现的次数
        for(String word : words){
            //检查单词是否在map中
            if(wordCountMap.containsKey(word)){
                //如果已经存在则增加计数
                wordCountMap.put(word, wordCountMap.get(word)+1);
            }else{
                //如果不存在 则添加到map中，并且计数+1
                wordCountMap.put(word,1);
            }
        }

        //打印结果 entry 英文是条目的意思
        for(Map.Entry<String,Integer> entry : wordCountMap.entrySet()){
            System.out.println("单词" + entry.getKey() + "次数" + entry.getValue());
        }

    }

}
