package org.smartRpc.server;

import org.smartRpc.Server.RpcService;
import org.smartRpc.client.CostTimeServer;

@RpcService(CostTimeServer.class)
public class CostTimeServerImp implements CostTimeServer {
    @Override
    public int add(int a, int c) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return a+c;
    }
}
