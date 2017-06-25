package com.squirrel.masterdata.gsmarena;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.squirrel.commons.DataProvider;
import com.squirrel.commons.DataStager;
import com.squirrel.commons.FileStager;
import com.squirrel.utils.SquirreCachedlList;
import com.squirrel.utils.SquirrelCacheUtils;

public class GSMArenaDataProvider extends DataProvider {

	@Override
	public boolean destroy() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "GSM Arena Crawler and Parser";
	}

	@Override
	public boolean crawlWebsite() {
		GSMArenaCrawler crawler = new GSMArenaCrawler();
		try {
			crawler.crawl(mConfigStore.getProperties());
			for (int i = 0; i < SquirreCachedlList.keyList.size(); i++) {
				List<String> list;
				String key = SquirreCachedlList.keyList.get(i);
				list = SquirrelCacheUtils.deSerializeObjects(key);
				dataStager.openBatch(key,"gsmArena\\data\\"+key);
				dataStager.stageValues(key+"MasterList", list);
				dataStager.closeBatch();				
			}
			
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean parseAndExtractData() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean init(DataStager dataStager){
		if(dataStager==null)
			return false;
		super.init(dataStager);
		mConfigStore = new GSMArenaConfigStore();
		return true;
	}
	
	public static void main(String[] args) {
		/*String key = "sony";
		List<String> list;
		list = SquirrelCacheUtils.deSerializeObjects(key);
		FileStager dataStager = new FileStager();
		dataStager.openBatch(key,"gsmArena\\data\\"+key);
		dataStager.stageValues(key+"MasterList", list);
		dataStager.closeBatch();*/
		GSMArenaDataProvider provider = new GSMArenaDataProvider();
		FileStager stager = new FileStager();
		provider.init(stager);
		provider.crawlWebsite();
	}

}
