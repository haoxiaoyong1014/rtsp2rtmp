package com.xuhuihealth.rtmp.server.rtmp.messages;

public abstract class RtmpControlMessage extends RtmpMessage {

	@Override
	public int getOutboundCsid() {
		return 2;
	}
}
