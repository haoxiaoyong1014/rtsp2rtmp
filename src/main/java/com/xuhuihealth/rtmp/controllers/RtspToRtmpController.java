package com.xuhuihealth.rtmp.controllers;

import com.xuhuihealth.rtmp.handler.StreamThread;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RestController
@RequestMapping("fy")
public class RtspToRtmpController {

   /* @Autowired
    private StreamService streamService;*/

    public static final ConcurrentMap<String, Long> STREAM_ID_MAP = new ConcurrentHashMap<>();
    public static final ConcurrentMap<Long, String> ID_STREAM_MAP = new ConcurrentHashMap<>();

    private static Map<Long, String> maps = new HashMap<>();
    private static final AtomicInteger poolNumber = new AtomicInteger(1);
    private static ExecutorService rtspToRtmp
            = Executors.newFixedThreadPool(64, new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            t.setName("rtsp-thread-pool-" + poolNumber.getAndIncrement() + "to rtmp");
            t.setDaemon(true);
            return t;
        }
    });


    @RequestMapping("rtsp2rtmp")
    public Long rtsp2rtmp(String streamUrl, Long i) {

        log.info("rtsp2rtmp，{},{}", streamUrl, i);
        if (StringUtils.isEmpty(streamUrl) || i == null){
            return 0L;
        }
        return buildStreamMap(streamUrl, i);
    }

    public synchronized Long buildStreamMap(String streamUrl, Long i){

        // 已经有相同的id号了，返回错误，前端处理
        if (ID_STREAM_MAP.containsKey(i)){
            return 0L;
        }
        ID_STREAM_MAP.put(i, streamUrl);

        if (!STREAM_ID_MAP.containsKey(streamUrl)){
            rtspToRtmp.execute(new StreamThread(streamUrl, i));
            STREAM_ID_MAP.put(streamUrl, i);
        }
        return  i;
    }

    public static synchronized String getRealStreamId(String streamId){

        if (StringUtils.isEmpty(streamId)){
            return null;
        }

        Long id = Long.valueOf(streamId);
        if (ID_STREAM_MAP.containsKey(id)){
            return String.valueOf(STREAM_ID_MAP.get(ID_STREAM_MAP.get(id)));
        }

        return null;
    }

    public static synchronized void removeBoth(String streamId){
        if (StringUtils.isEmpty(streamId)){
            return ;
        }

        Long id = Long.valueOf(streamId);
        if (ID_STREAM_MAP.containsKey(id)){
            STREAM_ID_MAP.remove(ID_STREAM_MAP.get(id));
            ID_STREAM_MAP.remove(id);
        }
    }
}
