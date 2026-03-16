package org.example;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        //动态扩容
        ByteBuf buf = Unpooled.buffer(10);    //容量只有10字节
        System.out.println(buf.capacity());
        //直接写一个字符串
        buf.writeCharSequence("卢本伟牛逼！", StandardCharsets.UTF_8);   //很明显这么多字已经超过10字节了
        System.out.println(buf.capacity());

    }
}