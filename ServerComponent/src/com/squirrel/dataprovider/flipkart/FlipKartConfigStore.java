package com.squirrel.dataprovider.flipkart;

import com.squirrel.commons.DataProviderConfigStore;

public class FlipKartConfigStore extends DataProviderConfigStore {

	@Override
	protected String getConfigPropertiesFileName() {
		// TODO Auto-generated method stub
		return "gsmarena.properties";
	}
	
	public FlipKartConfigStore(){
		initProperties();
	}
	

}
