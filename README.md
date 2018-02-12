自己在学习完Netty和Zookeeper以后
阅读了[黄勇老师《轻量级分布式 RPC 框架》](https://my.oschina.net/huangyong/blog/361751)后自己动手完成的一款RPC框架
优点有
1. 底层使用Netty通信 完全解决了TCP黏包的问题
2. 实现了服务的动态注册
3. 服务器部分使用了Spring，可以做到服务发布的动态扫描

相比于原框架的改进有

1. 序列化模块改成了更为熟悉的Jackson
2. 在Netty线程模型中 是每个EventLoop占用一个线程，而一个EventLoop则要负责多个Channel 当请求耗时操作时
    会阻塞EventLoop 因此采用了EventLoop接受到任务后直接提交给线程池，线程处理完后再发送结果
3. 修改了代理模型 增加了异步调用的架构，可以自己调用的时候传入回调函数，再执行结果返回的时候回调此函数
4. 连接的复用
示例
```       
           //普通调用：
           Hello hello = RpcProxyFactory.create(Hello.class);
           String liuhui = hello.hello("liuhui");
           System.out.println(liuhui);
           // 异步调用
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
                   })；
            //或者为了避免设置回调函数太迟以至于结果已经返回可以这样
             asyncProxy.call(new IAsyCallback() {
                        @Override
                        public void success(Object result) {
                            System.out.println("Right");
                            System.out.println(result);
                        }
            
                        @Override
                        public void fail(Exception e) {
                            System.out.println("failed");
                        }
                    },"hello","LiuHui");
            

```
使用方法见测试包    