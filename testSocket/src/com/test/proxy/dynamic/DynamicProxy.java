package com.test.proxy.dynamic;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

//定义一个位于代理类与委托类之间的中介类 - 要求实现InvocationHandler接口 - 作为调用处理器”拦截“对代理类方法的调用
public class DynamicProxy implements InvocationHandler {

	//obj为委托类对象；
	private Object obj; 
	 
    public DynamicProxy(Object obj) {
        this.obj = obj;
    }
	
    /**
     * 中介类持有一个委托类对象引用，在invoke方法中调用了委托类对象的相应方法，
	 * 通过聚合方式持有委托类对象引用，把外部对invoke的调用最终都转为对委托类对象的调用。
	 * 实际上，中介类与委托类构成了静态代理关系，在这个关系中，中介类是代理类，委托类就是委托类；
	 * 代理类与中介类也构成一个静态代理关系，在这个关系中，中介类是委托类，代理类是代理类。
	 * 也就是说，动态代理关系由两组静态代理关系组成，这就是动态代理的原理。下面我们来介绍一下如何”指示“以动态生成代理类
     */
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		// TODO Auto-generated method stub
		System.out.println("before      -     "+method.getName()+"()");
		
        Object result = method.invoke(obj, args);
        
        System.out.println("after       -     "+method.getName()+"()\n");
        return result;
	}

}
