package com.squirrel.hadoop.tasks;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.thrift.generated.Hbase;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;

public class ExampleClient2 {

	public static void main(String[] args) throws IOException {
		System.out
				.println("**************************START******************************");
		
		remote();
		Logger log = Logger.getLogger("ExampleClient");
		Configuration config = HBaseConfiguration.create();
		// Create table
		// HBaseAdmin admin = new HBaseAdmin(config);
		HTableDescriptor htd = new HTableDescriptor("product");
		// admin.createTable(htd);
		byte[] tablename = htd.getName();

		HTable table = new HTable(config, tablename);
		// 'product' , 'productCode', 'name','manufacturer','productCatagory'
		// ,'rating', 'avgprice' , 'info', 'flipkart', 'infibeam'
		String[] colFamilyNames = { "productCode", "name", "manufacturer",
				"productCatagory", "rating", "avgprice", "info", "flipkart" };
		HashMap<String, String> rows = HbaseUtils
				.getAllValuesFromTableAsString(table, colFamilyNames);
		for (String rowkey : rows.keySet()) {
			String data = rows.get(rowkey);
			String productKey = rowkey;
			String rating = HbaseUtils.extractValue("info:rating", data);
			String avgprice = HbaseUtils.extractValue("flipkart:price", data);
			if (!(rating != null && rating.length() > 0 && avgprice != null && avgprice
					.length() > 0))
				continue;
			
			String productCode = HbaseUtils.extractValue("productCode", data);
			String name = HbaseUtils.extractValue("name", data);
			String manufacturer = HbaseUtils.extractValue("manufacturer", data);
			String productCatagory = HbaseUtils.extractValue("productCatagory",
					data);

			// String productCode = HbaseUtils.extractValue("productCode",
			// data);
		}
		// String [][] colNames = {{"col1","col2"}};
		// HashMap<String, Object> map = HbaseUtils.getRow(table,
		// "micromax_X78", colFamilyNames);
		/*
		 * HashMap<String, HashMap<String, Object>> rows =
		 * HbaseUtils.getAllValuesFromTable(table, colFamilyNames); for(String
		 * rowkey : rows.keySet()){ HashMap<String, Object> row =
		 * rows.get(rowkey); for(String colFamilykey : rows.keySet()){ Object
		 * obj = rows.get(colFamilykey); if(obj instanceof String){ String value
		 * = } }
		 * 
		 * }
		 */
		// System.out.println(map);
		System.out
				.println("**************************END******************************");
	}
	
	public static void remote() throws IOException{
		Configuration config = HBaseConfiguration.create();
		String hbaseZookeeperQuorum="192.168.1.3";
		String hbaseZookeeperClientPort="2222";
		config.set(HConstants.ZOOKEEPER_QUORUM ,hbaseZookeeperQuorum);
		config.set(HConstants.ZOOKEEPER_CLIENT_PORT, hbaseZookeeperClientPort);

		HTable hTable = new HTable(config,"test");
		byte[] row1 = Bytes.toBytes("row1");
		Get g = new Get(row1);
		Result result = hTable.get(g);
		System.out.println("Get: " + result);
	}
}
