package com.michaelbruno.clusterlite.ehcache;

import java.util.Properties;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.distribution.CacheManagerPeerProvider;
import net.sf.ehcache.distribution.CacheManagerPeerProviderFactory;
import net.sf.ehcache.util.PropertyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LiteCacheManagerPeerProviderFactory extends CacheManagerPeerProviderFactory {
	
	private static final Logger log = LoggerFactory.getLogger(LiteCacheManagerPeerProviderFactory.class);
	
	private static final String PEERS = "peerHosts";
	private static final String PORT = "port";
	private static final String HEARTBEAT_FREQ = "heartbeatFreq";
	private static final String MESSAGE_TIMEOUT = "messageTimeout";
	private static final int PORT_DEF = 7800;
	private static final int HEARTBEAT_FREQ_DEF = 5000;
	private static final int MESSAGE_TIMEOUT_DEF = 1000;
	
	public CacheManagerPeerProvider createCachePeerProvider(CacheManager cacheManager, Properties properties) throws CacheException{
		
		String hosts = PropertyUtil.extractAndLogProperty(PEERS, properties);
		LiteCacheManagerPeerProvider provider = new LiteCacheManagerPeerProvider();
		
		if(hosts==null){
			log.debug("peerHosts property not set");
		}else{
			String[] hostArray = hosts.split(",");
			for(String s:hostArray){
				s = s.trim();
				provider.registerPeer(s);
			}
		}

		provider.setPort(PORT_DEF);
		provider.setHeartbeatFreq(HEARTBEAT_FREQ_DEF);
		provider.setMessageTimeout(MESSAGE_TIMEOUT_DEF);	
		
		String port = PropertyUtil.extractAndLogProperty(PORT , properties);
		if(port!=null){
			try{
				provider.setPort(Integer.valueOf(port));
			}catch(Exception ex){
				log.info("Invalid port, using defaults");
			}
		}
		
		String heartbeatFreq = PropertyUtil.extractAndLogProperty(HEARTBEAT_FREQ, properties);
		if(heartbeatFreq!=null){
			try{
				provider.setHeartbeatFreq(Integer.valueOf(heartbeatFreq));
			}catch(Exception ex){
				log.info("Invalid heartbeatFreq, using defaults");
			}
		}
		
		String messageTimeout = PropertyUtil.extractAndLogProperty(MESSAGE_TIMEOUT, properties);
		if(messageTimeout!=null){
			try{
				provider.setMessageTimeout(Integer.valueOf(messageTimeout));
			}catch(Exception ex){
				log.info("Invalid messageTimeout, using defaults");
			}
		}
		
		return provider;
		
	}

}
