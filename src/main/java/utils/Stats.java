package utils;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import org.apache.hadoop.io.Writable;

public class Stats implements Serializable, Writable {

	private static final long serialVersionUID = 1295187155052274275L;
	
	private double s0;
	private double[] s1;
	private double[] s2;
	
	public Stats() {
		
	/**
	 * Usato durante la fase di (de)serializzazione
	 */
		s0 = 0;
		s1 = null;
		s2 = null;
	}
	public Stats(int d) {
		s0 = 0;
		s1 = new double[d];
		s2 = new double[d];
	}
	
	/**
	 * Compute zero, first and second-order statistics given a posterior probability and the observation x:
	 * @param p  posterior probabilities related to one gaussian
	 * @param mu mean of the actual gaussian
	 * @param x sample point
	 */
//	public Stats(double p, double[] mu, double[] x) {
//		int d = x.length;
//		s0 = p;
//		s1 = new double[d];
//		s2 = new double[d]; 
//		for(int dim = 0; dim < d; dim++) {
//			double xdim = x[dim];
//			s1[dim] = p * xdim;
//			s2[dim] = s1[dim]*xdim;
//				
//		}
//	}
	public Stats(double p, double[] mu, double[] x) {
		int d = x.length;
		s0 = p;
		s1 = new double[d];
		s2 = new double[d]; 
		for(int dim = 0; dim < d; dim++) {
			double xdim = x[dim];
			double xMuDiff = xdim - mu[dim];
			s1[dim] = p * xdim;
			s2[dim] = xMuDiff * xMuDiff * p;
				
		}
	}

	/**
	 * Aggregate statistics 
	 * 
	 * @param statsList
	 */
	public Stats(List<Stats> statsList){
		//TODO
		//only for reducer, no combiners
		/**
		 * Costruttore utilizzato nel reducer
		 * per effettuare la condensazione di tutte
		 * le statistiche
		 */
		
		int n = statsList.size();
		int d = statsList.get(0).s1.length;		//TODO inserire controllo che la lista non sia vuota??
		s0 = 0;
		s1 = new double[d];
		s2 = new double[d];
		for(Stats iterStat:statsList) {
			s0 += iterStat.s0;
			double [] s1iter = iterStat.s1;
			double [] s2iter = iterStat.s2;
			for(int dim = 0; dim < d; dim++) {
				s1[dim] += s1iter[dim];
				s2[dim] += s2iter[dim];
			}
		}
	}
	
	public void update(List<Stats> statsList) {
		/*TODO*/
	} 
	
	public void update (GaussianParams[] params){
		//TODO
		/**
		 * Metodo che ricalcola le statistiche a partire
		 * da un vettore di parametri gaussiani
		 */
//		Note Lucia:
//		questo lo eliminerei oppure, se si vuole alleggerire il codice del mapper,
//		 si puó definisce un  metodo tipo
//		 public Stats[] computeStats(GaussianParams[] params, double[] x){
//		int k=params.length;
//		Stats[] stat=new Stats[k];
//		double[] p =PosteriorProbability.compute_p(params, x);//compute posterior probability
//		for(int i=0; i<k; i++) {
//			stat[i]=new Stats(p[i], params[i].getMu(), x); //compute statistics
//		}
//		
//		}
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

	@Override
	public void readFields(DataInput arg0) throws IOException {
		s0 = arg0.readDouble();
		s1 = new double[arg0.readInt()];
		for(int i = 0; i < s1.length; i++) {
			s1[i] = arg0.readDouble();
		}
		s2 = new double [arg0.readInt()];
		for(int i = 0; i<s2.length; i++) {
			s2[i] = arg0.readDouble();
		}
	}

	@Override
	public void write(DataOutput arg0) throws IOException {
		arg0.writeDouble(s0);
		arg0.writeInt(s1.length);
		for(int i= 0; i < s1.length; i++) {
			arg0.writeDouble(s1[i]);
		}
		arg0.writeInt(s2.length);
		for(int i = 0; i < s2.length; i++) {
			arg0.writeDouble(s2[i]);
		}
	}
}
