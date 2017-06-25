package com.squirrel.dataprovider.mobilestore;

import java.io.IOException;
import java.util.List;

public abstract class MobileStoreAbstractCrawler {
	
	public abstract List<String>  crawl() throws IOException;

}
