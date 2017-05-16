package com.test.socket.test;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.Executors;

import org.json.JSONException;
import org.json.JSONObject;

public class ClientSocketRequestThread implements Runnable {
	
	//服务器端IP地址
	private String ip;
	
	//服务器端开放的端口
	private int port;
	
	//向服务器端发送的信息参数对象
	private JSONObject object;
	
	//是否保持长连接
	private boolean isLongConnection = false;
	
	//接收服务器端返回的数据对象
	private  SocketVo socketVo;

	//构造方法
	public ClientSocketRequestThread(boolean isLongConnection, String ip, int port, JSONObject object) {
		this.isLongConnection = isLongConnection;
		this.ip = ip;
		this.port = port;
		this.object = object;
	}
	
	@Override
	public void run() {
		//定义一个空Socket
		Socket request = null;
		try {
			System.out.println("C -- 客户端启动..."+" ["+SocketUtil.getNowTime()+"]");
			
			// 连接到Socket服务器
			request = new Socket(ip, port);
			System.out.println("  C--客户端连接信息: "+request);
			
			// 向服务端发送信息
			SocketUtil.writeStr2Stream(setJSONStrParam(object), request.getOutputStream());
			System.out.println("  C--客户端向服务端发送的信息为："+setJSONStrParam(object));
			
			// 服务端返回的信息
			String reqStr = SocketUtil.readStrFromStream(request.getInputStream());
			JSONObject jsonObject2 = new JSONObject(reqStr);
			System.out.println("  C --客户端接收服务端返回的信息：" + jsonObject2.toString());
			
			//如果请求的是向服务器端获取歌曲列表信息,则将返回信息设置到SocketVo类中
			if(jsonObject2 != null && "1003".equals(jsonObject2.getString("reqType")) && "30001".equals(jsonObject2.getString("dealtype"))){
				
				//这里服务器返回的是歌曲列表数据
				this.getSocketVo();
				SocketVo.setJsonData(jsonObject2);
			}else{
				//取出连接请求状态信息
				String error_code = jsonObject2.getString("error_code"),
						error_msg = jsonObject2.getString("error_msg");
				
				Thread.sleep(3000);
				
				//如果连接请求成功并请求为长连接，运行心跳线程
				if (isLongConnection && "1".equals(error_code) && "ok".equals(error_msg)) {
					System.out.println("\nC - 客户端请求连接成功, 开启心跳线程..."+" ["+SocketUtil.getNowTime()+"]");
					
					//启动一个心跳线程保持在线连接
					ClientSocketHeartBreakThread heartBreaker = new ClientSocketHeartBreakThread(request);
					Executors.newCachedThreadPool().execute(heartBreaker);
				}
			}
		} catch (IOException e) {
			System.out.println("C - 客户端 Request run IOException异常 : " + e.getMessage()+" ["+SocketUtil.getNowTime()+"]");
			e.printStackTrace();
		} catch (Throwable e) {
			System.out.println("C - 客户端 Request run Throwable异常 : " + e.getMessage()+" ["+SocketUtil.getNowTime()+"]");
			e.printStackTrace();
		} 
	}
	
	//返回向服务器端发送的数据信息
	public String setJSONStrParam(JSONObject JSONObj) throws JSONException{
		JSONObject object = new JSONObject();
		if(JSONObj != null && !"".equals(JSONObj)){
			object = JSONObj;
		}else{
			object.put("reqType", "1001");
			object.put("placeCode", "1");
		}
		return object.toString();
	}

	public boolean isLongConnection() {
		return isLongConnection;
	}

	public void setLongConnection(boolean isLongConnection) {
		this.isLongConnection = isLongConnection;
	}
	
	public SocketVo getSocketVo() {
		return socketVo;
	}

	public void setSocketVo(SocketVo socketVo) {
		this.socketVo = socketVo;
	}

}