package mapreduce;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import utils.GaussianParams;
import utils.PosteriorProbability;
import utils.Stats;

public class GmmMapper extends Mapper<Object, Text, IntWritable, Stats> {

	@Override
	protected void map(Object key, Text value, Mapper<Object, Text, IntWritable, Stats>.Context context) throws IOException, InterruptedException {

		Configuration conf = context.getConfiguration();

		int k = conf.getInt("k", -1);
		if (k <= 0) {
			throw new RuntimeException("Cannot run GMM for zero Gaussians!");
		}

		// Parse input vector
		String[] split = value.toString().split("\\s+");
		int d = split.length;
		double[] x = new double[d];
		for (int dim = 0; dim < d; dim++) {
			x[dim] = Double.parseDouble(split[dim]);
		}

		// Load params from hdfs
		String paramsFilename = conf.getStrings("initParams")[0];
		GaussianParams[] params = GaussianParams.ReadParamsFromHdfs(paramsFilename, conf, k, d);

		//compute statistics
		Stats[] stat = new Stats[k];
		double[] p = PosteriorProbability.compute_p(params, x); //compute posterior probability
		for (int i = 0; i < k; i++) {
			stat[i] = new Stats(p[i], params[i].getMu(), x); //compute statistics
			context.write(new IntWritable(i), stat[i]);
		}
	}

}
