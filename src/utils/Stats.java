package utils;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import org.apache.hadoop.io.Writable;

public class Stats implements Serializable,Writable {

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
	
	public Stats(List<Stats> statsList){
		//TODO
		/**
		 * Costruttore utilizzato nel reducer
		 * per effettuare la condensazione di tutte
		 * le statistiche
		 */
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
		s0=arg0.readDouble();
		s1 = new double [arg0.readInt()];
		for(int i =0; i<s1.length;++i){
			s1[i]=arg0.readDouble();
		}
		s2 = new double [arg0.readInt()];
		for(int i =0; i<s2.length;++i){
			s2[i]=arg0.readDouble();
		}
	}

	@Override
	public void write(DataOutput arg0) throws IOException {
		arg0.writeDouble(s0);
		arg0.writeInt(s1.length);
		for(int i=0;i<s1.length;++i){
			arg0.writeDouble(s1[i]);
		}
		arg0.writeInt(s2.length);
		for(int i=0;i<s2.length;++i){
			arg0.writeDouble(s2[i]);
		}
		
		
	}
	
	
}
