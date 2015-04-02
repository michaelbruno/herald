package com.apollonarius.herald;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.xml.bind.DatatypeConverter;

import net.sf.ehcache.Element;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 
 * 
 * @author Michael Bruno
 *
 */

public class ClusterMessageEncoder {

	private static final Logger log = LoggerFactory.getLogger(ClusterMessageEncoder.class);
	private static final String DELIM = "|";
	
	public static String encode(ClusterMessage message){
		
		int byteCount=0;
		
		StringBuilder sb = new StringBuilder();
		sb.append(MessageType.CACHE.toString());
		sb.append(DELIM);
		sb.append(message.getCacheName());
		sb.append(DELIM);
		sb.append(message.getEventType().toString());
		sb.append(DELIM);
		
		try{
			if(message.getKey()!=null){
				ByteArrayOutputStream bsKey = new ByteArrayOutputStream();
				ObjectOutputStream osKey = new ObjectOutputStream(bsKey);
				osKey.writeObject(message.getKey());
				osKey.close();
				byte[] binaryKey = bsKey.toByteArray();
				sb.append(DatatypeConverter.printBase64Binary(binaryKey));
			}
			
			sb.append(DELIM);

			if(message.getElement()!=null){
				ByteArrayOutputStream bsElement = new ByteArrayOutputStream();
				ObjectOutputStream osElement = new ObjectOutputStream(bsElement);
				osElement.writeObject(message.getElement());
				osElement.close();
				byte[] binaryElement = bsElement.toByteArray();
				sb.append(DatatypeConverter.printBase64Binary(binaryElement));
			}
		
		}catch(IOException ex){
			log.warn("Cannot serialize object");
		}
		
		return sb.toString();
	}
	
	public static LiteEventMessage decode(String encodedMessage){
		
		LiteEventMessage message = null;
		
		String[] token = encodedMessage.split("\\|");
		try{
			if(!token[0].equals(MessageType.CACHE.toString())){
				throw new IllegalArgumentException();
			}
			
			String cacheName = token[1];
			CacheEventType eventType = CacheEventType.valueOf(((String)decodeToken(token[2])));
			Serializable key = null;
			Element element = null;
			
			if(!eventType.equals(CacheEventType.REMOVE_ALL)){
				key = (Serializable)decodeToken(token[3]);
				if(eventType.equals(CacheEventType.PUT)){
					element = (Element)decodeToken(token[4]);
				}
			}
			
			message = new LiteEventMessage(cacheName, eventType, key, element);
		
		}catch(Exception ex){
			log.warn("Invalid format for message");
		}
		
		return message;
		
	}
	
	public static Object decodeToken(String token) throws ClassNotFoundException, IOException{
		byte[] data = DatatypeConverter.parseBase64Binary(token);
		ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
		Object obj  = ois.readObject();
        ois.close();
        return obj;
	}

}
