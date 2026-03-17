package org.example;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.StandardCharsets;

public class TestChannelHandler extends ChannelInboundHandlerAdapter {

    //当Channel已经注册到自己的EventLoop上时调用，前面我们说了，
    // 一个Channel只会注册到一个EventLoop上，注册到EventLoop后，这样才会在发生对应事件时被通知。
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception{
        System.out.println("channelRegistered");
    }
    //从EventLoop上取消注册时
    public void changeUnregistered(ChannelHandlerContext ctx) throws Exception{
        System.out.println("channelUnregistered");
    }
    //当Channel已经处于活跃状态时被调用，此时Channel已经连接/绑定，并且已经就绪
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelActive");
    }
    //跟上面相反，不再活跃了，并且不在连接它的远程节点
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelInactive");
    }
    //当从Channel读取数据时被调用，可以看到数据被自动包装成了一个Object（默认是ByteBuf）
    public void channelRead(ChannelHandlerContext ctx,Object msg) throws Exception{
        ByteBuf buf = (ByteBuf) msg;
        System.out.println(
                Thread.currentThread().getName() + " >> 接收到客户端发送的数据："
                + buf.toString(StandardCharsets.UTF_8)
        );
        ByteBuf back = ctx.alloc().buffer();
        back.writeCharSequence("已收到",StandardCharsets.UTF_8);
        ctx.writeAndFlush(back);
        System.out.println("channelRead");
    }
    //上一个读取操作完成后调用
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelReadComplete");
    }
    //暂时不介绍
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        System.out.println("userEventTriggered");
    }
    //当Channel的可写状态发生改变时被调用
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelWritabilityChanged");
    }
    //出现异常时被调用
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("exceptionCaught"+cause);
    }

}
