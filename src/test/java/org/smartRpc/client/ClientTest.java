package org.smartRpc.client;

import org.smartRpc.bean.RpcResult;
import org.smartRpc.proxy.IAsyCallback;
import org.smartRpc.proxy.IAsyncObjectProxy;

import java.util.concurrent.ExecutionException;

public class ClientTest {

    public static void main(String[] args) {
//        Hello hello = RpcProxyFactory.create(Hello.class);
//        String liuhui = hello.hello("liuhui");
//        System.out.println(liuhui);
//       IAsyncObjectProxy asyncProxy = RpcProxyFactory.createAsyncProxy(Hello.class);
//        RpcResult call = asyncProxy.call("hello", "LIUhui");
//        call.setCallback(new IAsyCallback() {
//            @Override
//            public void success(Object result) {
//                System.out.println(result);
//                System.out.println("Callback Succeed");
//            }
//
//            @Override
//            public void fail(Exception e) {
//
//            }
//        });
//
//        asyncProxy.call(new IAsyCallback() {
//            @Override
//            public void success(Object result) {
//                System.out.println("Right");
//                System.out.println(result);
//            }
//
//            @Override
//            public void fail(Exception e) {
//                System.out.println("failed");
//            }
//        },"hello","LiuHui");
/*//
多线程调用版本*/
//Thread[] thread = new Thread[100];
//        for(int i=0;i<100;i++){
//            thread[i] = new Thread(()->{
//                Hello hello = RpcProxyFactory.create(Hello.class);
//                String result = hello.hello("LiuHui");
//                System.out.println(result);
//            });
//            thread[i].start();
//        }

//多线程回调版本
/*        Thread[] thread = new Thread[100];
        for(int i=0;i<100;i++){
            thread[i] = new Thread(()->{
                IAsyncObjectProxy asyncProxy = RpcProxyFactory.createAsyncProxy(Hello.class);
                IAsyCallback iAsyCallback = new IAsyCallback() {
                    @Override
                    public void success(Object result) {
                        System.out.println(result);
                    }

                    @Override
                    public void fail(Exception e) {
                        System.out.println("failure");
                    }
                };
                asyncProxy.call(iAsyCallback,"hello","liuhui");
            });
            thread[i].start();
        }*/
// 耗时 同步服务测试
        CostTimeServer costTimeServer = RpcProxyFactory.create(CostTimeServer.class);
        int c = costTimeServer.add(3,new Integer(3));
        System.out.println(c);
// 耗时异步服务测试
        IAsyncObjectProxy asyncProxy = RpcProxyFactory.createAsyncProxy(CostTimeServer.class);
        RpcResult rpcResult = asyncProxy.call(new IAsyCallback() {
            @Override
            public void success(Object result) {
                System.out.println(result);
            }

            @Override
            public void fail(Exception e) {
                System.out.println("Failure");
            }
        },"add", 1, 3);
        System.out.println("Call succeed");
        try {
            int s = (int) rpcResult.get();
            System.out.println(s);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
