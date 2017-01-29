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
    private ArrayList<KDSPoint> vertices;
    private J2DScene scene;

    public DCEL(J2DScene scene, ArrayList<KDSPoint> vertices) {
        faces = new ArrayList<>();
        edges = new ArrayList<>();
        this.vertices = vertices;
        this.scene = scene;
    }

    public ArrayList<Face> getFaces() {
        return faces;
    }

    public ArrayList<HalfEdge> getEdges() {
        return edges;
    }

    public ArrayList<KDSPoint> getVertices() {
        return vertices;
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

        // handle faces by traversing the edges and updating the faces
        Face c_face = createFace(c);
        c.setFace(c_face);
        for (HalfEdge e : c) {
            try {
                if (e.getFace() != c_face && e.getFace() != null) deleteFace(e.getFace());
            } catch (IllegalStateException ex) {
            }
            e.setFace(c_face);
        }

        // if c and c.twin share the same face, then don't process anything -- happens _only_ when it's the inf face
        if (c_twin.getFace() != c.getFace()) {
            Face c_twin_face = createFace(c_twin);
            c_twin.setFace(c_twin_face);
            for (HalfEdge e : c_twin) {
                try {
                    if (e.getFace() != c_twin_face && e.getFace() != null) deleteFace(e.getFace());
                } catch (IllegalStateException ex) {
                }
                e.setFace(c_twin_face);
            }
        }
        return c;
    }

    /**
     * Updates the face of the soon-to-be-removed edge e
     *
     * @param e half edge being removed
     */
    public void updateFace(HalfEdge e) {
        if (e.getFace().getOuterComponent() == e) {
            if (e.getNext() != null)
                e.getFace().setOuterComponent(e.getNext());
            else if (e.getPrev() != null)
                e.getFace().setOuterComponent(e.getPrev());
            else
                deleteFace(e.getFace());
        }
    }

    public void deleteFace(Face f) {
        faces.remove(f);
    }

    public void deleteEdge(HalfEdge e) {
        // remove e from its face if it's the outercomponent of said face. if e has no next and no prev, delete face
        updateFace(e);
        updateFace(e.getTwin());

        // process incident edge for the origin of e and e.twin
        if (e == e.getOrigin().getIncidentEdge()) {
            HalfEdge cand = oNext(e) != null ? oNext(e) : oPrev(e);
            e.getOrigin().setIncidentEdge(cand);
        }
        if (e.getTwin() == e.getTwin().getOrigin().getIncidentEdge()) {
            HalfEdge cand = oNext(e.getTwin()) != null ? oNext(e.getTwin()) : oPrev(e.getTwin());
            e.getTwin().getOrigin().setIncidentEdge(cand);
        }

        // process incident edges for e and e.twin
        updateIncidentEdges(e);
        updateIncidentEdges(e.getTwin());

        edges.remove(e);
        edges.remove(e.getTwin());

        e.undraw(scene);
        e.getTwin().undraw(scene);
        scene.repaint();
        System.out.println("INFO: Deleted an edge.");
        try {sleep(100);} catch (InterruptedException ex) {}
    }

    private void updateIncidentEdges(HalfEdge e) {
        // remove e from its prev and next edges, TODO: IS THAT ENOUGH? NO, gotta fix them
        if (e.getPrev() != null) {
            // might need a new next, it will always be e.oPrev if it exists, but don't use it if it's twin
            if (oPrev(e) != null && oPrev(e) != e.getTwin()) {
                e.getPrev().setNext(oPrev(e));
                oPrev(e).setPrev(e.getPrev());
            }
            else e.getPrev().setNext(null);
        }
        if (e.getNext() != null) {
            // its next might need a new prev, it will always be e.dNext if it exists, but don't use it if it's twin
            if (dNext(e) != null && dNext(e) != e.getTwin()) {
                e.getNext().setPrev(dNext(e));
                dNext(e).setNext(e.getNext());
            }
            else e.getNext().setNext(null);
        }
    }

    public Face createFace(HalfEdge e) {
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
            if (f.getOuterComponent().getFace() != f) {
                System.out.println("ERROR: Face is invalid! Drawing its outer component in YELLOW.");
                f.getOuterComponent().draw(scene, 0, Color.YELLOW);
                continue;
            }
            f.draw(scene);
            try {
                sleep(1000);
            } catch (InterruptedException ex) {}
        }
    }
}
