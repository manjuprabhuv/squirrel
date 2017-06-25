package com.squirrel.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class SquirrelCacheUtils {
	private static String cacheLocation = "D:\\project\\stage\\objCache";

	public static void serializeObject(String key, Object obj)
			throws IOException {
		File folder = new File(cacheLocation);
		if (!folder.exists()) {
			folder.mkdirs();
		}

		OutputStream file = new FileOutputStream(cacheLocation + "\\" + key);
		OutputStream buffer = new BufferedOutputStream(file);
		ObjectOutput output = new ObjectOutputStream(buffer);

		try {
			output.writeObject(obj);
		} finally {
			output.close();
		}

	}

	public static void clearCache() {
		File folder = new File(cacheLocation);
		deleteFolder(folder);
		File create = new File(cacheLocation);
		create.mkdirs();
	}

	public static List<String> deSerializeObject(String key)
			throws IOException, ClassNotFoundException {

		InputStream file = new FileInputStream(cacheLocation + "\\" + key);
		InputStream buffer = new BufferedInputStream(file);
		ObjectInput input = new ObjectInputStream(buffer);
		List<String> list = null;
		try {
			// deserialize the List
			list = (List<String>) input.readObject();

		} finally {
			input.close();
		}
		return list;
	}

	public static List<String> deSerializeObjects(String key) {
		File folder = new File(cacheLocation);
		FileFilter filter = new FileFilter(key);
		File[] listOfFiles = folder.listFiles(filter);

		int fileCount = listOfFiles.length;
		List<String> finalList = new ArrayList<String>();

		for (int i = 1; i <= fileCount; i++) {
			try {
				List<String> list = deSerializeObject(key + "_" + i);
				finalList.addAll(list);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

			}

		}
		return finalList;
	}

	public static void deleteFolder(File folder) {
		File[] files = folder.listFiles();
		if (files != null) { // some JVMs return null for empty dirs
			for (File f : files) {
				if (f.isDirectory()) {
					deleteFolder(f);
				} else {
					f.delete();
				}
			}
		}
		folder.delete();
	}

}
