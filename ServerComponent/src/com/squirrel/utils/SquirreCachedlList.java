package com.squirrel.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JEditorPane;

import com.sun.java.swing.plaf.windows.WindowsTreeUI.CollapsedIcon;

public class SquirreCachedlList {

	private List<String> list = new ArrayList<String>();
	//private volatile int  keyCounter = 0;
	private volatile Map<String, Integer> keyCounterMap = new HashMap<String, Integer>();
	//private String previousKey;
	public static List<String> keyList = new ArrayList<String>();
	private boolean first = true;
	

	public synchronized void add(String key, String value) throws IOException {

		if (list.size() < 10) {
			list.add(value);
			keyList.add(key);

		} else {
			if (first) {
				list.add(value);
				first = false;
			} else {
				list.add(value);
				Integer keyCounter = keyCounterMap.get(key);
				if(keyCounter==null){
					keyCounter = 1;
					keyCounterMap.put(key, keyCounter);
				}else{
					keyCounter++;
					keyCounterMap.put(key, keyCounter);
				}
				List<String> cacheList = new ArrayList<String>();
				cacheList.addAll(list);
				list.clear();
				SquirrelCacheUtils.serializeObject(key + "_" + keyCounter,
						cacheList);
			}
		}

	}

	public synchronized void finalizeCache(String key) {
		try {
			//keyCounter++;
			Integer keyCounter = keyCounterMap.get(key);
			keyCounter++;
			SquirrelCacheUtils.serializeObject(key + "_" + keyCounter,
					list);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
