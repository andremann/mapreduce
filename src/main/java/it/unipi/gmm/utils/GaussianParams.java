package it.unipi.gmm.utils;

public class GaussianParams {
	private double w;
	private double[] mu;
	private double[] sigma;
	
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
}
