package com.squirrel.commons;

import java.util.List;

public abstract class DataStager {

	public abstract void cancelBatch();

	public abstract boolean closeBatch();

	public abstract boolean destroy();

	public abstract String getName();

	public abstract boolean init();

	public abstract boolean openBatch(String stageFile,String stageFolderName);

	public abstract boolean stageValues(String rule, List<String> values);

}
