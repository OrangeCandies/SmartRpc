package org.smartRpc.proxy;

import org.smartRpc.bean.RpcResult;


public interface IAsyncObjectProxy {
    /**
     *
     * @param methodName 方法名
     * @param arg 参数列表
     * @return RpcResult 可以通过设置回调函数 当此次调用结果放回的时候会主动调用回调函数
     */
    public RpcResult call(String methodName,Object...arg);


    /**
     *
     * @param callback 设置回调函数
     * @param methodName  方法参数
     * @param arg  参数列表
     * @return RpcResult 封装了调用结果
     */
    public RpcResult call(IAsyCallback callback,String methodName,Object...arg);


}
