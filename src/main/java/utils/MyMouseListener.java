package utils;

import ProGAL.geom2d.Point;
import ProGAL.geom2d.Shape;
import ProGAL.geom2d.viewer.ClickListener;
import ProGAL.geom2d.viewer.J2DScene;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by clausvium on 25/12/16.
 */
public class MyMouseListener implements ClickListener {

    private J2DScene scene;
    private ArrayList<Point> points;

    public MyMouseListener(J2DScene scene) {
        this.scene = scene;
        points = new ArrayList<>();
    }

    @Override
    public void shapeClicked(Shape shape, MouseEvent mouseEvent) {
        Point point = scene.transformPoint(new java.awt.Point(mouseEvent.getX(), mouseEvent.getY()));
        System.out.println("Coordinates: (" + point.x() + ", " + point.y() + ")");
        points.add(point);
    }

    public ArrayList<Point> getPoints() {
        return this.points;
    }

    public void removePoints() {
        this.points = new ArrayList<>();
    }
}
