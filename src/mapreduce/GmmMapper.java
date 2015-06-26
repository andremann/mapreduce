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
import utils.Stats;

public class GmmMapper extends Mapper<Object, Text, IntWritable, Stats>{
	
	@Override
	protected void map(Object key, Text value, Mapper<Object, Text, IntWritable, Stats>.Context context)
			throws IOException, InterruptedException {
		
		Configuration conf = context.getConfiguration();
		
		int k = conf.getInt("k", -1);
		
		String paramsFilename = conf.getStrings("initParams")[0];
		
		String[] split = value.toString().split("\\s+");
		int d = split.length;
		GaussianParams params[] = readParamsFromHdfs(paramsFilename, context, k, d);
		Stats stat = new Stats(d);
		stat.update(params);
		double[] x = new double[d];
		for (int i = 0; i < d; i++) {
			x[i] = Double.parseDouble(split[i]);
			context.write(new IntWritable(i), stat);
		}
		
		// Load params from hdfs
		
		
		
		
	}
	
	/**
	 * Read from a parameter file structured as follows
	 * w_1
	 * mu_1
	 * sigma_1
	 * (repeat)
	 * @param filename
	 * @param k
	 * @return
	 * @throws IOException 
	 */
	private GaussianParams[] readParamsFromHdfs(String filename, Context context, int k, int d) throws IOException {
		GaussianParams[] params = new GaussianParams[k];
		for(int i=0; i<k;++i){
			params[i]=new GaussianParams();
		}

		Path pt=new Path(filename);
		FileSystem fs = FileSystem.get(context.getConfiguration());
		BufferedReader br=new BufferedReader(new InputStreamReader(fs.open(pt)));
		try {
		  String line;
		  line=br.readLine();
		  int counter = 0;
		  while (line != null){
		    int index = counter / 3;
		    switch (counter%3){
			    case 0: params[index].setW(Double.parseDouble(line));break;
			    case 1: params[index].setMu(line);break;
			    case 2: params[index].setSigma(line); break;
			    default: break;
		    }
		    counter++;
		    line = br.readLine();
		  }
		} finally {

		  br.close();
		}
		
		return params;
	}

}
