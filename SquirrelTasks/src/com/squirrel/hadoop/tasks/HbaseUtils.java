package com.squirrel.hadoop.tasks;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.NavigableMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Base64;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

import com.squirrel.hadoop.tasks.FlipKartImportTask.Map;

public class HbaseUtils {
	public static String extractValueMapper(String column, String data,String pattern) {
		// String txt =
		// "product_code::X101|product_code2::X1201|product_code3::X1031";
		if(!data.toLowerCase().contains(column.toLowerCase())){
			return "";
		}
		String re1 = "(" + column + ")"; // Variable Name 1
		String re2 = ".*?"; // Non-greedy match on filler
		//String re3 = "((?:[a-z][a-z0-9_]*))"; // Variable Name 2
		String re3 = pattern; // Variable Name 2

		Pattern p = Pattern.compile(re1 + re2 + re3, Pattern.CASE_INSENSITIVE
				| Pattern.DOTALL);
		Matcher m = p.matcher(data);
		if (m.find()) {
			// String var1=m.group(1);
			String var2 = m.group(2);
			return var2;
			// System.out.print("("+var1.toString()+")"+"("+var2.toString()+")"+"\n");
		}
		return "";
	}
	

	public static String getFileName(Context context) {
		InputSplit split = context.getInputSplit();
		Class<? extends InputSplit> splitClass = split.getClass();

		FileSplit fileSplit = null;
		if (splitClass.equals(FileSplit.class)) {
			fileSplit = (FileSplit) split;
		} else if (splitClass.getName().equals(
				"org.apache.hadoop.mapreduce.lib.input.TaggedInputSplit")) {
			// begin reflection hackery...

			try {
				Method getInputSplitMethod = splitClass
						.getDeclaredMethod("getInputSplit");
				getInputSplitMethod.setAccessible(true);
				fileSplit = (FileSplit) getInputSplitMethod.invoke(split);
			} catch (Exception e) {
				// wrap and re-throw error

			}

			// end reflection hackery
		}

		String filename = fileSplit.getPath().getName();
		return filename;
	}

	public static String getManufacturer(Context context) {
		String filename = getFileName(context);
		// int first = filename.lastIndexOf("/");
		int last = filename.lastIndexOf(".csv");
		filename = filename.substring(0, last);
		filename = filename.replaceAll("\\P{L}", "");
		return filename;
	}

	public static void addAColumnEntry(HTable table, String colFamilyName,
			String colName, String data) {
		try {
			// HTable table = new HTable(conf, tableName);
			String row = "row" + Math.random();
			byte[] rowKey = Bytes.toBytes(row);
			Put putdata = new Put(rowKey);
			putdata.add(Bytes.toBytes(colFamilyName), Bytes.toBytes(colName),
					Bytes.toBytes(data));
			table.put(putdata);
		} catch (IOException e) {
			System.out.println("Exception occured in adding data");
		}
	}

	// write a record to a table having just one column family or write only a
	// portion of a record

	public static void addRecordWithSingleColumnFamily(HTable table,
			String colFamilyName, String[] colName, String[] data) {
		try {
			// HTable table = new HTable(conf, tableName);
			String row = "row" + Math.random();
			byte[] rowKey = Bytes.toBytes(row);
			Put putdata = new Put(rowKey);
			if (colName.length == data.length) {
				for (int i = 0; i < colName.length; i++)
					putdata.add(Bytes.toBytes(colFamilyName),
							Bytes.toBytes(colName[i]), Bytes.toBytes(data[i]));
			}
			table.put(putdata);

		} catch (IOException e) {
			System.out.println("Exception occured in adding data");
		}
	}

	// add a record with any number of column families

	public static void addRecord(HTable table, String[] colFamilyName,
			String[][] colName, String[][] data) {
		try {
			// HTable table = new HTable(conf, tableName);
			String row = "row" + Math.random();
			byte[] rowKey = Bytes.toBytes(row);
			Put putdata = new Put(rowKey);
			for (int j = 0; j < colFamilyName.length; j++) {
				if (colName[j].length == data[j].length) {
					for (int i = 0; i < colName[j].length; i++)
						putdata.add(Bytes.toBytes(colFamilyName[j]),
								Bytes.toBytes(colName[j][i]),
								Bytes.toBytes(data[j][i]));
				}
			}
			table.put(putdata);

		} catch (IOException e) {
			System.out.println("Exception occured in adding data");
		}
	}

	// returns entry of a particular column of a record

	public static String getColEntry(HTable table, String rowName,
			String colFamilyName, String colName) {
		String result = null;
		try {
			// HTable table = new HTable(conf, tableName);
			byte[] rowKey = Bytes.toBytes(rowName);
			Get getRowData = new Get(rowKey);
			Result res = table.get(getRowData);
			byte[] obtainedRow = res.getValue(Bytes.toBytes(colFamilyName),
					Bytes.toBytes(colName));
			result = Bytes.toString(obtainedRow);
		} catch (IOException e) {
			System.out.println("Exception occured in retrieving data");
		}
		return result;
	}

