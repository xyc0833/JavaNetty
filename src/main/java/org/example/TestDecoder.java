package org.example;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class TestDecoder extends MessageToMessageDecoder<ByteBuf> {
    @Override
    //重写decode方法相当于可以 进行自定义操作
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        System.out.println("数据已经收到，正在进行解码");
        String text = byteBuf.toString(StandardCharsets.UTF_8);
        //解码后需要将解析后的数据丢进List中，如果丢进去多个数据，相当于数据被分成了多个，后面的Handler就需要每个都处理一次
        list.add(text);
        list.add(text + 2);
        list.add(text + 3);
    }
}
