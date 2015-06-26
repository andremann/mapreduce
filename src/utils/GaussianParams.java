package utils;



public class GaussianParams{
	private double w;
	private double[] mu;
	private double[] sigma;
	
	
	public GaussianParams(){
		//TODO
		/**
		 * Usato nel mapper.
		 */
		//Lucia:
//		qui non ho capito cosa volevate fare, 
//		perchè nel mapper i parametri li inizializziamo leggendoli da hdfs
	}
	
	
	public GaussianParams(Stats stat){
		//TODO
		/**
		 * Usato nel reducer.
		 */
	}
	
	public double getW() {
		return w;
	}
	public void setW(double w) {
		this.w = w;
	}
	public double[] getMu() {
		return mu;
	}
	public void setMu(double[] mu) {
		this.mu = mu;
	}
	
	public void setMu(String line) {
		
		//TODO
		return;
	}
	public double[] getSigma() {
		return sigma;
	}
	public void setSigma(double[] sigma) {
		this.sigma = sigma;
	}
	
	public void setSigma(String line) {
		//TODO
		return;
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
