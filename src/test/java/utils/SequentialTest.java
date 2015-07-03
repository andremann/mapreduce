package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.junit.Test;
/**
 * @author Lucia, Andrea 
 *
 */
public class SequentialTest {
	private static final double EPSILON = 0.05;
	private static final int MAX_ITERATIONS = 30;
	static String parFile = "/params.txt";
	static String xFile = "/x.txt";
	static String dimFile = "/dimensions.txt";
	
	static int k;
	static int d;
	

	@Test
	public void testSeq() throws Exception {
		System.out.println("\n TESTING: " + this.getClass().getCanonicalName());
		
		String[] dims = DimensionsReader.parse(new InputStreamReader(getClass().getResourceAsStream(dimFile)));
		k = Integer.parseInt(dims[0]);
		d = Integer.parseInt(dims[1]);
		
		GaussianParams[] oldParams =  GaussianParams.parse(new InputStreamReader(getClass().getResourceAsStream(parFile)), k, d);
		GaussianParams[] newParams = oldParams ;
		double[][] x = readx(xFile);
		int n = x.length;

		boolean toBeContinued = true;
		int nIternation = 0;
		while (toBeContinued && nIternation < MAX_ITERATIONS) {
			System.out.println("\n------------------------------ITERATION #" + nIternation + "------------------------------");
			Stats[][] stat = new Stats[k][n];
			oldParams=newParams;
			//mapper
			for(int s=0; s<n;s++) {
				double[] xs=x[s];
				double[] p = PosteriorProbability.compute_p(oldParams, xs); //compute posterior probability
				//System.out.println(Arrays.toString(xs));
				for (int i = 0; i < k; i++) {
					stat[i][s] = new Stats(p[i], oldParams[i].getMu(), xs); //compute statistic
//					GaussianParams test = new GaussianParams(d);
//					test.setW(stat[i][s].getS0());
//					test.setMu(stat[i][s].getS1());
//					test.setSigmaSqr(stat[i][s].getS2());
//					
					//System.out.println(String.format("%s\n%s\n%s", test.getWasString(), test.getMuAsString(), test.getSigmaAsString()));
					
				}
			}
			//reducer 
			//aggrego per chiave
			newParams=new GaussianParams[k];
			for(int i = 0; i < k; i++) {
				//System.out.println(i);
				Stats[] statI=stat[i];
				ArrayList<Stats> statList= new ArrayList <Stats>();
				for(Stats onestat:statI) {
					statList.add(onestat);
				}
				Stats globalStats = new Stats(statList);
//				GaussianParams test = new GaussianParams(globalStats.getS1().length);
//				test.setW(globalStats.getS0());
//				test.setMu(globalStats.getS1());
//				test.setSigmaSqr(globalStats.getS2());
//				System.out.println(String.format("%s\n%s\n%s", test.getWasString(), test.getMuAsString(), test.getSigmaSqrAsString()));
//				
				newParams[i] = new GaussianParams(globalStats, statList.size());

			}

			System.out.println("\n---OLD---");
			for (int i = 0; i < oldParams.length; i++) {
				String output = String.format("%s\n%s\n%s", oldParams[i].getWasString(), oldParams[i].getMuAsString(), oldParams[i].getSigmaSqrAsString());
				System.out.println("Gaussian"+i);
				System.out.println(output);
			}
			System.out.println("---NEW---");
			for (int i = 0; i < newParams.length; i++) {
				String output = String.format("%s\n%s\n%s", newParams[i].getWasString(), newParams[i].getMuAsString(), newParams[i].getSigmaSqrAsString());
				System.out.println("Gaussian"+i);
				System.out.println(output);
			}

			toBeContinued = GaussianParams.evaluateStop(oldParams, newParams, EPSILON);
			nIternation++;
		}
		System.out.println("");
		System.out.println("---Fitted GMM---");
		for (int i = 0; i < newParams.length; i++) {
			String output = String.format("%s %s %s\n%s %s %s\n%s %s %s","w"+i+"=[", newParams[i].getWasString(),"]","mu"+i+"=[", newParams[i].getMuAsString(),"]","sigmaSqr"+i+"=[", newParams[i].getSigmaSqrAsString(),"]");
			System.out.println("");
			System.out.println(output);
		}

	}
		
	private double[][] readx(String filename) throws IOException {
		ArrayList<double[]> xlist = new ArrayList<double[]> ();
		
		BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(xFile)));
		try {
			String line;
			line = br.readLine();
			while (line != null) {
				xlist.add(setx(line));
				line = br.readLine();
			}
		} finally {
			br.close();
		}
		int n = xlist.size();
		double[][] x = new double [n][d];
		for(int i = 0; i < n; i++) {
			x[i] = xlist.get(i);
		}
		return x;
	}

	static public double[] setx(String line) {
		double[]x = new double[d];
		String[] split = line.split("\\s+");
		if (split.length != d) {
			throw new RuntimeException("Input vector VS GaussianParams dimensions mismatch!!");
		}
		for (int i = 0; i < split.length; i++) {
			x[i] = Double.parseDouble(split[i]);
		}
		return x;
	}
}
