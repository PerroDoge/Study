package com.maweishan.zoo3.conf;

import org.apache.zookeeper.ZooKeeper;

/**
 * ZooKeeper统一管理配置文件
 * @author Doge
 */
public class Main {
    public static void main(String[] args) {

        ZooKeeper zooKeeper = ZooKeeperManager.getZooKeeper();
        Config conf = new Config();
        WatcherCallBack watcherCallBack = new WatcherCallBack();
        watcherCallBack.setZooKeeper(zooKeeper);
        watcherCallBack.setConf(conf);

        //  为了获得ZooKeeper中的配置文件，她应该是一个阻塞方法。
        watcherCallBack.await();

        // while循环的意义只是为了不断输出配置文件的内容，如果配置文件被删掉就会阻塞。
        while(true) {
            if(!"".equals(conf.getConf())) {

                System.out.println(conf.getConf());
            }else{
                System.out.println("oho~ 配置么得了");
                watcherCallBack.await();
            }

        }

    }
}
