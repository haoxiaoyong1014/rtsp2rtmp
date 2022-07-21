package com.xuhuihealth.rtmp.server.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.xuhuihealth.rtmp.server.cfg.MyLiveConfig;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;

public class ResouceTest {
    public static void main(String[] args) throws IOException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        Resource resource = new ClassPathResource("rtmpServer.yml");
        File targetFile = resource.getFile();

        MyLiveConfig cfg = mapper.readValue(targetFile, MyLiveConfig.class);


        int a=1;
    }
}
