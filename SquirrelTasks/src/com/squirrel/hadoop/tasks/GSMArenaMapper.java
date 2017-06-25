package com.squirrel.hadoop.tasks;

import java.io.IOException;

import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

public class GSMArenaMapper<K, V> extends MapReduceBase implements
		Mapper<LongWritable, Text, K, V> {

	private HTable table;

	public void map(LongWritable key, Text value, OutputCollector<K, V> output,
			Reporter reporter) throws IOException {
		String strValue = value.toString();
		strValue = strValue.replaceFirst(",", "");
		if (strValue.contains("|") && strValue.contains("::")) {
			String[] values = strValue.split("\\|");
			byte[] row = Bytes.toBytes("" + key);
			Put p1 = new Put(row);
			for (int i = 0; i < values.length; i++) {
				String col_value = values[i];
				String[] strArray = col_value.split("::");
				if (strArray.length != 2) {
					continue;
				} else {
					String column = strArray[0];
					String data = strArray[1];
					if (!(column.length() > 0 && data.length() > 0)) {
						continue;
					}
					if ("product_code".equalsIgnoreCase(column)) {
						p1.add(Bytes.toBytes("productCode"), null,
								Bytes.toBytes(data));
					} else if ("product_name".equalsIgnoreCase(column)) {
						p1.add(Bytes.toBytes("name"), null, Bytes.toBytes(data));
					} else {
						p1.add(Bytes.toBytes("info"), Bytes.toBytes(column),
								Bytes.toBytes(data));
					}
				}
			}
			table.put(p1);
			table.flushCommits();
		}

	}

	public void configure(JobConf jc) {
		super.configure(jc);
		// Create the HBase table client once up-front and keep it around
		// rather than create on each map invocation.
		try {
			this.table = new HTable(new HBaseConfiguration(jc), "product");
		} catch (IOException e) {
			throw new RuntimeException("Failed HTable construction", e);
		}
	}

}
