package com.apollonarius.herald;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;

/** 
 * 
 * @author Michael Bruno
 *
 */

public class ClusterMessage implements Serializable {

	private static final long serialVersionUID = 1L;
	public static final byte PROLOG = 0x01;
	public static final byte EPILOG = 0x04;
	
	private byte[] senderAddressBytes = new byte[16];
	private byte[] senderPortBytes = new byte[4];
	private byte[] clusterNameBytes = new byte[64];
	private byte[] messageTypeBytes = new byte[64];
	private byte[] payloadLengthBytes = new byte[4];
	private byte[] payload;
	
	public ClusterMessage(ClusterAddress senderAddress, 
			String clusterName, String messageType, byte[] payload){
		
		this.senderAddressBytes = senderAddress.getAddressBytes();
		this.senderPortBytes = ByteUtil.intToBytes(senderAddress.getPort());
		this.messageTypeBytes = messageType.getBytes();
		this.payloadLengthBytes = ByteUtil.intToBytes(payload.length);
		this.payload = payload;
	}

	public ClusterAddress getSenderAddress() {
		ClusterAddress ca = null;
		
		try{	
			ca = new ClusterAddress(senderAddressBytes, senderPortBytes);
		}catch(UnknownHostException ex){
			
		}
		return ca;
	}
	
	public byte[] getSenderAddressBytes(){
		return senderAddressBytes;
	}

	public String getMessageType() {
		return new String(messageTypeBytes);
	}
	
	public Integer getPayloadLength(){
		return ByteUtil.bytesToInt(payloadLengthBytes);
	}
	
	public byte[] getMessageTypeBytes(){
		return messageTypeBytes;
	}
	
	public byte[] getPayload() {
		return payload;
	}
	
	public byte[] getBytes(){
		
		//byte a = new byte
		
	}
	
}
