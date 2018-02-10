package org.smartRpc.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartRpc.bean.RpcResponse;
import org.smartRpc.bean.RpcResult;

import java.util.concurrent.ConcurrentHashMap;

public class RpcClientHandler extends SimpleChannelInboundHandler<RpcResponse> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcClientHandler.class);
    public static final ConcurrentHashMap<String,RpcResult> RPC_REQUSET = new ConcurrentHashMap<String, RpcResult>();

    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse msg) throws Exception {
        String requestId = msg.getRequestId();
        RpcResult rpcResult = RPC_REQUSET.get(requestId);
        RPC_REQUSET.remove(requestId);
        rpcResult.done(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        ctx.close();
        LOGGER.error("error in rpcClientHandler");
    }


}
