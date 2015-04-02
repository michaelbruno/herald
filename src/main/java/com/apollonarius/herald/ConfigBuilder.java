package com.apollonarius.herald;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigBuilder {
	
	private static final Logger log = LoggerFactory.getLogger(ConfigBuilder.class);
	
	public static final String HEARTBEAT_INTERVAL = "hearbeatInterval";
	public static final String MESSAGE_TIMEOUT = "messageTimeout";
	public static final String PEERS = "peers";
	public static final String LISTEN_ADDRESS  = "listenAddress";
	public static final Integer HEARTBEAT_INTERVAL_DEF = 500;
	public static final Integer MESSAGE_TIMEOUT_DEF = 500;
	public static final Integer LISTEN_PORT_DEF = 7801;
	
	protected Properties props;
	
	public ConfigBuilder(Properties props){
		this.props = props;
	}
	
	public Integer getHeartbeatInterval(){
		
		Integer val = HEARTBEAT_INTERVAL_DEF;
		String s = (String)props.get(HEARTBEAT_INTERVAL);
		
			if(s!=null){
				try{
					val = Integer.valueOf(s);
				}catch(NumberFormatException ex){
					log.warn("Invalid heartbeatInterval, using default");
				}
			}
		
			return val;
	}
	
	
	public Integer getMessageTimeout(){
		
		Integer val = MESSAGE_TIMEOUT_DEF;
		String s = (String)props.get(MESSAGE_TIMEOUT);
		
			if(s!=null){
				try{
					val = Integer.valueOf(s);
				}catch(NumberFormatException ex){
					log.warn("Invalid heartbeatInterval, using default");
				}
			}
		
			return val;
	}
	
	public ClusterAddress getListenAddress() throws UnknownHostException{
		String s = (String)props.get(LISTEN_ADDRESS);
		ClusterAddress ca = null;
		if(s==null){
			ca = new ClusterAddress(InetAddress.getLocalHost(), LISTEN_PORT_DEF);
		}
		
		return ca;
	}
	
	/*
	protected Integer hearbeatInterval;
	protected Integer messageTimeout;
	protected List<ClusterAddress> peers;
	protected ClusterAddress address;
	protected ServerSocket serverSocket;
	protected ExecutorService executor;
*/
}
