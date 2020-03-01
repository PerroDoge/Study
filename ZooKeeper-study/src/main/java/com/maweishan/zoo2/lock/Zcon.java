package com.maweishan.zoo2.lock;

import com.maweishan.zoo2.conf.DefaultWatcher;
import org.apache.zookeeper.ZooKeeper;

import java.util.concurrent.CountDownLatch;

public class Zcon {



    public static ZooKeeper getZooKeeper() {
        String ip="192.168.50.140,192.168.50.141,192.168.50.150/testLock";
        CountDownLatch latch = new CountDownLatch(1);
        DefaultWatcher watcher = new DefaultWatcher();
        ZooKeeper zooKeeper = null;

        watcher.setLatch(latch);
        try {
            zooKeeper = new ZooKeeper(ip, 1000, watcher);
            latch.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
            return zooKeeper;
    }

    public static void close(ZooKeeper zooKeeper) {
        try {
            zooKeeper.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
