package spanprob;

public class State implements Comparable<State> {

	private final String state;

	public State(String state) {
		this.state = state;
	}

	public String getState() {
		return state;
	}

	public String toString() {
		return state;
	}

	@Override
	public boolean equals(Object s) {
		return s instanceof State && state.equals(((State)s).state);
	}

	@Override
	public int hashCode() {
		return state.hashCode();
	}

	public State product(State other) {
		if(state.isEmpty()) return new State(other.state);
		if(other.state.isEmpty()) return new State(state);
		return new State(state + "," + other.state);
	}

	@Override
	public int compareTo(State o) {
		return state.compareTo(o.state);
	}
}








