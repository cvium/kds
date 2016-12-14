package convex_dt;

import ProGAL.geom2d.viewer.J2DScene;
import dcel.HalfEdge;
import kds.KDSPoint;

import java.util.ArrayList;
import java.util.Random;

import static java.util.Collections.sort;
import static java.lang.Thread.sleep;

/**
 * Created by cvium on 11-12-2016.
 */
public class run {
    public static void main(String[] args) throws Exception {
        ArrayList<KDSPoint> points = new ArrayList<>();
        ConvexShape circle = new Circle();

        if (false) {
            points.add(new KDSPoint(new double[]{0}, new double[]{0}));
            points.add(new KDSPoint(new double[]{-0.5}, new double[]{1}));
            points.add(new KDSPoint(new double[]{0.5}, new double[]{1.5}));
            points.add(new KDSPoint(new double[]{2}, new double[]{0.5}));
            points.add(new KDSPoint(new double[]{3}, new double[]{2.5}));
            points.add(new KDSPoint(new double[]{2.5}, new double[]{3}));
        } else if (false) {
            points.add(new KDSPoint(new double[]{-0.5}, new double[]{1}));
            points.add(new KDSPoint(new double[]{0}, new double[]{0}));
            points.add(new KDSPoint(new double[]{0.5}, new double[]{1.2}));
            points.add(new KDSPoint(new double[]{2}, new double[]{0.5}));
            points.add(new KDSPoint(new double[]{2.5}, new double[]{0.8}));
            points.add(new KDSPoint(new double[]{2.6}, new double[]{0.3}));
        } else if (true){
            points.add(new KDSPoint(new double[]{0}, new double[]{1}));
            points.add(new KDSPoint(new double[]{0.3}, new double[]{0.7}));
            points.add(new KDSPoint(new double[]{0.5}, new double[]{1.1}));
            points.add(new KDSPoint(new double[]{1}, new double[]{0.2}));
            points.add(new KDSPoint(new double[]{1.2}, new double[]{0.5}));
            points.add(new KDSPoint(new double[]{1.4}, new double[]{0.22}));
        }

        else{
            Random rand = new Random();

            for (int i = 0; i < 20; ++i) {
                double[] coeffsX = new double[1];
                double[] coeffsY = new double[1];
                for (int j = 0; j < 1; ++j) {
                    coeffsX[j] = -1 + (1 + 1) * rand.nextDouble();//
                    coeffsY[j] = -1 + (1 + 1) * rand.nextDouble();
                }
                points.add(new KDSPoint(coeffsX, coeffsY));
            }
        }

        sort(points);

        J2DScene scene = J2DScene.createJ2DSceneInFrame();
        for (KDSPoint p : points) {
            p.draw(scene, 0);
        }
        scene.centerCamera();
        scene.autoZoom();
        scene.repaint();

        ConvexDT dt = new ConvexDT(points, circle, scene);

        HalfEdge lower = dt.delaunay();
        //lower.draw(scene, 0, 2);

        System.out.println("Printing DCEL!");
        scene.removeAllShapes();
        scene.repaint();
        sleep(1000);
        for (KDSPoint p : points) {
            p.draw(scene, 0);
        }
        dt.getDcel().draw(scene);
    }
}
