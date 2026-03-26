package org.example;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

import java.nio.charset.StandardCharsets;

public class Server {
    public static void main(String[] args) {
        EventLoopGroup bossGroup = new NioEventLoopGroup(), workerGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap
                .group(bossGroup, workerGroup)   //指定事件循环组
                .channel(NioServerSocketChannel.class)   //指定为NIO的ServerSocketChannel
                .childHandler(new ChannelInitializer<SocketChannel>() {   //注意，这里的SocketChannel不是我们NIO里面的，是Netty的
                    @Override
                    protected void initChannel(SocketChannel channel) {
                        channel.pipeline()
                                .addLast(new HttpRequestDecoder())   //Http请求解码器
                                //搞一个聚合器，将内容聚合为一个FullHttpRequest，参数是最大内容长度
                                .addLast(new HttpObjectAggregator(Integer.MAX_VALUE))
                                //.addLast(new LoggingHandler(LogLevel.INFO))   //添加一个日志Handler，在请求到来时会自动打印相关日志
                                .addLast(new ChannelInboundHandlerAdapter(){
                                    @Override
                                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                        System.out.println("收到客户端的数据："+msg.getClass());  //看看是个啥类型
                                        FullHttpRequest request = (FullHttpRequest) msg;
                                        //请求进来了直接走解析
                                        PageResolver resolver = PageResolver.getInstance();
                                        ctx.channel().writeAndFlush(resolver.resolveResource(request.uri()));
                                        ctx.channel().close();
                                    }

                                    @Override
                                    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                                        if(evt instanceof IdleStateEvent){
                                            IdleStateEvent event = (IdleStateEvent) evt;
                                            if(event.state() == IdleState.WRITER_IDLE){
                                                System.out.println("好久都没写了，看视频的你真的有认真在跟着敲吗");
                                            }else if(event.state() == IdleState.READER_IDLE) {
                                                System.out.println("已经很久很久没有读事件发生了，好寂寞");
                                            }
                                        }
                                    }
                                })
                                .addLast(new HttpResponseEncoder());   //响应记得也要编码后发送哦

                    }
                });
        bootstrap.bind(8081);
    }

}
