package spanprob;

import org.apache.commons.math3.fraction.Fraction;
import java.util.Arrays;

public class Transition {

	private final State from, to;
	private final String[] label1,label2;
	private Fraction prob;

	public Transition(State from, State to, String[] label1, String[]label2, Fraction prob) {
		this.from=from;
		this.to=to;
		this.label1=label1;
		this.label2=label2;
		this.prob=prob;
	}
	
	/**
	 * to string
	 */
	public String toString() {
		return "-from ["+getFromString() +"] -to ["+getToString()+"]  "
				+ "|  ["+getLabel1String()+"] , ["+getLabel2String()+"] -Prob: "+prob+"  )";
	}

	/**
	 * @param prob
	 */
	public void setProb(Fraction prob) {
		this.prob = prob;
	}

	/**
	 * @return
	 */
	public State getTo() {
		return to;
	}

	/**
	 * @return
	 */
	public State getFrom() {
		return from;
	}

	/**
	 * @return
	 */
	public String getFromString() {	
		return from.toString();
	}

	/**
	 * @return
	 */
	public String getToString() {
		return to.toString();
	}
	
	/**
	 * @return
	 */
	public String[] getLabel1() {
		return label1;
	}
	
	/**
	 * @return
	 */
	public String[] getLabel2() {
		return label2;
	}
	
	/**
	 * @return
	 */
	public String getLabel1String() {
		String res="";
		for(String label : label1){
			res=res + "" +label;
		}
		return res;
	}

	/**
	 * @return
	 */
	public String getLabel2String() {
		
		String res="";
		for(String label : label2){
			res=res + "" +label;
		}
		return res;
	}
	
	/**
	 * @return
	 */
	public Fraction getProb() {
		return prob;
	}

	/**
	 * Tensor
	 * @param other
	 * @return
	 */
	public Transition and(Transition other) {
		return new Transition(
				this.from.product(other.from),
				this.to.product(other.to),
				this.getLabel1(),
				other.getLabel2(),
				this.getProb().multiply(other.getProb())
		);
	}

	/**
	 * Composition
	 * @param other
	 * @return
	 */
	public Transition mul(Transition other) {
		return new Transition(
				this.from.product(other.from),
				this.to.product(other.to),
				composeLabel(this.getLabel1(),other.getLabel1()),
				composeLabel(this.getLabel2(),other.getLabel2()),
				this.getProb().multiply(other.getProb())
		);
	}

	/**
	 * Composition of two labels
	 * @param label1
	 * @param label2
	 * @return
	 */
	static private String[] composeLabel(String[] label1, String[] label2) {
		String[] label = Arrays.copyOf(label1, label1.length + label2.length);
		System.arraycopy(label2, 0, label, label1.length, label2.length);
		return label;
	}
}