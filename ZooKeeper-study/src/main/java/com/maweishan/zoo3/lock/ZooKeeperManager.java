package com.maweishan.zoo3.lock;

import org.apache.zookeeper.ZooKeeper;

import java.util.concurrent.CountDownLatch;

/**
 * @author Doge
 */
public class ZooKeeperManager {
    private static ZooKeeper zooKeeper;
    private static String ip = "192.168.50.140,192.168.50.141,192.168.50.150/testLock";
    private static CountDownLatch latch = new CountDownLatch(1);
    private static DefaultWather wather;
    public static ZooKeeper getZooKeeper() {
        wather = new DefaultWather();
        wather.setLatch(latch);
        try {
            zooKeeper = new ZooKeeper(ip, 1000, wather);
            latch.await();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return zooKeeper;
    }

    public static void close() {
        if(zooKeeper != null) {
            try {
                zooKeeper.close();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
