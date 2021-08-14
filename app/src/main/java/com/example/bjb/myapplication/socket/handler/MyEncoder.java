package com.example.bjb.myapplication.socket.handler;


import java.nio.charset.Charset;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;


/**
 * 协议编码器
 */

public class MyEncoder extends MessageToByteEncoder<MyProtocolBean> {


    @Override
    protected void encode(ChannelHandlerContext ctx, MyProtocolBean myProtocolBean, ByteBuf byteBuf) throws Exception {

        if(myProtocolBean == null){
            throw new Exception("msg is null");
        }
        /**
         * 编码逻辑代码
         * 消息类型   消息长度    数据
         *   int     Int      bytes
         */

        //1. 写入消息类型
        byteBuf.writeInt(myProtocolBean.getType());
        //2. 写入消息长度
        byteBuf.writeInt(myProtocolBean.getLength());
        //3. 写入消息
        byteBuf.writeBytes(myProtocolBean.getContent().getBytes(Charset.forName("UTF-8")));

    }
}
