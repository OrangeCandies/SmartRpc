package org.smartRpc.client;

public class ClientTest {

    public static void main(String[] args) {
//        Hello hello = RpcProxyFactory.create(Hello.class);
//        String liuhui = hello.hello("liuhui");
//        System.out.println(liuhui);
//        IAsyncObjectProxy asyncProxy = RpcProxyFactory.createAsyncProxy(Hello.class);
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
// 多线程调用版本
/*        Thread[] thread = new Thread[100];
        for(int i=0;i<100;i++){
            thread[i] = new Thread(()->{
                Hello hello = RpcProxyFactory.create(Hello.class);
                String result = hello.hello("LiuHui");
                System.out.println(result);
            });
            thread[i].start();
        }*/
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
    }
}
