package com.test.socket.test;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONException;
import org.json.JSONObject;

import com.test.socket.udp.UDPSocketClient;

public class ClientSocketHeartBreakThread implements Runnable {
	
	/**
	 * 客户端socket
	 */
	private Socket request;

	/**
	 * 长连接条件标志
	 */
	private boolean isKeepAlive = true;
	
	/**
	 * 保存服务器端返回的数据对象
	 */
	private  SocketVo socketVo;

	//构造方法
	public ClientSocketHeartBreakThread(Socket request) {
		this.request = request;
	}

	@Override
	public void run() {
		//长连接
		while (isKeepAlive) {
			if(request != null){
				try {
					
					// 向服务端发送心跳数据
					SocketUtil.writeStr2Stream(setDataSendService(), request.getOutputStream());
					System.out.println("~C--客户端向服务端发送的信息为："+setDataSendService());
					
					// 获取请求时得到服务端返回的信息
					String reqStr = SocketUtil.readStrFromStream(request.getInputStream());
					System.out.println("~~C --服务端返回的信息：  " + reqStr);
					
					// 处理信息
					ClientHandleMsg(reqStr);
					
					// 睡眠时间小于服务器的setTimeout时间
					Thread.sleep(5000);
				} catch (IOException e) {
					System.out.println("C--客户端心跳线程IOException, 连接已关闭."+" ["+SocketUtil.getNowTime()+"]");
					e.printStackTrace();break;
				} catch (InterruptedException e) {
					System.out.println("C--客户端心跳线程InterruptedException, 连接已关闭."+" ["+SocketUtil.getNowTime()+"]");
					e.printStackTrace();break;
				} catch (JSONException e) {
					System.out.println("C--客户端心跳线程JSONException, 连接已关闭."+" ["+SocketUtil.getNowTime()+"]");
					e.printStackTrace();break;
				}
			}else{
				System.out.println("C--客户端与服务端连接断开，心跳线程 结束！！！"+" ["+SocketUtil.getNowTime()+"]");
			}
		}
	}
	
	//客户端处理服务端返回的信息
	public void ClientHandleMsg(String reqStr) throws JSONException{
		if (reqStr != null && !"".equals(reqStr)) {
			
			//将字符串转为JSONObject
			JSONObject object = new JSONObject(reqStr);
			
			//数据格式:{"reqType":"1003","boxCode":"1","placeCode":"1","dealtype":"20001"}
			String reqType = object.getString("reqType");
				
			//判断请求类型
			if(reqType != null && "1003".equals(reqType)){
				
				//获取\判断操作类型(30001为获取歌曲列表数据)
				String dealtype = object.getString("dealtype");
				if(dealtype != null && "30001".equals(dealtype)){
					
					//调用向服务器请求数据线程,获取歌曲列表信息
					ExecutorService executor = Executors.newCachedThreadPool();
					//192.168.1.190-192.168.1.110
					final String SERVER_IP = "192.168.1.110"; 
					final int PORT = 6666; 
					//短连接
					executor.execute(new ClientSocketRequestThread(true, SERVER_IP, PORT, object));
				}else{
					 
					//广播服务UDPSocket
					UDPSocketClient client = new UDPSocketClient();
					client.InitUDPSocketClient(object);
				}
			}
		}
	}
	
	//返回向服务器端发送的数据
	public String setDataSendService() throws JSONException{
		JSONObject jsonObject = new JSONObject();
		this.getSocketVo();
		
		//判断歌曲列表数据非空
		if(SocketVo.getJsonData() != null && !"".equals(SocketVo.getJsonData())){
			//设置歌曲列表数据
			jsonObject = SocketVo.getJsonData();
		}else{
			//设置心跳数据
			jsonObject.put("reqType", "1002");
			jsonObject.put("placeCode", "1");
		}
		SocketVo.setJsonData(null);
		
		return jsonObject.toString();
	}

	public Socket getRequest() {
		return request;
	}

	public void setRequest(Socket request) {
		this.request = request;
	}

	public boolean isKeepAlive() {
		return isKeepAlive;
	}

	public void setKeepAlive(boolean isKeepAlive) {
		this.isKeepAlive = isKeepAlive;
	}
	
	public SocketVo getSocketVo() {
		return socketVo;
	}

	public void setSocketVo(SocketVo socketVo) {
		this.socketVo = socketVo;
	}
	
}
