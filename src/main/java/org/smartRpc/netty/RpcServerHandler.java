package org.smartRpc.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartRpc.Server.ServerThreadPool;
import org.smartRpc.bean.RpcRequset;
import org.smartRpc.bean.RpcResponse;
import org.smartRpc.manager.ServiceManager;

import java.lang.reflect.Method;


public class RpcServerHandler extends SimpleChannelInboundHandler<RpcRequset> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcServerHandler.class);


    protected void channelRead0(final ChannelHandlerContext ctx, final RpcRequset msg) throws Exception {
        System.out.println("Taild catch message");
        System.out.println(msg);
        ServerThreadPool.summit(new Runnable() {
            public void run() {
                final RpcResponse response = new RpcResponse();
                response.setRequestId(msg.getRequestId());
                Object result = null;
                try {
                    result = handle(msg);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                    response.setError(throwable);
                }
                response.setResult(result);
                ctx.writeAndFlush(response).addListener(new GenericFutureListener<Future<? super Void>>() {

                    public void operationComplete(Future<? super Void> future) throws Exception {
                       LOGGER.debug("send response for requset with id="+response.getRequestId());
                    }
                });
            }
        });
    }

    private Object handle(RpcRequset requset) throws Throwable {
        String className = requset.getClassName();
        Object serviceTarget = ServiceManager.getServiceObject(className);
        // 采用了JDK代理 CGLib采用字节码技术 动态代理性能不如JDK
        String methodName = requset.getMethodName();
        Method method = null;
        method = serviceTarget.getClass().getMethod(requset.getMethodName(), requset.getParameterTypes());
        if (method != null) {
            return method.invoke(serviceTarget, requset.getParameters());
        } else {
            return null;
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        cause.printStackTrace();
        ctx.close();
    }
}
