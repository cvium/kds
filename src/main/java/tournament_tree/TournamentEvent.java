package tournament_tree;

import kds.solvers.EigenSolver;
import org.ejml.data.Complex64F;
import utils.Primitive;
import utils.RootFinder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.NoSuchElementException;

/**
 * Created by clausvium on 22/12/16.
 */
public class TournamentEvent<P extends Primitive> extends kds.Event<P>{

    private TournamentNode<P> node;
    private TournamentTreeWinner<P> winnerFunction;
    private boolean inFailedEvent;

    public TournamentEvent(TournamentNode<P> node, TournamentTreeWinner<P> winnerFunction, boolean inFailedEvent) {
        this.node = node;
        this.winnerFunction = winnerFunction;
        this.inFailedEvent = inFailedEvent;
    }

    public TournamentNode<P> getNode() {
        return node;
    }

    public void setNode(TournamentNode<P> node) {
        this.node = node;
    }

    @Override
    public void process(double t) {
        System.out.println("PROCESSING");
    }

    @Override
    public void computeFailureTime(EigenSolver solver, double t) {

        TournamentNode<P> leftChild = node.getLeftChild();
        TournamentNode<P> rightChild = node.getRightChild();

        if (leftChild == null || rightChild == null || leftChild.isNull() || rightChild.isNull()) {
            super.setFailureTime(-1);
            return;
        }

        double[] aCoeffsX = leftChild.getWinner().getCoeffsX();
        double[] aCoeffsY = leftChild.getWinner().getCoeffsY();
        double[] bCoeffsX = rightChild.getWinner().getCoeffsX();
        double[] bCoeffsY = rightChild.getWinner().getCoeffsY();


        double[] p1_coeffs = getDistCoeffs(winnerFunction.getP().getCoeffsX(), winnerFunction.getP().getCoeffsY(),
                aCoeffsX, aCoeffsY);

        double[] p2_coeffs = getDistCoeffs(winnerFunction.getP().getCoeffsX(), winnerFunction.getP().getCoeffsY(),
                bCoeffsX, bCoeffsY);

        double[] coeffs = new double[p1_coeffs.length];

        for (int i = 0; i < p1_coeffs.length; ++i) {
            coeffs[i] = p1_coeffs[i] - p2_coeffs[i];
        }

        try {
            double failureTime = RootFinder.findFirstRoot(coeffs, t, this.inFailedEvent);
            super.setFailureTime(failureTime);
        } catch (RootFinder.NoRootException ex) {
            // event is invalid
            this.setValid(false);
            super.setFailureTime(-1);
        }
    }

    double[] getDistCoeffs(double[] p1_coeffsX, double[] p1_coeffsY, double[] p2_coeffsX, double[] p2_coeffsY) {
        double[] x = expand(p1_coeffsX, p2_coeffsX);
        double[] y = expand(p1_coeffsY, p2_coeffsY);

        double[] res = new double[x.length];

        for (int i = 0; i < x.length; ++i) {
            res[i] = x[i] + y[i];
        }

        return res;
    }

    double[] expand(double[] p1_coeffs, double[] p2_coeffs) {
        double first = Math.pow(p1_coeffs[0], 2) - 2 * p1_coeffs[0] * p2_coeffs[0] + Math.pow(p2_coeffs[0], 2);
        double second = 2 * p1_coeffs[0] * p1_coeffs[1] - 2 * p1_coeffs[0] * p2_coeffs[1] - 2 * p1_coeffs[1] *
                p2_coeffs[0] + 2 * p2_coeffs[0] * p2_coeffs[1];
        second = 2 * (p1_coeffs[0] * (p1_coeffs[1] - p2_coeffs[1]) - p2_coeffs[0] * (p1_coeffs[1] - p2_coeffs[0]));
        double third = Math.pow(p1_coeffs[1], 2) - 2 * p1_coeffs[1] * p2_coeffs[1] + Math.pow(p2_coeffs[1], 2);

        return new double[]{first, second, third};
    }

    Complex64F[] findRoots(EigenSolver solver, double t, double[] ac, double[] bc) {
        double[] coeffs = new double[ac.length];

        for (int i = 0; i < ac.length; ++i) {
            coeffs[i] = ac[i] - bc[i];
        }
        return solver.findRoots(coeffs, t);
    }

}
