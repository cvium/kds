package kds;


/**
 * Created by clausvium on 28/12/15.
 */
public interface Solvers<ReturnType> {
    public ReturnType[] findRoots(double[] coefficients, double t);
}
