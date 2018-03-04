package org.smartRpc.client;

import org.smartRpc.bean.RpcResult;
import org.smartRpc.proxy.IAsyCallback;
import org.smartRpc.proxy.IAsyncObjectProxy;

public class ClientTest {

    /**
     *    此方法包含了整个框架被使用的方法和细节
     * @param args
     */
    public static void main(String[] args) {
        //  同步RPC调用
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
        // 异步RPC调用
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
        多线程调用版本同步RPC */
//Thread[] thread = new Thread[100];
//        for(int i=0;i<100;i++){
//            thread[i] = new Thread(()->{
//                Hello hello = RpcProxyFactory.create(Hello.class);
//                String result = hello.hello("LiuHui");
//                System.out.println(result);
//            });
//            thread[i].start();
//        }

//多线程异步PRC版本 (超时版本)
          Thread[] thread = new Thread[100];
        for(int i=0;i<10;i++){
            thread[i] = new Thread(()->{
                IAsyncObjectProxy asyncProxy = RpcProxyFactory.createAsyncProxy(CostTimeServer.class);
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
                RpcResult result = asyncProxy.call(iAsyCallback, "add", 1, 2);
                // result中可以查询结果

            });
            thread[i].start();
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
// 耗时 同步服务测试
/*        CostTimeServer costTimeServer = RpcProxyFactory.create(CostTimeServer.class);
        int c = costTimeServer.add(3,new Integer(3));
        System.out.println(c);
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
// 耗时异步服务测试
/*        IAsyncObjectProxy asyncProxy = RpcProxyFactory.createAsyncProxy(CostTimeServer.class);
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
        System.out.println("Call succeed");*/

    }
}
