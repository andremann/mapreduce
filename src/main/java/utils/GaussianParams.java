package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;


public class GaussianParams{
	/**
	 * mixture weight: 
	 * <img src="http://latex.codecogs.com/gif.latex? w \in \mathbb{R} "border="0"/>
	 */
	private double w;
	/**
	 * Mean
	 * <img src="http://latex.codecogs.com/gif.latex? \mu \in \mathbb{R}^d "border="0"/>
	 */
	private double[] mu;
	/**
	 * Variance
	 * <img src="http://latex.codecogs.com/gif.latex? \sigma^2 \in \mathbb{R}^d "border="0"/>
	 */
	private double[] sigmaSqr;
	
	/** The minimum allowable value for the variances*/
	static final double min_sigmaSqr = Math.pow(10, -10); // used in GaussianParams(Stats stat, int n) 


	public GaussianParams(int d){
		w = 0;
		mu = new double[d];
		sigmaSqr = new double[d];
	}
	
	/**
	 * Computing the new Gaussian parameters, given the 0-order, 1st-order and 2nd-order statistics.<p>
	 * It is well know that ML estimation of GMM is a 
	 * non-convex optimization problem for more that one Gaussian. 
	 * Hence different initializations might lead to different solutions.
	 * 
	*	Also note that some singularity can arise in the likelihood function when 
	*	a single Gaussian collapses onto a specific data point (see [1] 
	*	Section 9.2.2 for more details). Then in applying ML to GMM we must take steps
 	*	to avoid finding pathological solution. A suitable heuristics  is detecting when 
 	*	a Gaussian component is collapsing and resetting its mean  to a randomly chosen 
 	*	value while also resetting its covariance to some large value, and then continuing with 
 	*	the optimization. 
 	*
 	*<p>[1] C. M. Bishop, Pattern Recognition and Machine Learning, Information
Science and Statistics, Springer, 2006.
	 * @param stat
	 * @param n
	 * @throws Exception
	 */
	public GaussianParams(Stats stat, int n) throws Exception{
		/**
		 * Usato nel reducer.
		 */
		double nk = stat.getS0();
		mu = stat.getS1();
		sigmaSqr= stat.getS2() ;
		int d = mu.length; 
		w=nk/n;
		int nzero=0;
		if(nk!=0) {
			for(int dim = 0; dim < d; dim++) {		
				mu[dim] /= nk;
				sigmaSqr[dim] = sigmaSqr[dim]/nk -mu[dim] *mu[dim];
				
				// handle  too small sigmaSqr values
				if (sigmaSqr[dim] < min_sigmaSqr) {
					sigmaSqr[dim] = min_sigmaSqr;
					nzero++;
				}
				
				
			}	

		}
		else {
			throw new Exception("caso da risolvere!!!!");//TODO 
		}
			
		// handle pathological case when a Gaussian component is collapsing
		if(nzero == d) {
			for(int dim = 0; dim < d; dim++) {	
				sigmaSqr[dim] = 100;
				//mu[dim] = mu[dim] * (2 * Math.random() - 1); //TODO scommentare quando si scopre l'arcano
			}
		}
	}

//	public GaussianParams(Stats stat, int n) throws Exception{//OLD
//		/**
//		 * Usato nel reducer.
//		 */
//		double nk = stat.getS0();
//		mu = stat.getS1();
//		sigmaSqr = stat.getS2();
//		int d = mu.length; 
//		w=nk/n;
//		int nzero=0;
//		if(nk!=0) {
//			for(int dim = 0; dim < d; dim++) {		
//				// handle  too small sigmaSqr values
//				if (sigmaSqr[dim] < min_sigmaSqr) {
//					sigmaSqr[dim] = min_sigmaSqr;
//					nzero++;
//				}
//				mu[dim] /= nk;
//				sigmaSqr[dim] /= nk;
//			}	
//
//		}
//		else {
//			throw new Exception("caso da risolvere!!!!");//TODO 
//		}
//			
//		// handle pathological case when a Gaussian component is collapsing
//		if(nzero == d) {
//			for(int dim = 0; dim < d; dim++) {	
//				sigmaSqr[dim] = 100;
//				mu[dim] = mu[dim] * (2 * Math.random() - 1);
//			}
//		}
//	}

	public double getW() {
		return w;
	}

	public String getWasString() {
		return String.valueOf(this.getW());
	}

