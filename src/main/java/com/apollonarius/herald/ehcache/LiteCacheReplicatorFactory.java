package com.michaelbruno.clusterlite.ehcache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.distribution.CacheManagerPeerProvider;
import net.sf.ehcache.distribution.CacheReplicator;
import net.sf.ehcache.distribution.EventMessage;
import net.sf.ehcache.distribution.CachePeer;
import net.sf.ehcache.event.CacheEventListener;
import net.sf.ehcache.event.CacheEventListenerFactory;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/*
 * I didn't come up with these names don't blame me!
 */
public class LiteCacheReplicatorFactory  extends CacheEventListenerFactory {
	
	private static final Logger log = LoggerFactory.getLogger(LiteCacheReplicatorFactory.class);
	
	private static final int DEFAULT_ASYNCH_INTERVAL_MILLIS = 1000;
    private static final int DEFAULT_ASYNCH_MAXIMUM_BATCH_SIZE = 1000;

    private static final String PUTS = "replicatePuts";
    private static final String PUTS_VIA_COPY = "replicatePutsViaCopy";
    private static final String UPDATES = "replicateUpdates";
    private static final String UPDATES_VIA_COPY = "replicateUpdatesViaCopy";
    private static final String REMOVALS = "replicateRemovals";
    private static final String ASYNCHRONOUSLY = "replicateAsynchronously";
    private static final String ASYNCH_INTERVAL_MILLIS = "asynchronousReplicationIntervalMillis";
    private static final String ASYNCH_MAXIMUM_BATCH_SIZE = "asynchronousReplicationMaximumBatchSize";
    private static final int MIN_REASONABLE_INTERVAL = 10;

	@Override
	public CacheEventListener createCacheEventListener(Properties properties) {
		
		boolean replicatePuts = parseBoolean(PUTS, properties);
		boolean replicatePutsViaCopy = parseBoolean(PUTS_VIA_COPY, properties);
		boolean replicateUpdates = parseBoolean(UPDATES, properties);
		boolean replicateUpdatesViaCopy = parseBoolean(UPDATES_VIA_COPY, properties);
		boolean replicateRemovals = parseBoolean(REMOVALS, properties);
		boolean replicateAsynchronously = parseBoolean(ASYNCHRONOUSLY, properties);
		Integer asynchronousReplicationIntervalMillis = 
				parseInteger(ASYNCH_INTERVAL_MILLIS, properties);
		Integer asynchronousReplicationMaximumBatchSize = 
				parseInteger(ASYNCH_MAXIMUM_BATCH_SIZE, properties);
		
		
		CacheEventListener listener = new AsyncCacheReplicator();
		
		return listener;
	}
	
	
	
	private boolean parseBoolean(String name, Properties properties){
		return false;
	}
	
	private Integer parseInteger(String name, Properties properties){
		return null;
	}

}
