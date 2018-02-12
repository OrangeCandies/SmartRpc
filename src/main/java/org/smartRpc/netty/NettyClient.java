package org.smartRpc.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.smartRpc.bean.RpcRequset;
import org.smartRpc.bean.RpcResponse;
import org.smartRpc.bean.RpcResult;

import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

public class NettyClient {

    private String host;
    private int port;
    private EventLoopGroup group = new NioEventLoopGroup();
    private ChannelFuture channel;
    private AtomicBoolean isClosed = new AtomicBoolean(false);
    public NettyClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group).remoteAddress(new InetSocketAddress(host,port))
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ch.pipeline()
                                .addLast(new RpcEncoder(RpcRequset.class))
                                .addLast(new RpcDecoder(RpcResponse.class))
                                .addLast(new RpcClientHandler());
                    }
                }).option(ChannelOption.SO_KEEPALIVE,true);

        try {
            channel = bootstrap.connect().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();

        }
    }
    public RpcResult sent(RpcRequset requset){
        final CountDownLatch count = new CountDownLatch(1);
        RpcResult result = new RpcResult(requset);
        RpcClientHandler.RPC_REQUSET.put(requset.getRequestId(),result);
        channel.channel().writeAndFlush(requset).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) {
                count.countDown();
            }
        });
        try {
            count.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }

    public void close(){
        isClosed.compareAndSet(false,true);
        group.shutdownGracefully();
    }

    public boolean isClosed(){
        return isClosed.get();
    }
}
