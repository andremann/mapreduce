package mapreduce;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Reducer;

import utils.Stats;

public class GmmCombiner extends Reducer<IntWritable, Stats, IntWritable, Stats> {
	@Override
	protected void reduce(IntWritable key, Iterable<Stats> iterableValues, Reducer<IntWritable, Stats, IntWritable, Stats>.Context context) throws IOException, InterruptedException {
		ArrayList<Stats> statList = new ArrayList <Stats>();
		Iterator<Stats> it = iterableValues.iterator();
		while(it.hasNext()){
			statList.add(it.next());
		};
		Stats globalStats = new Stats(statList);
		context.write(key, globalStats);
		System.out.println("Combiner here!!");
	}
}
