package com.xuhuihealth.rtmp.handler;

import com.xuhuihealth.rtmp.server.HttpFlvServer;
import com.xuhuihealth.rtmp.server.RTMPServer;
import com.xuhuihealth.rtmp.server.cfg.MyLiveConfig;
import com.xuhuihealth.rtmp.server.manager.StreamManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Order(0)
public class NettyRunner implements ApplicationRunner {
    @Override
    public void run(ApplicationArguments args) throws Exception {

        readConfig();
        StreamManager streamManager = new StreamManager();

        int rtmpPort = MyLiveConfig.INSTANCE.getRtmpPort();
        int handlerThreadPoolSize = MyLiveConfig.INSTANCE.getHandlerThreadPoolSize();
        //1935
        RTMPServer rtmpServer = new RTMPServer(rtmpPort, streamManager, handlerThreadPoolSize);
        rtmpServer.run();

        if (!MyLiveConfig.INSTANCE.isEnableHttpFlv()) {
            return;
        }
        //18082
        int httpPort = MyLiveConfig.INSTANCE.getHttpFlvPort();
        HttpFlvServer httpFlvServer = new HttpFlvServer(httpPort, streamManager, handlerThreadPoolSize);
        httpFlvServer.run();

    }

    //init config
    private static void readConfig() {
        // ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        try {
        /*    Resource resource = new ClassPathResource("rtmpServer.yml");
            File file = resource.getFile();

            MyLiveConfig cfg = mapper.readValue(file, MyLiveConfig.class);
            log.info("读取rtmp服务器配置信息 : {}", cfg);*/

            MyLiveConfig cfg = new MyLiveConfig();
            cfg.setEnableHttpFlv(true);
            cfg.setRtmpPort(1935);
            cfg.setHttpFlvPort(18082);
            cfg.setSaveFlvFile(false);
            cfg.setHandlerThreadPoolSize(16);
            MyLiveConfig.INSTANCE = cfg;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
