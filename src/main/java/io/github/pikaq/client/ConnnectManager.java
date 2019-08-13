package io.github.pikaq.client;

import io.github.pikaq.common.util.NameThreadFactoryImpl;
import io.github.pikaq.common.util.RemotingUtils;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 连接管理器
 * 
 * @author pleuvoir
 *
 */
public class ConnnectManager {

	protected static final Logger LOG = LoggerFactory.getLogger(ConnnectManager.class);

	public static final ConnnectManager INSTANCE = new ConnnectManager();

	public ConcurrentHashMap<String, Channel> tables = new ConcurrentHashMap<String, Channel>();

	public synchronized void putChannel(Channel channel) {
		String addr = RemotingUtils.parseChannelRemoteAddr(channel);
		if (!validate(channel)) {
			return;
		}
		Channel prev = tables.putIfAbsent(addr, channel);
		if (prev != null && validate(prev)) {
			return;
		}
		tables.put(addr, channel);
	}

	public void fireHoldTask() {
		Executors.newSingleThreadScheduledExecutor((new NameThreadFactoryImpl("hold_conn")))
				.scheduleAtFixedRate(new Runnable() {
					@Override
					public void run() {
						tables.forEach((k, v) -> {
							if (!validate(v)) {
								LOG.debug("剔除连接通道：{}", v.localAddress());
								removeChannel(k);
							}
						});
					}
				}, 5, 30, TimeUnit.SECONDS);
	}

	public void printAliveChannel() {
		Executors.newSingleThreadScheduledExecutor((new NameThreadFactoryImpl("hold_conn")))
				.scheduleAtFixedRate(new Runnable() {
					@Override
					public void run() {
						tables.forEach((k, v) -> {
							LOG.debug("目前存活的通道：{}", v.localAddress());
						});
					}
				}, 60, 300, TimeUnit.SECONDS);
	}

	public synchronized void removeChannel(String addr) {
		tables.remove(addr);
	}

	public boolean validate(Channel channel) {
		return channel != null && channel.isActive();
	}

	private ConnnectManager() {
	}
}