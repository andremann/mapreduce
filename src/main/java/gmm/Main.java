package gmm;
import mapreduce.GmmMapper;
import mapreduce.GmmReducer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import utils.Stats;

public class Main {
	public static void main(String[] args) throws Exception {
		if(args.length<4){
			System.err.println("Usage program input_file output_folder param_file k");
			return;
		}
		Configuration conf = new Configuration();
		conf.setStrings("initParams", args[2]);
		conf.setInt("k", Integer.parseInt(args[3]));
		Job job = Job.getInstance(conf, "gmm");
		job.setJarByClass(Main.class);
		
		job.setOutputKeyClass(IntWritable.class);
		job.setOutputValueClass(Stats.class);

		job.setMapperClass(GmmMapper.class);
		
		job.setMapOutputValueClass(Stats.class);
		//job.setCombinerClass(GmmCombiner.class);
		job.setReducerClass(GmmReducer.class);
		//job.setNumReduceTasks(1);

		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);

		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));

		job.waitForCompletion(true);
	}

}
