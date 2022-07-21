package com.xuhuihealth.rtmp.server.handlers;

import com.google.common.base.Splitter;
import com.xuhuihealth.rtmp.controllers.RtspToRtmpController;
import com.xuhuihealth.rtmp.server.entities.Stream;
import com.xuhuihealth.rtmp.server.entities.StreamName;
import com.xuhuihealth.rtmp.server.manager.StreamManager;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import static io.netty.handler.codec.http.HttpHeaderNames.*;

@Slf4j
public class HttpFlvHandler extends SimpleChannelInboundHandler<HttpObject> {

    private static final String STREAM_NAME_KEY = "stream_name_key";
    private static final AttributeKey<StreamName> STREAM_NAME_ATTR = AttributeKey.newInstance(STREAM_NAME_KEY);

    StreamManager streamManager;
    private static List<StreamName> ctxs = new ArrayList<>();

    private static List<ChannelHandlerContext> ctxChannels = new ArrayList<>();

    public HttpFlvHandler(StreamManager streamManager) {
        this.streamManager = streamManager;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.warn("flv inactive");
        StreamName sn = ctx.channel().attr(STREAM_NAME_ATTR).get();
        if (sn != null){
            String outStreamName = sn.getName();
            String realStreamName = RtspToRtmpController.getRealStreamId(outStreamName);
            StreamName inStreamName = new StreamName(sn.getApp(), realStreamName);
            Stream stream = streamManager.getStream(inStreamName);

            if (stream != null) {
                if (stream.removeHttpFlvSubscriber(ctx.channel())){
                    RtspToRtmpController.removeBoth(outStreamName);
                    streamManager.remove(sn);
                }else {
                    RtspToRtmpController.ID_STREAM_MAP.remove(Long.valueOf(outStreamName));
                }
            }
        }
        super.channelInactive(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
        if (msg instanceof HttpRequest) {
            HttpRequest req = (HttpRequest) msg;

            String uri = req.uri();
            List<String> appAndStreamName = Splitter.on("/").omitEmptyStrings().splitToList(uri);
            if (appAndStreamName.size() != 2) {
                httpResponseStreamNotExist(ctx, uri);
                return;
            }


            String app = appAndStreamName.get(0);
            String streamName = appAndStreamName.get(1);
            if (streamName.endsWith(".flv")) {
                streamName = streamName.substring(0, streamName.length() - 4);
            }

            StreamName outStreamName = new StreamName(app, streamName);
            streamName = RtspToRtmpController.getRealStreamId(streamName);
            StreamName sn = new StreamName(app, streamName);
            log.info("http stream :{} requested", sn);
            Stream stream = streamManager.getStream(sn);

            if (stream == null) {
                httpResponseStreamNotExist(ctx, uri);
                return;
            }
            // 尝试检测是否有内容，或者是否达到5秒等时间
            int count = 50;
            while (stream.getContent() == null || count > 1){
                try {
                    Thread.sleep(100);
                }catch(InterruptedException e){
                    log.error("wait content filled");
                }finally {
                    count --;
                }
            }

            ctx.channel().attr(STREAM_NAME_ATTR).set(outStreamName);
            DefaultHttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
            response.headers().set(CONTENT_TYPE, "video/x-flv");
            response.headers().set(TRANSFER_ENCODING, "chunked");

            response.headers().set(ACCESS_CONTROL_ALLOW_ORIGIN, "*");
            response.headers().set(ACCESS_CONTROL_ALLOW_HEADERS, "Origin, X-Requested-With, Content-Type, Accept");
            response.headers().set(ACCESS_CONTROL_ALLOW_METHODS, "GET, POST, PUT,DELETE");
            ctx.writeAndFlush(response);
//            if (!CollectionUtils.isEmpty(ctxs)/*|| !CollectionUtils.isEmpty(ctxChannels)*/) {
//
//               /* ChannelHandlerContext channelHandlerContext = ctxChannels.get(0);
//                channelHandlerContext.channel().close();
//                ctxChannels.removeAll(ctxChannels);*/
//
//                StreamName preStreamName = ctxs.get(0);
//                streamManager.remove(preStreamName);
//                ctxs.removeAll(ctxs);
//            }
//            ctxs.add(sn);
           // ctxChannels.add(ctx);
            log.info("===== 接收http flv 请求");
            stream.addHttpFlvSubscriber(ctx.channel());

        }

        if (msg instanceof HttpContent) {

        }

    }

    private void httpResponseStreamNotExist(ChannelHandlerContext ctx, String uri) {
        ByteBuf body = Unpooled.wrappedBuffer(("stream [" + uri + "] not exist").getBytes());
        DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                HttpResponseStatus.NOT_FOUND, body);
        response.headers().set(CONTENT_TYPE, "text/plain");
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

}
