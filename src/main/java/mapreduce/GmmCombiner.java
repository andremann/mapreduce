package mapreduce;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Reducer;

import utils.Stats;

public class GmmCombiner extends Reducer<IntWritable, Stats, IntWritable, Stats> {
	@Override
	protected void reduce(IntWritable key, Iterable<Stats> iterableValues, Reducer<IntWritable, Stats, IntWritable, Stats>.Context context) throws IOException, InterruptedException {
		Stats globalStats = new Stats(iterableValues);
		context.write(key, globalStats);
		//System.out.println("Combiner here!!");
	}
}
