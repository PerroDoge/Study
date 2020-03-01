package com.maweishan.zoo.lock;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

/**
 * @author Doge
 */
public class Main_LockTest {

    private static ZooKeeper zooKeeper;
    public static void main(String[] args) throws Exception {
        zooKeeper = Zcon.getZooKeeper();

        for(int i = 0; i < 10; i++) {
            WatcherCallBack watcherCallBack = new WatcherCallBack();
            String lock = "lock";
            new Thread(() -> {

                try {
                    watcherCallBack.setZooKeeper(zooKeeper);
                    //尝试获得锁
                    watcherCallBack.trylock();

                    System.out.println(Thread.currentThread().getName()+ "at working");
                    Thread.sleep(1000);
                    watcherCallBack.unlock();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }).start();
        }

        while(true) {

        }
    }
}
