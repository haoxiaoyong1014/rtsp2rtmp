package com.xuhuihealth.rtmp.server;

import com.xuhuihealth.rtmp.server.handlers.*;
import com.xuhuihealth.rtmp.server.manager.StreamManager;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RTMPServer {

    private int port;

    ChannelFuture channelFuture;

    EventLoopGroup eventLoopGroup;
    StreamManager streamManager;
    int handlerThreadPoolSize;


    public RTMPServer(int port, StreamManager sm, int threadPoolSize) {
        this.port = port;
        this.streamManager = sm;
        this.handlerThreadPoolSize = threadPoolSize;
    }

    public void run() throws Exception {
        eventLoopGroup = new NioEventLoopGroup();

        ServerBootstrap b = new ServerBootstrap();
        DefaultEventExecutorGroup executor = new DefaultEventExecutorGroup(handlerThreadPoolSize);
        b.group(eventLoopGroup).channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        //CorsConfig corsConfig = CorsConfigBuilder.forAnyOrigin().allowNullOrigin().allowCredentials().build();
                        ch.pipeline().addLast(new ConnectionAdapter()).addLast(new HandShakeDecoder())
                                .addLast(new ChunkDecoder()).addLast(new ChunkEncoder())
                                .addLast(executor, new RtmpMessageHandler(streamManager)
                                //.addLast(new CorsHandler(corsConfig)
                                );
                    }
                }).option(ChannelOption.SO_BACKLOG, 128).childOption(ChannelOption.SO_KEEPALIVE, true);

        channelFuture = b.bind(port).sync();

        log.info("RTMP Server start , listen at :{}", port);

    }

    public void close() {
        try {
            channelFuture.channel().closeFuture().sync();
            eventLoopGroup.shutdownGracefully();
        } catch (Exception e) {
            log.error("close rtmp server failed", e);
        }
    }


}
