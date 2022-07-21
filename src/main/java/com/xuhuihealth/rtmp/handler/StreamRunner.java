package com.xuhuihealth.rtmp.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import java.util.List;
import java.util.Map;

/*@Component
@Slf4j
@Order(1)*/
public class StreamRunner implements ApplicationRunner, EnvironmentAware {

    @Autowired
    private StreamService streamService;

    private Binder binder;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Map config;
        List<Map> configs = binder.bind("url", Bindable.listOf(Map.class)).get();
        // 遍历从数据源
        for (int i = 0; i < configs.size(); i++) {
            config = configs.get(i);
            // 获取数据源的key，以便通过该key可以定位到数据源
            String streamUrl = config.get("stream-url").toString();
            //log.error("源：{}", streamUrl);
            //streamService.play(streamUrl,i);
        }
    }

    @Override
    public void setEnvironment(Environment environment) {
        binder = Binder.get(environment);
    }
}
