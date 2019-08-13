package io.github.pikaq.initialization;


import akka.actor.ActorSystem;
import com.typesafe.config.ConfigFactory;
import io.github.pikaq.ClientChannelInfoManager;
import io.github.pikaq.client.ClientRemoteCommandtDispatcher;
import io.github.pikaq.common.util.SingletonFactoy;
import io.github.pikaq.extension.ExtensionLoader;
import io.github.pikaq.initialization.support.Initable;
import io.github.pikaq.protocol.command.DefaultRemoteCommandFactory;
import io.github.pikaq.protocol.command.RemoteCommandFactory;
import io.github.pikaq.protocol.serialization.Serializer;
import io.github.pikaq.server.ServerRemoteCommandtDispatcher;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FrameworkInit implements Initable {

	
	@Override
	public int getOrder() {
		return HIGHEST_LEVEL;
	}

	@Override
	public void init() {
		log.info("FrameworkInit init.");
		
		//加载SPI
		ExtensionLoader.initExtension(Serializer.class);
		
		//注册单例
		
		//远程命令工厂
		SingletonFactoy.register(RemoteCommandFactory.class, new DefaultRemoteCommandFactory());
		//客户端命令分发器
		SingletonFactoy.register(ClientRemoteCommandtDispatcher.class, new ClientRemoteCommandtDispatcher());
		//服务端命令分发器
		SingletonFactoy.register(ServerRemoteCommandtDispatcher.class, new ServerRemoteCommandtDispatcher());
		//服务端客户端信息管理器
		SingletonFactoy.register(ClientChannelInfoManager.class, new ClientChannelInfoManager());
		//akka
		SingletonFactoy.register(ActorSystem.class, initActorSystem());
	}
	
	
	//初始化ActorSystem
	private static ActorSystem initActorSystem() {
		StringBuilder s = new StringBuilder();
		s.append("akka.loggers = [\"akka.event.slf4j.Slf4jLogger\"], ");
		s.append("akka.logging-filter = \"akka.event.slf4j.Slf4jLoggingFilter\", ");
		if (log.isTraceEnabled()) {
			s.append("akka.loglevel = \"TRACE\", ");
		} else if (log.isDebugEnabled()) {
			s.append("akka.loglevel = \"DEBUG\", ");
		} else if (log.isInfoEnabled()) {
			s.append("akka.loglevel = \"INFO\", ");
		} else if (log.isWarnEnabled()) {
			s.append("akka.loglevel = \"WARNING\", ");
		} else if (log.isErrorEnabled()) {
			s.append("akka.loglevel = \"ERROR\", ");
		}
		return ActorSystem.create("system", ConfigFactory.parseString(s.toString()));
	}
	
}
