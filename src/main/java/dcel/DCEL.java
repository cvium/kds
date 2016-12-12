package dcel;

import ProGAL.geom2d.viewer.J2DScene;
import kds.KDSPoint;

import java.util.ArrayList;

import static java.lang.Thread.sleep;

/**
 * Created by cvium on 28-11-2016.
 */
public class DCEL {
    private ArrayList<Face> faces;
    private ArrayList<HalfEdge> edges;
    private ArrayList<Vertex> vertices;
    private J2DScene scene;

    public DCEL(J2DScene scene) {
        faces = new ArrayList<>();
        edges = new ArrayList<>();
        vertices = new ArrayList<>();
        this.scene = scene;
    }

    public HalfEdge createEdge(KDSPoint a, KDSPoint b) {
        HalfEdge e = new HalfEdge(a, b);
        //e.setFace(createFace(e));
        createTwin(e);
        //edges.add(e);
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

    public HalfEdge connect(HalfEdge a, HalfEdge b) {
        HalfEdge c = new HalfEdge(a.getDestination(), b.getOrigin());
        HalfEdge c_twin = new HalfEdge(b.getOrigin(), a.getDestination());
        edges.add(c);
        edges.add(c_twin);

        // add prev and next
        c.setPrev(a);
        c.setNext(b);

        c.setTwin(c_twin);
        c_twin.setTwin(c);
        // remove the face because we'll most likely get 2 new ones
        faces.remove(a.getFace());
        // create two new faces
        Face c_face = createFace(c);
        Face c_twin_face = createFace(c_twin);
        c.setFace(c_face);
        c_twin.setFace(c_twin_face);
        // connect the twin
        if (a.getNext() != null) {
            c_twin.setNext(a.getNext());
            a.getNext().setPrev(c_twin);
        } else if (c.getPrev() != null){
            c_twin.setNext(c.getPrev().getTwin());
            c.getPrev().getTwin().setPrev(c_twin);
        }
        if (b.getPrev() != null) {
            c_twin.setPrev(b.getPrev());
            b.getPrev().setNext(c_twin);
        } else if (c.getNext() != null){
            c_twin.setPrev(c.getNext().getTwin());
            c.getNext().getTwin().setNext(c_twin);
        }
        // update a and b's prev/next
        a.setNext(c);
        b.setPrev(c);
        // set faces
        HalfEdge tmp = c.getNext();
        int direction = 1; // 1 == next
        while (true) {
            if (tmp == c) break;
            if (tmp == null && direction == 1) {
                // this means the face is not closed, so we have to start over
                direction = 0;
                tmp = c.getPrev();
                if (tmp == null) break;
            } else if (tmp == null) {
                break;
            }
            tmp.setFace(c_face);
            tmp = direction == 1 ? tmp.getNext() : tmp.getPrev();
        }

        tmp = c_twin.getNext();
        direction = 1; // 1 == next
        while (true) {
            if (tmp == c_twin) break;
            if (tmp == null && direction == 1) {
                // this means the face is not closed, so we have to start over
                direction = 0;
                tmp = c_twin.getPrev();
                if (tmp == null) break;
            } else if (tmp == null) {
                break;
            }
            tmp.setFace(c_twin_face);
            tmp = direction == 1 ? tmp.getNext() : tmp.getPrev();
        }

        //scene.removeAllShapes();
        c_face.draw(scene);
        scene.repaint();
        //scene.removeAllShapes();
        return c;
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
            //twin.setFace(createFace(e));
            edges.add(twin);
            e.setTwin(twin);
            twin.setTwin(e);
        }
    }

    public void draw(J2DScene scene) {
        for (Face f : faces) {
            f.draw(scene);
        }
    }
}
