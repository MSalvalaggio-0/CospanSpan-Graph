package spanprob;

public class Main {

  public static void main(String[] args) {
    long time1 = System.currentTimeMillis();
    SpanProb pf = PhilosophersUtils.makePF(5);
    System.out.println("PF5: " + (System.currentTimeMillis() - time1) +"ms");
    printInfo(pf);
  }

  /**
   * Printing method
   * @param sp
   */
  public static void printInfo(SpanProb sp) {
    System.out.println(sp.getName() + " Initial: " + sp.getInitialState());
    System.out.println(sp.getName() + " #states: " + sp.getStates().size());
    Utils.SpanProbToCSVForGephi(sp);
    Utils.SpanProbTxt(sp);
    Utils.SpanProbMatrixToCSV(sp.matrixToString(sp.buildMatrix()));
  }

}





