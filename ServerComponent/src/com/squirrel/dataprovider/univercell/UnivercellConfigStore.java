package com.squirrel.dataprovider.univercell;

import com.squirrel.commons.DataProviderConfigStore;

public class UnivercellConfigStore extends DataProviderConfigStore {

	@Override
	protected String getConfigPropertiesFileName() {
		// TODO Auto-generated method stub
		return "univercell_mobile.properties";
	}
	
	public UnivercellConfigStore(){
		initProperties();
	}
	

}
