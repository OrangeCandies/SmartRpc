package org.smartRpc.bean;

import org.smartRpc.proxy.IAsyCallback;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * 封装Rpc异步调用结果
 */
public class RpcResult implements Future<Object> {


    private RpcResponse response = null;
    private RpcRequset rpcRequset = null;
    private Sync sync = null;

    private IAsyCallback callback;
    public RpcResult(RpcRequset requset){
        sync = new Sync();
        this.rpcRequset = requset;
    }
    /**
     * 不支持取消已经发送成功等待结果的Result
     * @param mayInterruptIfRunning
     * @return
     */
    public boolean cancel(boolean mayInterruptIfRunning) {
        throw new UnsupportedOperationException();
    }

    /**
     *  不支持查询此次调用是否已经取消
     * @return
     */
    public boolean isCancelled() {
        throw new UnsupportedOperationException();
    }

    public boolean isDone() {
        return sync.isDone();
    }

    public Object get() {
        sync.acquire(1);
        if(this.response != null){
            return response.getResult();
        }else{
            return null;
        }
    }

    public Object get(long timeout, TimeUnit unit) throws InterruptedException {
        boolean succeed = sync.tryAcquireNanos(1, unit.toNanos(timeout));
        if(succeed){
            if(this.response != null){
                return response.getResult();
            }else{
                return null;
            }
        }else{
            throw new RuntimeException("Time out with request id="+rpcRequset.getRequestId());
        }
    }

    public void done(RpcResponse response){
        // 设置response
        this.response = response;
        // 释放同步锁
        sync.release(1);
        // 执行回调结果
        runCallBack();

    }

    private void runCallBack(){
        // 当回调函数为设置的时候方法直接返回
        if(callback == null){
            return;
        }
        if(!response.isError()){
            callback.success(response.getResult());
        }else{
            callback.fail(new Exception(response.getError()));
        }
    }

    public void setCallback(IAsyCallback callback){
        this.callback = callback;
    }


    /**
     * 借助AQS实现的一个锁 完成对Future.get()调用的阻塞
     */
    static class Sync extends AbstractQueuedSynchronizer{

        /**
         * 初始值设置为1 意味着调用Acquire()方法会被阻塞 直到有方法调用了Release()
         */
        Sync(){
            setState(1);
        }

        @Override
        protected boolean tryAcquire(int arg) {
            if(compareAndSetState(0,1)){

                // 设置持有线程来判断当前是否已经成功获取锁
                setExclusiveOwnerThread(Thread.currentThread());
                return true;
            }else{
                return false;
            }
        }

        @Override
        protected boolean tryRelease(int arg) {

            return compareAndSetState(1,0);

        }

        public boolean isDone(){
            return Thread.currentThread().equals(getExclusiveOwnerThread());
        }
    }
}
