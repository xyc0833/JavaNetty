package org.example;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        //创建一个初始容量为10的ByteBuf缓冲区，这里的Unpooled是用于快速生成ByteBuf的工具类
        //至于为啥叫Unpooled是池化的意思，ByteBuf有池化和非池化两种，区别在于对内存的复用，我们之后再讨论
        ByteBuf buf = Unpooled.buffer(10);
        System.out.println("初始状态："+Arrays.toString(buf.array()));
        buf.writeInt(-8888);//写入一个Int数据
        System.out.println("写入Int后："+Arrays.toString(buf.array()));
        buf.readShort();//无需翻转，直接读取一个short数据出来
        System.out.println("读取Short后："+Arrays.toString(buf.array()));
        buf.discardReadBytes();//丢弃操作，会将当前的可读部分内容丢到最前面，并且读写指针向前移动丢弃的距离
        System.out.println("丢弃之后："+Arrays.toString(buf.array()));
        buf.clear();    //清空操作，清空之后读写指针都归零
        System.out.println("清空之后："+Arrays.toString(buf.array()));


        //我们也可以将一个byte[]直接包装进缓冲区（和NIO是一样的）不过写指针的值一开始就跑到最后去了
        ByteBuf buf01 = Unpooled.wrappedBuffer("abcdefg".getBytes());
        //除了包装，也可以复制数据，copiedBuffer()会完完整整将数据拷贝到一个新的缓冲区中
        buf01.readByte();   //读取一个字节
        ByteBuf slice = buf01.slice();   //现在读指针位于1，然后进行划分

        System.out.println(slice.arrayOffset());   //得到划分出来的ByteBuf的偏移地址
        System.out.println(Arrays.toString(slice.array()));
        //buf01.writeInt(123);
    }
}