package com.apollonarius.herald;

import java.net.InetAddress;
import java.net.UnknownHostException;

/** 
 * 
 * @author Michael Bruno
 *
 */

public class ClusterAddress{
	
	private final InetAddress inetAddress;
	private final Integer port;
	
	public ClusterAddress(InetAddress inetAddress, Integer port){
		this.inetAddress=inetAddress;
		this.port=port;
	}
	
	public ClusterAddress(byte[] addressBytes, byte[] portBytes) throws UnknownHostException{
		
		byte[] ab;
		
		if(addressBytes.length==4){
			ab = addressBytes;
		}else{
			boolean v4 = true;
			for(int i=0;i<12;i++){
				if(addressBytes[i]!=0){
					v4 = false;
				}
			}
			
			if(v4){
				ab= new byte[4];
				ByteUtil.copyBytes(addressBytes, ab);
			}else{
				ab = addressBytes;
			}
			
		}
		
		this.inetAddress = InetAddress.getByAddress(ab);
		this.port = ByteUtil.bytesToInt(portBytes);
		
	}
	
	public InetAddress getInetAddress() {
		return inetAddress;
	}
	
	public byte[] getAddressBytes(){
		return inetAddress.getAddress();
	}
	
	public Integer getPort() {
		return port;
	}
	
	public byte[] getPortBytes(){
		return ByteUtil.intToBytes(port);
	}

}
