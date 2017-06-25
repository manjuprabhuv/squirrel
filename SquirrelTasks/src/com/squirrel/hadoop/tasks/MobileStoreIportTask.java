package com.squirrel.hadoop.tasks;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.MultiTableOutputFormat;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

public class MobileStoreIportTask {

	private static Charset charset = Charset.forName("UTF-8");
	private static Set<String> match = new TreeSet<String>();
	private static Set<String> nomatch = new TreeSet<String>();
	private static Set<String> unique = new TreeSet<String>();

	public static class Map extends
			Mapper<Object, Text, ImmutableBytesWritable, Writable> {

		@Override
		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {

			String stringvalue = value.toString();
			// Matcher matcher = mTitlePattern.matcher(xml);

			try {
				String strValue = value.toString();
				strValue = strValue.replaceFirst(",", "");
				if (strValue.contains("|") && strValue.contains("::")) {
					String[] values = strValue.split("\\|");
					// product_code::X101|product_href::/micromax-x101/p/itmdbd96pyheaa8e?pid=MOBDCWQJS9FTG3D8&ref=5fc7df8f-4919-4633-aef8-c6a6b5088001|product_name::Micromax
					// X101|rating::4 stars|price::Rs. 999|product_cash_back::

					String manufacturer = HbaseUtils.extractValueMapper(
							"manufacturer", stringvalue, "([a-zA-Z0-9 .%]+)");

					String productCode = HbaseUtils.extractValueMapper(
							"product_code", stringvalue, "([a-zA-Z0-9_ -.%]+)");
					productCode = stripColor(productCode);
					String productName = HbaseUtils.extractValueMapper(
							"product_name", stringvalue, "([a-zA-Z0-9_ -.%]+)");
					if(productName.toLowerCase().contains("sony ericsson")){
						productName = productName.replaceAll("Sony Ericsson", "sonyericsson");
					}
					unique.add(productName);
					Matcher.matchcounter++;
					String rowkey = manufacturer + "_" + productCode;
					rowkey = rowkey.toLowerCase();
					HTable hTable = new HTable(context.getConfiguration(),
							context.getConfiguration().get("tablename"));

					Get get = new Get(Bytes.toBytes(rowkey));
					Result res = hTable.get(get);

					String tableRowKey = "";
					if (res.getRow() != null)
						tableRowKey = Bytes.toString(res.getRow());
					// String tableRowKey = getTableRowKey(context,
					// "productCode", null, rowkey, CompareOp.EQUAL);
					if (tableRowKey == null || tableRowKey.length() == 0) {
						SquirrelLuceneUtils lucene = new SquirrelLuceneUtils();
						List<String> match = lucene.productComparator(
								productName.toLowerCase(), 50,
								"/home/manjuprabhuv/lucene/gsmindex");
						if (match.size() > 0)
							tableRowKey = Matcher.approxMatch(match,
									productName, manufacturer,false);
						
						if (tableRowKey == null || tableRowKey.length() == 0) {
							Matcher.nomatchcounter++;
							nomatch.add(productName);
						//	System.out.println(productName);
							return;
						}
					}
					//System.out.println(productName + ">>" + tableRowKey);
					match.add(productName);
					byte[] row = Bytes.toBytes(tableRowKey);
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
							
							if(column.equalsIgnoreCase("product_href")){
								data = "http://www.themobilestore.in"+data;
							}
							p1.add(Bytes.toBytes("mobilestore"),
									Bytes.toBytes(column), Bytes.toBytes(data));
							// create 'product' , 'productCode',
							// 'name','manufacturer','productCatagory'
							// ,'rating', 'avgprice' , 'info', 'flipkart',
							// 'infibeam'

						}
					}
					 hTable.put(p1);
					// System.out.println(stringvalue);
					// context.write(mTableName, p1);
					// System.out.println("NO Match >> "+Matcher.nomatchcounter);
					// System.out.println("Match >> "+Matcher.matchcounter);
				} else {
					context.setStatus("parsing " + stringvalue
							+ ".................");
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private String stripColor(String str) {
			int startIndex = str.indexOf("_(");
			if (startIndex == -1)
				return str;
			str = str.substring(0, startIndex);
			return str;
		}

		private String getTableRowKey(Context context, String columnFamily,
				String column, String value, CompareOp enumval)
				throws IOException {
			Scan scan = new Scan();

			FilterList list = new FilterList(FilterList.Operator.MUST_PASS_ALL);
			SingleColumnValueFilter filter = new SingleColumnValueFilter(
					Bytes.toBytes(columnFamily), null, enumval,
					Bytes.toBytes(value));
			list.addFilter(filter);
			scan.setFilter(list);

			HTable hTable = new HTable(context.getConfiguration(), context
					.getConfiguration().get("tablename"));
			ResultScanner rs = null;
			Result res = null;
			rs = hTable.getScanner(scan);

			while ((res = rs.next()) != null) {
				String tableRowKey = Bytes.toString(res.getRow());
				return tableRowKey;
			}

			return null;

		}

		/*
		 * private String getManufacturer(String filename) { int first =
		 * filename.indexOf("/"); int last = filename.indexOf("-"); filename =
		 * filename.substring(first + 1, last); return filename; }
		 */

		@Override
		protected void setup(Context context) throws IOException,
				InterruptedException {
			Configuration configuration = context.getConfiguration();
			String hbaseZookeeperQuorum = "master";
			String hbaseZookeeperClientPort = "2222";
			configuration
					.set(HConstants.ZOOKEEPER_QUORUM, hbaseZookeeperQuorum);
			configuration.set(HConstants.ZOOKEEPER_CLIENT_PORT,
					hbaseZookeeperClientPort);
			// mTitlePattern = Pattern.compile("<title>([^<]*)</title>");
			mTableName = new ImmutableBytesWritable(Bytes.toBytes(configuration
					.get("tablename")));
		}

		// private Pattern mTitlePattern;
		private ImmutableBytesWritable mTableName;
		// private static final byte[] CONTENT = "content".getBytes(charset);

	}

	public static Job configureJob(Configuration conf, String[] args)
			throws IOException {
		// String tableName = args[0];
		String importpath = args[0];
		conf.set("tablename", "product");
		// conf.set("productCatagory", "mobile");

		Job job = new Job(conf, "infibeam Import");
		job.setJarByClass(MobileStoreIportTask.class);
		job.setMapperClass(Map.class);
		job.setNumReduceTasks(0);
		job.setOutputFormatClass(MultiTableOutputFormat.class);
		// MultipleI org.apache.hadoop.mapreduce.lib.input.
		MultipleInputs.addInputPath(job, new Path(importpath),
				TextInputFormat.class, MobileStoreIportTask.Map.class);
		// MultipleInputs.addInputPath(job, new Path(importFile))
		// FileInputFormat.addInputPath(job, new Path(importFile));

		return job;
	}

	public static void main(String[] args) throws Exception {
		Configuration conf = HBaseConfiguration.create();

		String[] otherArgs = new GenericOptionsParser(conf, args)
				.getRemainingArgs();
		if (otherArgs.length != 1) {
			System.err.println("Usage: infibeam importPath");
			System.exit(-1);
		}

		Job job = configureJob(conf, otherArgs);
		job.waitForCompletion(true) ;
		System.out.println("Total Records"+Matcher.matchcounter);
		System.out.println("Unique Records"+unique.size());
		System.out.println("Match Count =="+match.size());
		System.out.println("No Match Count =="+nomatch.size());
		System.out.println("**********************MATCH*********************************");
		for (Iterator<String> iterator = match.iterator(); iterator.hasNext();) {
			//S type = (type) iterator.next();
			System.out.println( iterator.next());
			
		}
		System.out.println("**********************NO MATCH*********************************");
		
		for (Iterator<String> iterator = nomatch.iterator(); iterator.hasNext();) {
			//S type = (type) iterator.next();
			System.out.println( iterator.next());
			
		}
	}
}
