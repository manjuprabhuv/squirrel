package com.squirrel.utils;

import java.io.File;
import java.io.FilenameFilter;

public class FileFilter implements FilenameFilter{
	String name;

	public FileFilter(String name) {
		this.name = name;
	}

	@Override
	public boolean accept(File dir, String name) {
		return name.contains(this.name);
	}
}
