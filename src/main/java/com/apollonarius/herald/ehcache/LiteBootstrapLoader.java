package com.michaelbruno.clusterlite.ehcache;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.bootstrap.BootstrapCacheLoader;

public class LiteBootstrapLoader implements BootstrapCacheLoader{
	
	public LiteBootstrapLoader(){
		
	}

	public void load(Ehcache cache) throws CacheException {
		//final LiteCacheManagerPeerProvider cachePeerProvider = LiteCacheManagerPeerProvider.getCachePeerProvider(cache);
        //final BootstrapRequest bootstrapRequest = new BootstrapRequest(cache, true, this.maximumChunkSizeBytes);
        //final LiteBootstrapManager bootstrapManager = cachePeerProvider.getBootstrapManager();
        //bootstrapManager.handleBootstrapRequest(bootstrapRequest);
		
		
		// do nothing here?
	}
	

	public boolean isAsynchronous() {
		return true;
	}
	
	@Override
    public Object clone() throws CloneNotSupportedException {
        return new LiteBootstrapLoader();
    }

}
