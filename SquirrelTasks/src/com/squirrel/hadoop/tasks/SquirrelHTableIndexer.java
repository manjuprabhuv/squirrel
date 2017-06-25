package com.squirrel.hadoop.tasks;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

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
import org.apache.lucene.index.IndexWriter;

public class SquirrelHTableIndexer {

	public static void main(String[] args) throws IOException, ParseException {
		System.out
				.println("**************************START******************************");
		Set<String> set = new TreeSet<String>();
		Logger log = Logger.getLogger("SquirrelHTableIndexer");
		HTable table = remoteHbaseTable("product");
		String[] colFamilyNames = { "productCode", "name", "manufacturer",
				"productCatagory", "rating", "avgprice", "summary" };
		SquirrelLuceneUtils indexer = new SquirrelLuceneUtils();
		IndexWriter w = indexer
				.initializeIndex("/home/manjuprabhuv/lucene/index");
		HashMap<String, String> rows = HbaseUtils
				.getAllValuesFromTableAsString(table, colFamilyNames);
		int count = 0;
		for (String rowkey : rows.keySet()) {
			String data = rows.get(rowkey);
			// System.out.println(data);
			String productKey = rowkey;

			String pattern = "=(.*?)}";// =([a-zA-Z0-9 /.%]+)}
			String summarypattern = ":(.*?)=";// =([a-zA-Z0-9 /.%]+)}

			String productCode = HbaseUtils.extractValueMapper("productCode",
					data, pattern);
			String name = HbaseUtils.extractValueMapper("name", data, pattern);
			String manufacturer = HbaseUtils.extractValueMapper("manufacturer",
					data, pattern);
			String productCatagory = HbaseUtils.extractValueMapper(
					"productCatagory", data, pattern);
			String avgrating = HbaseUtils.extractValueMapper("rating", data,
					pattern);
			String avgprice = HbaseUtils.extractValueMapper("avgprice", data,
					pattern);
			if (avgprice.isEmpty()) {
				continue;
			}
			count++;

			Map<String, String> indexValues = new HashMap<String, String>();

			String[] values = data.split(",");
			String summaryValue = "";
			for (String string : values) {
				if (string.contains("summary:")) {
					String key = HbaseUtils.extractValueMapper("summary",
							string, summarypattern);
					String value = HbaseUtils.extractValueMapper("summary:"
							+ key, string, pattern);
					String summryinfo = key + "==" + value;
					if (summaryValue.isEmpty()) {
						summaryValue = summryinfo;
					} else {
						summaryValue = summaryValue + "::" + summryinfo;
					}
				}

			}
			indexValues.put("summary", summaryValue);
			if (!avgrating.isEmpty()) {
				double d = Double.parseDouble(avgrating);
				if (d > 5) {
					System.out.println(name);
				}
			}

			// System.out.println("AVERAGE RATING >>>>>"+avgrating);
			indexValues.put("productKey", productKey.trim());
			indexValues.put("rating", avgrating.trim());
			indexValues.put("avgprice", avgprice.trim());
			indexValues.put("productCode", productCode.trim());
			String fullname = "";
			if (name.trim().toLowerCase()
					.contains(manufacturer.trim().toLowerCase())) {
				fullname = name.trim();
			} else {
				fullname = manufacturer.trim() + " " + name.trim();
			}

			set.add(fullname);
			indexValues.put("indexname", fullname.toLowerCase());
			indexValues.put("name", fullname.trim());
			indexValues.put("manufacturer", manufacturer.trim());
			indexValues.put("productCatagory", productCatagory.trim());

			indexer.addDoc(w, indexValues);

		}
		w.close();
		System.out.println("Indexed" + count);
		for (Iterator<String> iterator = set.iterator(); iterator.hasNext();) {
			// S type = (type) iterator.next();
			System.out.println(iterator.next());

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
