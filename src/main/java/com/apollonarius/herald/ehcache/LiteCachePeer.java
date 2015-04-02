package com.michaelbruno.clusterlite.ehcache;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LiteCachePeer{
	
	private static final Logger log = LoggerFactory.getLogger(LiteCachePeer.class);

    //private final String hostname;
    //private final Integer rmiRegistryPort;
    //private Integer remoteObjectPort;
    private final Ehcache cache;
    
    public LiteCachePeer(Ehcache cache){
    	this.cache = cache;
    }

	public void put(Element element) throws IllegalArgumentException,
			IllegalStateException{
		cache.put(element, true);
	}

	public boolean remove(Serializable key) throws IllegalStateException{
		return cache.remove(key, true);
	}

	public void removeAll() throws IllegalStateException {
		cache.removeAll(true);
	}

	public void send(List<LiteEventMessage> eventMessages) {
		for (int i = 0; i < eventMessages.size(); i++) {
            LiteEventMessage eventMessage = eventMessages.get(i);
            if (eventMessage.getEventType() == CacheEventType.PUT) {
                put(eventMessage.getElement());
            } else if (eventMessage.getEventType() == CacheEventType.REMOVE) {
                remove(eventMessage.getKey());
            } else if (eventMessage.getEventType() == CacheEventType.REMOVE_ALL) {
                removeAll();
            } else {
                log.error("Unknown event: {}", eventMessage);
            }
        }
	}

	public String getName() throws RemoteException {
		return cache.getName();
	}

	public String getGuid() throws RemoteException {
		return cache.getGuid();
	}

	//public String getUrl() throws RemoteException {
		// TODO Auto-generated method stub
	//	return null;
	//}

	//public String getUrlBase() throws RemoteException {
		// TODO Auto-generated method stub
	//	return null;
	//}

	public List getKeys(){
		List keys = cache.getKeys();
        if (keys instanceof Serializable) {
            return keys;
        }
        return new ArrayList(keys);
	}

	public Element getQuiet(Serializable key) throws RemoteException {
		return cache.getQuiet(key);
	}

	public List<Element> getElements(List keys) throws RemoteException {
		
        List<Element> elements = new ArrayList<Element>();
        if(keys!=null){
        	for (int i = 0; i < keys.size(); i++) {
        		Serializable key = (Serializable)keys.get(i);
        		Element element = cache.getQuiet(key);
        		if (element != null) {
        			elements.add(element);
        		}
        	}
        }
        return elements;
	}
	
	

}
