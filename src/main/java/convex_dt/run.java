package convex_dt;

import ProGAL.geom2d.viewer.J2DScene;
import convex_dt.shapes.Circle;
import convex_dt.shapes.ConvexShape;
import dcel.HalfEdge;
import kds.KDSPoint;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

import static java.util.Collections.sort;
import static java.lang.Thread.sleep;
import static utils.Helpers.pointsToFile;

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
        } else if (false){
            points.add(new KDSPoint(new double[]{0}, new double[]{1}));
            points.add(new KDSPoint(new double[]{0.3}, new double[]{0.7}));
            points.add(new KDSPoint(new double[]{0.5}, new double[]{1.1}));
            points.add(new KDSPoint(new double[]{1}, new double[]{0.2}));
            points.add(new KDSPoint(new double[]{1.2}, new double[]{0.5}));
            points.add(new KDSPoint(new double[]{1.4}, new double[]{0.22}));
        } else if (false) {
            points.add(new KDSPoint(new double[]{0}, new double[]{0.5}));
            points.add(new KDSPoint(new double[]{0.2}, new double[]{0.55}));
            points.add(new KDSPoint(new double[]{0.5}, new double[]{0.2}));
            points.add(new KDSPoint(new double[]{0.55}, new double[]{0}));
            points.add(new KDSPoint(new double[]{0.62}, new double[]{0.21}));
            points.add(new KDSPoint(new double[]{0.67}, new double[]{-0.01}));
            points.add(new KDSPoint(new double[]{0.75}, new double[]{0.35}));
            points.add(new KDSPoint(new double[]{0.8}, new double[]{0.6}));
            points.add(new KDSPoint(new double[]{0.85}, new double[]{-0.2}));
            points.add(new KDSPoint(new double[]{0.9}, new double[]{0.05}));
        } else if (false) {
            points.add(new KDSPoint(new double[]{-0.8431111966529843},new double[]{0.17428081066151369}));
            points.add(new KDSPoint(new double[]{-0.7629621471780856},new double[]{0.12223152060482145}));
            points.add(new KDSPoint(new double[]{-0.6139065477272441},new double[]{-0.764229972536429}));
            points.add(new KDSPoint(new double[]{-0.5864251463643968},new double[]{0.7344629858826073}));
            points.add(new KDSPoint(new double[]{0.10421285147636072},new double[]{0.9111815751924657}));
            points.add(new KDSPoint(new double[]{0.2633836683890438},new double[]{0.9035111211172113}));
            points.add(new KDSPoint(new double[]{0.4126815329364306},new double[]{-0.237377536700335}));
            points.add(new KDSPoint(new double[]{0.6708184845145735},new double[]{0.7025077149803187}));
            points.add(new KDSPoint(new double[]{0.8678863509000811},new double[]{-0.2812875118536673}));
            points.add(new KDSPoint(new double[]{0.950933999167934},new double[]{-0.7474612069325521}));
        } else if (false) {
            points.add(new KDSPoint(new double[]{-0.952805576850756},new double[]{0.8344112441610374}));
            points.add(new KDSPoint(new double[]{-0.8053985537735562},new double[]{0.26813573956699055}));
            points.add(new KDSPoint(new double[]{-0.7698203451589347},new double[]{-0.4277295311557152}));
            points.add(new KDSPoint(new double[]{-0.6654568044040803},new double[]{0.38940161927652883}));
            points.add(new KDSPoint(new double[]{-0.6267912711751258},new double[]{-0.6871564213204056}));
            points.add(new KDSPoint(new double[]{-0.5325066783233527},new double[]{-0.11110191639896505}));
            points.add(new KDSPoint(new double[]{-0.4756535260015018},new double[]{-0.9352377278838779}));
            points.add(new KDSPoint(new double[]{-0.40473022842652884},new double[]{0.38173613190538247}));
            points.add(new KDSPoint(new double[]{-0.0077880672087131675},new double[]{-0.1144654412966819}));
            points.add(new KDSPoint(new double[]{0.8977062892429617},new double[]{0.479309675690081}));

        } else if (true) {
            points.add(new KDSPoint(new double[]{0},new double[]{0}));
            points.add(new KDSPoint(new double[]{-0.2},new double[]{0.5}));
            points.add(new KDSPoint(new double[]{-0.4},new double[]{0.1}));
            points.add(new KDSPoint(new double[]{-1},new double[]{1}));
            points.add(new KDSPoint(new double[]{-1.2},new double[]{0.8}));
            points.add(new KDSPoint(new double[]{-1.4},new double[]{1.1}));
        }

        else{
            Random rand = new Random();

            for (int i = 0; i < 10; ++i) {
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

        pointsToFile(points);

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
        for (KDSPoint p : points) {
            p.draw(scene, 0);
        }
        for (HalfEdge e : dt.getDcel().getEdges()) {
            e.draw(scene, 0, Color.CYAN);
        }
        System.out.println("Faces: " + dt.getDcel().getFaces().size());
        dt.getDcel().draw(scene);
        System.out.println("DONE. If there are any CYAN colored edges, something is wrong!");
    }
}
