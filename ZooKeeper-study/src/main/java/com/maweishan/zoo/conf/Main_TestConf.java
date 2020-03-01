package com.maweishan.zoo.conf;

import org.apache.zookeeper.ZooKeeper;

/**
 * @author Doge
 */
public class Main_TestConf {

    private static ZooKeeper zooKeeper = null;
    private static MyConf myConf = new MyConf();
    private static WatcherCallBack watcherCallBack = new WatcherCallBack();




    public static void main(String[] args) throws Exception {
        zooKeeper = Zcon.getZooKeeper();

        watcherCallBack.setZooKeeper(zooKeeper);
        watcherCallBack.setMyConf(myConf);

        watcherCallBack.aWait();

        while (true) {
            if(myConf.getConf().equals("")) {
                System.out.println("nonono,完蛋了");
                watcherCallBack.aWait();
            }else{
                System.out.println("the data is " + myConf.getConf());
            }
            Thread.sleep(1000);
        }
    }
}
