package com.hex.shopper.descovery;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

public class ZKGetData {

	private static ZooKeeper zk;
	private static ZooKeeperConnection conn;
	private String gPath = "";
	//private Map<String, String> nodeMap = null;
	
	
	public Stat znode_exists(String path) throws KeeperException, InterruptedException {
		return zk.exists(path, true);
	}

	
	public String getConfData(String node) {
		/*nodeMap = new HashMap<String, String>();
		nodeMap.put(key, value)
		*/
		
		System.out.println("***** calling getConfData() ****");
		
		//String discoveryPath = "http://localhost:8181/cxf/prod/products";
		String discoveryPath = "";
		
		final CountDownLatch connectedSignal = new CountDownLatch(50);
			
		conn = new ZooKeeperConnection();
		System.out.println("=======================================================");
		String ipPort = "127.0.0.1:2181";
		
		System.out.println("IP with port:"+ipPort);
		
		try {
			zk = conn.connect(ipPort);
		} catch (IOException e) {
			System.out.println("**** Exception occured during the ZK Connection *****");
			System.out.println("ERROR : " + e.getStackTrace());
		}catch (InterruptedException e) {
			System.out.println("**** Exception occured during the ZK Connection *****");
			System.out.println("ERROR : " + e.getStackTrace());
		}
		
		System.out.println("zk Detail : " + zk.getSessionId());
		
		
		try {
			discoveryPath = getDataFromNode(node, connectedSignal);
		} catch (UnsupportedEncodingException e) {
			System.out.println("**** UnsupportedEncodingException occured during the ZK Node reading *****");
			System.out.println("ERROR : ");
			e.printStackTrace();
		} catch (KeeperException e) {
			System.out.println("**** KeeperException occured during the ZK Node reading *****");
			System.out.println("ERROR : " + e.getMessage());
			e.printStackTrace();
		} catch (InterruptedException e) {
			System.out.println("**** InterruptedException occured during the ZK Node reading *****");
			System.out.println("ERROR : ");
			e.printStackTrace();
		}
		
			
		return discoveryPath;
	}

	private String getDataFromNode(String path,
			final CountDownLatch connectedSignal) throws KeeperException,
			InterruptedException, UnsupportedEncodingException {
		
		System.out.println("***** calling under getDataFromNode() *****");
		String discoveryInfo = null;
		
		//For Global path assignment
		gPath = path;
		
		Stat stat = znode_exists(path);
		
		if (stat != null) {
			System.out.println("***** calling under stat ->> getDataFromNode() *****" + stat  );
			byte[] b = zk.getData(path, new Watcher() {

				@SuppressWarnings("incomplete-switch")
				public void process(WatchedEvent we) {
					if (we.getType() == Event.EventType.None) {
						switch (we.getState()) {
						case Expired:
							connectedSignal.countDown();
							break;
						}
					} else {
						String pathIn = gPath;
						try {
							byte[] bn = zk.getData(pathIn, false, null);
							String data = new String(bn, "UTF-8");
							System.out.println(data);
							connectedSignal.countDown();
						} catch (Exception ex) {
							System.out.println("**** Exception occured during the ZKGetData.getDataFromNode() inner class process() *****");
							System.out.println(ex.getMessage());
						}
					}
				}
			}, null);

			discoveryInfo = new String(b, "UTF-8");
			System.out.println("Discovery Info from ZKGetData.getDataFromNode() :" + discoveryInfo);
			//connectedSignal.await();
		} else {
			System.out.println("**** Node does not exists *****");
			System.out.println("**** Exception occured during the ZKGetData.getDataFromNode() *****");			
		}
		return discoveryInfo;
	}

}
