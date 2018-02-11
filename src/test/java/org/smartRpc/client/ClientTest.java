package org.smartRpc.client;

import org.smartRpc.bean.RpcResult;
import org.smartRpc.proxy.IAsyCallback;
import org.smartRpc.proxy.IAsyncObjectProxy;

public class ClientTest {

    public static void main(String[] args) {
        Hello hello = RpcProxyFactory.create(Hello.class);
        String liuhui = hello.hello("liuhui");
        System.out.println(liuhui);
        IAsyncObjectProxy asyncProxy = RpcProxyFactory.createAsyncProxy(Hello.class);
        RpcResult call = asyncProxy.call("hello", "LIUhui");
        call.setCallback(new IAsyCallback() {
            @Override
            public void success(Object result) {
                System.out.println(result);
                System.out.println("Callback Succeed");
            }

            @Override
            public void fail(Exception e) {

            }
        });


    }
}
