package com.coltcn.chatserver.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

import java.nio.charset.Charset;

/**
 * Created by majf on 2015/10/12.
 */
public class ChatServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println(String.format("client[%s] Unregistered", ctx.channel()));
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(String.format("client[%s] Inactive", ctx.channel()));
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println(String.format("client[%s] Registered", ctx.channel()));
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        WebSocketFrame frame = (WebSocketFrame) msg;
        ByteBuf buf = frame.content();

        String msgStr = buf.toString(Charset.forName("utf-8"));
        System.out.println("recive:"+msgStr);
        WebSocketFrame outFrame = new TextWebSocketFrame(msgStr);
        ctx.channel().writeAndFlush(outFrame);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(String.format("client[%s] Active",ctx.channel()));
    }
}
