package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.junit.Test;
/**
 * @author Lucia
 *
 */
public class SequentialTest { 
	public static int  maxNFeatures=10000;
	//TODO cambiare parfile e xfile prima di eseguire
	static String parfile="C:\\Users\\Lucia\\SkyDrive\\UNI\\cloudComp\\project\\mapreduce\\src\\main\\resources\\ParametriGMM_INRIA_HOLIDAYS\\kMeans-k64_INRIA-Holidays.txt";
			//"/Users/andrea/git/mapreduce/src/main/resources/params.txt";
	static String xfile="C:\\Users\\Lucia\\SkyDrive\\UNI\\cloudComp\\project\\mapreduce\\src\\main\\resources\\ParametriGMM_INRIA_HOLIDAYS\\INRIA_dataset_SIFTs"+maxNFeatures+"LF.txt";
	//"/Users/andrea/git/mapreduce/src/main/resources/x.txt";
	static String dimfile="C:\\Users\\Lucia\\SkyDrive\\UNI\\cloudComp\\project\\mapreduce\\src\\main\\resources\\ParametriGMM_INRIA_HOLIDAYS\\dimensions.txt";
	static int k=64;
	static int d=128;
	private static final double EPSILON = 0.05;
	private static final int MAX_ITERATIONS = 1000;



	@Test
	public void testSeq() throws Exception {
	//public static void main(String[] args) throws Exception {
		//System.out.println("\n TESTING: " + this.getClass().getCanonicalName());
		
		GaussianParams[] oldParams = readParams(parfile);
		GaussianParams[] newParams = oldParams ;
		double[][] x = readx(xfile);
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

//			System.out.println("\n---OLD---");
//			for (int i = 0; i < oldParams.length; i++) {
//				String output = String.format("%s\n%s\n%s", oldParams[i].getWasString(), oldParams[i].getMuAsString(), oldParams[i].getSigmaSqrAsString());
//				System.out.println("Gaussian"+i);
//				System.out.println(output);
//			}
//			System.out.println("---NEW---");
//			for (int i = 0; i < newParams.length; i++) {
//				String output = String.format("%s\n%s\n%s", newParams[i].getWasString(), newParams[i].getMuAsString(), newParams[i].getSigmaSqrAsString());
//				System.out.println("Gaussian"+i);
//				System.out.println(output);
//			}

			toBeContinued = GaussianParams.evaluateStop(oldParams, newParams, EPSILON);
			nIternation++;


		}



	}
	private static GaussianParams[] readParams(String filename) throws IOException {
		GaussianParams[] params = new GaussianParams[k];
		for (int i = 0; i < k; i++) {
			params[i] = new GaussianParams(d);
		}

		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(filename))));
		try {
			String line;
			line = br.readLine();
			int counter = 0;
			while (line != null) {
				int index = counter / 3;
				switch (counter % 3) {
				case 0:
					params[index].setW(Double.parseDouble(line));
					break;
				case 1:
					params[index].setMu(line);
					break;
				case 2:
					params[index].setSigmaSqr(line);
					break;
				default:
					break;
				}
				counter++;
				line = br.readLine();
			}
		} finally {
			br.close();
		}
		return params;
	}
	
	private static double[][] readx(String filename) throws IOException {
		ArrayList<double[]> xlist = new ArrayList<double[]> ();
		
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(filename))));
		try {
			String line;
			line = br.readLine();
			int counter = 0;
			while (line != null) {
				xlist.add(setx(line));
				counter++;
				line = br.readLine();
			}
		} finally {
			br.close();
		}
		int n=xlist.size();
		double[][] x= new double [n][d];
		for(int i=0;i<n;i++) {
			x[i]=xlist.get(i);
		}
		return x;
	}

	static public double[] setx(String line) {
		double[]x=new double[d];
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
