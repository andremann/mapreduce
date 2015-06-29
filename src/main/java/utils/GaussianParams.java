package utils;

import java.util.Arrays;


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
	 * <img src="http://latex.codecogs.com/gif.latex? \sigma \in \mathbb{R}^d "border="0"/>
	 */
	private double[] sigma;
	/** The minimum allowable value for the variances*/
	static final double min_sigmaSqr = (float) Math.pow(10, -12); // used in GaussianParams(Stats stat, int n) 


	public GaussianParams(int d){
		w = 0;
		mu = new double[d];
		sigma = new double[d];
	}


	public GaussianParams(Stats stat, int n) throws Exception{
		/**
		 * Usato nel reducer.
		 */
		double nk = stat.getS0();
		mu = stat.getS1();
		sigma = stat.getS2();
		int d = mu.length; 
		//		if(d!=sigma.length) { 
		//			throw new Exception("parameters mu and sigma should have the same dimensionality");
		//			}
		w=nk/n;
		for(int dim = 0; dim < d; dim++) {
			// handle pathological case with too small sigma values
			if (sigma[dim] < min_sigmaSqr) {
				sigma[dim] = 10000;
				mu[dim] = mu[dim] * (2 * Math.random() - 1);
				//sigma[dim] = min_sigmaSqr;
			}

			//TODO controllare che non stia dividendo per zero ..

			if(nk != 0){mu[dim] /= nk;
			sigma[dim] /= nk;
			}
			else {
				throw new Exception("caso da risolvere");
			}
		}		
	}

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
		return vectorToString(this.getMu());
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

	public double[] getSigma() {
		return sigma;
	}

	public String getSigmaAsString() {
		return vectorToString(this.getSigma());
	}

	public void setSigma(String line) {
		String[] split = line.split("\\s+");
		if (split.length != sigma.length) {
			throw new RuntimeException("Input vector VS GaussianParams dimensions mismatch!!");
		}
		for (int i = 0; i < split.length; i++) {
			sigma[i] = Double.parseDouble(split[i]);
		}
		return;
	}

	private String vectorToString(double[] v) {
		return Arrays.toString(v);
	}

	/**
	 * Compute part of the posterior probability related only to the actual gaussian of parameter theta=(w, mu, sigma):<br>
	 * p is the probability that the object x is assigned to the actual Gaussian <br>
	 * p can also viewed as the responsibility that the actual Gaussian takes for
	 * "explaining" the observation x<br>
	 * 
	 * All the computation are performed in the log domain
	 *
	 * <p>
	 * <img src="http://latex.codecogs.com/gif.latex?{log(w*p(x|w,\mu,\sigma) )=-\frac{d}{2}\log(2\pi)}+
	 * \log(w)}-
	 * {\frac{1}{2}\sum_{j=1}^d\left[\log(\sigma_j)+ \dfrac{(x_j-\mu_j)^2}{\sigma_j}\right]
	 * } " border="0"/>
	 * 
	 * @param x sample point
	 * @return log(w*p(x|theta) )
	 * @throws Exception
	 */
	public double compute_partial_p(double[] x) {
		double p=0;
		int d=x.length;
		for (int j = 0; j < d; j++) {
			double muj = mu[j];
			double sigmaj = sigma[j];
			double xMudiff=x[j]-muj;
			p+=Math.log(sigmaj)+ (xMudiff*xMudiff)/sigmaj;
		}
		p=Math.log(w)-(d*Math.log(2 * Math.PI)+p)/2.0;
		return p; 	
	}


}
