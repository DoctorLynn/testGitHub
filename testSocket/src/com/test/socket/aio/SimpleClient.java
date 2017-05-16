package com.test.socket.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * 客户端程序
 * @author Administrator
 *
 */
public class SimpleClient {

	private AsynchronousSocketChannel client; 
	
	public SimpleClient(String host, int port) throws IOException, InterruptedException, ExecutionException { 
		 this.client = AsynchronousSocketChannel.open(); 
		 Future<?> future = client.connect(new InetSocketAddress(host, port)); 
		 future.get(); 
	} 
	 
	public void write(byte b) { 
		
		 ByteBuffer byteBuffer = ByteBuffer.allocate(32);
		 System.out.println("byteBuffer="+byteBuffer);
		 
		 //向 buffer 写入读取到的字符
		 byteBuffer.put(b); 
		 byteBuffer.flip();
		 
		 System.out.println("byteBuffer="+byteBuffer);
		 client.write(byteBuffer); 
	}
	
}
