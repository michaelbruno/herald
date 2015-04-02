package com.michaelbruno.clusterlite.ehcache;

import java.io.Serializable;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.distribution.EventMessage;

public class LiteEventMessage implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private final CacheEventType eventType;
	private final Element element;
	private final String cacheName;
	private final Serializable key;
	
	 public LiteEventMessage(String cacheName, CacheEventType eventType, Serializable key, Element element) {
		 	this.cacheName = cacheName;
	        this.eventType = eventType;
	        this.key = key;
	        this.element = element;
	    }

	    public CacheEventType getEventType() {
	        return eventType;
	    }

	    public Element getElement() {
	        return element;
	    }
	    
	    public String getCacheName(){
	    	return cacheName;
	    }
	    
	    public Serializable getKey(){
	    	return key;
	    }

}
