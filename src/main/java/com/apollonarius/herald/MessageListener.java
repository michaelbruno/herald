package com.apollonarius.herald;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageListener extends Thread{

	private static final Logger log = LoggerFactory.getLogger(MessageListener.class);
	
	private ServerSocket serverSocket;
	
	MessageListener(ServerSocket serverSocket){
		this.serverSocket = serverSocket;
	}

	public void run() {
		Socket socket =  null;
		String message;
		boolean process = true;

		try {

			log.info("Listener active");
			while (process) {

				socket = serverSocket.accept();

				try {
					BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					message  = br.readLine();
					log.error("Message Received: {}", message);
					if(message.startsWith(MessageType.CACHE.toString())){
						processCacheEvent(message);
					}else if(message.startsWith(MessageType.CLUSTER.toString())){
						processClusterEvent(message);
					}
				} catch (Exception ex) {
					log.error("listener fail: {}", ex);
				} finally {
					try {
						//ois.close();
						socket.close();
					} catch (Exception ex) {
						log.error("EOM listener fail: {}", ex);
					}
				}
			}
		} catch (SocketException ex) {
			log.debug("socket interrupted, shutting down");
		} catch (Exception ex){
			log.error("cannot access socket, shutting down");
		}
	}
	/*
	private void evict(String message){
		
		log.error("IN EVICT CLUSTER WORKER NOW!!!!");
	
		String[] tokens = message.split("\\|");
		ApplicationContext actx = Holders.getGrailsApplication().getMainContext();
		SimpleCacheService cacheService = (SimpleCacheService)actx.getBean("simpleCacheService");
		if(tokens[0].equalsIgnoreCase("evict")){
			cacheService.removeObject(tokens[2], tokens[1],false);
			log.debug("evicted object {} from cache {}", tokens[1], tokens[2]);
		}else if(tokens[0].equalsIgnoreCase("evictAll")){
			cacheService.flush(tokens[1],false);
		}else{
			log.debug("No VALID commands");
		}
		
	}
	*/
	private void processClusterEvent(String rawMessage){
		
	}
	
	private void processCacheEvent(String rawMessage){
		
	}
}
