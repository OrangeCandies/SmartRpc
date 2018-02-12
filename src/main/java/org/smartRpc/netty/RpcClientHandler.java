package org.smartRpc.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartRpc.bean.RpcResponse;
import org.smartRpc.bean.RpcResult;
import org.smartRpc.client.ClientManager;

import java.util.concurrent.ConcurrentHashMap;

public class RpcClientHandler extends SimpleChannelInboundHandler<RpcResponse> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcClientHandler.class);
    public static final ConcurrentHashMap<String,RpcResult> RPC_REQUSET = new ConcurrentHashMap<String, RpcResult>();

    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse msg) {
        String requestId = msg.getRequestId();
        RpcResult rpcResult = RPC_REQUSET.get(requestId);
        RPC_REQUSET.remove(requestId);
        rpcResult.done(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        // 连接中断 通知Client重新寻找可用服务器和重连
        ClientManager.tryToReconnect();
        LOGGER.error("error in rpcClientHandler");
    }


}
