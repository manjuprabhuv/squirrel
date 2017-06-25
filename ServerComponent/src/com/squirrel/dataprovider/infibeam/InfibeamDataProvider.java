package com.squirrel.dataprovider.infibeam;

import java.io.IOException;
import java.util.List;

import com.squirrel.commons.DataProvider;
import com.squirrel.commons.DataStager;
import com.squirrel.commons.FileStager;
import com.squirrel.utils.SquirreCachedlList;
import com.squirrel.utils.SquirrelCacheUtils;

public class InfibeamDataProvider extends DataProvider {

	@Override
	public boolean destroy() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Infibeam Crawler and Parser";
	}

	@Override
	public boolean crawlWebsite() {
		InfibeamInfoCrawler crawler = new InfibeamInfoCrawler();
		try {
			crawler.crawl();
			for (int i = 0; i < SquirreCachedlList.keyList.size(); i++) {
				List<String> list;
				String key = SquirreCachedlList.keyList.get(i);
				list = SquirrelCacheUtils.deSerializeObjects(key);
				dataStager.openBatch(key,"infibeam\\"+key);
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
		mConfigStore = new InfibeamConfigStore();
		return true;
	}
	
	public static void main(String[] args) {
		InfibeamDataProvider provider = new InfibeamDataProvider();
		FileStager stager = new FileStager();
		provider.init(stager);
		provider.crawlWebsite();
	}

}
