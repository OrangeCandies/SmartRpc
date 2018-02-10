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

public class NettyClient {

    private String host;
    private int port;
    private EventLoopGroup group = new NioEventLoopGroup();
    private Channel channel;
    public NettyClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() throws InterruptedException {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group).remoteAddress(new InetSocketAddress(host,port))
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new RpcEncoder(RpcRequset.class))
                                .addLast(new RpcDecoder(RpcResponse.class))
                                .addLast(new RpcClientHandler());
                    }
                }).option(ChannelOption.SO_KEEPALIVE,true);
        ChannelFuture sync = bootstrap.connect().sync();
        channel = sync.channel();
    }

    public RpcResult sent(RpcRequset requset){
        final CountDownLatch count = new CountDownLatch(1);
        RpcResult result = new RpcResult(requset);
        RpcClientHandler.RPC_REQUSET.put(requset.getRequestId(),result);
        channel.write(requset).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
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
        group.shutdownGracefully();
    }
}
