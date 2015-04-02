package com.apollonarius.herald;

import java.net.ServerSocket;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 
 * 
 * @author Michael Bruno
 *
 */

public class ClusterRegulator {
	
	private static final Logger log = LoggerFactory.getLogger(ClusterRegulator.class);
	
	protected Integer hearbeatInterval;
	protected Integer messageTimeout;
	protected List<ClusterAddress> peers;
	protected List<String> messageHandlers;
	protected ClusterAddress address;
	protected ServerSocket serverSocket;
	protected ExecutorService executor;
	
	
	public ClusterRegulator(Properties properties){
		
		
		
	}
	
	@PostConstruct
	public void initialize() throws Exception{
		
		ServerSocket serverSocket = new ServerSocket(address.getPort(), 100, 
				address.getInetAddress());

		log.info("Starting cluster listener....");
		executor = Executors.newSingleThreadExecutor();
		executor.execute(new MessageListener(serverSocket));
		log.info("done with post construct");
	}
	
	@PreDestroy
	public void shutdown(){

		try{
			serverSocket.close();
			log.info("cluster socket closed");
		}catch(Exception ex){
			log.warn("unable to close cluster socket: {}", ex);
		}
		
		if(executor!=null){
			executor.shutdown();
		}
	}

	public Integer getHearbeatInterval() {
		return hearbeatInterval;
	}

	public void setHearbeatInterval(Integer hearbeatInterval) {
		this.hearbeatInterval = hearbeatInterval;
	}

	public Integer getMessageTimeout() {
		return messageTimeout;
	}

	public void setMessageTimeout(Integer messageTimeout) {
		this.messageTimeout = messageTimeout;
	}

	public List<ClusterAddress> getPeers() {
		return peers;
	}

	public void setPeers(List<ClusterAddress> peers) {
		this.peers = peers;
	}

	public ClusterAddress getAddress() {
		return address;
	}

	public void setAddress(ClusterAddress address) {
		this.address = address;
	}

}
