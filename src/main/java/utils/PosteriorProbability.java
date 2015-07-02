
package utils;
 
public class PosteriorProbability {
	/**
	 * Compute posterior probability:<br>
	 * p[i] is the probability that the object x is assigned
	 * to i-th Gaussian <br>
	 * p[i] can also viewed as the responsibility that Gaussian i takes for
	 * "explaining" the observation x
	 * 
	 * <p>
	 * All the computation are performed in the log domain:
	 * <p>
	 * <img src="http://latex.codecogs.com/gif.latex?{
	 * p(i|x, \Theta)=exp\left(log(p(i|x, \Theta))\right)
	 * } " border="0"/>
	 * <p>
	 * <img src="http://latex.codecogs.com/gif.latex?{
	 * log (p(i|x, \Theta))= log\left(w_i*p(x|w_i,\mu_i,\sigma_i^2)\right)- log\left(\sum_{s=1}^k w_s*p(x|w_s,\mu_s,\sigma_s^2)\right)
	 * } " border="0"/>
	 * 
	 * @param theta parameter of k gaussians
	 * @param x sample point
	 * @return posterior probabilities vector
	 * @throws Exception
	 */
	public static final double[] compute_p(GaussianParams[] theta, double[] x) {
		int k = theta.length;
		if(k > 0) {
			double[] p = new double[k];
			p[0] = theta[0].compute_partial_p(x);
			double tmp_logSum = p[0];

			for(int i = 1; i < k; i++) {
				p[i] = theta[i].compute_partial_p(x);
				tmp_logSum = log_sum(tmp_logSum, p[i]);
			}

			for(int i = 0; i < k; i++) {
				p[i] = Math.exp(p[i] - tmp_logSum);
			}
			return p; 	
		}else
			return null;
	}
	/**
	 *  Compute log(a+b) given log(a) and log(b)
	 * 
	 * @param log_a
	 * @param log_b
	 * @return
	 */
	static final double log_sum(double log_a, double log_b) {
		if (log_a < log_b)
			return (log_b + Math.log(1 + Math.exp(log_a - log_b)));
		else
			return (log_a + Math.log(1 + Math.exp(log_b - log_a)));
	}

}
