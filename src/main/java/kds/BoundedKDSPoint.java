package kds;

/**
 * Created by cvium on 12-01-2016.
 */
public class BoundedKDSPoint extends KDSPoint {
    double XBoundary = 10;
    double YBoundary = 10;

    public BoundedKDSPoint(double[] coeffsX, double[] coeffsY) {
        super(coeffsX, coeffsY);
    }


}
