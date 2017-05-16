package com.test.socket.threadpool;

import java.util.List;
import java.util.Vector;

/**
 * Java线程池实现
 * @author Administrator
 *
 */
public class ThreadPool {

	//空线程池
	private static ThreadPool instance_ = null;
    //定义优先级别常数，空闲的线程按照优先级不同分别存放在三个vector中
    public static final int LOW_PRIORITY = 0; 
    public static final int NORMAL_PRIORITY = 1;
    public static final int HIGH_PRIORITY = 2;
    //保存空闲线程的List，或者说它是"池"
    private List<PooledThread>[] idleThreads_;  
    private boolean shutDown_ = false;
    //以创建的线程的个数
    private int threadCreationCounter_; 
    //是否输出调试信息
    private boolean debug_ = false;    
    //构造函数，因为这个类视作为singleton实现的，因此构造函数为私有
	@SuppressWarnings("unchecked")
	private ThreadPool() {       
        // 产生空闲线程.三个vector分别存放分别处在三个优先级的线程的引用
        @SuppressWarnings("rawtypes")
		List[] idleThreads = {new Vector(5), new Vector(5), new Vector(5)};
        idleThreads_ = idleThreads;
        threadCreationCounter_ = 0;
    }
     
    public int getCreatedThreadsCount() {
        return threadCreationCounter_;
    }
    //通过这个函数得到线程池类的实例
    public static ThreadPool instance() {
        if (instance_ == null)
            instance_ = new ThreadPool();
        return instance_;
    }
     
    public boolean isDebug() {
        return debug_;
    }
    public void setDebug(boolean newDebug){
        debug_ = newDebug;
    }
    
    /**
     * 将线程repoolingThread从新放回到池中，这个方式是同步方法。
     * 这个方法会在多线程的环境中调用，设计这个方法的目的是让工作者线程
     * 在执行完target中的任务后，调用池类的repool()方法，
     * 将线程自身从新放回到池中。只所以这么做是因为线程池并不能预见到
     * 工作者线程何时会完成任务。参考PooledThread的相关代码。
     * @param repoolingThread
     */
    protected synchronized void repool(PooledThread repoolingThread){
        if (!shutDown_) {
            if (debug_){
                System.out.println("ThreadPool.repool() : repooling ");
            }
            switch (repoolingThread.getPriority()){
                case Thread.MIN_PRIORITY :
                {
                    idleThreads_[LOW_PRIORITY].add(repoolingThread);
                    break;
                }
                case Thread.NORM_PRIORITY :
                {
                    idleThreads_[NORMAL_PRIORITY].add(repoolingThread);
                    break;
                }
                case Thread.MAX_PRIORITY :
                {
                    idleThreads_[HIGH_PRIORITY].add(repoolingThread);
                    break;
                }
                default :
                    throw new IllegalStateException("发现一个非法优先级的线程!");
            }
            //通知所有的线程
            notifyAll();
        }else{
            if (debug_){
                System.out.println("ThreadPool.repool() : 销毁进入的线程.");
            }
            //关闭线程
            repoolingThread.shutDown();
        }
        if (debug_) {
            System.out.println("ThreadPool.recycle() : 完成.");
        }
    }
    
    //停止池中所有线程
    public synchronized void shutdown(){
        shutDown_ = true;
        if (debug_){
            System.out.println("ThreadPool : 关闭 ...");
        }
        for (int prioIndex = 0; prioIndex <= HIGH_PRIORITY; prioIndex++){
            @SuppressWarnings("rawtypes")
			List prioThreads = idleThreads_[prioIndex];
            for (int threadIndex = 0; threadIndex < prioThreads.size(); threadIndex++){
                PooledThread idleThread = (PooledThread) prioThreads.get(threadIndex);
                idleThread.shutDown();
            }
        }
        notifyAll();
        if (debug_)
        {
            System.out.println("ThreadPool : 关闭完成.");
        }
    }
    
    //以Runnable为target，从池中选择一个优先级为priority的线程创建线程
    //并让线程运行。
    public synchronized void start(Runnable target, int priority){
    	//被选出来执行target的线程
        PooledThread thread = null;  
        @SuppressWarnings("rawtypes")
		List idleList = idleThreads_[priority];
        if (idleList.size() > 0) {
            //如果池中相应优先级的线程有空闲的，那么从中取出一个
            //设置它的target，并唤醒它
            //从空闲的线程队列中获取
            int lastIndex = idleList.size() - 1;
            thread = (PooledThread) idleList.get(lastIndex);
            idleList.remove(lastIndex);
            thread.setTarget(target);
        }
        //池中没有相应优先级的线程
        else { 
            threadCreationCounter_++;
            // 创建新线程，
            thread = new PooledThread(target, "PooledThread #" + threadCreationCounter_, this);
            // 新线程放入池中
            switch (priority) {
                case LOW_PRIORITY :
                {
                    thread.setPriority(Thread.MIN_PRIORITY);
                    break;
                }
                case NORMAL_PRIORITY :
                {
                    thread.setPriority(Thread.NORM_PRIORITY);
                    break;
                }
                case HIGH_PRIORITY :
                {
                    thread.setPriority(Thread.MAX_PRIORITY);
                    break;
                }
                default :
                {
                    thread.setPriority(Thread.NORM_PRIORITY);
                    break;
                }
            }
            //启动这个线程
            thread.start();
        }
    }
    
    @SuppressWarnings("static-access")
	public static void main(String[] args){
        System.out.println("开始测试线程池... ");
        System.out.println("创建线程池 ThreadPool ");
        ThreadPool pool = ThreadPool.instance();
        pool.setDebug(true);
        class TestRunner implements Runnable{
            public int count = 0;
            public void run() {
                System.out.println("测试者运行休眠5秒...");
                //此方法使本线程睡眠5秒
                synchronized (this) {
                    try {
                    	//等待5秒时间
                        wait(5000);
                    }
                    catch (InterruptedException ioe) {
                    	ioe.printStackTrace();
                    }
                }
                System.out.println("测试者离开.  ");
                count++;
            }
        }
        System.out.println("启动新线程... ");
        TestRunner runner = new TestRunner();
        pool.start(runner, pool.HIGH_PRIORITY);
        System.out.println("计数 : " + runner.count);
        System.out.println("线程数 : " + pool.getCreatedThreadsCount());
        pool.shutdown();
    }
}
