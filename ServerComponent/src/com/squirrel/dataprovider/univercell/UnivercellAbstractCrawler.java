package com.squirrel.dataprovider.univercell;

import java.io.IOException;
import java.util.List;

public abstract class UnivercellAbstractCrawler {
	
	public abstract List<String>  crawl() throws IOException;

}
