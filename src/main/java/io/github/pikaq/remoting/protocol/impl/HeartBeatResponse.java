package io.github.pikaq.remoting.protocol.impl;

import io.github.pikaq.remoting.protocol.ResponsePacket;

public class HeartBeatResponse implements ResponsePacket {

	public static final HeartBeatResponse INSTANCE = new HeartBeatResponse();
}