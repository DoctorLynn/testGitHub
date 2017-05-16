package com.test.socket.test;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SocketUtil {

	/**
	 * 向输出流中写入数据
	 * @param str - 传入数据JSON字符串
	 * @param out - Socket输出流
	 * @throws IOException
	 */
	public static void writeStr2Stream(String str, OutputStream out) throws IOException {
		try {
			//数据输出流
			DataOutputStream bos = new DataOutputStream(out); 
			
			//字符串转字节数组
			byte[] bytSend = str.getBytes("UTF-8");
			
			//写进输出流
            bos.writeInt(bytSend.length);
            bos.write(bytSend);
            
            //清空输出流
            bos.flush();
		} catch (IOException ex) {
			System.out.println("writeStr2Stream - IOException"+ex+" ["+SocketUtil.getNowTime()+"]");
			throw ex;
		}
	}

	/**
	 * 从输入流中读取数据
	 * @param in - Socket输入流
	 * @throws IOException 
	 */
	public static String readStrFromStream(InputStream in) throws IOException {
		
		//数据输入流
		DataInputStream dis = new DataInputStream(in); 
		
        //获取到的消息
		String resultMsg=""; 
		try {
	        int nlen = 0;
	        if((nlen = dis.readInt()) > 0) {
	          byte[] bytRead = new byte[nlen];
	          dis.readFully(bytRead);

	          resultMsg = new String(bytRead, "UTF-8");
	        }
		} catch (IOException e) {
			System.out.println("readStrFromStream - IOException"+e+" ["+SocketUtil.getNowTime()+"]");
			throw e;
		}
		
		return resultMsg;
	}
	
	public static String getNowTime(){
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
	}
	
}
