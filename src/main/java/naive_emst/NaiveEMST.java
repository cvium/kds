package naive_emst;

import dcel.HalfEdge;
import kds.KDSPoint;

import java.util.ArrayList;

/**
 * Created by cvium on 07-12-2016.
 */
public class NaiveEMST {
    ArrayList<KDSPoint> points;
    ArrayList<HalfEdge> edges;
    private double weight;

    public NaiveEMST() {

    }

    public NaiveEMST(ArrayList<KDSPoint> points) {
        this.points = points;
    }

    public void compute() {



    }

    public ArrayList<KDSPoint> getPoints() {
        return points;
    }

    public void setPoints(ArrayList<KDSPoint> points) {
        this.points = points;
    }

    public ArrayList<HalfEdge> getEdges() {
        return edges;
    }
}
