package com.michaelbruno.clusterlite.ehcache;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.distribution.CacheManagerPeerProvider;
import net.sf.ehcache.distribution.CacheReplicator;
import net.sf.ehcache.distribution.EventMessage;
//import net.sf.ehcache.event.CacheEventListener;
import net.sf.ehcache.distribution.CachePeer;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class AsyncCacheReplicator  implements CacheReplicator{
	
	private static final Logger log = LoggerFactory.getLogger(AsyncCacheReplicator.class);
	
	private static final int BATCH_SIZE = 100;
	private static final String NOCOPY = "Object with key {} cannot be replicated";
	private Queue<LiteEventMessage> replicationQueue; 
	private boolean started;
	private ExecutorService executor = null;
	
	protected boolean replicatePuts;
	protected boolean replicatePutsViaCopy;
	protected boolean replicateUpdates;
	protected boolean replicateUpdatesViaCopy;
	protected boolean replicateRemovals;
	protected Integer replicationInterval;
	protected Integer maxBatchSize;
	
	
	
	public AsyncCacheReplicator(
            boolean replicatePuts,
            boolean replicatePutsViaCopy,
            boolean replicateUpdates,
            boolean replicateUpdatesViaCopy,
            boolean replicateRemovals,
            Integer replicationInterval,
            Integer maxBatchSize
            ) {
           
	      		this.replicatePuts = replicatePuts;
	      		this.replicatePutsViaCopy = replicatePutsViaCopy;
	      		this.replicateUpdates = replicateUpdates;
	        	this.replicateUpdatesViaCopy = replicateUpdatesViaCopy;
	        	this.replicateRemovals = replicateRemovals;
	        	this.replicationInterval = replicationInterval;
	        	this.maxBatchSize = maxBatchSize;
	        	
	        	started = false;
	        	replicationQueue = new ConcurrentLinkedQueue<LiteEventMessage>();
	        	
    }

	public void notifyElementRemoved(Ehcache cache, Element element)
			throws CacheException {
		
	        if (!replicateRemovals) {
	            return;
	        }
	        
	        if(!started){
	        	startReplicationThread(cache);
	        }

	        if (!element.isKeySerializable()) {
	            log.warn(NOCOPY,element.getObjectKey());
	            return;
	        }
	        
	        appendQueue(new LiteEventMessage(cache.getName(), CacheEventType.REMOVE, 
	        		(Serializable)element.getObjectKey(), null));
		
	}

	public void notifyElementPut(Ehcache cache, Element element) throws CacheException {

	        if (!replicatePuts) {
	            return;
	        }
	        
	        if(!started){
	        	startReplicationThread(cache);
	        }

	        if (replicatePutsViaCopy) {
	            if (!element.isSerializable()) {
	            	log.warn(NOCOPY, element.getObjectKey());
	                return;
	            }
	            appendQueue(new LiteEventMessage(cache.getName(), CacheEventType.PUT, null, element));
	        } else {
	            if (!element.isKeySerializable()) {
	            	log.warn(NOCOPY ,element.getObjectKey());
	                return;
	            }
	            appendQueue(new LiteEventMessage(cache.getName(), CacheEventType.REMOVE, 
	            		(Serializable)element.getObjectKey(), null));
	        }
		
	}

	public void notifyElementUpdated(Ehcache cache, Element element)
			throws CacheException {

	        if (!replicateUpdates) {
	            return;
	        }
	        
	        if(!started){
	        	startReplicationThread(cache);
	        }
	        
            if (!element.isSerializable()) {
                log.warn(NOCOPY,element.getObjectKey());
                return;
            }

	        if (replicateUpdatesViaCopy) {
	            appendQueue(new LiteEventMessage(cache.getName(), CacheEventType.PUT, null, element));
	        } else {
	        	appendQueue(new LiteEventMessage(cache.getName(), CacheEventType.REMOVE, 
	        			(Serializable)element.getObjectKey(), null));
	        }
		
	}

	public void notifyElementExpired(Ehcache cache, Element element) {
        if(!started){
        	startReplicationThread(cache);
        }
		// need we do anything else?
	}

	public void notifyElementEvicted(Ehcache cache, Element element) {
        if(!started){
        	startReplicationThread(cache);
        }
		// need we do anything else?
	}

	public void notifyRemoveAll(Ehcache cache) {

        if (!replicateRemovals) {
            return;
        }
        
        if(!started){
        	startReplicationThread(cache);
        }

        appendQueue(new LiteEventMessage(cache.getName(), CacheEventType.REMOVE_ALL, null, null));
		
	}

	public void dispose() {
		// TODO Auto-generated method stub
		
	}
	
    public Object clone() throws CloneNotSupportedException {
        //shutup checkstyle
        super.clone();
        return new AsyncCacheReplicator(replicatePuts, replicatePutsViaCopy,
                replicateUpdates, replicateUpdatesViaCopy, replicateRemovals, replicationInterval,
                maxBatchSize);
    }
    
    protected void appendQueue(LiteEventMessage eventMessage) {
    	replicationQueue.add(eventMessage);
    }

    private void broadcastEvents() {
        List<LiteEventMessage> eventMessages = extractEventMessages(BATCH_SIZE);

        if (!eventMessages.isEmpty()) {
            for (InetAddress address : listRemoteCachePeers(eventMessages.get(0).getCacheName())) {
                try {
                    cachePeer.send(eventMessages);
                } catch (Exception ex) {
                    log.warn("Unable to send message to remote peer.  Message was: {}", ex.getMessage());
                }
            }
        }
    }

    private void broadcastQueue() {
        while (!replicationQueue.isEmpty()) {
        	broadcastQueue();
        }
    }


    private List<LiteEventMessage> extractEventMessages(int limit) {

    	List<LiteEventMessage> list = new ArrayList<LiteEventMessage>(Math.min(replicationQueue.size(), limit));
        
        while (list.size() < limit) {
            LiteEventMessage message = replicationQueue.poll();
            if (message==null) {
                break;
            } else{
                list.add(message);
            }
        }

        return list;
    }

	@SuppressWarnings("unchecked")
	private List<InetAddress> listRemoteCachePeers(Ehcache cache) {
        CacheManagerPeerProvider provider = cache.getCacheManager().getCacheManagerPeerProvider("SOCKET");
        return provider.listRemoteCachePeers(cache);
    }

	public boolean isReplicateUpdatesViaCopy() {
		return replicateUpdatesViaCopy;
	}

	public boolean notAlive() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean alive() {
		// TODO Auto-generated method stub
		return true;
	}
	
	public void startReplicationThread(Ehcache cache){
		started = true;
		executor = Executors.newSingleThreadExecutor();
		executor.execute(new CacheReplicationThread(cache, replicationInterval, maxBatchSize));
		
	}
	

}
