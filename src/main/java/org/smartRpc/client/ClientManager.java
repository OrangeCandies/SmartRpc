package org.smartRpc.client;

import org.smartRpc.bean.RpcRequset;
import org.smartRpc.bean.RpcResult;
import org.smartRpc.netty.NettyClient;
import org.smartRpc.util.ConfigUtil;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientManager {

    private  static ServiceDiscovery discovery = new ServiceDiscovery(ConfigUtil.ZK_REGISTRY_ADDRESS);
    private static NettyClient nettyClient = connectNetty();


    // 超时关闭NettyCLient 每次调用发送会更新技术值
    private static ScheduledExecutorService timer = Executors.newSingleThreadScheduledExecutor();
    private static AtomicInteger count = new AtomicInteger(0);

    static {
        timer.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                int value = count.incrementAndGet();
                if(value == 10){
                    System.out.println("Closed");
                    nettyClient.close();
                    timer.shutdown();
                }
            }
        },0,500, TimeUnit.MILLISECONDS);
    }


    public static NettyClient connectNetty() {
        String[] info = discovery.discovery().split(":");
        String host = info[0];
        int port = Integer.parseInt(info[1]);
        nettyClient = new NettyClient(host,port);
        nettyClient.start();
        return  nettyClient;
    }


    public static RpcResult sendRequset(RpcRequset requset){
        RpcResult result = null;
        // 当Netty未初始化 或者 netty以及超时关闭 或者 连接远程出现故障
        // 重新寻找服务器
        if(nettyClient == null || nettyClient.isClosed()||nettyClient.isWrong()){
            nettyClient = connectNetty();
        }
        count.set(0);
        if(nettyClient != null){
          result = nettyClient.sent(requset);
          return result;
        }

        new Throwable(" Netty is closed ! please restart client").printStackTrace();
        return null;
    }


}
