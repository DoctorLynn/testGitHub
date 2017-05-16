package com.test.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * java 定时备份数据库
 */
public class BackUpDb {

	public static String backUp() throws IOException{
		//数据库的用户名
		String user = "root"; 
		//数据库的密码 
		String password = "111111";
		//要备份的数据库名 
		String database = "mysql";
		Date date = new Date();
      	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      	//保存备份的文件名称
      	String filepath = "d:\\E\\"+sdf.format(date)+".sql";
      	File file = new File(filepath);
      	//创建文件
      	if(!file.exists()){
      		file.createNewFile();  
      	}
      	String stmt = "cmd /C E:\\mysql\\mysql-5.7.16\\bin\\mysqldump -h localhost -u"+user+" -p"+password+"  --default-character-set=utf8 --single-transaction --databases " + database +">" + filepath;
//      	String stmt = "cmd /C mysqldump -h localhost -u"+user+" -p"+password+"  --default-character-set=utf8 --single-transaction --databases " + database +">" + filepath;
      	try {
	        Runtime.getRuntime().exec(stmt);
	        System.out.println("数据库备份文件已经保存到 " + filepath + " 中."+"   ["+sdf.format(new Date())+"]");
      	} catch (IOException e) {
      		e.printStackTrace();
      	}
  		return filepath;
 	}

	/**
	 * 创建定时器
	 */
	public static class PickTask {
		//定时器工具，使用的时候会在主线程之外起一个单独的线程执行指定的计划任务，可以指定执行一次或者反复执行多次。
		private Timer timer = new Timer();
		//建立一个要执行的任务TimerTask.TimerTask是一个实现了Runnable接口的抽象类，代表一个可以被Timer执行的任务。
		private  TimerTask task = new TimerTask() {
			//重写run方法来指定具体的任务
			public void run() {
		        Date date = new Date();
		        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		        String beginDate = sdf.format(date);
		        
		        String beginTime = beginDate.substring(11, 16);
		        System.out.println("数据库备份开始时间："+beginDate);
		
		        // 设定备份时间
		        if (!beginTime.equals("15:21")) {
			        try {
			        	// 执行文件备份
//			        	backUp(); 
			        	// 取出备份的文件名字
			        	String dbName = backUp().toString(); 
			        	String path = "d:\\E\\";
			        	int nameNo = dbName.lastIndexOf("\\");
			        	//判断文件是否存在，如果存在，则备份成功，如果不存在则备份不成功需要重新备份
				        File file = new File(path, dbName.substring(nameNo + 1,dbName.length()));
				        if (file.exists()){
			               System.out.println("数据库文件备份成功.");
				        }else{
			                System.out.println("数据库备份失败，重新备份...");
			               //在备份未成功的情况下重新备份
			                new PickTask().start(1, 1);
				        }
			        } catch (FileNotFoundException e) {
			        	System.out.println("未找到文件");
			        	e.printStackTrace();
			        } catch (IOException e) {
			        	e.printStackTrace();
			        }
		       }else{
		    	   System.out.println("时间还不到呢，不要着急哦！");
		       }
			}
		};

	     //start 方法不能少，主要是schedule方法
	     public void start(int delay, int internal) {
	    	 /**
	    	  * @param task - 一个指定具体的任务的 TimerTask 类
	    	  * @param delay - 延迟执行.调用 schedule() 方法后，要等待这么长的时间才可以第一次执行 run() 方法
	    	  * @param internal - 调用间隔.第一次调用之后，从第二次开始每隔多长的时间调用一次 run() 方法
	    	  */
	    	 timer.schedule(task, delay * 1000, internal * 1000);
	     }
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		PickTask pickTask = new PickTask();
		pickTask.start(1, 3);
	}

}
