package sortedlist;

import kds.*;
import kds.solvers.EigenSolver;
//import org.apache.commons.math3.complex.Complex;
import org.ejml.data.Complex64F;
import utils.Primitive;

import java.util.ArrayList;
import java.util.Collections;
import java.util.NoSuchElementException;

/**
 * Created by clausvium on 21/12/15.
 */
public class SortedList<P extends Primitive> implements KDS<P, SortedEvent<P>> {
    private EventQueue<SortedEvent<P>> eq;
    private ArrayList<P> primitives;
    private EigenSolver solver;

    public SortedList() {
        this.primitives = new ArrayList<>();
        this.eq = new EventQueue<>();
        this.solver = new EigenSolver();
        initialize(0);
    }

    public SortedList(double starttime, ArrayList<P> primitives) {
        this.primitives = primitives;
        this.eq = new EventQueue<>();
        this.solver = new EigenSolver();
        initialize(starttime);
    }

    @Override
    public EventQueue<SortedEvent<P>> getEventQueue() {
        return eq;
    }

    public ArrayList<P> getPrimitives() {
        return primitives;
    }

    @Override
    public boolean audit(double t) {
        ArrayList<Primitive> ps = new ArrayList<>();
        for (P p : primitives) {
            p.updatePosition(t);
            ps.add(p);
        }
        Collections.sort(ps);
        for (int i = 0; i < primitives.size(); ++i) {
            if (ps.get(i) != primitives.get(i)) {
                System.out.println("Is: " + primitives.get(i).getPoint(t).x() + " (" + primitives.get(i).getIdx() + ")"
                        + " should be: " + ps.get(i).getPoint(t).x() + " (" + ps.get(i).getIdx() + ")");
                return false;
            }
        }
        return true;
    }

    @Override
    public void initialize(double starttime) {
        for (P p : primitives) {
            p.updatePosition(starttime);
        }
        Collections.sort(primitives);

        for (int i = 0; i < primitives.size() - 1; ++i) {
            P a = primitives.get(i);
            a.setIdx(i);
            P b = primitives.get(i+1);
            b.setIdx(i+1);

            createEvent(starttime, a, b, false);
        }
    }

    Complex64F[] findRoots(double t, double[] ac, double[] bc) {
        double[] coeffs = new double[ac.length];

        for (int i = 0; i < ac.length; ++i) {
            coeffs[i] = ac[i] - bc[i];
        }
        return solver.findRoots(coeffs, t);
    }


    double computeFailureTime(double t, P a, P b, boolean inFailedEvent) {
        double[] aCoeffsX = a.getCoeffsX();
        double[] bCoeffsX = b.getCoeffsX();

        Complex64F[] rootsX = new Complex64F[0];

        if (aCoeffsX.length > 0 && bCoeffsX.length > 0) {
            rootsX = findRoots(t, aCoeffsX, bCoeffsX);
        }

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

        return -1;
    }

    @Override
    public void update(SortedEvent<P> event, double t) {
        P a = event.getA();
        P b = event.getB();

        int start = a.getIdx() > 0 ? a.getIdx() - 1 : a.getIdx();
        int end = b.getIdx() < primitives.size() - 1 ? b.getIdx() + 1 : primitives.size();
        for (int i = start; i < end; ++i) {
            primitives.get(i).updatePosition(t);
        }

        a.removeEvents();
        b.removeEvents();

        Collections.swap(primitives, a.getIdx(), b.getIdx());
        a.setIdx(a.getIdx() + 1);
        b.setIdx(b.getIdx() - 1);

        a.setInEvent(true);
        b.setInEvent(true);
        if (b.getIdx() > 0) {
            P p = primitives.get(b.getIdx() - 1);
            p.removeEvents();
            createEvent(t, p, b, false);
        }
        if (a.getIdx() < primitives.size() - 1) {
            P p = primitives.get(a.getIdx() + 1);
            createEvent(t, a, p, false);
        }
        // inFailedEvent seems to be used to make sure it chooses the root _after_ current time t
        // TODO improve, confusing variable, why not just always force it to pick smallest root after t??
        createEvent(t, b, a, true);
    }

    private void createEvent(double t, P a, P b, boolean inFailedEvent) {
        double failureTime = computeFailureTime(t, a, b, inFailedEvent);

        if (failureTime >= t) {
            SortedEvent<P> event = new SortedEvent<>(this, failureTime, a, b);
            a.getEvents().add(event);
            eq.add(event);
        }
    }
}