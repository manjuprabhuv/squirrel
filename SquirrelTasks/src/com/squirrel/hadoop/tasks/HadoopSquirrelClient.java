package com.squirrel.hadoop.tasks;

import java.io.IOException;
import java.util.HashMap;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.client.HTable;

public class HadoopSquirrelClient {
	//Sample Call
	public static void main(String[] args) throws IOException {
		getRowExample();
	}
	
	
	
	public static void getRowExample() throws IOException{
		HTable table = remoteHbaseTable("product");
		String[] colFamilyNames = { "productCode", "name", "manufacturer",
				"productCatagory", "rating", "avgprice", "info", "flipkart" };
		HashMap<String, Object> rows = HbaseUtils.getRow(table, "micromax_X78", colFamilyNames);
		System.out.println(rows);
		for (String rowkey : rows.keySet()) {
			Object value = rows.get(rowkey);
			System.out.println("Key :: "+rowkey);
			if(value instanceof String){
				System.out.println("Key :: "+rowkey+" ====== Value ::"+value);
			}else{
				HashMap<String, Object> row = (HashMap<String, Object>)rows.get(rowkey);
				for (String colNames : row.keySet()) {
					String colvalue =(String)row.get(colNames);
					System.out.println("colNames :: "+colNames+" ====== colvalue ::"+colvalue);
				}
			}
			
		
		}
	}
	
	public static void getAllValuesExample() throws IOException{
		HTable table = remoteHbaseTable("product");
		String[] colFamilyNames = { "productCode", "name", "manufacturer",
				"productCatagory", "rating", "avgprice", "info", "flipkart" };
		HashMap<String, String> rows = HbaseUtils
				.getAllValuesFromTableAsString(table, colFamilyNames);
		for (String rowkey : rows.keySet()) {
			String data = rows.get(rowkey);
			String productKey = rowkey;
			String rating = HbaseUtils.extractValueIndexer("info:rating", data);
			String avgprice = HbaseUtils.extractValueIndexer("flipkart:price", data);
			if (!(rating != null))
				continue;
			
			String productCode = HbaseUtils.extractValueIndexer("productCode", data);
			String name = HbaseUtils.extractValueIndexer("name", data);
			String manufacturer = HbaseUtils.extractValueIndexer("manufacturer", data);
			String productCatagory = HbaseUtils.extractValueIndexer("productCatagory",
					data);

			// String productCode = HbaseUtils.extractValue("productCode",
			// data);
		}
	}
	public static HTable remoteHbaseTable(String table) throws IOException{
		Configuration config = HBaseConfiguration.create();
		String hbaseZookeeperQuorum="192.168.1.3";
		String hbaseZookeeperClientPort="2222";
		config.set(HConstants.ZOOKEEPER_QUORUM ,hbaseZookeeperQuorum);
		config.set(HConstants.ZOOKEEPER_CLIENT_PORT, hbaseZookeeperClientPort);

		HTable hTable = new HTable(config,table);
		return hTable;
	}
	

}
