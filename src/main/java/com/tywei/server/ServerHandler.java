package com.tywei.server;


import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;


/**
 * Created by tywei on 16/6/12.
 */
//@ChannelHandler.Sharable
public class ServerHandler extends ChannelInboundHandlerAdapter {

    private static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        String in = (String) msg;
        if (in.equals("quit")) {
            channels.remove(ctx.channel());
            return;
        }

        for (Channel ch : channels) {
            System.out.println("开始转发到其他客户端！:" + ch.id());
            ch.writeAndFlush(in);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.out.println("exceptionCaught...");
        cause.printStackTrace();                //5
        channels.remove(ctx.channel());
        ctx.close();                            //6
    }


    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        System.out.println("add channel addrs:" + ctx.channel().remoteAddress());
        boolean rs = channels.add(ctx.channel());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channel remove:" + ctx.channel().remoteAddress());
        channels.remove(ctx.channel());
    }

}
