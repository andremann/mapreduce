package mapreduce;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import utils.GaussianParams;
import utils.Stats;


public class GmmReducer extends Reducer<IntWritable, Stats, Text, Text> {
	@Override
	protected void reduce(IntWritable key, Iterable<Stats> iterableValues, Reducer<IntWritable, Stats, Text, Text>.Context context) throws IOException, InterruptedException {
		Stats globalStats = new Stats(iterableValues);
		// GaussianParams test = new GaussianParams(globalStats.getS1().length);
		// test.setW(globalStats.getS0());
		// test.setMu(globalStats.getS1());
		// test.setSigma(globalStats.getS2());
		// System.out.println(String.format("\nW: %s\nmu: %s\nsigma: %s",
		// test.getWasString(), test.getMuAsString(), test.getSigmaAsString()));

		GaussianParams theta;
		try {
			theta = new GaussianParams(globalStats, globalStats.getS1().length);
			String output = String.format("%s\n%s\n%s", theta.getWasString(), theta.getMuAsString(), theta.getSigmaSqrAsString());
			context.write(null, new Text(output));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
