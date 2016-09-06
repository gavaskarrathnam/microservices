package com.hex.shopper.descovery;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

import com.hex.shopper.util.HostHelper;

/**
 * 
 * @author Gavaskar Rathnam
 *
 */
public class ZkConnect {
	private ZooKeeper zk;
    private CountDownLatch connSignal = new CountDownLatch(0);

    //host should be 127.0.0.1:3000,127.0.0.1:3001,127.0.0.1:3002
    public ZooKeeper connect(String host) throws Exception {
        zk = new ZooKeeper(host, 3000, new Watcher() {
            public void process(WatchedEvent event) {
                if (event.getState() == KeeperState.SyncConnected) {
                    connSignal.countDown();
                }
            }
        });
        connSignal.await();
        return zk;
    }

    public void close() throws InterruptedException {
        zk.close();
    }

    public void createNode(String path, byte[] data) throws Exception{
        zk.create(path, data, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    }

    public void updateNode(String path, byte[] data) throws Exception{
        zk.setData(path, data, zk.exists(path, true).getVersion());
    }

    public void deleteNode(String path) throws Exception{
        zk.delete(path,  zk.exists(path, true).getVersion());
    }

    public String getNodeDetail(String node){
        //System.out.println("@@@@@@@@@@@@@@@@@@@@@@@ ZkConnect.getNodeDetail() @@@@@@@@@@@@@@@@@@@@@");
        ZooKeeper zk = null;
        StringBuilder discoveryURI = new StringBuilder();
        HostHelper helper = new HostHelper();
        Map<String, String> hostMap = helper.getIPAddress();
		try {
			zk = connect( hostMap.get("zk.ipaddress") + ":" + hostMap.get("zk.port"));			
	        String newNode = node;
	        byte[] data = zk.getData(newNode, true, zk.exists(newNode, true));

	        //System.out.println("GetData before setting" );
	        for ( byte dataPoint : data){
	        	discoveryURI.append((char)dataPoint);
	        }
		} catch (Exception e) {
			System.out.println("**** Node does not exists *****");
			e.printStackTrace();
		}
		//System.out.println("Final result :" + discoveryURI.toString());
        return discoveryURI.toString();
    }
    
   		    
}
