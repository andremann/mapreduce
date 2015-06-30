/*******************************************************************************
 * Copyright (c) 2013, Lucia Vadicamo (NeMIS Lab., ISTI-CNR, Italy)
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met: 
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer. 
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * @author Lucia
 *
 */
public class prova {
	static String parfile="C:\\Users\\Lucia\\SkyDrive\\UNI\\cloudComp\\project\\mapreduce\\src\\main\\resources\\params.txt";
	static String xfile="C:\\Users\\Lucia\\SkyDrive\\UNI\\cloudComp\\project\\mapreduce\\src\\main\\resources\\x.txt";
	static int k=2;
	static int d=3;
	private static final double EPSILON = 0.05;
	private static final int MAX_ITERATIONS = 100;

	public static void main(String[] args) throws Exception {
		GaussianParams[] oldParams= readParams(parfile);
		GaussianParams[] newParams=oldParams ;
		double[][] x =readx(xfile);
		System.out.println("prova");
		int n=x.length;

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
				for (int i = 0; i < k; i++) {
					stat[i][s] = new Stats(p[i], oldParams[i].getMu(), xs); //compute statistic
					//Log.info(String.format("%s\n%s\n%s", params[i].getWasString(), params[i].getMuAsString(), params[i].getSigmaAsString()));
				}
			}
			//reducer 
			//aggrego per chiave
			 newParams=new GaussianParams[k];
			for(int i=0; i<k; i++) {
				Stats[] statI=stat[i];
				ArrayList<Stats> statList= new ArrayList <Stats>();
				for(Stats onestat:statI) {
					statList.add(onestat);
				}
				Stats globalStats = new Stats(statList);
				newParams[i] = new GaussianParams(globalStats, statList.size());

			}

			System.err.println("\n---OLD---");
			for (int i = 0; i < oldParams.length; i++) {
				String output = String.format("%s\n%s\n%s", oldParams[i].getWasString(), oldParams[i].getMuAsString(), oldParams[i].getSigmaAsString());
				System.err.println(output);
			}
			System.err.println("---NEW---");
			for (int i = 0; i < newParams.length; i++) {
				String output = String.format("%s\n%s\n%s", newParams[i].getWasString(), newParams[i].getMuAsString(), newParams[i].getSigmaAsString());
				System.err.println(output);
			}

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
					params[index].setSigma(line);
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
