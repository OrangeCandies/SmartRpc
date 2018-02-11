package org.smartRpc.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ClientThreadPool {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientThreadPool.class);
    private static volatile Executor threadPool = null;

    public static void summit(Runnable task){
        if(threadPool == null){
            synchronized (ClientThreadPool.class){
                if(threadPool == null){
                    threadPool = new ThreadPoolExecutor(4,16,3*60*1000, TimeUnit.MILLISECONDS,
                            new ArrayBlockingQueue<Runnable>(32));
                }
            }
        }
        threadPool.execute(task);
    }
}
