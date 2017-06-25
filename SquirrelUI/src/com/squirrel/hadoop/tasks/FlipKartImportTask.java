package com.squirrel.hadoop.tasks;

import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Put;
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

public class FlipKartImportTask {

	private static Charset charset = Charset.forName("UTF-8");

	public static class Map extends
			Mapper<Object, Text, ImmutableBytesWritable, Writable> {

		@Override
		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {

			String stringvalue = value.toString();
			// Matcher matcher = mTitlePattern.matcher(xml);
			System.out.println(stringvalue);
			try {
				String strValue = value.toString();
				strValue = strValue.replaceFirst(",", "");
				if (strValue.contains("|") && strValue.contains("::")) {
					String[] values = strValue.split("\\|");
					// product_code::X101|product_href::/micromax-x101/p/itmdbd96pyheaa8e?pid=MOBDCWQJS9FTG3D8&ref=5fc7df8f-4919-4633-aef8-c6a6b5088001|product_name::Micromax
					// X101|rating::4 stars|price::Rs. 999|product_cash_back::

					String manufacturer = HbaseUtils
							.extractValue("product_href", stringvalue);
					String productCode = HbaseUtils.extractValue(
							"product_code", stringvalue);

					byte[] row = Bytes
							.toBytes(manufacturer + "_" + productCode);
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

							p1.add(Bytes.toBytes("flipkart"),
									Bytes.toBytes(column), Bytes.toBytes(data));
							// create 'product' , 'productCode',
							// 'name','manufacturer','productCatagory'
							// ,'rating', 'avgprice' , 'info', 'flipkart',
							// 'infibeam'

						}
					}					
					context.write(mTableName, p1);
				} else {
					context.setStatus("parsing " + stringvalue
							+ ".................");
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		/*private String getManufacturer(String filename) {
			int first = filename.indexOf("/");
			int last = filename.indexOf("-");
			filename = filename.substring(first + 1, last);
			return filename;
		}*/

		@Override
		protected void setup(Context context) throws IOException,
				InterruptedException {
			Configuration configuration = context.getConfiguration();

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

		Job job = new Job(conf, "Flipkart Import");
		job.setJarByClass(FlipKartImportTask.class);
		job.setMapperClass(Map.class);
		job.setNumReduceTasks(0);
		job.setOutputFormatClass(MultiTableOutputFormat.class);
		// MultipleI org.apache.hadoop.mapreduce.lib.input.
		MultipleInputs.addInputPath(job, new Path(importpath),
				TextInputFormat.class, FlipKartImportTask.Map.class);
		// MultipleInputs.addInputPath(job, new Path(importFile))
		// FileInputFormat.addInputPath(job, new Path(importFile));

		return job;
	}

	public static void main(String[] args) throws Exception {
		Configuration conf = HBaseConfiguration.create();

		String[] otherArgs = new GenericOptionsParser(conf, args)
				.getRemainingArgs();
		if (otherArgs.length != 1) {
			System.err.println("Usage: FlipKart importPath");
			System.exit(-1);
		}

		Job job = configureJob(conf, otherArgs);
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}
