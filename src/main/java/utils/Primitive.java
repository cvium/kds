package utils;

import ProGAL.geom2d.Point;
import kds.Event;

import java.util.ArrayList;

/**
 * Created by clausvium on 22/12/16.
 */
public interface Primitive extends Comparable<Primitive> {
    void updatePosition(double t);
    void removeEvents();
    void setInEvent(boolean inEvent);
    void setIdx(int idx);
    int getIdx();
    ArrayList<Event> getEvents();
    int compareTo(Primitive other);
    double[] getCoeffsX();
    double[] getCoeffsY();
    Point getPoint(double t);
    //boolean inEvent();
    //Primitive getPrimitive(double t);
}
