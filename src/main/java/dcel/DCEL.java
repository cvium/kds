package dcel;

import ProGAL.dataStructures.Pair;
import ProGAL.geom2d.viewer.J2DScene;
import kds.KDSPoint;

import java.awt.*;
import java.util.ArrayList;

import static java.lang.Thread.sleep;
import static utils.Helpers.*;

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
        a.setIncidentEdge(e);
        //e.setFace(createFace(e));
        createTwin(e);
        edges.add(e);
        return e;
    }

    public HalfEdge createEdge(KDSPoint a, KDSPoint b, Face f) {
        HalfEdge e = new HalfEdge(a, b);
        a.setIncidentEdge(e);
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
        if (a.getDestination().getIncidentEdge() == null)
            a.getDestination().setIncidentEdge(c);
        if (b.getOrigin().getIncidentEdge() == null)
            b.getOrigin().setIncidentEdge(c_twin);

        c.setTwin(c_twin);
        c_twin.setTwin(c);
        edges.add(c);
        edges.add(c_twin);

        // Handling a

        // case 1: a has no next -> not much to do
        if (a.getNext() == null) {
            a.setNext(c);
            a.getTwin().setPrev(c_twin);

            c.setPrev(a);
            c_twin.setNext(a.getTwin());

            // update face
            c.setFace(a.getFace());
            c_twin.setFace(a.getTwin().getFace());
        }
        // case 2: a has a next -> locate the proper incident edges to pre/append c
        else if (a.getNext() != null) {
            Pair<HalfEdge,HalfEdge> ccw_cw = getCCWAndCW(c_twin, a.getDestination());
            HalfEdge ccw = ccw_cw.fst;
            HalfEdge cw = ccw_cw.snd;

            assert cw != null;
            assert ccw != null;
            // handle cw - set proper faces later
            c.setPrev(cw.getTwin());
            cw.getTwin().setNext(c);

            // handle ccw - set proper faces later
            c_twin.setNext(ccw);
            ccw.setPrev(c_twin);
        }

        // Handling b

        // case 1: b has no prev -> c can be tacked on easily
        if (b.getPrev() == null) {
            b.setPrev(c);
            b.getTwin().setNext(c_twin);

            c.setNext(b);
            c_twin.setPrev(b.getTwin());
            c.setFace(b.getFace()); // c shares the same face as b
            c_twin.setFace(b.getTwin().getFace()); // c twin shares same face with b twin
        }
        // case 2: b has a prev -> we need to locate the edges CCW and CW from c if they exist
        else if (b.getPrev() != null) {
            Pair<HalfEdge,HalfEdge> ccw_cw = getCCWAndCW(c, b.getOrigin());

            HalfEdge ccw = ccw_cw.fst;
            HalfEdge cw = ccw_cw.snd;

            assert ccw != null;
            assert cw != null;

            // handle ccw - set proper faces later
            c.setNext(ccw);
            ccw.setPrev(c);

            // handle cw - set proper faces later
            c_twin.setPrev(cw.getTwin());
            cw.getTwin().setNext(c_twin);
        }

        // only update faces if we created a new one
        if (a.getNext() != null || b.getPrev() != null) {
            // handle faces by traversing the edges and updating the face
            Face c_face = createFace(c);
            c.setFace(c_face);
            HalfEdge tmp = c.getNext();
            faces.remove(tmp.getFace());
            while (tmp != null && tmp != c) {
                tmp.setFace(c_face);
                tmp = tmp.getNext();
            }

            Face c_twin_face = createFace(c_twin);
            c_twin.setFace(c_twin_face);
            tmp = c_twin.getNext();
            faces.remove(tmp.getFace());
            while (tmp != null && tmp != c_twin) {
                tmp.draw(scene, 0, Color.CYAN);
                scene.repaint();
                tmp.setFace(c_twin_face);
                tmp = tmp.getNext();
            }
        }

        c.getFace().draw(scene);
        scene.repaint();
        /*try {
            scene.removeAllShapes();
            scene.repaint();
            c.getFace().draw(scene);
            scene.repaint();
            sleep(5000);

            scene.removeAllShapes();
            scene.repaint();
            sleep(1000);
            c_twin.getFace().draw(scene);
            scene.repaint();
            sleep(5000);
        } catch (Exception e) {}*/
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
            if (e.getDestination().getIncidentEdge() == null)
                e.getDestination().setIncidentEdge(twin);
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
