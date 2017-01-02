package utils;

import kds.solvers.EigenSolver;
import org.ejml.data.Complex64F;

import java.util.ArrayList;
import java.util.Collections;
import java.util.NoSuchElementException;

/**
 * Created by clausvium on 02/01/17.
 */
public class RootFinder {

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
}
