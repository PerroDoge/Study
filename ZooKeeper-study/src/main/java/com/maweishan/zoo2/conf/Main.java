package com.maweishan.zoo2.conf;

import org.apache.zookeeper.ZooKeeper;

public class Main {
    public static void main(String[] args) {
        ZooKeeper zooKeeper = Zcon.getZooKeeper();
        MyConf conf = new MyConf();
        WatchCallBack watchCallBack = new WatchCallBack();
        watchCallBack.setConf(conf);
        watchCallBack.setZooKeeper(zooKeeper);

        watchCallBack.await();


        while(true) {

            if(conf.getConf().equals("")) {
                System.out.println("nonono,糟糕了");
                watchCallBack.await();
            }else{
                System.out.println(conf.getConf());
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }
}
