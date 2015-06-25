package it.unipi.gmm.mapreduce;

import it.unipi.gmm.utils.GaussianParams;
import it.unipi.gmm.utils.Stats;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class GmmMapper extends Mapper<Object, Text, IntWritable, Stats>{
	
	@Override
	protected void map(Object key, Text value, Mapper<Object, Text, IntWritable, Stats>.Context context)
			throws IOException, InterruptedException {
		
		Configuration conf = context.getConfiguration();
		int k = conf.getInt("k", -1);
		String paramsFilename = conf.getStrings("initParams")[0];
		
		String[] split = value.toString().split("\\s+\t");
		int d = split.length;
		double[] x = new double[d];
		for (int i = 0; i < d; i++) {
			x[i] = Double.parseDouble(split[i]);
		}
		
		// Load params from hdfs
		GaussianParams params[] = readParamsFromHdfs("", context, k, d);
		
		
		
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
		Path pt=new Path("hdfs://" + filename);
		FileSystem fs = FileSystem.get(context.getConfiguration());
		BufferedReader br=new BufferedReader(new InputStreamReader(fs.open(pt)));
		try {
		  String line;
		  line=br.readLine();
		  int counter = 0;
		  while (line != null){
		    System.out.println(line);
		    int index = counter / 3;
		    switch (counter%3){
			    case 0: params[index].setW(Double.parseDouble(line));break;
			    case 1: params[index].setMu(line);break;
			    case 2: params[index].setSigma(line); break;
			    default: break;
		    }
		    //counter++;
		    // be sure to read the next line otherwise you'll get an infinite loop
		    line = br.readLine();
		  }
		} finally {
		  // you should close out the BufferedReader
		  br.close();
		}
		
		return params;
	}

}
