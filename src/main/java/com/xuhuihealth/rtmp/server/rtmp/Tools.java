package com.xuhuihealth.rtmp.server.rtmp;

import java.util.Random;

public class Tools {
	private static Random random = new Random();;
	public static byte[] generateRandomData(int size) {
		byte[] bytes = new byte[size];
		random.nextBytes(bytes);
		return bytes;
	}
}

