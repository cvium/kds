package sortedList;

import kds.*;
import org.apache.commons.math3.analysis.solvers.LaguerreSolver;
import org.apache.commons.math3.complex.Complex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.NoSuchElementException;

/**
 * Created by clausvium on 21/12/15.
 */
public class SortedList implements KDS<SortedEvent> {
    EventQueue<SortedEvent> eq;
    ArrayList<KDSPoint> points;
    LaguerreSolver solver;

    public SortedList() {
        this.points = new ArrayList<>();
        this.eq = new EventQueue<>();
        this.solver = new LaguerreSolver();
        initialize();
    }

    public SortedList(ArrayList<KDSPoint> points) {
        this.points = points;
        this.eq = new EventQueue<>();
        this.solver = new LaguerreSolver();
        initialize();
    }

    @Override
    public boolean audit(double t) throws Exception {
        ArrayList<KDSPoint> ps = new ArrayList<>();
        for (KDSPoint p : points) {
            p.updatePosition(t);
            ps.add(p);
        }
        Collections.sort(ps);
        for (int i = 0; i < points.size(); ++i) {
            if (ps.get(i) != points.get(i)) {
                System.out.println("Is: " + points.get(i).getPoint(t).x() + " (" + points.get(i).getIdx() + ")"
                        + " should be: " + ps.get(i).getPoint(t).x() + " (" + ps.get(i).getIdx() + ")");
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

            createCertificate(0.0, a, b, false);
        }
    }

    Complex[] findRoots(double t, double[] ac, double[] bc) {
        double[] coeffs = new double[ac.length];

        for (int i = 0; i < ac.length; ++i) {
            coeffs[i] = ac[i] - bc[i];
        }
        return solver.solveAllComplex(coeffs, t);
    }


    double computeFailureTime(double t, KDSPoint a, KDSPoint b, boolean inFailedEvent) {
        double[] aCoeffsX = a.getCoeffsX();
        double[] bCoeffsX = b.getCoeffsX();

        Complex[] rootsX = new Complex[0];

        if (aCoeffsX.length > 0 && bCoeffsX.length > 0) {
            rootsX = findRoots(t, aCoeffsX, bCoeffsX);
        }

        ArrayList<Double> rootsXR = new ArrayList<>();

        for (Complex r : rootsX) {
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

        return -1;
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
        a.setIdx(a.getIdx() + 1);
        b.setIdx(b.getIdx() - 1);

        a.setInEvent(true);
        b.setInEvent(true);
        if (b.getIdx() > 0) {
            KDSPoint p = points.get(b.getIdx() - 1);
            p.removeCertificates();
            createCertificate(t, p, b, false);
        }
        if (a.getIdx() < points.size() - 1) {
            KDSPoint p = points.get(a.getIdx() + 1);
            createCertificate(t, a, p, false);
        }
        createCertificate(t, b, a, true);

        //eq.remove(event);
    }

    private void createCertificate(double t, KDSPoint a, KDSPoint b, boolean inFailedEvent) {
        Certificate<SortedEvent> cert = new Certificate<>();
        cert.setFailureTime(computeFailureTime(t, a, b, inFailedEvent));
        // if failure time is less than t, then it will never fail in the future
        if (cert.getFailureTime() >= t) {
            a.getCertificates().add(cert);
            SortedEvent<KDSPoint> e = new SortedEvent<>(cert, this, a, b);
            eq.add(e);
        }
    }
}