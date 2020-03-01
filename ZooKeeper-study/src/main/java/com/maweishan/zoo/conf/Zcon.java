package com.maweishan.zoo.conf;

import org.apache.zookeeper.ZooKeeper;

import java.util.concurrent.CountDownLatch;

/**
 * @author Doge
 * ZooKeeper的连接。
 */
public class Zcon {


    private static final String IP_ADDRESS="192.168.50.140,192.168.50.141,192.168.50.150/testConf";
    private static CountDownLatch latch = new CountDownLatch(1);

    public static ZooKeeper getZooKeeper() throws Exception {
        ZooKeeper zooKeeper = new ZooKeeper(IP_ADDRESS, 1000, new DefaultWatcher(latch));
        latch.await();
        return zooKeeper;
    }
}
