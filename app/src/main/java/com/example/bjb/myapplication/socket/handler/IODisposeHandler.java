package com.example.bjb.myapplication.socket.handler;


import android.util.Log;

import com.example.bjb.myapplication.activity.NettyActivity;
import com.example.bjb.myapplication.socket.SocketService;
import com.example.bjb.myapplication.socket.entity.LoginRequest;
import com.example.bjb.myapplication.utils.HardwareUtils;
import com.example.bjb.myapplication.utils.SPUtil;
import com.google.gson.Gson;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class IODisposeHandler extends SimpleChannelInboundHandler<MyProtocolBean> {

    private OnNettyMessageListener onNettyMessageListener;


    public static Channel channel;

    /**
     * 建立连接
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("收到连接:" + ctx.channel());
        channel = ctx.channel();

        //登录
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(SPUtil.getInstance().getString("username",""));
        loginRequest.setPassword(SPUtil.getInstance().getString("password",""));
        loginRequest.setTerminalId(HardwareUtils.getMachineCode());

        loginRequest.setTerminalName(SPUtil.getInstance().getString("terminalname",""));

        loginRequest.setType("3");
        String content = new Gson().toJson(loginRequest);

        channel.writeAndFlush(new MyProtocolBean(1, content.getBytes().length, content));


    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);

        ctx.close();
        NettyActivity.isHearting =false;
        IODisposeHandler.channel = null;

        if(onNettyMessageListener != null){
            onNettyMessageListener.getException();
        }


    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)  {

        Log.e("channel", "连接出错" + cause.toString());

        ctx.close();
        NettyActivity.isHearting =false;
        IODisposeHandler.channel = null;

        if(onNettyMessageListener != null){
            onNettyMessageListener.getException();
        }
    }



    public void setOnNettyMessageListener(OnNettyMessageListener onNettyMessageListener) {
        this.onNettyMessageListener = onNettyMessageListener;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MyProtocolBean msg) throws Exception {
        if(onNettyMessageListener != null){
            onNettyMessageListener.getMessage(msg);
        }
    }

    public interface OnNettyMessageListener {
        void getMessage(MyProtocolBean msg);
        void getException();
    }

}
