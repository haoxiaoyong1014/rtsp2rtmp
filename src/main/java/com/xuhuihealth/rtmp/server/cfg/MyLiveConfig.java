package com.xuhuihealth.rtmp.server.cfg;

import lombok.Data;

@Data
public class MyLiveConfig {

	public static MyLiveConfig INSTANCE = null;

	int rtmpPort;
	int httpFlvPort;
	boolean saveFlvFile;
	String saveFlVFilePath;
	int handlerThreadPoolSize;
	boolean enableHttpFlv;
	
	
}
