package dcel;

import kds.KDSPoint;

/**
 * Created by cvium on 28-11-2016.
 */
public class Vertex {
    private KDSPoint point;
    private HalfEdge halfEdge;

    public Vertex() {

    }
    public Vertex(KDSPoint point) {
        this.point = point;
    }

    public HalfEdge getHalfEdge() {
        return halfEdge;
    }

    public void setHalfEdge(HalfEdge halfEdge) {
        this.halfEdge = halfEdge;
    }

    public KDSPoint getPoint() {
        return point;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vertex)) return false;

        Vertex vertex = (Vertex) o;

        if (point != null ? !point.equals(vertex.point) : vertex.point != null) return false;
        return halfEdge != null ? halfEdge.equals(vertex.halfEdge) : vertex.halfEdge == null;
    }

    @Override
    public int hashCode() {
        int result = point != null ? point.hashCode() : 0;
        result = 31 * result + (halfEdge != null ? halfEdge.hashCode() : 0);
        return result;
    }
}
