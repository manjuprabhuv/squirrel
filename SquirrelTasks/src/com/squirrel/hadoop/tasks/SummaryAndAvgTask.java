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
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;
import org.apache.lucene.index.IndexWriter;

public class SummaryAndAvgTask {
	static final String[] colFamilyNames = { "productCode", "name",
			"manufacturer", "info", "flipkart", "infibeam", "univercell",
			"mobilestore" };
	static final String[] ratinginfo = { "info", "flipkart" };
	static final String[] priceinfo = { "flipkart", "infibeam", "univercell",
			"mobilestore" };

	static int counter=0;
	public static void main(String[] args) throws IOException, ParseException {
		System.out
				.println("**************************START******************************");

		Logger log = Logger.getLogger("GSMArenaIndexer");
		HTable table = remoteHbaseTable("product");

		HashMap<String, String> rows = HbaseUtils
				.getAllValuesFromTableAsString(table, colFamilyNames);
		
		for (String rowkey : rows.keySet()) {
			String data = rows.get(rowkey);
			//System.out.println("rowkey >>" + rowkey);
			byte[] row = Bytes.toBytes(rowkey);
			Put p1 = new Put(row);
			boolean b1 =populatesummary(data, p1);
			boolean b2 =populaterating(data, p1);
			boolean b3 =populateavgPrice(data, p1);
			if((b1 || b2|| b3) && rowkey.length()>0)
				table.put(p1);
		}
		
		System.out
				.println("**************************END******************************");
	}

	static boolean populaterating(String data, Put p1) {
		// String pattern = "=([a-zA-Z0-9 /.%]+)}";
		String flipkartrating = HbaseUtils.extractValueMapper(
				"flipkart:rating", data, "=(.*?)}");
		String gsmrating = HbaseUtils.extractValueMapper("info:rating", data,
				"=(.*?)}");
		String avgrating = "";
		if (gsmrating.length() > 0 && flipkartrating.length() > 0) {

			double gsmrate = Double.parseDouble(gsmrating);
			gsmrate = gsmrate / 2;
			double fliprate = Double.parseDouble(flipkartrating);
			double avg = (gsmrate + fliprate) / 2;
			DecimalFormat df = new DecimalFormat("#.#");
			avgrating = df.format(avg);

		} else if (gsmrating.length() > 0) {
			// System.out.println(gsmrating);
			double gsmrate = Double.parseDouble(gsmrating);
			gsmrate = gsmrate / 2;
			DecimalFormat df = new DecimalFormat("#.#");
			avgrating = df.format(gsmrate);

		}
		if (avgrating.length() > 0) {
			p1.add(Bytes.toBytes("rating"), null, Bytes.toBytes(""+avgrating));
			return true;
			//System.out.println("avgrating >>" + avgrating);
		}
		return false;
	}

	static boolean populateavgPrice(String data, Put p1) {
		int count = 0;
		int total = 0;
	
		for (int i = 0; i < priceinfo.length; i++) {
			String price = HbaseUtils.extractValueMapper(priceinfo[i]
					+ ":price", data, "=(.*?)}");
			price = price.replace(",", "");
			price = price.replace("\\.", "");
			if (price.length() > 0) {
				try {
					int temp = Integer.parseInt(price);
					if (temp > 0) {
						total = total + temp;
						count++;
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}
		int avgPrice=0;
		if(count>0)
			avgPrice = total / count;
		if (avgPrice > 0) {
			if(count==4){
				counter++;
				System.out.println(counter);
			}
			p1.add(Bytes.toBytes("avgprice"), null, Bytes.toBytes(""+avgPrice));
			//System.out.println("avgprice >>" + avgPrice);
			return true;
		}
		
		return false;

	}

	static boolean populatesummary(String data, Put p1) {
		boolean returb = false;
		String pattern = "=([a-zA-Z0-9 /.%]+)}";// =([a-zA-Z0-9 /.%]+)}

		String dispType = HbaseUtils.extractValueMapper("info:type", data,
				"=(.*?)}");
		String dispSize = HbaseUtils.extractValueMapper("info:size", data,
				"=(.*?)}");// <title>([^<]*)</title>
		if(dispSize.isEmpty()){
			dispSize = HbaseUtils.extractValueMapper("info:resolution", data,
					"=(.*?)}");
		}
		String memory = HbaseUtils.extractValueMapper("info:internal", data,
				"=(.*?)}");
		String opertatinSystem = HbaseUtils.extractValueMapper("info:os", data,
				"=(.*?)}");
		String cpu = HbaseUtils.extractValueMapper("info:cpu", data, "=(.*?)}");
		if(cpu.isEmpty()){
			cpu = HbaseUtils.extractValueMapper("info:processor", data, "=(.*?)}");
		}
		String wlan = HbaseUtils.extractValueMapper("info:wlan", data,
				"=(.*?)}");
		String camera = HbaseUtils.extractValueMapper("info:camera_primary",
				data, "=(.*?)}");
		if(camera.isEmpty()){
			camera = HbaseUtils.extractValueMapper("info:primary_camera",
					data, "=(.*?)}");
		}
		if (wlan.length() > 2) {
			wlan = "wi-fi";
		}
		String _3g = HbaseUtils.extractValueMapper("info:3g", data, "=(.*?)}");
		if (_3g.length() > 2) {
			_3g = "Yes";
		}
		String gprs = HbaseUtils.extractValueMapper("info:gprs", data, "=(.*?)}");
		if(memory.isEmpty()){
			memory = HbaseUtils.extractValueMapper("info:expandable_memory", data, "=(.*?)}");
			if(memory.isEmpty()){
				memory = HbaseUtils.extractValueMapper("info:memory", data, "=(.*?)}");
			}
		}
		if (gprs.length() > 0) {
			returb = true;
			p1.add(Bytes.toBytes("summary"), Bytes.toBytes("GPRS"),
					Bytes.toBytes(dispType));
		}
		if (dispType.length() > 0) {
			returb = true;
			p1.add(Bytes.toBytes("summary"), Bytes.toBytes("Display Type"),
					Bytes.toBytes(dispType));
		}
		if (dispSize.length() > 0) {
			returb = true;
			p1.add(Bytes.toBytes("summary"), Bytes.toBytes("Display Size"),
					Bytes.toBytes(dispSize));
		}
		if (memory.length() > 0) {
			returb = true;
			p1.add(Bytes.toBytes("summary"), Bytes.toBytes("Memory"),
					Bytes.toBytes(memory));
		}
		if (opertatinSystem.length() > 0) {
			returb = true;
			p1.add(Bytes.toBytes("summary"), Bytes.toBytes("OS"),
					Bytes.toBytes(opertatinSystem));
		}
		if (cpu.length() > 0) {
			returb = true;
			p1.add(Bytes.toBytes("summary"), Bytes.toBytes("CPU"),
					Bytes.toBytes(cpu));
		}
		if (wlan.length() > 0) {
			returb = true;
			p1.add(Bytes.toBytes("summary"), Bytes.toBytes("Wi-Fi"),
					Bytes.toBytes(wlan));
		}
		if (_3g.length() > 0) {
			returb = true;
			p1.add(Bytes.toBytes("summary"), Bytes.toBytes("3G"),
					Bytes.toBytes(_3g));
		}
		if (camera.length() > 0) {
			returb = true;
			p1.add(Bytes.toBytes("summary"), Bytes.toBytes("Camera"),
					Bytes.toBytes(camera));
		}
		return returb;
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
