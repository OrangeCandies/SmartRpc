package org.smartRpc.proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartRpc.bean.RpcRequset;
import org.smartRpc.bean.RpcResult;
import org.smartRpc.netty.NettyClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;

public class ProxyObject<T> implements InvocationHandler,IAsyncObjectProxy {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProxyObject.class);

    private Class<T> tClass;

    public ProxyObject(Class<T> tClass) {


        this.tClass = tClass;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

       // 将一些不适用代理的方法进行逃逸
        if (Object.class == method.getDeclaringClass()) {
            String name = method.getName();
            if ("equals".equals(name)) {
                return proxy == args[0];
            } else if ("hashCode".equals(name)) {
                return System.identityHashCode(proxy);
            } else if ("toString".equals(name)) {
                return proxy.getClass().getName() + "@" +
                        Integer.toHexString(System.identityHashCode(proxy)) +
                        ", with InvocationHandler " + this;
            } else {
                throw new IllegalStateException(String.valueOf(method));
            }
        }

        RpcRequset request = new RpcRequset();
        request.setRequestId(UUID.randomUUID().toString());
        request.setClassName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setParameterTypes(method.getParameterTypes());
        request.setParameters(args);


        NettyClient client = new NettyClient("127.0.0.1",8080);
        client.start();
        RpcResult result = client.sent(request);
        //此方法会被异步阻塞直到结果返回
        return result.get();
    }

    @Override
    public RpcResult call(String methodName, Object... arg) {
        NettyClient client = new NettyClient("127.0.0.1",8080);
        try {
            client.start();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        RpcRequset requset = createRequest(tClass.getName(),methodName,arg);
        RpcResult re = client.sent(requset);
        // 此方法会直接返回
        return re;
    }

    private RpcRequset createRequest(String className, String methodName, Object[] args) {
        RpcRequset request = new RpcRequset();
        request.setRequestId(UUID.randomUUID().toString());
        request.setClassName(className);
        request.setMethodName(methodName);
        request.setParameters(args);
        Class[] parameterTypes = new Class[args.length];
        // Get the right class type
        for (int i = 0; i < args.length; i++) {
            parameterTypes[i] = getClassType(args[i]);
        }
        request.setParameterTypes(parameterTypes);

        return request;
    }
   // todo 测试
    private Class<?> getClassType(Object obj){
        Class<?> classType = obj.getClass();
        String typeName = classType.getName();
        switch (typeName){
            case "java.lang.Integer":
                return Integer.TYPE;
            case "java.lang.Long":
                return Long.TYPE;
            case "java.lang.Float":
                return Float.TYPE;
            case "java.lang.Double":
                return Double.TYPE;
            case "java.lang.Character":
                return Character.TYPE;
            case "java.lang.Boolean":
                return Boolean.TYPE;
            case "java.lang.Short":
                return Short.TYPE;
            case "java.lang.Byte":
                return Byte.TYPE;
        }

        return classType;
    }
}
