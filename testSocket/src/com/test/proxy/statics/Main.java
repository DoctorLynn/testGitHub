package com.test.proxy.statics;

/**
 *静态代理
 *若代理类在程序运行前就已经存在，那么这种代理方式被成为静态代理，这种情况下的代理类通常都是我们在Java代码中定义的。 
 *通常情况下，静态代理中的代理类和委托类会实现同一接口或是派生自相同的父类。
 */
public class Main {
	public static void main(String[] args) {
		BusinessAgent agent = new BusinessAgent(new Vendor());
		agent.sell();
		agent.ad();
	}
}
