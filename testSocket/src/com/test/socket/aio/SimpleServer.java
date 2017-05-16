package com.test.socket.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ExecutionException;

/**
 * 服务端程序
 * @author Administrator
 * Java AIO
 * AIO 相关的类和接口：
		java.nio.channels.AsynchronousChannel：标记一个 Channel 支持异步 IO 操作；
		java.nio.channels.AsynchronousServerSocketChannel：ServerSocket 的 AIO 版本，创建 TCP 服务端，绑定地址，监听端口等；
		java.nio.channels.AsynchronousSocketChannel：面向流的异步 Socket Channel，表示一个连接；
		java.nio.channels.AsynchronousChannelGroup：异步 Channel 的分组管理，目的是为了资源共享。
		一个 AsynchronousChannelGroup 绑定一个线程池，这个线程池执行两个任务：处理 IO 事件和派发 CompletionHandler。
		AsynchronousServerSocketChannel 创建的时候可以传入一个 AsynchronousChannelGroup，
		那么通过 AsynchronousServerSocketChannel 创建的 AsynchronousSocketChannel 将同属于一个组，共享资源；
		java.nio.channels.CompletionHandler：异步 IO 操作结果的回调接口，用于定义在 IO 操作完成后所作的回调工作。
		AIO 的 API 允许两种方式来处理异步操作的结果：返回的 Future 模式或者注册 CompletionHandler，推荐用 CompletionHandler 的方式，
		这些 handler 的调用是由 AsynchronousChannelGroup 的线程池派发的。这里线程池的大小是性能的关键因素
 */
public class SimpleServer {

	public SimpleServer(int port) throws IOException { 
		
		//把ServerSocket绑定到本地端口上，等待连接
		final AsynchronousServerSocketChannel listener = AsynchronousServerSocketChannel.open().bind(new InetSocketAddress(port));
		
		//监听消息，收到后启动 Handle 处理模块
		listener.accept(null, new CompletionHandler<AsynchronousSocketChannel, Void>() {
			
			public void completed(AsynchronousSocketChannel ch, Void att) {
				// 接受下一个连接 
				listener.accept(null, this);
				// 处理当前连接 
				handle(ch);
			}
			@Override
			public void failed(Throwable exc, Void attachment){
				System.out.println("failed - Throwable:"+exc);
				System.out.println("failed - Void:"+attachment);
			}
		});
		}
		//处理当前连接 
		public void handle(AsynchronousSocketChannel ch) { 
			//开一个 Buffer 
			ByteBuffer byteBuffer = ByteBuffer.allocate(32);
			
			try {
				//读取输入 
				ch.read(byteBuffer).get();
			} catch (InterruptedException e) { 
				e.printStackTrace(); 
			} catch (ExecutionException e) {
				e.printStackTrace(); 
			} 
			
			byteBuffer.flip(); 
			System.out.println("服务端-------:"+byteBuffer.get()); 
			
			// Do something 
		}
	
}