	// returns a row in the form of a string.

	public static String getRow(HTable table, String rowName,
			String colFamilyName, String[] colName) {
		String result = colName[0];
		try {
			// HTable table = new HTable(conf, tableName);
			byte[] rowKey = Bytes.toBytes(rowName);
			Get getRowData = new Get(rowKey);
			Result res = table.get(getRowData);
			for (int j = 0; j < colName.length; j++) {
				byte[] obtainedRow = res.getValue(Bytes.toBytes(colFamilyName),
						Bytes.toBytes(colName[j]));
				System.out.println(colName[j]);
				String s = Bytes.toString(obtainedRow);
				if (j == 0)
					result = colName[j] + "=" + s;
				else
					result = result + "&" + colName[j] + "=" + s;
				System.out.println(s);
			}

		} catch (IOException e) {
			System.out.println("Exception occured in retrieving data");
		}
		return result;
	}

	// returns an arraylist of all entries of a column.

	public static ArrayList<String> getCol(HTable table, String colFamilyName,
			String colName) {
		ArrayList<String> al = new ArrayList<String>();
		ResultScanner rs = null;
		Result res = null;

		try {
			// HTable table = new HTable(conf, tableName);

			Scan scan = new Scan();
			scan.addColumn(Bytes.toBytes(colFamilyName), Bytes.toBytes(colName));
			rs = table.getScanner(scan);
			while ((res = rs.next()) != null) {
				String colEntry = null;
				byte[] obtCol = res.getValue(Bytes.toBytes(colFamilyName),
						Bytes.toBytes(colName));
				colEntry = Bytes.toString(obtCol);
				al.add(colEntry);
			}

		} catch (IOException e) {
			System.out.println("Exception occured in retrieving data");
		} finally {
			rs.close();
		}
		return al;

	}
	/*public static HashMap<String, Object> getRow(HTable table, String rowName,
			String[] colFamilyName,String[][] column) {
		HashMap<String, Object> columnFamily = new HashMap<String, Object>();
		try {
			// HTable table = new HTable(conf, tableName);
			byte[] rowKey = Bytes.toBytes(rowName);
			Get getRowData = new Get(rowKey);
			Result res = table.get(getRowData);
			String s = "";
			if(res.size()==0){
				return columnFamily;
			}
			for (int i = 0; i < colFamilyName.length; i++) {
				
				HashMap<String, Object> cloumn = new HashMap<String, Object>();					
				String[] columns = getColumnsFromFamily(colFamilyName[i],
						res);
				if (columns.length == 1 && columns[0].length() == 0) {
					byte[] obtainedRow = res.getValue(
							Bytes.toBytes(colFamilyName[i]),
							Bytes.toBytes(""));
					s = Bytes.toString(obtainedRow);
					columnFamily.put(colFamilyName[i], s);
				} else {
					
					for (String column : columns) {
						byte[] obtainedRow = res.getValue(
								Bytes.toBytes(colFamilyName[i]),
								Bytes.toBytes(column));
						s = Bytes.toString(obtainedRow);
						System.out.println(s);
						cloumn.put(column, s);
					}
					columnFamily.put(colFamilyName[i], cloumn);
				}
			}

		} catch (IOException e) {
			System.out.println("Exception occured in retrieving data");
		}
		return columnFamily;
	
		
	}*/
	public static HashMap<String, Object> getRow(HTable table, String rowName,
			String[] colFamilyName) {
		HashMap<String, Object> columnFamily = new HashMap<String, Object>();
		try {
			// HTable table = new HTable(conf, tableName);
			byte[] rowKey = Bytes.toBytes(rowName);
			Get getRowData = new Get(rowKey);
			Result res = table.get(getRowData);
			String s = "";
			if(res.size()==0){
				return columnFamily;
			}
			for (int i = 0; i < colFamilyName.length; i++) {
				
				HashMap<String, Object> cloumn = new HashMap<String, Object>();					
				String[] columns = getColumnsFromFamily(colFamilyName[i],
						res);
				if (columns.length == 1 && columns[0].length() == 0) {
					byte[] obtainedRow = res.getValue(
							Bytes.toBytes(colFamilyName[i]),
							Bytes.toBytes(""));
					s = Bytes.toString(obtainedRow);
					columnFamily.put(colFamilyName[i], s);
				} else {
					
					for (String column : columns) {
						byte[] obtainedRow = res.getValue(
								Bytes.toBytes(colFamilyName[i]),
								Bytes.toBytes(column));
						s = Bytes.toString(obtainedRow);
						System.out.println(s);
						cloumn.put(column, s);
					}
					columnFamily.put(colFamilyName[i], cloumn);
				}
			}

		} catch (IOException e) {
			System.out.println("Exception occured in retrieving data");
		}
		return columnFamily;
	
		
	}
	// returns a list of hashmaps, each hashmap containing entries of a single
	// record.

