package com.hex.shopper.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 
 * @author Gavaskar Rathnam
 *
 */
public class HostHelper {

	/**
	 * 
	 * @return
	 */
	public Map<String, String> getIPAddress() {
		String root = getRoot();
		Properties prop = new Properties();
		InputStream input = null;
		Map<String, String> hostMap = new HashMap<String, String>();
		try {
			System.out.println("Full Path :" + root + "config.properties");
			input = new FileInputStream(root + "config.properties");

			// load a properties file
			prop.load(input);
			
			// get the property value and print it out
			for(String key : prop.stringPropertyNames()) {
			  System.out.println(key + " => " + prop.getProperty(key));
			  hostMap.put(key, prop.getProperty(key).trim());
			}
			
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		System.out.println("Host Data size:" + hostMap.size());
		return hostMap;
	}

	/**
	 * 
	 * @return
	 */
	public String getRoot() {
		Properties p = System.getProperties();
		String kHome = p.getProperty("karaf.home");
		kHome = kHome.replace("\\", "/");
		StringBuffer path = new StringBuffer(kHome);
		path.append("/deploy/");
		String root = path.toString();
		return root;
	}
}
