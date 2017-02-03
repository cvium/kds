package naive_emst;

import ProGAL.geom2d.viewer.J2DScene;
import convex_dt.ConvexDT;
import convex_dt.shapes.CircleShape;
import dcel.DCEL;
import dcel.HalfEdge;
import kds.KDSPoint;
import utils.UnionSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Created by cvium on 07-12-2016.
 */
public class NaiveEMST {
    ArrayList<KDSPoint> points;
    ArrayList<HalfEdge> edges;
    private double weight;
    ConvexDT dt;
    CircleShape circle;

    public NaiveEMST() {
        circle = new CircleShape();
        this.edges = new ArrayList<>();
    }

    public NaiveEMST(ArrayList<KDSPoint> points) {
        circle = new CircleShape();
        this.points = points;
        this.edges = new ArrayList<>();
    }

    public void compute(double t) throws Exception {
        // first we compute the DT
        dt = new ConvexDT(points, circle, null);
        dt.setTime(t);
        dt.delaunay(points);
        //dt.getDcel().draw(dt.getScene());

        kruskal(dt.getDcel());
    }

    public void kruskal(DCEL dcel) {
        // Sort the edges by weight in nondecreasing order
        ArrayList<HalfEdge> edges = dcel.getEdges();
        System.out.println("Number of edges in dcel: " + edges.size());
        edges.sort((o1, o2) -> {
            if (o1.getLength() < o2.getLength())
                return -1;
            if (o1.getLength() > o2.getLength())
                return 1;
            else return 0;
        });

        UnionSet unionSet = new UnionSet(points.size());
        // run greedy algorithm

        for (HalfEdge e : edges) {
            int v = e.getOrigin().getIdx();
            int w = e.getDestination().getIdx();
            if (!unionSet.connected(v, w)) {
                unionSet.union(v, w);
                this.edges.add(e);
            }
        }
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
