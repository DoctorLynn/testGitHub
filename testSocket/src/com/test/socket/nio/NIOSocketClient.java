package com.test.socket.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * NIOSocket 客户端
 * @author Administrator
 */
public class NIOSocketClient {
	
	// 空闲计数器,如果空闲超过10次,将检测server是否中断连接.
	private static int idleCounter = 0;
	//信道选择器 
	private Selector selector;
	//与服务器通信的信道  
	private SocketChannel socketChannel;
	//
	private ByteBuffer temp = ByteBuffer.allocate(1024);
	
	//服务器端的IP和开放的端口
	private static final String IP = "127.0.0.1";
	private static final int PORT = 9999;
	
	//与服务器端保持连接的确认数据
	private JSONObject heart = null;
	private String heartMsg = "";
	
	//是否持续连接中
	private boolean isLongConn = true;
	//重新连接次数
	private static int resConnNum = 0;
	
	//是否有请求数据发送
	private boolean isReq;
	
	//构造函数 - 初始设置心跳数据
	private NIOSocketClient(boolean isReq) {
		this.isReq = isReq;
		heart = new JSONObject();
		try {
			heart.put("reqType", "1002");
			heart.put("placeCode", "1");
			heart.put("time", System.currentTimeMillis());
			heartMsg = heart.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	//启动客户端
	private void startClient() {
		try {
			// 连接远程server
			socketChannel = SocketChannel.open();
			// 如果快速的建立了连接,返回true.如果没有建立,则返回false,并在连接后出发Connect事件.
			Boolean isConnected = socketChannel.connect(new InetSocketAddress(IP, PORT));
			
			// true，则此通道将被置于阻塞模式；如果为 false，则此通道将被置于非阻塞模式
			socketChannel.configureBlocking(false);
			
			//打开并注册选择器到信道.
			this.selector = Selector.open();
			//将选择器绑定到监听信道,只有非阻塞信道才可以注册选择器. - 每个channel只对应一个SelectionKey - 
			SelectionKey key = socketChannel.register(selector, SelectionKey.OP_READ);
			
			//连接上后,发送确认连接数据
			if (isConnected) {
				System.out.println("客户端启动成功...");
				System.out.println("客户端发送确认连接信息1:");
				this.ClientSendMsg();
			} else {
				// 如果连接还在尝试中,则注册connect事件的监听. connect成功以后会出发connect事件.
			    key.interestOps(SelectionKey.OP_CONNECT);
			}
		} catch (Exception e) {
			System.out.println("客户端启动异常: ");
			e.printStackTrace();
		}
		
		//创建一个拥有自动回收线程功能且没有限制的线程池。 - 处理服务端返回的信息和发送心跳数据线程
		ExecutorService executorService = Executors.newCachedThreadPool();
		executorService.execute(new ClientHandleThread());
//		new Thread(new ClientHandleThread()).start();
	}
	
	//处理服务端返回的信息和发送心跳数据线程
	private class ClientHandleThread implements Runnable{
		@Override
		public void run() {
			//反复循环,等待IO
			if(isLongConn){
				while (isLongConn) {
					try {
						// 等待某信道就绪(或超时) - 阻塞,等待事件发生,或者10秒超时. num为发生事件的数量.
						int num = selector.select(10000);
						if (num == 0) {
							idleCounter ++;
							if(idleCounter >10) {
								// 如果server断开了连接,发送消息将失败.
								try {
									System.out.println("客户端发送确认连接信息2:");
								    ClientSendMsg();
								} catch(ClosedChannelException e) {
									System.out.println("客户端信息发送异常: ");
									e.printStackTrace();
									socketChannel.close();
									return;
								}
							}
							continue;
						} else {
							idleCounter = 0;
						}
						
						//取得迭代器.selectedKeys()中包含了每个准备好某一I/O操作的信道的SelectionKey 
						Set<SelectionKey> keys = selector.selectedKeys();
						//Selected-key Iterator 代表了所有通过 select() 方法监测到可以进行 IO 操作的 channel
						Iterator<SelectionKey> it = keys.iterator();
						
						//hasNext()检查序列中是否还有元素
						while (it.hasNext()) {
							//next()获得序列中的下一个元素 - 第一次调用Iterator的next()方法时，它返回序列的第一个元素
							SelectionKey key = it.next();
							//remove()将迭代器新返回的元素删除
							it.remove();
							
							//连接事件发生
							if (key.isConnectable()) {
								// socket 连接中
								SocketChannel sc = (SocketChannel)key.channel();
								
								// 如果正在连接，则完成连接
								if (sc.isConnectionPending()) {
								    sc.finishConnect();
								}
								// 设置成非阻塞 
								sc.configureBlocking(false);
								
								// 这里可以发送信息...
								//sc.write(ByteBuffer.wrap(new String("向服务端发送了一条信息...").getBytes(Charset.forName("UTF-8"))));  
								
								//在和服务端连接成功之后，为了可以接收到服务端的信息，需要给通道设置读的权限。  
			                    sc.register(selector, SelectionKey.OP_READ); 
							}
							// 获得了可读的事件 - 判断是否有数据发送过来
							if (key.isReadable()) {
								//得到事件发生的Socket通道  -  接收服务器端返回的数据.
								SocketChannel sc = (SocketChannel)key.channel();
								//设置读取的缓冲区
								temp = ByteBuffer.allocate(1024);
								int count = sc.read(temp);
								if (count<0) {
									sc.close();
									continue;
								}
								// 切换buffer到读状态,内部指针归位.
								temp.flip();
								String msg = Charset.forName("UTF-8").decode(temp).toString();
								System.out.println("客户端接收到来自服务器端["+sc.getRemoteAddress()+"]的信息:   "+msg+"   =>"+getNowTime()+"\n");
								
								// 接收处理服务器返回的信息 - 
								JSONObject jsonObj = new JSONObject(msg);
								String error_code = "", error_msg = "";
								if(jsonObj != null){
									error_code = jsonObj.getString("error_code");
									error_msg = jsonObj.getString("error_msg");
								}
								
								//判断连接请求服务器返回的信息;连接确认成功服务端返回ok
								if("1".equals(error_code) && "ok".equals(error_msg)){
									//如果没有操作请求数据发送(isReq == false),就发送心跳数据
									if(!isReq){
										//线程休眠每5秒向服务器端发送一次心跳数据
										Thread.sleep(5000);
										
										System.out.println("客户端发送心跳信息2:");
										System.out.println("客户端发送的数据为:   "+heartMsg);
										
										sc.write(ByteBuffer.wrap(heartMsg.getBytes(Charset.forName("UTF-8"))));
									}else{
										//TODO: 这里向服务器端发送请求操作数据, 然后根据服务端状态返回值将isReq置为false
										
										System.out.println("客户端发送心跳信息3:");
										System.out.println("客户端发送的数据为:   "+heartMsg);
										
										sc.write(ByteBuffer.wrap(heartMsg.getBytes(Charset.forName("UTF-8"))));
										isReq = false;
									}
								}else{
									//重新发送确认连接信息
									ClientSendMsg();
								}
								
								// 清空buffer
								temp.clear();
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
						isLongConn = false; break;
					} catch (InterruptedException e) {
						e.printStackTrace();
						isLongConn = false; break;
					} catch (JSONException e) {
						e.printStackTrace();
						isLongConn = false; break;
					}
				}
			}else if(resConnNum < 10){
				//重新连接
				startClient();
				resConnNum ++;
			}else{
				System.out.println("客户端启动失败, 自动重启次数超过10次!");
			}
		}
	}
	
	//客户端发送确认连接数据
	private void ClientSendMsg() throws IOException {
		JSONObject object = new JSONObject();
		try {
			object.put("reqType", "1001");
			object.put("placeCode", "1");
			object.put("time", System.currentTimeMillis());
		} catch (JSONException e) {
			System.out.println("客户端发送数据异常:");
			e.printStackTrace();
		}
		String msg = object.toString();
		System.out.println("客户端发送的数据为:   "+msg);
		
		//写入发送数据
		socketChannel.write(ByteBuffer.wrap(msg.getBytes(Charset.forName("UTF-8"))));
	}
	
	public static String getNowTime(){
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
	}
	
	public static void main(String[] args) {
		//TODO:
		NIOSocketClient client = new NIOSocketClient(true);
		client.startClient();
	}
}
