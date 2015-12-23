package sortedList;

import ProGAL.geom2d.Point;
import kds.*;
import org.ejml.data.Complex64F;

import java.util.ArrayList;
import java.util.Collections;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * Created by clausvium on 21/12/15.
 */
public class SortedList implements KDS<SortedEvent> {
    EventQueue eq;
    ArrayList<KDSPoint> points;
    //LaguerreSolver solver;

    public SortedList() {
        this.points = new ArrayList<>();
        this.eq = new EventQueue();
        //this.solver = new LaguerreSolver();
        initialize();
    }

    public SortedList(ArrayList<KDSPoint> points) {
        this.points = points;
        this.eq = new EventQueue();
        //this.solver = new LaguerreSolver();
        initialize();
    }

    @Override
    public boolean audit(double t) {
        ArrayList<KDSPoint> ps = new ArrayList<>();
        for (KDSPoint p : points) {
            p.updatePosition(t);
            ps.add(p);
        }
        Collections.sort(ps);
        for (int i = 0; i < points.size(); ++i) {
            if (ps.get(i) != points.get(i)) {
                System.out.println("Is: " + points.get(i).getPoint(t).x() + " should be: " + ps.get(i).getPoint(t).x());
                return false;
            }
        }
        return true;
    }

    @Override
    public void initialize() {
        Collections.sort(points);

        for (int i = 0; i < points.size() - 1; ++i) {
            KDSPoint a = points.get(i);
            a.setIdx(i);
            KDSPoint b = points.get(i+1);
            b.setIdx(i+1);

            createCertificate(0.0, a, b);
        }
    }

    Complex64F[] findRoots(double[] ac, double[] bc) {
        int minSize, maxSize;
        boolean is_a_greater;
        if (ac.length <= bc.length) {
            minSize = ac.length;
            maxSize = bc.length;
            is_a_greater = false;
        } else {
            minSize = bc.length;
            maxSize = ac.length;
            is_a_greater = true;
        }

        double[] coeffs = new double[maxSize];

        for (int i = 0; i < maxSize; ++i) {
            if (i < minSize) {
                coeffs[i] = ac[i] - bc[i];
            } else {
                coeffs[i] = is_a_greater ? ac[i] : -bc[i];
            }
        }
        return PolynomialRootFinder.findRoots(coeffs);
    }


    double computeFailureTime(double t, KDSPoint a, KDSPoint b) {
        double[] aCoeffsX = a.getCoeffsX();
        double[] bCoeffsX = b.getCoeffsX();

        Complex64F[] rootsX = new Complex64F[0];

        if (aCoeffsX.length > 0 && bCoeffsX.length > 0) {
            rootsX = findRoots(aCoeffsX, bCoeffsX);
        }

        ArrayList<Double> rootsXR = new ArrayList<>();

        for (Complex64F r : rootsX) {
            if (r.isReal() && r.getReal() > t) {
                rootsXR.add(r.getReal());
            }
        }

        Collections.sort(rootsXR);

        try {
            return Collections.min(rootsXR);
        } catch (NoSuchElementException e) {
            return -1;
        }
    }

    @Override
    public void update(SortedEvent event, double t) {
        for (KDSPoint p : points) {
            p.updatePosition(t);
        }
        KDSPoint a = (KDSPoint) event.getA();
        KDSPoint b = (KDSPoint) event.getB();

        a.removeCertificates();
        b.removeCertificates();

        Collections.swap(points, a.getIdx(), b.getIdx());
        int aidx = a.getIdx();
        a.setIdx(b.getIdx());
        b.setIdx(aidx);

        a.setInEvent(true);
        b.setInEvent(true);
        if (b.getIdx() > 0) {
            KDSPoint p = points.get(b.getIdx() - 1);
            p.removeCertificates();
            createCertificate(t, p, b);
        }
        if (a.getIdx() < points.size() - 2) {
            KDSPoint p = points.get(a.getIdx() + 1);
            createCertificate(t, a, p);
        }
        createCertificate(t, b, a);
    }

    private void createCertificate(double t, KDSPoint a, KDSPoint b) {
        Certificate<SortedEvent> cert = new Certificate<>();
        cert.setFailureTime(computeFailureTime(t, a, b));
        // if failure time is less than 0, then it will never fail in the future
        if (cert.getFailureTime() > t) {
            b.getCertificates().add(cert);
            SortedEvent<KDSPoint> e = new SortedEvent<>(cert, this, a, b);
            eq.queue.add(e);
        }
    }
}