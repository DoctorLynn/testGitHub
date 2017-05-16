package com.test.socket.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

import org.json.JSONObject;

/**
 * 客户端
 */
public class UDPSocketClient {

	public static void main(String[] args){
		//TODO:
	}
	
	public void InitUDPSocketClient(JSONObject object){
		//多播地址
		String host = "225.0.0.1";
		//端口
		int port = 9999;
		
		//{"reqType":"1003","boxCode":"1","placeCode":"1","dealtype":"20001"}
		String message = object.toString();
		System.out.println("U--客户端向服务端发送的信息为："+message);
		
		try {
			InetAddress group = InetAddress.getByName(host);
			MulticastSocket s = new MulticastSocket();
			
			//加入多播组
			s.joinGroup(group);
			DatagramPacket dp = new DatagramPacket(message.getBytes(),message.length(),group,port);
			s.send(dp);
			s.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
