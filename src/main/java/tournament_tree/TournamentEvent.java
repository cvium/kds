package tournament_tree;

import kds.solvers.EigenSolver;
import org.ejml.data.Complex64F;
import utils.Primitive;
import utils.Polynomial;

/**
 * Created by clausvium on 22/12/16.
 */
public class TournamentEvent<P extends Primitive> extends kds.Event<P> {

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
        leftChild.getWinner().updatePosition(t);
        rightChild.getWinner().updatePosition(t);

        double[] aCoeffsX = leftChild.getWinner().getCoeffsX();
        double[] aCoeffsY = leftChild.getWinner().getCoeffsY();
        double[] bCoeffsX = rightChild.getWinner().getCoeffsX();
        double[] bCoeffsY = rightChild.getWinner().getCoeffsY();


        double[] p1_coeffs = getDistCoeffs(winnerFunction.getP().getCoeffsX(), winnerFunction.getP().getCoeffsY(),
                aCoeffsX, aCoeffsY);

        double[] p2_coeffs = getDistCoeffs(winnerFunction.getP().getCoeffsX(), winnerFunction.getP().getCoeffsY(),
                bCoeffsX, bCoeffsY);

        double[] coeffs = Polynomial.subtract(p1_coeffs, p2_coeffs);

        try {
            double failureTime = Polynomial.findFirstRoot(coeffs, t, this.inFailedEvent);
            super.setFailureTime(failureTime);
        } catch (Polynomial.NoRootException ex) {
            // event is invalid
            this.setValid(false);
            super.setFailureTime(-1);
        }
    }

    double[] getDistCoeffs(double[] p1_coeffsX, double[] p1_coeffsY, double[] p2_coeffsX, double[] p2_coeffsY) {

        // dist^2 = (p2_x - p1_x)^2 + (p2_y - p1_y)^2
        double[] x = Polynomial.subtract(p1_coeffsX, p2_coeffsX);
        double[] y = Polynomial.subtract(p1_coeffsY, p2_coeffsY);

        double[] x_squared = Polynomial.multiplication(x, x);
        double[] y_squared = Polynomial.multiplication(y, y);

        return Polynomial.add(x_squared, y_squared);
    }
}
