package com.apollonarius.herald;


import java.net.InetAddress;

import net.sf.ehcache.distribution.CachePeer;

/** 
 * 
 * @author Michael Bruno
 *
 */

public class ClusterEventThread extends Thread{
	
	protected InetAddress address;
	protected LiteCacheManagerPeerProvider peerProvider;
	
	public ClusterEventThread(LiteCacheManagerPeerProvider peerProvider, InetAddress address){
		this.address=address;
		this.peerProvider=peerProvider;
	}

	
	public void run(){
		boolean running=true;
		
		try{
			while(running){
			
	            for (CachePeer cachePeer : peerProvider.  (eventMessages.get(0).getEhcache())) {
	                try {
	                    cachePeer.send(eventMessages);
	                } catch (Exception ex) {
	                    log.warn("Unable to send message to remote peer.  Message was: {}", ex.getMessage());
	                }
	            }
			
				this.sleep(500);
			}
		}catch(Exception ex){
			
		}
	}
	
	
	
	private String createAliveMessage(){
		StringBuilder sb = new StringBuilder();
		
		sb.append(MessageType.CLUSTER.toString());
		sb.append("|");
		sb.append(ClusterEventType.ALIVE.toString());
		sb.append("|");
		sb.append(address.getHostAddress());
		
		return sb.toString();
	}
}
