package com.coltcn.chatserver.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by majfc on 2015/10/12.
 */
public class WebSocketServerHandler extends SimpleChannelInboundHandler<Object> {

    private static final Logger logger = Logger.getLogger(WebSocketServerHandler.class.getName());

    private WebSocketServerHandshaker handshaker;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
        //��ͳHTTP
        if (o instanceof FullHttpRequest){
            handleHttpRequest(channelHandlerContext, (FullHttpRequest) o);
        }else if (o instanceof WebSocketFrame){ //websocket
            handleWebSocketFrame(channelHandlerContext, (WebSocketFrame) o);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    private void handleHttpRequest(ChannelHandlerContext ctx,FullHttpRequest req){
        //�������ʧ�ܣ�����http�쳣
        if (!req.getDecoderResult().isSuccess()
                || (!"websocket".equals(req.headers().get("Upgrade")))){
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
            return;
        }

        //����������Ӧ����
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory("/wx",null,false);
        handshaker = wsFactory.newHandshaker(req);

        if (handshaker == null){
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        }else {
            handshaker.handshake(ctx.channel(),req);
        }
    }

    private void handleWebSocketFrame(ChannelHandlerContext ctx,WebSocketFrame frame){
        //�ж��Ƿ�ر�ָ��
        if (frame instanceof CloseWebSocketFrame){
            handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
            return;
        }
        //�ж��Ƿ�Ping��Ϣ
        if (frame instanceof PingWebSocketFrame){
            ctx.channel().writeAndFlush(new PongWebSocketFrame(frame.content().retain()));
            return;
        }
        //ֻ�����ı���Ϣ
        if(!(frame instanceof TextWebSocketFrame)){
            throw new UnsupportedOperationException(String.format("%s frame types not supported",frame.getClass().getName()));
        }

        //���ش�Ӧ��Ϣ
        String reqMsg = ((TextWebSocketFrame) frame).text();
        if (logger.isLoggable(Level.FINE)){
            logger.fine(String.format("%s received %s",ctx.channel(),reqMsg));
        }
        ctx.writeAndFlush(new TextWebSocketFrame(reqMsg));
    }

    private void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest req, FullHttpResponse res) {
        //����Ӧ����ͻ���
        if (res.getStatus().code()!=200){
            ByteBuf buf = Unpooled.copiedBuffer(res.getStatus().toString(), CharsetUtil.UTF_8);
            res.content().writeBytes(buf);
            buf.release();
            HttpHeaders.setContentLength(res, res.content().readableBytes());
        }

        //����Ƿ�keep-Alive,�ر�����
        ChannelFuture f =ctx.channel().writeAndFlush(res);
        if(!HttpHeaders.isKeepAlive(req) || res.getStatus().code() !=200){
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
