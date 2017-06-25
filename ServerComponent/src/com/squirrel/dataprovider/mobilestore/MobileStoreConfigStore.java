package com.squirrel.dataprovider.mobilestore;

import com.squirrel.commons.DataProviderConfigStore;

public class MobileStoreConfigStore extends DataProviderConfigStore {

	@Override
	protected String getConfigPropertiesFileName() {
		// TODO Auto-generated method stub
		return "gsmarena.properties";
	}
	
	public MobileStoreConfigStore(){
		initProperties();
	}
	

}
