package com.squirrel.commons;

public abstract class DataProvider {
	protected DataProviderConfigStore mConfigStore = null;
	protected DataStager dataStager = null;

	public boolean init(DataStager dataStager) {
		if (dataStager == null)
			return false;

		this.dataStager = dataStager;
		return true;
	}

	public abstract boolean destroy();

	public abstract String getName();

	public abstract boolean crawlWebsite();

	public abstract boolean parseAndExtractData();

}
