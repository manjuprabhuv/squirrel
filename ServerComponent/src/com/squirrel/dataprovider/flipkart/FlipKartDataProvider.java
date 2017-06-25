package com.squirrel.dataprovider.flipkart;

import java.io.IOException;
import java.util.List;

import com.squirrel.commons.DataProvider;
import com.squirrel.commons.DataStager;
import com.squirrel.commons.FileStager;
import com.squirrel.utils.SquirreCachedlList;
import com.squirrel.utils.SquirrelCacheUtils;

public class FlipKartDataProvider extends DataProvider {

	@Override
	public boolean destroy() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "FlipKart Crawler and Parser";
	}

	@Override
	public boolean crawlWebsite() {
		FlipKartInfoCrawler crawler = new FlipKartInfoCrawler();
		try {
			crawler.crawl();
			for (int i = 0; i < SquirreCachedlList.keyList.size(); i++) {
				List<String> list;
				String key = SquirreCachedlList.keyList.get(i);
				list = SquirrelCacheUtils.deSerializeObjects(key);
				dataStager.openBatch(key,"flipkart\\"+key);
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
		mConfigStore = new FlipKartConfigStore();
		return true;
	}
	
	public static void main(String[] args) {
		FlipKartDataProvider provider = new FlipKartDataProvider();
		FileStager stager = new FileStager();
		provider.init(stager);
		provider.crawlWebsite();
	}

}
