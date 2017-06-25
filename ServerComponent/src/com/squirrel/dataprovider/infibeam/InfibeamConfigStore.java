package com.squirrel.dataprovider.infibeam;

import com.squirrel.commons.DataProviderConfigStore;

public class InfibeamConfigStore extends DataProviderConfigStore {

	@Override
	protected String getConfigPropertiesFileName() {
		// TODO Auto-generated method stub
		return "gsmarena.properties";
	}
	
	public InfibeamConfigStore(){
		initProperties();
	}
	

}
