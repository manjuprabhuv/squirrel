package com.squirrel.hadoop.tasks;

import java.io.IOException;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.lib.NullOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;




public class SquirrelGSMArenaImportTask extends Configured implements Tool{

	
	
	public int run(String[] args) throws IOException {
		if (args.length != 1) {
			System.err.println("Usage: SquirrelGSMArenaReaderTask <input>");
			return -1;
		}
		JobConf jc = new JobConf(getConf(), getClass());
		jc.setJobName("GSM Arena Data Loader");
		FileInputFormat.addInputPath(jc, new Path(args[0]));
		jc.setMapperClass(GSMArenaMapper.class);
		jc.setNumReduceTasks(0);
		jc.setOutputFormat(NullOutputFormat.class);
		JobClient.runJob(jc);
		return 0;
	}

	public static void main(String[] args) throws Exception {
		int exitCode = ToolRunner.run(new HBaseConfiguration(),
				new SquirrelGSMArenaImportTask(), args);
		System.exit(exitCode);
	}

}
