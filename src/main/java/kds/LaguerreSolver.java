package kds;

import org.apache.commons.math3.complex.Complex;

/**
 * Created by clausvium on 28/12/15.
 */
public class LaguerreSolver implements Solvers<Complex>{
    private org.apache.commons.math3.analysis.solvers.LaguerreSolver solver;
    public LaguerreSolver() {
        solver = new org.apache.commons.math3.analysis.solvers.LaguerreSolver();
    }
    public Complex[] findRoots(double[] coefficients, double t) {
        return solver.solveAllComplex(coefficients, t);
    }
}
