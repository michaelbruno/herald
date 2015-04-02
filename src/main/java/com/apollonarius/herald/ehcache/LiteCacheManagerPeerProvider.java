package com.michaelbruno.clusterlite.ehcache;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.distribution.CacheManagerPeerProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@SuppressWarnings("rawtypes")
public class LiteCacheManagerPeerProvider  implements CacheManagerPeerProvider{
	
	private static final Logger log = LoggerFactory.getLogger(LiteCacheManagerPeerProvider.class);
	
	private ExecutorService executor = null;
	
	protected Map<InetAddress, Long> peerMap;
	protected CacheManager cacheManager;
	protected Integer port;
	protected Integer messageTimeout;
	protected Integer heartbeatFreq;
	protected ServerSocket serverSocket;
	
	
	public LiteCacheManagerPeerProvider() {
        super();
    }

	public void init(){
		
		try{
			serverSocket = new ServerSocket(port, 1000, InetAddress.getByName("0.0.0.0"));
			executor = Executors.newSingleThreadExecutor();
			executor.execute(new MessageListener(serverSocket));
			log.info("MessageListener initialized.");
		}catch(IOException ex){
			log.error("failed to initialize listener, no replication will occur.");
		}
		
	}
	
	
	public List listRemoteCachePeers(Ehcache cache){
		return listRemoteCachePeers();
	}
	
	public List<InetAddress> listRemoteCachePeers(){
		List<InetAddress> peerList  = new ArrayList<InetAddress>();
		
		for(InetAddress inet:peerMap.keySet()){
			peerList.add(inet);
		}
		
		return peerList;
	}
	
	
	public void registerPeer(String address){
		try{
			InetAddress inet = InetAddress.getByName(address);
			Date d = new Date();
			peerMap.put(inet, new Long(d.getTime()));
			log.debug("Peer {} registered", address);
		}catch(UnknownHostException ex){
			log.error("Unable to register peer:{}",address);
		}
		
	}
	
	public void unregisterPeer(String address){
		try{
			InetAddress inet = InetAddress.getByName(address);
			peerMap.remove(inet);
			log.debug("Peer {} unregistered", address);
		}catch(UnknownHostException ex){
			log.error("Unable to unregister peer:{}",address);
		}
	}
	
	public void dispose(){

		try{
			serverSocket.close();
			log.info("cluster socket closed");
		}catch(Exception ex){
			log.info("unable to close cluster socket: {}", ex);
		}
		
		if(executor!=null){
			executor.shutdown();
		}
	}
	
	public Integer getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public Integer getMessageTimeout() {
		return messageTimeout;
	}

	public void setMessageTimeout(Integer messageTimeout) {
		this.messageTimeout = messageTimeout;
	}

	public Integer getHeartbeatFreq() {
		return heartbeatFreq;
	}

	public void setHeartbeatFreq(Integer heartbeatFreq) {
		this.heartbeatFreq = heartbeatFreq;
	}

	public String getScheme(){
		return "SOCKET";
	}
	
	public long getTimeForClusterToForm(){
		return 0;
	}
	
}
