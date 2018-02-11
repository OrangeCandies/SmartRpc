package org.smartRpc.server;

import org.smartRpc.Server.RpcService;
import org.smartRpc.client.Hello;

@RpcService(Hello.class)
public class HelloImp implements Hello {
    @Override
    public String hello(String name) {
        return "HELLO "+name;
    }
}
