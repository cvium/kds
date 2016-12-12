package convex_dt;

import ProGAL.geom2d.viewer.J2DScene;
import dcel.DCEL;
import dcel.HalfEdge;
import kds.KDSPoint;

import java.util.ArrayList;
import java.util.Random;

import static java.util.Collections.sort;

/**
 * Created by cvium on 11-12-2016.
 */
public class run {
    public static void main(String[] args) throws Exception {
        ArrayList<KDSPoint> points = new ArrayList<>();
        ConvexShape circle = new Circle();
        Random rand = new Random();

        for (int i = 0; i < 6; ++i) {
            double[] coeffsX = new double[3];
            double[] coeffsY = new double[3];
            for (int j = 0; j < 3; ++j) {
                coeffsX[j] = -1 + (1 + 1) * rand.nextDouble();//
                coeffsY[j] = -1 + (1 + 1) * rand.nextDouble();
            }
            points.add(new KDSPoint(coeffsX, coeffsY));
        }

        sort(points);

        J2DScene scene = J2DScene.createJ2DSceneInFrame();
        for (KDSPoint p : points) {
            p.draw(scene, 0);
        }
        scene.centerCamera();
        scene.autoZoom();
        scene.repaint();

        ConvexDT dt = new ConvexDT(points, circle);
        dt.setScene(scene);

        HalfEdge lower = dt.delaunay();
        //lower.draw(scene, 0, 2);

        System.out.println("Printing DCEL!");
        //scene.removeAllShapes();
        for (KDSPoint p : points) {
            p.draw(scene, 0);
        }
        dt.getDcel().draw(scene);
    }
}
