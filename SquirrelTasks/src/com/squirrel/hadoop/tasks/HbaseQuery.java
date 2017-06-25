package com.squirrel.hadoop.tasks;

import java.io.IOException;
import java.util.HashMap;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp;
import org.apache.hadoop.hbase.util.Bytes;

public class HbaseQuery {

	/**
	 * @param args
	 */
	static final String[] colFamilyNames = { "productCode", "name",
		"manufacturer", "info", "flipkart", "infibeam", "univercell",
		"mobilestore" };
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		HTable table = remoteHbaseTable("product");
		int count =test(table);
		System.out.println(count);

	}
	
	public static int test(HTable table) throws IOException{
		HashMap<String, String> rows = HbaseUtils
			.getAllValuesFromTableAsString(table, colFamilyNames);
		int counter =0;
		//System.out.println(rows.size());
		for (String rowkey : rows.keySet()) {
			String data = rows.get(rowkey);
			String name = HbaseUtils.extractValueMapper("name", data,
					"=(.*?)}");
			String infibeam = HbaseUtils.extractValueMapper("infibeam:price", data,
					"=(.*?)}");
			String flip = HbaseUtils.extractValueMapper("flipkart:price", data,
					"=(.*?)}");
			String uni = HbaseUtils.extractValueMapper("univercell:price", data,
					"=(.*?)}");
			String mob = HbaseUtils.extractValueMapper("mobilestore:price", data,
					"=(.*?)}");
			int temp=0;
			if(!infibeam.isEmpty()){
				temp++;
			}
			if(!mob.isEmpty()){
				//temp++;
			}
			if(!flip.isEmpty()){
				//temp++;
			}
			if(!uni.isEmpty()){
				//temp++;
			}
			if(temp==1){
				//System.out.println(name);
				counter++;
			}
			
		}
		return counter;
		
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
