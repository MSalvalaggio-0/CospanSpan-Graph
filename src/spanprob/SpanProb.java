package spanprob;

import org.apache.commons.math3.fraction.Fraction;
import java.util.*;

public class SpanProb {


	private String name = "";
	private HashSet<State> states;
	private List<Transition> transitions;
	private final HashMap<State, List<Transition>> outgoing;
	private final State initialState;


	public SpanProb(State initialState, List<State> states, Transition[] transitions) {
		this(initialState, new HashSet<>(states), new ArrayList<>(Arrays.asList(transitions)));
	}

	private SpanProb(State initialState, HashSet<State> states, List<Transition> transitions) {
		this.initialState = initialState;
		this.states = states;
		this.transitions=transitions;
		outgoing = new HashMap<>();
		for(State s : states) {
			outgoing.put(s, new LinkedList<>());
		}
		for(Transition t : transitions) {
			outgoing.get(t.getFrom()).add(t);
		}
	}

	public Collection<State> getStates() {
		return states;
	}

	public Transition[] getTransitions() {
		return transitions.toArray(new Transition[0]);
	}

	public State getInitialState(){
		return initialState;
	}


	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	/**
	 * Compose
	 * @param sp
	 * @return
	 */
	public SpanProb mul(SpanProb sp) {
		HashSet<State> newStates = productState(states, sp.states);
		List<Transition> newTransitions = transitionProduct(transitions, sp.transitions);
		return new SpanProb(initialState.product(sp.initialState), newStates, newTransitions);
	}

	/**
	 * Tensor
	 * @param sp
	 * @return
	 */
	public SpanProb and(SpanProb sp) {
		HashSet<State> newStates = productState(states, sp.states);
		List<Transition> newTransitions = transitionProductAND(transitions, sp.transitions);
		SpanProb spRes = new SpanProb(initialState.product(sp.initialState), newStates, newTransitions);
		spRes.reachability();
		spRes.normalize();
		return spRes;
	}

	private static HashSet<State> productState(HashSet<State> left, HashSet<State> right) {
		HashSet<State> newStates = new HashSet<>();
		for(State s1 : left) {
			for(State s2: right) {
				newStates.add(s1.product(s2));
			}
		}
		return newStates;
	}


	/**
	 * Normalization
	 */
	private void normalize() {
		//get outgoing transitions for states who have it
		for(State state : getStates()) {
			List<Transition> transitions = getOutgoingTransitions(state);
			if(transitions.size() > 0) {
				Fraction prob = new Fraction(0);
				for(Transition x : transitions) {
					prob = prob.add(x.getProb());
				}
				//normalize
				Fraction normalizer = new Fraction(1);
				normalizer = normalizer.divide(prob);
				for(Transition x : transitions) {
					x.setProb((x.getProb().multiply(normalizer)));
				}
			}
		}
	}

	/**
	 * Outgoing Transition from state s
	 * @param s
	 * @return all the transitions that have s like FROM state
	 */
	public List<Transition> getOutgoingTransitions(State s) {
		return outgoing.get(s);
	}
	
	/**
	 * Cartesian product of the Transition
	 * @param t1
	 * @param t2
	 * @return 
	 */
	static private List<Transition> transitionProduct(List<Transition> t1, List<Transition> t2) {
		List<Transition> newTransitions = new ArrayList<>(t1.size() * t2.size());
		for(Transition transition1 : t1) {
			for(Transition transition2 : t2) {
				newTransitions.add(transition1.mul(transition2));
			}
		}
		return newTransitions;
	}
	
	/**
	 * Tensor Cartesian product for the Transition
	 * @param t1
	 * @param t2
	 * @return
	 */
	static private List<Transition> transitionProductAND(List<Transition> t1, List<Transition> t2) {
		List<Transition> newTransitions = new LinkedList<>();
		for(Transition transition1 : t1) {
			for(Transition transition2 : t2) {
				if(Arrays.equals(transition1.getLabel2(), transition2.getLabel1())) {
					newTransitions.add(transition1.and(transition2));
				}
			}
		}
	
		return newTransitions;
	}

	/**
	 * Find outgoing transition from the initial state, recursively, excluding non reachable states
	 */
	public void reachability() {
		HashSet<State> toVisit = new HashSet<>();
		HashSet<State> visited = new HashSet<>();
		LinkedList<Transition> visitedTransitions = new LinkedList<>();
		toVisit.add(initialState);
		while(!toVisit.isEmpty()) {
			State current = toVisit.iterator().next();
			toVisit.remove(current);
			visited.add(current);

			for(Transition t : getOutgoingTransitions(current)) {
				visitedTransitions.add(t);
				if(!visited.contains(t.getTo())){
					toVisit.add(t.getTo());
				}
			}
		}
		
		this.transitions = visitedTransitions;
		this.states = visited;
	}

