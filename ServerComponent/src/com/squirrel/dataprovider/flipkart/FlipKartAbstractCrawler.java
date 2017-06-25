package com.squirrel.dataprovider.flipkart;

import java.io.IOException;
import java.util.List;

public abstract class FlipKartAbstractCrawler {
	
	public abstract List<String>  crawl() throws IOException;

}
