package org.smartRpc.client;

import org.smartRpc.bean.RpcRequset;
import org.smartRpc.bean.RpcResult;
import org.smartRpc.netty.NettyClient;
import org.smartRpc.util.ConfigUtil;

public class ClientManager {

    private  ServiceDiscovery discovery = new ServiceDiscovery(ConfigUtil.ZK_REGISTRY_ADDRESS);
    private NettyClient nettyClient;
    public ClientManager() {
        nettyClient = connectNetty();
    }

    public NettyClient connectNetty() {
        String[] info = discovery.discovery().split(":");
        String host = info[0];
        int port = Integer.parseInt(info[1]);
        nettyClient = new NettyClient(host,port);
        try {
            nettyClient.start();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return  nettyClient;
    }

    public RpcResult sendRequset(RpcRequset requset){
        RpcResult result = null;
        if(nettyClient == null){
            nettyClient = connectNetty();
        }
        if(nettyClient != null){
          result = nettyClient.sent(requset);
          return result;
        }

        new Throwable(" Netty is closed ! please restart client").printStackTrace();
        return null;
    }

    public void close(){
        nettyClient.close();
    }


}
