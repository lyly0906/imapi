package cn.xyz.commons.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.xyz.commons.support.Callback;




public  class ThreadUtil{
	public static final ExecutorService mThreadPool = Executors.newFixedThreadPool(1000);
	
	public static void executeInThread(Callback callback){
		mThreadPool.execute(new Runnable() {
    		@Override
			public void run() {
    			callback.execute(Thread.currentThread().getName());
    		}
		});
	}

}
