package dcel;

import ProGAL.geom2d.viewer.J2DScene;
import kds.KDSPoint;

import java.util.ArrayList;

/**
 * Created by cvium on 28-11-2016.
 */
public class DCEL {
    private ArrayList<Face> faces;
    private ArrayList<HalfEdge> edges;
    private ArrayList<Vertex> vertices;

    public DCEL() {
        faces = new ArrayList<>();
        edges = new ArrayList<>();
        vertices = new ArrayList<>();
    }

    public HalfEdge createEdge(KDSPoint a, KDSPoint b) {
        HalfEdge e = new HalfEdge(a, b);
        e.setFace(createFace(e));
        createTwin(e);
        edges.add(e);
        return e;
    }

    public HalfEdge createEdge(KDSPoint a, KDSPoint b, Face f) {
        HalfEdge e = new HalfEdge(a, b);
        e.setFace(f);
        createTwin(e);
        edges.add(e);
        return e;
    }

    public HalfEdge createEdge() {
        HalfEdge e = new HalfEdge();
        createTwin(e);
        edges.add(e);
        return e;
    }

    public void deleteEdge(HalfEdge e) {
        // remove e from its prev and next edges, TODO: IS THAT ENOUGH?
        if (e.getFace().getOuterComponent() == e) {
            if (e.getNext() != null)
                e.getFace().setOuterComponent(e.getNext());
            else if (e.getPrev() != null)
                e.getFace().setOuterComponent(e.getPrev());
            else
                faces.remove(e.getFace());
        }

        if (e.getPrev() != null) e.getPrev().setNext(null);
        if (e.getNext() != null) e.getNext().setPrev(null);
        // have to remove the twin as well
        if (e.getTwin().getPrev() != null) e.getTwin().getPrev().setNext(null);
        if (e.getTwin().getNext() != null) e.getTwin().getNext().setPrev(null);
        edges.remove(e);
        edges.remove(e.getTwin());
    }

    private Face createFace(HalfEdge e) {
        Face f = new Face(e);
        faces.add(f);
        return f;
    }

    private void createTwin(HalfEdge e) {
        // create twin if we don't have it. Some annoying bookkeeping
        HalfEdge twin = e.getTwin();
        if (twin == null) {
            twin = new HalfEdge(e.getDestination(), e.getOrigin());
            twin.setFace(createFace(e));
            edges.add(twin);
            e.setTwin(twin);
            twin.setTwin(e);
            if (e.getNext() != null)
                twin.setPrev(e.getNext().getTwin());
            if (e.getPrev() != null)
                twin.setNext(e.getPrev().getTwin());
        }
    }

    public void draw(J2DScene scene) {
        for (Face f : faces) {
            f.draw(scene);
        }
    }
}
