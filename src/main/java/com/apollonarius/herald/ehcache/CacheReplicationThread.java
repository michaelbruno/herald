package com.michaelbruno.clusterlite.ehcache;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CacheReplicationThread extends Thread{
	
	private static final Logger log = LoggerFactory.getLogger(CacheReplicationThread.class);
	
	protected LiteCacheManagerPeerProvider peerProvider;
	protected Queue<LiteEventMessage> replicationQueue;
	
	public CacheReplicationThread(LiteCacheManagerPeerProvider peerProvider, 
			Queue<LiteEventMessage> replicationQueue, Integer replicationInterval, Integer maxBatchSize){
		this.peerProvider=peerProvider;
		this.replicationQueue = replicationQueue;
	}

	
	public void run(){
		boolean running=true;
		
		try{
			while(running){
			
	            // build message
				
				while(replicationQueue!=null && replicationQueue.isEmpty()){
					this.sleep(500);
				}
				transmitBatch();
				
			}
		}catch(Exception ex){
			
		}
	}
	
	
	
	private void transmitBatch(){
		int max = replicationQueue.size();
		StringBuilder sb = new StringBuilder();
		for(int i=0;i<max;i++){
			LiteEventMessage eventMessage = replicationQueue.poll();
			sb.append(EventMessageEncoder.encode(eventMessage));
			sb.append("\n");
		}
		
		for(InetAddress address:peerProvider.listRemoteCachePeers()){
			try{
				log.debug("OPENING SOCKET");
				Socket client = new Socket();
				client.connect(new InetSocketAddress(
						address, peerProvider.getPort()),peerProvider.getMessageTimeout());
				log.debug("Just connected to {}", client.getRemoteSocketAddress());
				OutputStream outStream = client.getOutputStream();
				outStream.write(sb.toString().getBytes("utf-8"));
				outStream.close();
				client.close();
			}catch(IOException ex){
				log.error("failed to send replication message to {}", address);
			}
		}
	}
}
