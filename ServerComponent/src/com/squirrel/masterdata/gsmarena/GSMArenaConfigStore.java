package com.squirrel.masterdata.gsmarena;

import com.squirrel.commons.DataProviderConfigStore;

public class GSMArenaConfigStore extends DataProviderConfigStore {

	@Override
	protected String getConfigPropertiesFileName() {
		// TODO Auto-generated method stub
		return "gsmarena.properties";
	}
	
	public GSMArenaConfigStore(){
		initProperties();
	}
	

}
