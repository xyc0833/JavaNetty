package org.example;

import io.netty.buffer.*;


import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        ByteBufAllocator allocator = PooledByteBufAllocator.DEFAULT;
        ByteBuf buf = allocator.directBuffer(10);   //申请一个容量为10的直接缓冲区
        buf.writeChar('T');    //随便操作操作
        System.out.println(buf.readChar());
        buf.release();    //释放此缓冲区

        ByteBuf buf2 = allocator.directBuffer(10);   //重新再申请一个同样大小的直接缓冲区
        System.out.println(buf2 == buf);
    }
}