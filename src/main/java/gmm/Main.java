package gmm;
import mapreduce.GmmMapper;
import mapreduce.GmmReducer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.mortbay.log.Log;

import utils.GaussianParams;
import utils.Stats;

public class Main {
	private static final int N_REDUCERS = 1;
	private static final int MAX_ITERATIONS = 10;
	private static final double EPSILON = 0.05;

	public static void main(String[] args) throws Exception {
		
		if(args.length < 5){
			System.out.println("Usage program <input_file> <output_folder> <param_file> <k> <d>");
			return;
		}
		// CLI params
		String inputFilename = args[0];
		String outputFolder = args[1];
		String paramsFilename = args[2];
		String k = args[3];
		String d = args[4];
		
		Configuration conf = new Configuration();
		conf.setStrings("initParams", paramsFilename);
		conf.setInt("k", Integer.parseInt(args[3]));
		
		// iterations
		boolean toBeContinued = true;
		int nIternation = 0;
		while (toBeContinued && nIternation < MAX_ITERATIONS) {
			System.out.println("\n------------------------------ITERATION #" + nIternation + "------------------------------");
			Job job = Job.getInstance(conf, "gmm");
			job.setJarByClass(Main.class);
			
			job.setOutputKeyClass(IntWritable.class);
			job.setOutputValueClass(Stats.class);

			job.setMapperClass(GmmMapper.class);
			
			job.setMapOutputValueClass(Stats.class);
			//job.setCombinerClass(GmmCombiner.class);
			job.setReducerClass(GmmReducer.class);
			job.setNumReduceTasks(N_REDUCERS);

			job.setInputFormatClass(TextInputFormat.class);
			job.setOutputFormatClass(TextOutputFormat.class);

			FileInputFormat.addInputPath(job, new Path(inputFilename));
			FileOutputFormat.setOutputPath(job, new Path(outputFolder));

			job.waitForCompletion(true);
			
			// eval escape condition
			GaussianParams[] oldParams = GaussianParams.ReadParamsFromHdfs(paramsFilename, conf, Integer.parseInt(k), Integer.parseInt(d));
			System.out.println("\n---OLD---");
			for (int i = 0; i < oldParams.length; i++) {
				String output = String.format("%s\n%s\n%s", oldParams[i].getWasString(), oldParams[i].getMuAsString(), oldParams[i].getSigmaSqrAsString());
				System.out.println("Gaussian #" + i + "\n" + output);
			}
			
			
			FileSystem fs = FileSystem.get(conf);
			fs.delete(new Path(paramsFilename), true);
			
			FileUtil.copyMerge(fs, new Path(outputFolder), fs, new Path(paramsFilename), false, conf, null);
			
			GaussianParams[] newParams = GaussianParams.ReadParamsFromHdfs(paramsFilename, conf, Integer.parseInt(k), Integer.parseInt(d));
			System.out.println("\n---NEW---");
			for (int i = 0; i < newParams.length; i++) {
				String output = String.format("%s\n%s\n%s", newParams[i].getWasString(), newParams[i].getMuAsString(), newParams[i].getSigmaSqrAsString());
				System.out.println("Gaussian #" + i + "\n" + output);
			}
			
			toBeContinued = GaussianParams.evaluateStop(oldParams, newParams, EPSILON);
			
			fs.delete(new Path(outputFolder), true);
			
			nIternation++;
		}
		System.out.println("Program exited. Reason:\n \t-enough approximation: " + !toBeContinued + "\n \t-max iterations exceeded: " + !(nIternation < MAX_ITERATIONS));
	}

	
	private static class PartFileFilter implements PathFilter {
		public boolean accept(Path p) {
			return (p.getName().contains("part-r") ? true : false);
		}
	}
}
