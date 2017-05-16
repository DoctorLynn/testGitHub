package com.test.proxy.dynamic;

import java.lang.reflect.Proxy;

import com.test.proxy.statics.Sell;
import com.test.proxy.statics.Vendor;

/**
 * 动态生成代理类
 * 代理类在程序运行时创建的代理方式被成为动态代理。也就是说，这种情况下，代理类并不是在Java代码中定义的，而是在运行时根据我们在Java代码中的“指示”动态生成的。
 * 相比于静态代理，动态代理的优势在于可以很方便的对代理类的函数进行统一的处理，而不用修改每个代理类的函数。
 */
public class Main {

	public static void main(String[] args) {
		//创建中介类实例
        DynamicProxy  inter = new DynamicProxy(new Vendor());
        
        //加上这句将会产生一个$Proxy0.class文件，这个文件即为动态生成的代理类文件
        System.getProperties().put("sun.misc.ProxyGenerator.saveGeneratedFiles","true"); 
        
        /**
         *获取代理类实例sell
         * public static Object newProxyInstance(ClassLoader loader, Class<?>[] interfaces, InvocationHandler h) throws IllegalArgumentException
         * loader：定义了代理类的ClassLoder；
         * interfaces：代理类实现的接口列表
         * h：调用处理器，也就是我们上面定义的实现了InvocationHandler接口的类实例
         */
        Sell sell = (Sell)(Proxy.newProxyInstance(Sell.class.getClassLoader(), new Class[] {Sell.class}, inter));
        
        //通过代理类对象调用代理类方法，实际上会转到invoke方法调用
        sell.sell();
        sell.ad();
	}
	
}