	public Fraction[][] buildMatrix() {
		//cause there are so many label, compose a list of unique label
  		List<String> labelL = new ArrayList<String>();
  		for(int i=0;i<getTransitions().length;i++) {
  			if(!labelL.contains(getTransitions()[i].getLabel1String()+""+getTransitions()[i].getLabel2String())){
  				labelL.add(getTransitions()[i].getLabel1String()+""+getTransitions()[i].getLabel2String());
  			}
  		}
  		
  		//compose the label and build the transition matrix
  		List<Fraction[][]> labelForMatrix = new ArrayList<Fraction[][]>();
  		String [] left = new String[] {};
  		String [] right = new String[] {};
  		for(String label : labelL) {
  			char[] labelChar = label.toCharArray();
  			left = new String[labelChar.length/2];
  			int ind=0;
  			for(int i=0;i<(left.length);i++){
  				left[i]=labelChar[i]+"";
  				ind=i;
  			}
  			right = new String[labelChar.length/2];
  			for(int i=0;i<(right.length);i++){
  				right[i]=labelChar[i+ind+1]+"";
  			}
  					
  			labelForMatrix.add(toMatrix(left,right));
  		}
  		
  		//sum all the matrix to a total transition matrix
  		Fraction[][] totalmatrixP = new Fraction[getStates().size()][getStates().size()];
  		if(labelForMatrix.size()>1) {
  			totalmatrixP = addMatrix(labelForMatrix.get(0), labelForMatrix.get(1));
  			for(Fraction[][] matrix : labelForMatrix) {
  				totalmatrixP=addMatrix(totalmatrixP, matrix);
  			}
  			
  		}
  		else {
  			totalmatrixP =labelForMatrix.get(0);
  		}
  		
  		return totalmatrixP;
	}
	
	/**
	 * Compose matrix of a pair of label, need in input the label to compute, left/right
	 * @param label1 left
	 * @param label2 right
	 * @return Bidimensional array of type Fraction[][]
	 */
	public Fraction[][] toMatrix(String[] label1, String[] label2) {
		int nState = this.states.size();
		HashMap<State, Integer> statePositions = new HashMap<>();
		int stateNumber = 0;
		Iterator<State> it = states.stream().sorted().iterator();
		while (it.hasNext()) {
			statePositions.put(it.next(), stateNumber++);
		}
		Fraction[][] mat = new Fraction[nState][nState];
		for (int i = 0; i < mat.length; i++) {
		    for (int j = 0; j < mat[i].length; j++) {
		        mat[i][j]= new Fraction(0);
		    }
		}

		for(Transition transition : transitions) {
			if(Arrays.equals(transition.getLabel1(),label1)
					&& Arrays.equals(transition.getLabel2(),label2)) {
				int posFrom= statePositions.get(transition.getFrom());
				int posTo= statePositions.get(transition.getTo());
				mat[posFrom][posTo] = mat[posFrom][posTo].add(transition.getProb());
			}
		}
		
		return mat;
	}
	
	/**
	 * c = a + b
	 * @param a
	 * @param b
	 * @return
	 */
    public static Fraction[][] addMatrix(Fraction[][] a, Fraction[][] b) {
        int m = a.length;
        int n = a[0].length;
        Fraction[][] c = new Fraction[m][n];
        for (int i = 0; i < m; i++)
            for (int j = 0; j < n; j++)
                c[i][j] = a[i][j].add(b[i][j]);
        return c;
    }

    /**
     * c = a - b
     * @param a
     * @param b
     * @return
     */
    public static Fraction[][] subtractMatrix(Fraction[][] a, Fraction[][] b) {
        int m = a.length;
        int n = a[0].length;
        Fraction[][] c = new Fraction[m][n];
        for (int i = 0; i < m; i++)
            for (int j = 0; j < n; j++)
                c[i][j] = a[i][j].subtract(b[i][j]);
        return c;
    }

    /**
     * c = a * b
     * @param a
     * @param b
     * @return
     */
    public static Fraction[][] multiplyMatrix(Fraction[][] a, Fraction[][] b) {
        int m1 = a.length;
        int n1 = a[0].length;
        int m2 = b.length;
        int n2 = b[0].length;
        if (n1 != m2) throw new RuntimeException("Illegal matrix dimensions.");
        Fraction[][] c = new Fraction[m1][n2];
        for (int i = 0; i < m1; i++)
            for (int j = 0; j < n2; j++)
                for (int k = 0; k < n1; k++)
                    c[i][j] =c[i][j].add(a[i][k].multiply(b[k][j]));
        return c;
    }

	/**
	 * matrix to string
	 * @param matrix
	 * @return
	 */
	public String matrixToString(Fraction[][] matrix) {
		StringBuilder builder = new StringBuilder();
		for (Fraction[] matrix1 : matrix) {
			for (Fraction fraction : matrix1) {
				if (fraction.equals(new Fraction(0))) {
					builder.append("0;");
				} else {
					builder.append(fraction.floatValue()).append(";");
				}
			}
			builder.append("\r\n");
		}
		return builder.toString();
	}
	
	/** 
	 * Print like a string the SpanProb
	 * @return 
	 */
	public String toString() {
		StringBuilder builder = new StringBuilder("\n "+name+"\n");
		for(State s : getStates()) {
			builder.append("\n   " + s + "\n");
		}
		for(Transition t : getTransitions()) {
			builder.append("\n	TRANSITION:  from: ("+t.getFromString()+ ")  to ("+ t.getToString() + ")  "
					+ "|  label: ["+t.getLabel1String()+" , "+t.getLabel2String()+ "]  |  prob: "+t.getProb() + "\n");
		}

		return builder.toString();
	}
}