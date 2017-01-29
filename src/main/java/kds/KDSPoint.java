package kds;

import ProGAL.geom2d.*;
import ProGAL.geom2d.Point;
import ProGAL.geom2d.viewer.J2DScene;
import dcel.HalfEdge;
import utils.Primitive;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by clausvium on 21/12/15.
 */
public class KDSPoint implements Primitive {
    double[] coeffsX;
    double[] coeffsY;
    int idx;
    boolean inEvent;
    ArrayList<Event> events;
    double x;
    double y;
    Point p;
    Circle c;
    double lastUpdated;
    HalfEdge incidentEdge;

    public ArrayList<Event> getEvents() {
        return events;
    }

    public void setEvents(ArrayList<Event> events) {
        this.events = events;
    }

    public void removeEvents() {
        for (Event e : events) {
            //c.setFailureTime(Double.MAX_VALUE);
            e.setValid(false);
        }
        this.events.clear();
    }

    public boolean isInEvent() {
        return inEvent;
    }

    public void setInEvent(boolean inEvent) {
        this.inEvent = inEvent;
    }

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    @Override
    public int compareTo(Primitive other) {
        KDSPoint othercast = (KDSPoint) other;
        double xdiff = Math.abs(this.x - othercast.x);
        double ydiff = Math.abs(this.y - othercast.y);
        if (xdiff <= 1e-10) {
            if (ydiff <= 1e-10) {
                return 0;
            } else {
                return this.y < othercast.y ? -1 : 1;
            }
        } else {
            return this.x < othercast.x ? -1 : 1;
        }
    }

    public KDSPoint(double[] coeffsX, double[] coeffsY) {
        this.x = coeffsX[0];
        this.y = coeffsY[0];
        this.coeffsX = coeffsX;
        this.coeffsY = coeffsY;
        this.events = new ArrayList<>();
        this.p = new Point(x, y);
        this.c = new Circle(p, 0.01);
        lastUpdated = 0;
    }

    public KDSPoint() {

    }

    public KDSPoint(Point point) {
        this.x = point.x();
        this.y = point.y();
        this.coeffsX = new double[]{point.x()};
        this.coeffsX = new double[]{point.y()};
        this.events = new ArrayList<>();
        this.p = point;
        this.c = new Circle(point, 0.01);
        lastUpdated = 0;
    }

    public double[] getCoeffsX() {
        return coeffsX;
    }

    public double[] getCoeffsY() {
        return coeffsY;
    }

    public void setCoeffsX(double[] coeffsX) {
        this.coeffsX = coeffsX;
    }

    public void setCoeffsY(double[] coeffsY) {
        this.coeffsY = coeffsY;
    }

    public Point getPoint(double t) {
        if (lastUpdated < t) {
            double new_x = 0;

            for (int i = 0; i < coeffsX.length; ++i) {
                new_x += coeffsX[i] * Math.pow(t, i);
            }

            double new_y = 0;

            for (int i = 0; i < coeffsY.length; ++i) {
                new_y += coeffsY[i] * Math.pow(t, i);
            }

            this.x = new_x;
            this.y = new_y;
            p.setCoord(0, x);
            p.setCoord(1, y);
            lastUpdated = t;
        }

        return p;
    }

    @Override
    public double getDistance(Primitive other) {
        return getPoint().getSquaredDistance(other.getPoint());
    }

    @Override
    public Point getPoint() {
        return p;
    }

    public void swap(KDSPoint other) {
        double[] old_coeffsX = other.coeffsX;
        double[] old_coeffsY = other.coeffsY;
        int old_idx = other.idx;
        boolean old_inEvent = other.inEvent;
        ArrayList<Event> old_events = new ArrayList<>(other.events);

        other.coeffsX = this.coeffsX;
        other.coeffsY = this.coeffsY;
        other.idx = this.idx;
        other.inEvent = this.inEvent;
        other.events = new ArrayList<>(this.events);

        this.coeffsX = old_coeffsX;
        this.coeffsY = old_coeffsY;
        this.idx = old_idx;
        this.inEvent = old_inEvent;
        this.events = new ArrayList<>(old_events);
    }

    public void draw(J2DScene scene, double t, Color c) {
        /*Circle circ = new Circle(getPoint(t), 0.01);
        scene.addShape(circ, c);*/
        getPoint(t).toScene(scene, 0.01, c);
        this.inEvent = false;
    }

    public void draw(J2DScene scene, double t) {
        Color color = isInEvent() ? java.awt.Color.RED : java.awt.Color.BLUE;
        c.setCenter(getPoint(t));
        scene.addShape(c, color);
        this.inEvent = false;
    }

    public void updatePosition(double t) {
        getPoint(t);
    }


    public double getX() {
        updatePosition(0);
        return x;
    }

    public double getX(double t) {
        updatePosition(t);
        return x;
    }

    public double getY() {
        updatePosition(0);return y;
    }

    public double getY(double t) {
        updatePosition(t);
        return y;
    }

    public HalfEdge getIncidentEdge() {
        //assert incidentEdge.getOrigin() == this;
        return incidentEdge;
    }

    public void setIncidentEdge(HalfEdge incidentEdge) {
        assert incidentEdge.getOrigin() == this;
        this.incidentEdge = incidentEdge;
    }
}
