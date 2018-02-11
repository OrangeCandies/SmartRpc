package org.smartRpc;

import org.apache.zookeeper.*;

import java.io.IOException;

public class test {
    public static void main(String[] args) {
        try {
            ZooKeeper zooKeeper = new ZooKeeper("127.0.0.1:2181", 5000, new Watcher() {
                @Override
                public void process(WatchedEvent watchedEvent) {
                    if(watchedEvent.getState() == Event.KeeperState.SyncConnected)
                    {
                        System.out.println("Connectioned");
                    }
                }
            },false);
            if(zooKeeper != null){
                String path = zooKeeper.create("/Registry","Hello".getBytes(),
                        ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }
}
