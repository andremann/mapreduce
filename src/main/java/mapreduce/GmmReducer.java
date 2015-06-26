package mapreduce;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import utils.GaussianParams;
import utils.Stats;


public class GmmReducer extends Reducer<IntWritable, Stats, Text, GaussianParams > {
	@Override
	protected void reduce(IntWritable key, Iterable<Stats> iterableValues,
			Reducer<IntWritable, Stats, Text, GaussianParams >.Context context)
			throws IOException, InterruptedException {
		ArrayList<Stats> statList = new ArrayList <Stats>();
		Iterator<Stats> it = iterableValues.iterator();
		while(it.hasNext()){
			statList.add(it.next());
		};
		Stats globalStats = new Stats(statList);
		GaussianParams theta;
		try {
			theta = new GaussianParams(globalStats,statList.size());
			context.write(new Text(""), theta);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
