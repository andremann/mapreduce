package it.unipi.gmm.utils;

import java.io.Serializable;
import java.util.List;

public class Stats implements Serializable {
	private double s0;
	private double[] s1;
	private double[] s2;
	
	public Stats(int d) {
		s0 = 0;
		s1 = new double[d];
		s2 = new double[d];
	}
	
	public void update(List<Stats> statsList) {
		/*TODO*/
	} 

	public double getS0() {
		return s0;
	}

	public void setS0(double s0) {
		this.s0 = s0;
	}

	public double[] getS1() {
		return s1;
	}

	public void setS1(double[] s1) {
		this.s1 = s1;
	}

	public double[] getS2() {
		return s2;
	}

	public void setS2(double[] s2) {
		this.s2 = s2;
	}
	
	
}
