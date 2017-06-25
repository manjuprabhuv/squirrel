package com.squirrel.commons;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public abstract class DataTransformer {

	protected Properties mConfigProperties;

	private DataTransformer mNextTransformer;

	protected abstract boolean doInit();

	protected abstract boolean doTransform();

	protected abstract String getConfigPropertiesFileName();

	public final String getConfigProperty(String propertyName) {
		if (mConfigProperties == null)
			return null;
		return mConfigProperties.getProperty(propertyName);
	}

	public abstract String getName();

	protected abstract DataTransformer getNextTransformer();

	protected boolean init() {
		mNextTransformer = getNextTransformer();
		return doInit()
				&& (mNextTransformer == null || mNextTransformer.init());
	}

	protected void initConfigProperties() {

		mConfigProperties = new Properties();
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
					mConfigProperties.load(is);
			} finally {
				if (is != null)
					is.close();
			}
		} catch (IOException e) {

		}

	}

	public final boolean transform() {
		if (!init())
			return false;
		return doTransform()
				&& (mNextTransformer == null || mNextTransformer.transform());
	}

	private String absPath(String str) {
		str = str.replace(".", "/");
		return str;
	}
}
