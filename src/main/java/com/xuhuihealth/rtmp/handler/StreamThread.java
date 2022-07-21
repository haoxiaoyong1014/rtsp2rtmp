package com.xuhuihealth.rtmp.handler;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * @author haoxiaoyong
 * @date created at 下午2:41 on 2022/3/1
 * @github https://github.com/haoxiaoyong1014
 * @blog www.haoxiaoyong.cn
 */
@Slf4j
public class StreamThread implements Runnable {

    private String streamUrl;

    private Long i;

    public StreamThread(String streamUrl, Long i) {
        this.streamUrl = streamUrl;
        this.i = i;
    }

    @Override
    public void run() {
        try {
            new ConvertVideoPakcet().from(streamUrl)
                    .to("rtmp://localhost:1935/live/" + i)
                    .go()
                    .close();
        } catch (IOException e) {
            log.error("tranform process error!", e);
        }
    }
}
