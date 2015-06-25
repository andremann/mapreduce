package it.unipi.gmm;

import it.unipi.gmm.mapreduce.GmmMapper;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

public class Main {
	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		Job job = Job.getInstance(conf, "gmm");
		job.setJarByClass(Main.class);
		
		job.setOutputKeyClass(IntWritable.class);
		//job.setOutputValueClass(SuffStats.class);

		job.setMapperClass(GmmMapper.class);
		//job.setCombinerClass(GmmCombiner.class);
		//job.setReducerClass(GmmReducer.class);
		//job.setNumReduceTasks(1);

		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);

		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		conf.setStrings("initParams", args[2]);
		conf.setInt("k", Integer.parseInt(args[3]));

		job.waitForCompletion(true);
	}

}
