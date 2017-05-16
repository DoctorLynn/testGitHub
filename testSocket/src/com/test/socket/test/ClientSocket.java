package com.test.socket.test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientSocket {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// 创建一个可缓存的线程池，调用execute将重用以前构造的线程（如果线程可用）;如果现有线程没有可用的，则创建一个新线   程并添加到池中。终止并从缓存中移除那些已有 60 秒钟未被使用的线程。
		ExecutorService executor = Executors.newCachedThreadPool();
		
		//119.29.84.88 - 19001
		//192.168.20.113
		//192.168.1.110
		//192.168.1.190
		final String SERVER_IP = "119.29.84.88"; 
		final int PORT = 19001; 

		//短连接
		//executor.execute(new ClientSocketRequestThread(false,SERVER_IP, PORT, null));
		
		/**
		 * 参数1  true: 长连接(false:短连接)
		 * 参数2  SERVER_IP:服务端IP
		 * 参数3  PORT:服务端开放的端口
		 * 参数4  向服务端发送的数据对象JSONObject格式
		 */
		executor.execute(new ClientSocketRequestThread(true, SERVER_IP, PORT, null));
	}
	
}
