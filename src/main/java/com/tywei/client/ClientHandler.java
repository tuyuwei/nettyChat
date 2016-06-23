package com.tywei.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

import java.util.Date;

/**
 * Created by tywei on 16/6/12.
 */
//@ChannelHandler.Sharable
public class ClientHandler extends SimpleChannelInboundHandler<String> {


    @Override
    public void messageReceived(ChannelHandlerContext ctx, String msg) {
        System.out.println("client received: " + msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx,
                                Throwable cause) {                    //4
        cause.printStackTrace();
        ctx.close();
    }
}
