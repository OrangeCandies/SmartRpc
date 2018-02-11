package org.smartRpc.Server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.commons.collections4.MapUtils;
import org.smartRpc.bean.RpcRequset;
import org.smartRpc.bean.RpcResponse;
import org.smartRpc.manager.ServiceManager;
import org.smartRpc.netty.RpcDecoder;
import org.smartRpc.netty.RpcEncoder;
import org.smartRpc.netty.RpcServerHandler;
import org.smartRpc.util.ConfigUtil;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;

public class Server implements ApplicationContextAware,InitializingBean{

    private int port;
    private EventLoopGroup workGroup = new NioEventLoopGroup();
    private EventLoopGroup bossGroup = new NioEventLoopGroup();
    private ServiceRegistry serviceRegistry = new ServiceRegistry(ConfigUtil.ZK_REGISTRY_ADDRESS);
    public Server(int port){
        this.port = port;
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        start();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String,Object> serviceBean = applicationContext.getBeansWithAnnotation(RpcService.class);
        if(!MapUtils.isEmpty(serviceBean)){
            for(Object o : serviceBean.values()){
                String interfaceName = o.getClass().getAnnotation(RpcService.class).value().getName();
                ServiceManager.setServiceClass(interfaceName,o);
            }
        }
    }

    public void start(){
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup,workGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new RpcDecoder(RpcRequset.class))
                                .addLast(new RpcEncoder(RpcResponse.class))
                                .addLast(new RpcServerHandler());
                    }
                }).option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true);

        try {
            if(serviceRegistry != null){
                serviceRegistry.registry("127.0.0.1:"+port);
            }

            ChannelFuture f = bootstrap.bind(port).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void close(){
        if(bossGroup != null){
            bossGroup.shutdownGracefully();
        }
        if(workGroup != null){
            workGroup.shutdownGracefully();
        }
    }
}
