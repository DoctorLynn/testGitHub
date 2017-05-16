package com.test.proxy.statics;

//生产厂家 - 委托类
public class Vendor implements Sell {

	/**
	 * 销售
	 */
	@Override
	public void sell() {
		// TODO Auto-generated method stub
		System.out.println("进入 销售 方法...");
	}

	/**
	 * 广告
	 */
	@Override
	public void ad() {
		// TODO Auto-generated method stub
		System.out.println("进入 广告 方法...");

	}

}
