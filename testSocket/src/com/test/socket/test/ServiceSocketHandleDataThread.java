package com.test.socket.test;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 服务器端处理请求线程，这里用于写具体的业务逻辑
 */
public class ServiceSocketHandleDataThread implements Runnable {
	
	// 客户端请求
	private Socket request;

	//构造方法
	public ServiceSocketHandleDataThread(Socket request) {
		this.request = request;
	}

	@Override
	public void run() {
		try {
			//设置超时：15秒
			request.setSoTimeout(15000);

			//长连接
			while (true) {
				//获取请求时得到套接字请求信息
				String reqStr = "";
				try {
					//读取客户端发送的数据，如果read()得到超时异常
					reqStr = SocketUtil.readStrFromStream(request.getInputStream());
				} catch (SocketException e) {
					//然后停止while循环，停止服务
					System.out.println("S -  服务器端SocketException, 客户端请求已关闭."+" ["+SocketUtil.getNowTime()+"]");
					e.printStackTrace();break;
				} catch (IOException e) {
					System.out.println("S--服务器端IOException, 客户端请求已关闭."+" ["+SocketUtil.getNowTime()+"]");
					e.printStackTrace();break;
				} 
				System.out.println("S--服务器端接收到客户端发送的数据:  " + reqStr);

				// 服务器端处理信息
				ServiceSocketHandleMsg(reqStr);
				
				Thread.sleep(5000);
			}
		}catch (Exception e) {
			System.out.println("S--服务器端Exception:  "+e.getMessage());
			e.printStackTrace();
		}  catch (Throwable e) {
			System.out.println("S--服务器端Throwable:  "+e.getMessage());
			e.printStackTrace();
		} finally {
			if (request != null) {
				try {
					request.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 服务器端处理客户端发送过来的信息
	 * @param reqStr JSON字符串
	 * @throws JSONException 
	 * @throws IOException 
	 */
	private void ServiceSocketHandleMsg(String reqStr) throws JSONException, IOException {
		// TODO Auto-generated method stub
		if (reqStr != null && !"".equals(reqStr)) {
			JSONObject jsonObject = new JSONObject(reqStr);
			jsonObject.put("error_code", "1");
			jsonObject.put("error_msg", "ok");
			
			String reqType = "", dealtype = "";
			if(jsonObject != null && !"".equals(jsonObject)){
				reqType = jsonObject.getString("reqType");
				if("1003".equals(reqType)){
					dealtype = jsonObject.getString("dealtype");
				}
			}
			//客户端请求歌曲列表数据
			if("1003".equals(reqType) && "30001".equals(dealtype)){
				Map<String, Object> tempSongMap = null;
				ArrayList<String> songList = new ArrayList<String>();
				int i = 0;
				while (i < 20) {
					tempSongMap = new HashMap<String, Object>();
					tempSongMap.put("songName", "歌曲_"+i);
					tempSongMap.put("songSinger", "歌手_"+i);
					songList.add(tempSongMap.toString());
					i ++;
				}
				jsonObject.put("songList", songList);
			}
			System.out.println(" : S--服务器端返回给客户端的数据："+jsonObject.toString());
			
			SocketUtil.writeStr2Stream(jsonObject.toString(), request.getOutputStream());
		}
	}

}
