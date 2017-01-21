package utils;

import kds.solvers.EigenSolver;
import org.ejml.data.Complex64F;

import java.util.ArrayList;
import java.util.Collections;
import java.util.NoSuchElementException;

/**
 * Created by clausvium on 02/01/17.
 */
public class Polynomial {

    public static class NoRootException extends Exception {

    }

    public static double findFirstRoot(double[] coeffs, double t, boolean inFailedEvent) throws NoRootException {
        if (coeffs.length == 0) throw new NoRootException();

        EigenSolver solver = new EigenSolver();
        Complex64F[] rootsX = solver.findRoots(coeffs, t);

        ArrayList<Double> rootsXR = new ArrayList<>();

        for (Complex64F r : rootsX) {
            if (Math.abs(r.getImaginary()) < 1e-10 && r.getReal() >= t) {
                rootsXR.add(Math.abs(r.getReal()));
            }
        }

        if (inFailedEvent) {
            Collections.sort(rootsXR);

            for (double r : rootsXR) {
                if (r-t > 1e-10) {
                    return r;
                }
            }
        } else {
            try {
                return Collections.min(rootsXR);
            } catch (NoSuchElementException e) {
                // do nothing
            }
        }

        throw new NoRootException();
    }

    public static double[] multiplication(double[] p1, double[] p2) {
        int totalLength = p1.length + p2.length - 1;
        double[] result = new double[totalLength];
        for (int i = 0; i < p1.length; i++) {
            for (int j = 0; j < p2.length; j++) {
                result[i + j] += p1[i] * p2[j];
            }
        }

        return result;
    }

    public static double[] subtract(double[] p1, double[] p2) {
        int max_length = p1.length < p2.length ? p2.length : p1.length;
        double[] result = new double[max_length];

        for (int i = 0; i < max_length; ++i) {
            if (i >= p1.length)
                result[i] = p2[i];
            else if (i >= p2.length)
                result[i] = -p1[i];
            else
                result[i] = p2[i] - p1[i];
        }

        return result;
    }

    public static double[] add(double[] p1, double[] p2) {
        int max_length = p1.length < p2.length ? p2.length : p1.length;
        double[] result = new double[max_length];

        for (int i = 0; i < max_length; ++i) {
            if (i >= p1.length)
                result[i] = p2[i];
            else if (i >= p2.length)
                result[i] = p1[i];
            else
                result[i] = p2[i] + p1[i];
        }

        return result;
    }
}
