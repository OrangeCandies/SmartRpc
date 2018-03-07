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

下面对于整个RPC过程做个总结
> 0. 服务的注册
  
  首先，使用了Zookeeper作为服务的注册中心，当一个服务端上线的时候会主动去Zookeeper上注册
  ZooKeeper上根据自己提供的服务分类，如果自己提供该服务的话会在服务节点下创建一个子节点，
  包含了自己的实际调用地址。这样，服务的注册就完成了。
  
> 1. 客户端发起调用

  当客户端要发起一次调用的时候，首先会借助代理类工厂方法（eg：RpcProxyFactory.create(Hello.class) ）
  创建一个动态代理类，该动态代理类的invoke方法中，会发起一次远程的PRC调用，这时候会去创建一个Request对象
  包含了要调用的方法的基本信息，然后把整个Request交给通信模块。
  
> 2. 通信

  通信模块接到上层的请求服务后，首先会去服务注册中心拉取提供该服务的服务端信息，接下来随机选择或者权重选择一个
  服务端，使用Netty完成连接，发送Request，并根据Requst设置的回调信息生成一个Result并保存起来。
  
> 3. 服务端完成服务

   服务端接受到RPC调用信息后发起一次调用，并把调用结果封装成Response信息，回送给客户端
 
> 4. 客户端接收到调用结果

   客户端接收到调用结果时候根据ResestId取出Result，调用设置好的回调函数，一次调用完成。
   
>Something to say
 
 支持同步调用和异步调用，每次调用调用会返回一个RpcResult对象，这个对象提供一个get()方法，这个方法借助内部的一个AQS
 实现了同步调用的阻塞，当RpcResponse返回时候会调用done()方法，该方法会解除阻塞。异步调用直接返回一个RpcResult对象
 并不会阻塞，也可以通过IsDone()方法来获取异步调用结果是否已经返回。