package com.test.socket.aio;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * Main 函数
 * @author Administrator
 *
 */
public class AIODemoTest {

	public void testServer() throws IOException, InterruptedException { 
		 SimpleServer server = new SimpleServer(9999); 
		 
		 //由于是异步操作，所以睡眠一定时间，以免程序很快结束
		 Thread.sleep(10000);
	} 
	 
	public void testClient() throws IOException, InterruptedException, ExecutionException { 
		SimpleClient client = new SimpleClient("127.0.0.1", 9999); 
		client.write((byte) 11); 
	}
	 
	public static void main(String[] args){
		AIODemoTest demoTest = new AIODemoTest();
		
		//服务端
		try {
			demoTest.testServer();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//客户端
		try {
			demoTest.testClient();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
