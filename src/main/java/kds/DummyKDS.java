package kds;

import kds.solvers.EigenSolver;
import sortedlist.SortedEvent;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by cvium on 12-01-2016.
 */
public class DummyKDS implements KDS<BoundedKDSPoint, Event<BoundedKDSPoint>> {
    EventQueue<SortedEvent> eq;
    int degree = 3;
    int numPoints = 10;
    double starttime;
    ArrayList<BoundedKDSPoint> points;

    public DummyKDS(double starttime, int numPoints, int degree) {
        this.starttime = starttime;
        this.numPoints = numPoints;
        this.degree = degree;
        this.eq = new EventQueue<>();
        points = new ArrayList<>();
        Random rand = new Random();
        for (int i = 0; i < numPoints; ++i) {
            double[] coeffsX = new double[degree];
            double[] coeffsY = new double[degree];

            for (int j = 0; j < degree; ++j) {
                coeffsX[j] = -1 + (1 + 1) * rand.nextDouble();//
                coeffsY[j] = -1 + (1 + 1) * rand.nextDouble();
            }
            points.add(new BoundedKDSPoint(coeffsX, coeffsY));
        }
        initialize(starttime);
    }

    @Override
    public boolean audit(double t) {
        return false;
    }

    @Override
    public void initialize(double starttime) {

    }

    @Override
    public void update(Event event, double t) {

    }

    @Override
    public EventQueue<Event<BoundedKDSPoint>> getEventQueue() {
        return null;
    }

    @Override
    public ArrayList<BoundedKDSPoint> getPoints() {
        return points;
    }
}
