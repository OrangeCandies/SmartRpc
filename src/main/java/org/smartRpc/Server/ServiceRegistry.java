package org.smartRpc.Server;

import org.apache.zookeeper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartRpc.util.ConfigUtil;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class ServiceRegistry{
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceRegistry.class);

    private final CountDownLatch count = new CountDownLatch(1);

    private String registryAddress;

    public ServiceRegistry(String registryAddress) {
        this.registryAddress = registryAddress;
    }

    public void registry(String data){
        if(data != null){
            ZooKeeper zooKeeper = connectServer();
            if(zooKeeper != null){
                createNode(zooKeeper,data);
            }
        }
    }

    private void createNode(ZooKeeper zooKeeper, String data) {
        byte[] info = data.getBytes();
        try {
            String path = zooKeeper.create(ConfigUtil.ZK_DATA_PATH,info,
                    ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);


        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private ZooKeeper connectServer() {
        ZooKeeper zooKeeper = null;
        try {
            zooKeeper = new ZooKeeper(registryAddress, ConfigUtil.ZK_TIMEOUT, new Watcher() {
                @Override
                public void process(WatchedEvent watchedEvent) {
                    if(watchedEvent.getState() == Event.KeeperState.SyncConnected){
                        count.countDown();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            count.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.err.println(zooKeeper);
        return zooKeeper;
    }
}
