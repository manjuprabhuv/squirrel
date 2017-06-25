package com.squirrel.hadoop.tasks;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.log4j.Logger;
import org.apache.lucene.index.IndexWriter;

public class ScannerTest {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public static void main(String[] args) throws IOException, ParseException {
		System.out
				.println("**************************START******************************");

		Logger log = Logger.getLogger("SquirrelHTableIndexer");
		HTable table = remoteHbaseTable("product");
		String[] colFamilyNames = { "productCode", "name", "manufacturer",
				"productCatagory", "rating", "avgprice", "info", "flipkart","infibeam" };
		
		
		HashMap<String, String> rows = HbaseUtils
				.getAllValuesFromTableAsString(table, colFamilyNames);
		for (String rowkey : rows.keySet()) {
			String data = rows.get(rowkey);

			String pattern = "=([a-zA-Z0-9 /.%]+)}";
			String name = HbaseUtils.extractValueMapper("name", data,pattern);
			String manufacturer = HbaseUtils.extractValueMapper("manufacturer", data,pattern);
			String flipkartPrice = HbaseUtils.extractValueMapper(
					"flipkart:price", data, pattern);
			String infibeamPrice = HbaseUtils.extractValueMapper(
					"infibeam:price", data, pattern);
			if(infibeamPrice.length()>0)
			System.out.println(name+"_"+manufacturer);


		}
	
		System.out
				.println("**************************END******************************");
	}

	public static HTable remoteHbaseTable(String table) throws IOException {
		Configuration config = HBaseConfiguration.create();
		String hbaseZookeeperQuorum = "master";
		String hbaseZookeeperClientPort = "2222";
		config.set(HConstants.ZOOKEEPER_QUORUM, hbaseZookeeperQuorum);
		config.set(HConstants.ZOOKEEPER_CLIENT_PORT, hbaseZookeeperClientPort);

		HTable hTable = new HTable(config, table);
		return hTable;
	}

}