	public static HashMap<String, HashMap<String, Object>> getAllValuesFromTable(
			HTable table, String[] colFamilyName) {
		ResultScanner rs = null;
		
		HashMap<String, HashMap<String, Object>> rows = new HashMap<String, HashMap<String, Object>>();
		Result res = null;
		try {
			// HTable table = new HTable(conf, tableName);
			Scan scan = new Scan();
			rs = table.getScanner(scan);
			while ((res = rs.next()) != null) {
				
				String s = "";
				HashMap<String, Object> cloumnFamily = new HashMap<String, Object>();
				for (int i = 0; i < colFamilyName.length; i++) {
					
					HashMap<String, Object> cloumn = new HashMap<String, Object>();					
					String[] columns = getColumnsFromFamily(colFamilyName[i],
							res);
					// Not columns
					if (columns.length == 1 && columns[0].length() == 0) {
						byte[] obtainedRow = res.getValue(
								Bytes.toBytes(colFamilyName[i]),
								Bytes.toBytes(""));
						s = Bytes.toString(obtainedRow);
						cloumnFamily.put(colFamilyName[i], s);
					} else {
						
						for (String column : columns) {
							byte[] obtainedRow = res.getValue(
									Bytes.toBytes(colFamilyName[i]),
									Bytes.toBytes(column));
							s = Bytes.toString(obtainedRow);
							System.out.println(s);
							cloumn.put(column, s);
						}
						cloumnFamily.put(colFamilyName[i], cloumn);
					}
				}
				String rowkey = Bytes.toString(res.getRow());
				rows.put(rowkey, cloumnFamily);
			}
		} catch (IOException e) {
			System.out.println("Exception occured in retrieving data");
		} finally {
			rs.close();
		}
		return rows;
	}
	
	public static HashMap<String, String> getAllValuesFromTableAsString(
			HTable table, String[] colFamilyName) {
		ResultScanner rs = null;
		
		HashMap<String, String> rows = new HashMap<String, String>();
		Result res = null;
		try {
			// HTable table = new HTable(conf, tableName);
			Scan scan = new Scan();
			rs = table.getScanner(scan);
			while ((res = rs.next()) != null) {
				
				String value = "";
				String data = "";
				//HashMap<String, Object> cloumnFamily = new HashMap<String, Object>();
				for (int i = 0; i < colFamilyName.length; i++) {
					
				//	HashMap<String, Object> cloumn = new HashMap<String, Object>();					
					String[] columns = getColumnsFromFamily(colFamilyName[i],
							res);
					// Not columns
					if (columns.length == 1 && columns[0].length() == 0) {
						byte[] obtainedRow = res.getValue(
								Bytes.toBytes(colFamilyName[i]),
								Bytes.toBytes(""));
						value = Bytes.toString(obtainedRow);
						data = data + "{"+colFamilyName[i]+"="+value+"},";
					} else {
						
						for (String column : columns) {
							byte[] obtainedRow = res.getValue(
									Bytes.toBytes(colFamilyName[i]),
									Bytes.toBytes(column));
							value = Bytes.toString(obtainedRow);
							//System.out.println(s);
							data = data + "{"+colFamilyName[i]+":"+column+"="+value+"},";
							//cloumn.put(column, s);
						}
					//	cloumnFamily.put(colFamilyName[i], cloumn);
					}
				}
				String rowkey = Bytes.toString(res.getRow());
				rows.put(rowkey, data);
				
			}
		} catch (IOException e) {
			System.out.println("Exception occured in retrieving data");
		} finally {
			rs.close();
		}
		return rows;
	}

	private static String[] getColumnsFromFamily(String columnFamily, Result res) {
		NavigableMap<byte[], byte[]> familyMap = res.getFamilyMap(Bytes
				.toBytes(columnFamily));
		String[] Quantifers = new String[familyMap.size()];

		int counter = 0;
		for (byte[] bQunitifer : familyMap.keySet()) {
			Quantifers[counter++] = Bytes.toString(bQunitifer);

		}
		return Quantifers;
	}

	// function to delete a row from the table.

	public static String deleteTableRow(HTable table, String rowName) {
		String result = null;
		try {
			// HTable table = new HTable(conf, tableName);
			byte[] rowKey = Bytes.toBytes(rowName);
			Delete delRowData = new Delete(rowKey);
			table.delete(delRowData);
		} catch (IOException e) {
			System.out.println("Exception occured in retrieving data");
		}
		return result;

	}

	public static String convertScanToString(Scan scan) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(out);
		scan.write(dos);
		return Base64.encodeBytes(out.toByteArray());
	}

}
