package com.test.proxy.statics;

//微商代理 - 静态代理类
public class BusinessAgent implements Sell {

	private Vendor mVendor;
	 
    public BusinessAgent(Vendor vendor) {
        this.mVendor = vendor;
    }
	
	@Override
	public void sell() {
		// TODO Auto-generated method stub
		System.out.println("\nbefore    -    sell()");
        mVendor.sell();
        System.out.println("after    -    sell()");
	}

	@Override
	public void ad() {
		// TODO Auto-generated method stub
		System.out.println("\nbefore    -    ad()");
        mVendor.ad();
        System.out.println("after    -    ad()");
	}

}
