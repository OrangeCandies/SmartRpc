package org.smartRpc.client;

import org.smartRpc.proxy.IAsyncObjectProxy;
import org.smartRpc.proxy.ProxyObject;

import java.lang.reflect.Proxy;

public class RpcProxyFactory {

    /**
     *    创建同步代理实例
     * @param interfaces
     * @param <T>
     * @return
     */
    public static <T> T create(Class<T> interfaces){
        return (T) Proxy.newProxyInstance(interfaces.getClassLoader(),
                new Class<?>[]{interfaces},
                new ProxyObject<>(interfaces));
    }

    /**
     *  创建异步代理实例
     *  调用方法通过call(MethodName,args[])来调用
     * @param tClass
     * @param <T>
     * @return
     */
    public static <T> IAsyncObjectProxy createAsyncProxy(Class<T> tClass){
        return  new ProxyObject<T>(tClass);
    }


}
