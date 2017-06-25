package com.squirrel.dataprovider.infibeam;

import java.io.IOException;
import java.util.List;

public abstract class InfibeamAbstractCrawler {
	
	public abstract List<String>  crawl() throws IOException;

}
