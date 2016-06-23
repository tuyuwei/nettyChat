package com.tywei.client;


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.Scanner;


/**
 * Created by tywei on 16/6/12.
 */
public class Client {

    private Bootstrap bootstrap;
    private ChannelFuture future;
    private String host = "127.0.0.1";
    private int port = 9090;

    public Client(String[] args) {
        switch (args.length) {
            case 1:
                port = Integer.parseInt(args[0]);
                break;
            case 2:
                host = args[0];
                port = Integer.parseInt(args[1]);
                break;
        }
    }

    public static void main(String[] args) throws Exception {
        new Client(args).start();
    }

    public void start() throws Exception {
        NioEventLoopGroup worker = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        try {
            bootstrap
                    .group(worker)
                    .channel(NioSocketChannel.class)
                    .remoteAddress(host, port)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 2, 0, 2));
                            ch.pipeline().addLast(new LengthFieldPrepender(2));
                            ch.pipeline().addLast(new StringEncoder());
                            ch.pipeline().addLast(new StringDecoder());
                            ch.pipeline().addLast(new ClientHandler());
                        }
                    });

            future = bootstrap.connect().sync();
            in();
            future.channel().closeFuture().sync();
        } finally {
            worker.shutdownGracefully().sync();
        }

    }


    public void in() {
        System.out.println("start send msg.");
        String in;
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            in = scanner.nextLine();
            if (in.equals("quit")) {
                System.out.println("close channel.");
                future.channel().close();
                break;
            }
            if (future.channel().isActive() == false) {
                System.out.println("channel close.");
                break;
            }
            System.out.println("send: " + in);
            future.channel().writeAndFlush(in);
        }
    }
}
