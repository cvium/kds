package naive_emst;

import ProGAL.geom2d.viewer.J2DScene;
import convex_dt.ConvexDT;
import convex_dt.shapes.CircleShape;
import convex_dt.shapes.ConvexShape;
import dcel.HalfEdge;
import kds.KDSPoint;
import kds.Simulator;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;

import static java.util.Collections.sort;
import static utils.Helpers.pointsToFile;

/**
 * Created by cvium on 28-11-2016.
 */
public class run {
    public static void main(String[] args) throws Exception {
        ArrayList<KDSPoint> points = new ArrayList<>();
        Random rand = new Random();

        for (int i = 0; i < 20; ++i) {
            double[] coeffsX = new double[1];
            double[] coeffsY = new double[1];
            for (int j = 0; j < 1; ++j) {
                coeffsX[j] = -2 + (2 + 2) * rand.nextDouble();//
                coeffsY[j] = -2 + (2 + 2) * rand.nextDouble();
            }
            KDSPoint p = new KDSPoint(coeffsX, coeffsY);
            p.setIdx(i);
            points.add(p);
        }

        //pointsToFile(points);
        sort(points);

        J2DScene scene = J2DScene.createJ2DSceneInFrame();
        for (KDSPoint p : points) {
            p.draw(scene, 0);
        }
        scene.centerCamera();
        scene.autoZoom();
        scene.repaint();

        NaiveEMST emst = new NaiveEMST(points);
        emst.compute(0);

        //ConvexDT dt = new ConvexDT(points, new CircleShape(), scene);
        //dt.delaunay(points);

        for (KDSPoint p : points) {
            p.draw(scene, 0);
        }
        for (HalfEdge e : emst.getEdges()) {
            e.draw(scene, 0, Color.CYAN);
        }
        System.out.println("DONE. If there are any CYAN colored edges, something is wrong!");
    }
}
