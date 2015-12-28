package kds;


/**
 * Created by clausvium on 28/12/15.
 */
public interface Solver<ReturnType> {
    public ReturnType[] findRoots(double[] coefficients, double t);
}
