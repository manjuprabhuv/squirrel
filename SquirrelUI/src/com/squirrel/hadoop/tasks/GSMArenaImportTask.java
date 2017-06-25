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


public class GSMArenaImportTask {
	
	private static Charset charset = Charset.forName("UTF-8");
  public static class Map extends
      Mapper<Object, Text, ImmutableBytesWritable, Writable> {
	  
    @Override
		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {
    	
			String stringvalue = value.toString();
			//Matcher matcher = mTitlePattern.matcher(xml);
			System.out.println(stringvalue);
			try{
				String strValue = value.toString();
				strValue = strValue.replaceFirst(",", "");
				if (strValue.contains("|") && strValue.contains("::")) {
					String[] values = strValue.split("\\|");
					String manufacturer = HbaseUtils.getManufacturer(context);
					String productCode = HbaseUtils.extractValue("product_code",stringvalue);
					byte[] row = Bytes.toBytes(manufacturer+"_"+productCode);
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
							}//create 'product' , 'productCode', 'name','manufacturer','productCatagory' ,'rating', 'avgprice' , 'info', 'flipkart', 'infibeam'

						}
					}
					p1.add(Bytes.toBytes("manufacturer"), null, Bytes.toBytes(manufacturer));
					String prodCatagory = context.getConfiguration().get("productCatagory");
					p1.add(Bytes.toBytes("productCatagory"), null, Bytes.toBytes(prodCatagory));
					context.write(mTableName, p1);
				}else{
					context.setStatus("parsing "+stringvalue+".................");
				}
				
			}catch(Exception e){
				e.printStackTrace();
			}
		}
    

    @Override
    protected void setup(Context context)
				throws IOException, InterruptedException {
      Configuration configuration = context.getConfiguration();
			
			//mTitlePattern = Pattern.compile("<title>([^<]*)</title>");
			mTableName =
				new ImmutableBytesWritable(Bytes
																	 .toBytes(configuration
																						.get("tablename")));
		}

		//private Pattern mTitlePattern;
		private ImmutableBytesWritable mTableName;
		//private static final byte[] CONTENT = "content".getBytes(charset);
		
	}

  public static Job configureJob(Configuration conf, String [] args)
  		throws IOException {
    //String tableName = args[0];
    String importpath = args[0];   
    conf.set("tablename", "product");
    conf.set("productCatagory", "mobile");

    Job job = new Job(conf, "GSMArena Import");
    job.setJarByClass(GSMArenaImportTask.class);
    job.setMapperClass(Map.class);
    job.setNumReduceTasks(0);  
    job.setOutputFormatClass(MultiTableOutputFormat.class);
	//MultipleI	org.apache.hadoop.mapreduce.lib.input.
    MultipleInputs.addInputPath(job, new Path(importpath),
            TextInputFormat.class, GSMArenaImportTask.Map.class);
  //  MultipleInputs.addInputPath(job, new Path(importFile))
  //  FileInputFormat.addInputPath(job, new Path(importFile));
		
    return job;
  }

  public static void main(String[] args) throws Exception {
    Configuration conf = HBaseConfiguration.create();

    String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
		if (otherArgs.length != 1) {
			System.err.println("Usage: GSMArenaImportTask importPath");
			System.exit(-1);
		}

		Job job = configureJob(conf, otherArgs);
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}

