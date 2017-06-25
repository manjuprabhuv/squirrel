package com.squirrel.commons;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public abstract class DataProviderConfigStore {
	private Properties mProperties;

	protected abstract String getConfigPropertiesFileName();

	protected final void initProperties() {

		mProperties = new Properties();
		try {
			InputStream is = null;
			try {
				String path = absPath(getClass().getPackage().getName());
				is = this
						.getClass()
						.getClassLoader()
						.getResourceAsStream(
								path + "/" + getConfigPropertiesFileName());
				if (is != null)
					mProperties.load(is);
			} finally {
				if (is != null)
					is.close();
			}
		} catch (IOException e) {

		}
		//
	}

	public final boolean setProperty(String propertyName, String propertyValue) {
		if (mProperties == null || propertyName == null)
			return false;
		mProperties.setProperty(propertyName, propertyValue);
		return true;
	}

	private String absPath(String str) {
		str = str.replace(".", "/");
		return str;
	}

	public final String getProperty(String propertyName) {
		if (mProperties == null)
			return null;
		return mProperties.getProperty(propertyName);
	}
	
	public final Properties getProperties() {
		if (mProperties == null)
			return null;
		return mProperties;
	}

}
