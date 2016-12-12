package dcel;

import ProGAL.geom2d.viewer.J2DScene;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by cvium on 29-11-2016.
 */
public class Face {
    private HalfEdge outerComponent;
    private ArrayList<HalfEdge> innerComponent;

    public Face() {
    }

    public Face(HalfEdge outerComponent) {
        this.outerComponent = outerComponent;
    }

    public Face(HalfEdge outerComponent, ArrayList<HalfEdge> innerComponent) {
        this.outerComponent = outerComponent;
        this.innerComponent = innerComponent;
    }

    public HalfEdge getOuterComponent() {
        return outerComponent;
    }

    public void setOuterComponent(HalfEdge outerComponent) {
        this.outerComponent = outerComponent;
    }

    public ArrayList<HalfEdge> getInnerComponent() {
        return innerComponent;
    }

    public void setInnerComponent(ArrayList<HalfEdge> innerComponent) {
        this.innerComponent = innerComponent;
    }

    public void addInnerComponent(HalfEdge edge) {
        this.innerComponent.add(edge);
    }

    public void delInnerComponent(HalfEdge edge) {
        this.innerComponent.remove(edge);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Face)) return false;

        Face face = (Face) o;

        if (outerComponent != null ? !outerComponent.equals(face.outerComponent) : face.outerComponent != null)
            return false;
        return innerComponent != null ? innerComponent.equals(face.innerComponent) : face.innerComponent == null;
    }

    @Override
    public int hashCode() {
        int result = outerComponent != null ? outerComponent.hashCode() : 0;
        result = 31 * result + (innerComponent != null ? innerComponent.hashCode() : 0);
        return result;
    }

    public void draw(J2DScene scene) {
        outerComponent.draw(scene, 0, Color.green);
        HalfEdge e = outerComponent.getNext();

        while (e!= null && e != outerComponent) {
            e.draw(scene, 0, Color.green);
            e = e.getNext();
        }
    }
}
