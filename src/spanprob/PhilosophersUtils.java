package spanprob;

import org.apache.commons.math3.fraction.Fraction;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class PhilosophersUtils {

  static final Set<String> alphabetI = new HashSet<>(Arrays.asList("t", "r", "E"));
  static final SpanProb ID = Constant.ID.generateSpanProb(alphabetI);
  static final SpanProb UNIT = Constant.UNIT.generateSpanProb(alphabetI);
  static final SpanProb COUNIT = Constant.COUNIT.generateSpanProb(alphabetI);

  public static SpanProb makePF(int n) {
    SpanProb p = initializePhilosopher();
    SpanProb f = initializeFork();
    SpanProb pf = p.and(f);
    SpanProb res = pf;
    for (int i = 1; i < n; i++) {
      res = res.and(pf);
    }
    res = UNIT.and(res.mul(ID)).and(COUNIT);
    res.setName("PF"+n);
    return res;
  }

  public static SpanProb initializePhilosopher() {
    State s1 = new State("1"), s2 = new State("2"), s3 = new State("3"), s4 = new State("4");

    Transition t12 = new Transition(s1, s2, new String[]{("t")}, new String[]{("E")}, Fraction.ONE_HALF);
    Transition t23 = new Transition(s2, s3, new String[]{("E")}, new String[]{("t")}, Fraction.ONE_HALF);
    Transition t34 = new Transition(s3, s4, new String[]{("r")}, new String[]{("E")}, Fraction.ONE_HALF);
    Transition t41 = new Transition(s4, s1, new String[]{("E")}, new String[]{("r")}, Fraction.ONE_HALF);

    //self cycle
    Transition t11 = new Transition(s1, s1, new String[]{("E")}, new String[]{("E")}, Fraction.ONE_HALF);
    Transition t22 = new Transition(s2, s2, new String[]{("E")}, new String[]{("E")}, Fraction.ONE_HALF);
    Transition t33 = new Transition(s3, s3, new String[]{("E")}, new String[]{("E")}, Fraction.ONE_HALF);
    Transition t44 = new Transition(s4, s4, new String[]{("E")}, new String[]{("E")}, Fraction.ONE_HALF);

    Transition[] t = {t11, t12, t22, t23, t33, t34, t44, t41};
    return new SpanProb(s1, Arrays.asList(s1, s2, s3, s4), t);
  }

  public static SpanProb initializeFork() {
    State s1 = new State("1"), s2 = new State("2"), s3 = new State("3");

    Transition t12 = new Transition(s1, s2, new String[]{("E")}, new String[]{("t")}, Fraction.ONE_THIRD);
    Transition t21 = new Transition(s2, s1, new String[]{("E")}, new String[]{("r")}, Fraction.ONE_HALF);
    Transition t13 = new Transition(s1, s3, new String[]{("t")}, new String[]{("E")}, Fraction.ONE_THIRD);
    Transition t31 = new Transition(s3, s1, new String[]{("r")}, new String[]{("E")}, Fraction.ONE_HALF);

    //self cycle
    Transition t11 = new Transition(s1, s1, new String[]{("E")}, new String[]{("E")}, Fraction.ONE_THIRD);
    Transition t22 = new Transition(s2, s2, new String[]{("E")}, new String[]{("E")}, Fraction.ONE_HALF);
    Transition t33 = new Transition(s3, s3, new String[]{("E")}, new String[]{("E")}, Fraction.ONE_HALF);

    Transition[] t = {t11, t12, t13, t22, t21, t33, t31};
    return new SpanProb(s1, Arrays.asList(s1, s2, s3), t);
  }

}