	public void setW(double w) {
		this.w = w;
	}

	public double[] getMu() {
		return mu;
	}

	public String getMuAsString() {
		String outString = "";
		for (int i = 0; i < mu.length; i++) {
			outString += mu[i] + " ";
		}
		return outString;
	}

	public void setMu(String line) {
		String[] split = line.split("\\s+");
		if (split.length != mu.length) {
			throw new RuntimeException("Input vector VS GaussianParams dimensions mismatch!!");
		}
		for (int i = 0; i < split.length; i++) {
			mu[i] = Double.parseDouble(split[i]);
		}
		return;
	}
	
	public void setMu(double[] mu) {
		this.mu = mu; 
	}

	public double[] getSigmaSqr() {
		return sigmaSqr;
	}

	public String getSigmaSqrAsString() {
		String outString = "";
		for (int i = 0; i < sigmaSqr.length; i++) {
			outString += sigmaSqr[i] + " ";
		}
		return outString;
	}

	public void setSigmaSqr(String line) {
		String[] split = line.split("\\s+");
		if (split.length != sigmaSqr.length) {
			throw new RuntimeException("Input vector VS GaussianParams dimensions mismatch!!");
		}
		for (int i = 0; i < split.length; i++) {
			sigmaSqr[i] = Double.parseDouble(split[i]);
		}
		return;
	}

	private String vectorToString(double[] v) {
		return Arrays.toString(v);
	}

	/**
	 * Compute part of the posterior probability related only to the actual gaussian of parameter theta=(w, mu, sigmaSqr):<br>
	 * p is the probability that the object x is assigned to the actual Gaussian <br>
	 * p can also viewed as the responsibility that the actual Gaussian takes for
	 * "explaining" the observation x<br>
	 * 
	 * All the computation are performed in the log domain
	 *
	 * <p>
	 * <img src="http://latex.codecogs.com/gif.latex?{log(w*p(x|w,\mu,\sigma^2) )=-\frac{d}{2}\log(2\pi)}+
	 * \log(w)}-
	 * {\frac{1}{2}\sum_{j=1}^d\left[\log(\sigma_j^2)+ \dfrac{(x_j-\mu_j)^2}{\sigma_j^2}\right]
	 * } " border="0"/>
	 * 
	 * @param x sample point
	 * @return log(w*p(x|theta) )
	 * @throws Exception
	 */
	public double compute_partial_p(double[] x) {
		double p = 0.0;
		int d = x.length;
		for (int j = 0; j < d; j++) {
			double muj = mu[j];
			double sigmaj = sigmaSqr[j];
			double xMudiff = x[j] - muj;
			p += Math.log(sigmaj) + (xMudiff * xMudiff) / sigmaj;
		}
		p = Math.log(w) - (d*Math.log(2 * Math.PI) + p) / 2.0;
		return p; 	
	}

	/**
	 * Read from a parameter file structured as follows w_1 mu_1 sigma_1
	 * (repeat)
	 * 
	 * @param filename
	 * @param k
	 * @return
	 * @throws IOException
	 */
	public static GaussianParams[] ReadParamsFromHdfs(String filename, Configuration conf, int k, int d) throws IOException {
		Path pt = new Path(filename);
		FileSystem fs = FileSystem.get(conf);
		return parse(new InputStreamReader(fs.open(pt)), k, d);
	}
	
	public static GaussianParams[] parse(InputStreamReader reader, int k, int d) throws IOException {
		GaussianParams[] params = new GaussianParams[k];
		for (int i = 0; i < k; i++) {
			params[i] = new GaussianParams(d);
		}
		
		BufferedReader br = new BufferedReader(reader);
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

	public static boolean evaluateStop(GaussianParams[] oldParams, GaussianParams[] newParams, double epsilon) {
		double norm = 0;
		for (int i = 0; i < oldParams.length; i++) {
			double[] muOld = oldParams[i].getMu();
			double[] muNew = newParams[i].getMu();
			for (int j = 0; j < muOld.length; j++) {
				double diff = muOld[j] - muNew[j];
				norm += diff * diff;
			}
		}
		norm = Math.sqrt(norm);
		System.out.println("\n ----NORM----\n" + norm);
		return norm > epsilon;
	}

	public void setSigmaSqr(double[] sigmaSqr) {
		// TODO Auto-generated method stub
		this.sigmaSqr = sigmaSqr;
	}

}
