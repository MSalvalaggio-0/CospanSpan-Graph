package spanprob;

import org.apache.commons.math3.fraction.Fraction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;


public enum Constant {
	ID(1, 1),
	DIAG(1, 2),
	CODIAG(2, 1),
	UNIT(0, 2),
	COUNIT(2, 0),
	// TWIST(2, 2) TODO
	;

	public final int left, right;
	Constant(int left, int right) {
		this.left = left;
		this.right = right;
	}

	public SpanProb generateSpanProb(Set<String> alphabet) {
		State state = new State("");
		int n = alphabet.size();
		Fraction prob = new Fraction(1, n);
		Transition[] transitions = new Transition[n];
		int i = 0;
		for(String symbol : alphabet) {
			String[] leftLabels = new String[this.left];
			String[] rightLabels = new String[this.right];
			Arrays.fill(leftLabels, symbol);
			Arrays.fill(rightLabels, symbol);
			transitions[i++] = new Transition(state, state, leftLabels, rightLabels, prob);
		}
		
		ArrayList<State> states = new ArrayList<>(1);
		states.add(state);
		SpanProb span = new SpanProb(state, states,transitions);
		return span;
	}
}









