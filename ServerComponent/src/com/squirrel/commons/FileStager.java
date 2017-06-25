package com.squirrel.commons;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class FileStager extends DataStager {
	private final String STAGE_FOLDER = "D:\\project\\stage";

	private static final String BLANK_VALUE = "";

	private File outputFile;

	private BufferedWriter mBatchWriter;

	@Override
	public void cancelBatch() {
		try {
			if (mBatchWriter != null)
				mBatchWriter.close();
		} catch (IOException e) {

		}
		mBatchWriter = null;
		outputFile = null;
	}

	@Override
	public boolean closeBatch() {

		try {
			if (mBatchWriter != null)
				mBatchWriter.close();
		} catch (IOException e) {

		}
		mBatchWriter = null;
		outputFile = null;

		return true;
	}

	@Override
	public boolean destroy() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public String getName() {
		return "File Stager";
	}

	@Override
	public boolean init() {

		return true;
	}

	@Override
	public boolean openBatch(String stageFile,String stageFolderName) {
		File folder = new File(STAGE_FOLDER+"\\"+stageFolderName);
		if(!folder.exists()){
			folder.mkdirs();
		}
		SimpleDateFormat simpleDateFormat =
        new SimpleDateFormat("dd-MM-yy-HH-mm");
		String dateAsString = simpleDateFormat.format(new Date());
		outputFile = new File(folder, stageFile+dateAsString+".csv");

		try {
			mBatchWriter = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(outputFile), "UTF-8"));

		} catch (IOException e) {

		}
		return true;
	}

	@Override
	public boolean stageValues(String rule, List<String> values) {

		try {
			mBatchWriter.write("\"");
			mBatchWriter.write(rule);
			mBatchWriter.write("\"");
			mBatchWriter.write("\n");
			for (String value : values) {
				mBatchWriter.write(",");
				mBatchWriter.write(fieldValueToBeWritten(value));
				mBatchWriter.write("\n");
			}

			mBatchWriter.write("\n");
		} catch (IOException e) {
			//throw new Exception();
		}

		return true;
	}

	private String fieldValueToBeWritten(String fieldValue) {
		return (fieldValue != null) ? fieldValue : BLANK_VALUE;
	}

}
