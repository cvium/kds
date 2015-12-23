/**
 * Created by clausvium on 21/12/15.
 */
package sortedList;

import ProGAL.geom2d.Point;
import ProGAL.geom2d.viewer.J2DScene;
import ProGAL.geom2d.*;
import kds.Certificate;
import kds.KDSPoint;

import java.awt.*;
import java.util.*;

class run {
    private static int N = 10;
    private static int M = 5;
    private static int T = 10;
    private static double TIMESTEP = 0.01;

    public static void main(String[] args) throws Exception {
        J2DScene scene = J2DScene.createJ2DSceneInFrame();
        Random rand = new Random();
        ArrayList<KDSPoint> points = new ArrayList<>(N);
        for (int i = 0; i < N; ++i) {
            double[] coeffsX = new double[M];
            double[] coeffsY = new double[M];

            for (int j = 0; j < M; ++j) {
                coeffsX[j] = rand.nextDouble();
                coeffsY[j] = 0;
            }
            points.add(new KDSPoint(coeffsX, coeffsY));
        }
        SortedList sl = new SortedList(points);

        double t = 0.0;

        while (t <= T) {
            scene.removeAllShapes();
            try {
                if (sl.eq.queue.element().getCertificate().getFailureTime() <= t) {
                    SortedEvent event = (SortedEvent) sl.eq.queue.poll();
                    if (event.getCertificate().isValid()) {
                        event.process(t);
                        System.out.println("EVENT at time " + t);
                    }
                    if (!sl.audit(t)) {
                        throw new Exception("PLS");
                    }
                }
            } catch (NoSuchElementException e) {
                // do nothing
            }
            for(int i = 0; i < N; ++i) {
                KDSPoint kp = sl.points.get(i);

                kp.draw(scene, t);
            }
            scene.centerCamera();
            scene.autoZoom();
            try{Thread.sleep(100);} catch(InterruptedException e) {}
            double nextFailTime;
            try {
                if (Math.abs(sl.eq.queue.element().getCertificate().getFailureTime() - t) < 0.01 &&
                        sl.eq.queue.element().getCertificate().isValid()) {
                    nextFailTime = sl.eq.queue.peek().getCertificate().getFailureTime();
                } else {
                    nextFailTime = t + TIMESTEP;
                }
            } catch (NoSuchElementException e) {
                nextFailTime = t + TIMESTEP;
            }
            t = nextFailTime;
        }
    }
}
