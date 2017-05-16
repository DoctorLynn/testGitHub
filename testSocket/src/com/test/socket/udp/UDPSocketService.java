package com.test.socket.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * UDPSocket服务器端
 */
public class UDPSocketService {

	public static void main(String[] args){
		
		UDPSocketService service = new UDPSocketService();
		service.InitUDPSocketService();
	}
	
	//初始化UDPSocket服务器端
	public void InitUDPSocketService(){
		//多播地址
		final String host = "225.0.0.1";
		//端口
		final int port = 9999;
		//每次读取数据长度
		final int length = 2048;
		
		//字节数组
		byte[] buf = new byte[length];
		
		//定义本程序的MulticastSocket实例；MulticastSocket可以将数据报以广播的方式发送到多个客户端，也能够接收其它主机的广播信息-DatagramSocket只允许数据报发送给指定的目标地址
		MulticastSocket ms = null;
		//数据报的载体。如果用来接收数据
		DatagramPacket dp = null;
		StringBuffer sbuf = new StringBuffer();
		
		try {			
			//创建用于发送、接收数据的MulticastSocket对象；使用本机默认地址、指定端口来创建一个MulticastSocket对象
			ms = new MulticastSocket(port);
			//将数据包中length长的数据装进buf数组。（以指定字节数组创建准备接受数据的DatagramPacket对象）
			dp = new DatagramPacket(buf, length);
			
			//加入多播地址
			InetAddress group = InetAddress.getByName(host);
			//将该socket加入指定的多点广播地址
			ms.joinGroup(group);
			System.out.println("U - UDPSocket服务器端打开：");
			
			//receive()是阻塞方法，会等待客户端发送过来的信息
			ms.receive(dp);
			ms.close();
			
			//读取信息
			int i;
			for(i=0;i<length;i++){
				if(buf[i] == 0){
					break;
				}
				sbuf.append((char) buf[i]);
			}			
			System.out.println("U - UDPSocket服务器端收到客户端消息：" + sbuf.toString());
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
}
