package com.maweishan.zoo2.conf;

import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class Zcon {



    public static ZooKeeper getZooKeeper() {
        String IP="192.168.50.140,192.168.50.141,192.168.50.150/testConf";
        CountDownLatch latch = new CountDownLatch(1);
        DefaultWatcher watcher = new DefaultWatcher();
        ZooKeeper zooKeeper = null;

        watcher.setLatch(latch);
        try {
            zooKeeper = new ZooKeeper(IP, 1000, watcher);
            latch.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
            return zooKeeper;
    }
}
