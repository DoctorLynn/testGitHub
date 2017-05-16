package com.test.socket.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
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
 * NIOSocket 服务端
 * @author Administrator
 */
public class NIOSocketService {
	
	//获取处理客户端请求连接的线程
	private static SelectorLoop connectionBell;
	//读取处理客户端发送的数据的线程
	private static SelectorLoop readBell;
	//读取处理客户端发送的数据的线程是否启动
	private boolean isReadBellRunning = false;
	//是否持续连接中
	private boolean isLongConn = true;
	
	//服务器端IP和开放的端口
	private static final String IP = "127.0.0.1";
	private static final int PORT = 9999;
	
	private ExecutorService executorService = null;
	
	public NIOSocketService(){
		//创建一个固定线程大小的线程池
		executorService = Executors.newFixedThreadPool(5);
	}
	
	// 启动服务器
	public void startServer() throws IOException {
		//实例化 监听客户端请求连接信息线程 - 准备好一个闹钟.当有链接进来的时候响.
		connectionBell = new SelectorLoop();
		
		//实例化 读取处理客户端发送信息线程 - 准备好一个闹装,当有read事件进来的时候响.
		readBell = new SelectorLoop();
		
		//打开监听信道  -  开启一个server channel来监听
		ServerSocketChannel ssc = ServerSocketChannel.open();
		// 开启非阻塞模式
		ssc.configureBlocking(false);
		
		//与本地端口绑定 
		ServerSocket socket = ssc.socket();
		socket.bind(new InetSocketAddress(IP,PORT));
		
		// 给闹钟规定好要监听报告的事件,这个闹钟只监听新连接事件. - 为了将Channel和Selector配合使用，必须将channel注册到selector上。通过SelectableChannel.register()方法来实现
		//将选择器绑定到监听信道,只有非阻塞信道才可以注册选择器.并在注册过程中指出该信道可以进行Accept操作 
		ssc.register(connectionBell.getSelector(), SelectionKey.OP_ACCEPT);
		System.out.println("服务器端启动成功...");
		
		//获取处理客户端请求连接线程
		executorService.execute(connectionBell);
//		new Thread(connectionBell).start();
	}
	
	// Selector轮询线程类
	public class SelectorLoop implements Runnable {
		
		//Selector（选择器）是Java NIO中能够检测一到多个NIO通道，并能够知晓通道是否为诸如读写事件做好准备的组件。这样，一个单独的线程可以管理多个channel，从而管理多个网络连接。
		private Selector selector;
		private ByteBuffer temp = ByteBuffer.allocate(1024);
		
		private SelectorLoop() throws IOException {
			//通过调用Selector.open()方法创建一个Selector
			this.selector = Selector.open();
		}
		private Selector getSelector() {
			return this.selector;
		}

		@Override
		public void run() {
			//反复循环,等待IO  
			while(isLongConn) {
				try {
				    // 线程执行selector对象的select方法时进入阻塞状态,只有当至少一个注册的事件发生的时候才会继续.
					this.selector.select();
					
					/**
					 * 当向Selector注册Channel时，register()方法会返回一个SelectionKey对象,这个对象包含:
					 * interest集合 - interest集合是你所选择的感兴趣的事件集合。可以通过SelectionKey读写interest集合
					 *	ready集合 - ready 集合是通道已经准备就绪的操作的集合。在一次选择(Selection)之后，你会首先访问这个ready set
					 *	Channel - Channel  channel  = selectionKey.channel();
					 *	Selector  -Selector selector = selectionKey.selector();
					 *	附加的对象（可选） - 可以将一个对象或者更多信息附着到SelectionKey上，这样就能方便的识别某个给定的通道
					 *
					 *取得迭代器.selectedKeys()中包含了每个准备好某一I/O操作的信道的SelectionKey - Selected-key Iterator 代表了所有通过 select() 方法监测到可以进行 IO 操作的 channel
					 */
					Set<SelectionKey> selectKeys = this.selector.selectedKeys();
					//迭代选择键
					Iterator<SelectionKey> it = selectKeys.iterator();
					//使用hasNext()检查序列中是否还有元素
					while (it.hasNext()) {
						//第一次调用Iterator的next()方法时，它返回序列的第一个元素(next()获得序列中的下一个元素)
						SelectionKey key = it.next();
						//使用remove()将迭代器新返回的元素删除
						it.remove();
						// 处理事件. 可以用多线程来处理.
						this.dispatch(key);
					}
				} catch (IOException e) {
					e.printStackTrace();
					isLongConn = false;break;
				} catch (InterruptedException e) {
					e.printStackTrace();
					isLongConn = false;break;
				}catch (JSONException e) {
					e.printStackTrace();
					isLongConn = false;break;
				}
			}
		}
		//处理事件
		private void dispatch(SelectionKey key) throws IOException, InterruptedException, JSONException {
			//有客户端连接请求时  
			if (key.isAcceptable()) {
				// 这是一个connection accept事件, 并且这个事件是注册在ServerSocketChannel上的.
				ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
				// 接受一个连接.
				SocketChannel sc = ssc.accept();
				
				// 对新的连接的channel注册read事件. 使用readBell闹钟.
				sc.configureBlocking(false);
				sc.register(readBell.getSelector(), SelectionKey.OP_READ);
				
				// 如果读取线程还没有启动,那就启动一个读取线程.
				synchronized(NIOSocketService.this) {
					if (!NIOSocketService.this.isReadBellRunning) {
						NIOSocketService.this.isReadBellRunning = true;
						
						//启动一个读取线程
						executorService.execute(readBell);
//						new Thread(readBell).start();
					}
				}
			} else if (key.isReadable()) {
				// 判断是否有数据发送过来 - 从客户端读取数据
				// 这是一个read事件,并且这个事件是注册在socketchannel上的.
				SocketChannel sc = (SocketChannel) key.channel();
				// 写数据到buffer
				int count = sc.read(temp);
				if (count < 0) {
					// 客户端已经断开连接.
					System.out.println("客户端已经断开连接.");
					key.cancel();
					sc.close();
					return;
				}
				// 切换buffer到读状态,内部指针归位.
				temp.flip();
				
				String msg = Charset.forName("UTF-8").decode(temp).toString();
				System.out.println("\n服务器端接收到来自客户端["+sc.getRemoteAddress()+"]的信息:   "+msg+"                 =>"+getNowTime());
				
				Thread.sleep(1000);
				
				//处理客户端发送的数据并返回信息
				String returnMsg = serviceHandleMsg(msg);
				System.out.println("服务器端返回的信息为:   "+returnMsg+"   =>"+getNowTime());
				
				// 服务器端返回信息.
				sc.write(ByteBuffer.wrap(returnMsg.getBytes(Charset.forName("UTF-8"))));
				
				// 清空buffer
				temp.clear();
			}
		}
		
		//服务器端处理并返回信息
		private String serviceHandleMsg(String msg) throws JSONException {
			JSONObject jsonObject = null;
			if(!"".equals(msg)){
				jsonObject = new JSONObject(msg);
				String reqType = jsonObject.getString("reqType");
				
				//1001为请求连接, 1002为心跳连接
				if("1001".equals(reqType) || "1002".equals(reqType)){
					jsonObject.put("error_msg", "ok");
				}
				jsonObject.put("error_code", "1");
				jsonObject.put("time", System.currentTimeMillis());
			}
			return jsonObject.toString();
		}
	}
	
	public static String getNowTime(){
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
	}
	
	public static void main(String[] args) throws IOException {
		//TODO:
		new NIOSocketService().startServer();
	}
}
