package com.example.bjb.myapplication.socket.handler;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class DecoderHandler extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> out) throws Exception {
//解码逻辑代码

//1. 获取消息类型
        int command = byteBuf.readInt();
//2. 获取消息长度
        int length = byteBuf.readInt();
//3. 获取消息
        byte [] bytes = new byte[length];

        byteBuf.readBytes(bytes);
//一下两行代码是先变为json字符串，然后再转为对象。
        String jsonStr = new String(bytes,"UTF-8");

        out.add(jsonStr);

    }


}
