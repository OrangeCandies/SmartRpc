package org.smartRpc.Server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ServerThreadPool {

    private final static Logger LOGGER  = LoggerFactory.getLogger(ServerThreadPool.class);
    private volatile static Executor threadpool = null;

    public static void summit(Runnable tast){
        if(threadpool == null){
            synchronized (ServerThreadPool.class){
                if(threadpool == null){
                    // 初始化一个核心线程数为10 最大线程数为40，存活5分钟 ，最大大小为256的任务队列
                    threadpool = new ThreadPoolExecutor(10,40,5*60*1000,
                            TimeUnit.MILLISECONDS,new ArrayBlockingQueue<Runnable>(256));
                }
            }

        }
        threadpool.execute(tast);
    }


}
