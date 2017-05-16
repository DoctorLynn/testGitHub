package com.test.socket.test;

import org.json.JSONObject;

/**
 * 数据存放类
 */
public class SocketVo {

	/**
	 * 请求获取歌曲列表数据时,存放歌曲列表数据
	 */
	private static JSONObject jsonData;

	public static JSONObject getJsonData() {
		return jsonData;
	}

	public static void setJsonData(JSONObject jsonData) {
		SocketVo.jsonData = jsonData;
	}

}
