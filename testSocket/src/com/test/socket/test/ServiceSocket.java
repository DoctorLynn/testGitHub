package com.test.socket.test;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServiceSocket {

	public static void main(String[] args) {
		
		ServiceSocket serviceSocket = new ServiceSocket();
		serviceSocket.ServerSocketStart();
	}
	
	/**
	 * 启动服务端Socket
	 */
	public void ServerSocketStart(){
		//定义一个ServerSocket
		ServerSocket server = null;
		
		/**
		 * Executors提供了一系列工厂方法用于创先线程池，返回的线程池都实现了ExecutorService接口
		 * 创建一个可缓存的线程池，调用execute将重用以前构造的线程（如果线程可用）。
		 * 如果现有线程没有可用的，则创建一个新线   程并添加到池中。终止并从缓存中移除那些已有 60 秒钟未被使用的线程。
		 */
		ExecutorService executor = Executors.newCachedThreadPool();
		
		try {
			// 实例化一个ServerSocket
			server = new ServerSocket(6666);
			System.out.println("S--服务端启动: "+server+" ["+SocketUtil.getNowTime()+"]");
			Socket request = null;
			
			//长连接
			while (true) {
				// 从连接队列中取出一个连接，如果没有则等待
				request = server.accept();
				System.out.println("S--请求连接信息: "+request);
				
				// 当得到请求时，服务器将启动一个线程来处理请求。然后继续监听
				executor.execute(new ServiceSocketHandleDataThread(request));
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("S1 - IOException："+e.getMessage()+" ["+SocketUtil.getNowTime()+"]");
		} finally {
			if (server != null) {
				try {
					server.close();
				} catch (IOException e) {
					e.printStackTrace();
					System.out.println("S2 - IOException："+e.getMessage()+" ["+SocketUtil.getNowTime()+"]");
				}
			}
		}
	}
	
}
