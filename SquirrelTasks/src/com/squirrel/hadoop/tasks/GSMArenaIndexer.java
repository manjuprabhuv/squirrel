package com.squirrel.hadoop.tasks;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.log4j.Logger;
import org.apache.lucene.index.IndexWriter;

public class GSMArenaIndexer {

	public static void main(String[] args) throws IOException, ParseException {
		System.out
				.println("**************************START******************************");

		Logger log = Logger.getLogger("GSMArenaIndexer");
		HTable table = remoteHbaseTable("product");
		String[] colFamilyNames = { "productCode", "name", "manufacturer"};
		SquirrelLuceneUtils indexer = new SquirrelLuceneUtils();
		IndexWriter w = indexer
				.initializeIndex("/home/manjuprabhuv/lucene/gsmindex");
		HashMap<String, String> rows = HbaseUtils
				.getAllValuesFromTableAsString(table, colFamilyNames);
		for (String rowkey : rows.keySet()) {
			String data = rows.get(rowkey);
			//System.out.println(data);
			String productKey = rowkey;
			String pattern  = "=([a-zA-Z0-9 /.%]+)}";//=([a-zA-Z0-9 /.%]+)}
		
			String productCode = HbaseUtils.extractValueMapper("productCode", data,pattern);
			String name = HbaseUtils.extractValueMapper("name", data,"=([a-zA-Z0-9 -/.%]+)}");
			String manufacturer = HbaseUtils.extractValueMapper("manufacturer", data,pattern);
			
			Map<String, String> indexValues = new HashMap<String, String>();
			
			if(name.equalsIgnoreCase(productCode) && productKey.toLowerCase().contains("_v_")){
				continue;
			}
			//System.out.println("AVERAGE RATING >>>>>"+avgrating);
			indexValues.put("productKey", productKey.toLowerCase());	
			indexValues.put("productCode", productCode.toLowerCase());			
			indexValues.put("name", name.toLowerCase());
			
			indexValues.put("manufacturer", manufacturer.toLowerCase());
			

			indexer.addDoc(w, indexValues);

		}
		w.close();
		System.out
				.println("**************************END******************************");
	}
	
	public static HTable remoteHbaseTable(String table) throws IOException{
		Configuration config = HBaseConfiguration.create();
		String hbaseZookeeperQuorum="master";
		String hbaseZookeeperClientPort="2222";
		config.set(HConstants.ZOOKEEPER_QUORUM ,hbaseZookeeperQuorum);
		config.set(HConstants.ZOOKEEPER_CLIENT_PORT, hbaseZookeeperClientPort);

		HTable hTable = new HTable(config,table);
		return hTable;
	}


}
