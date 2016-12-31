package tournament_tree;

import ProGAL.geom2d.viewer.J2DScene;
import kds.KDSPoint;
import kds.Simulator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;

/**
 * Created by clausvium on 30/12/16.
 */
public class run {

    public static void main(String[] args) throws Exception {
        KDSPoint p = new KDSPoint(new double[]{0.1}, new double[]{0.3});
        DistanceFunction<KDSPoint> distanceFunction = new DistanceFunction<>(p);
        ArrayList<KDSPoint> leaves = new ArrayList<>();
        Random rand = new Random();

        for (int i = 0; i < 3; ++i) {
            double[] coeffsX = new double[1];
            double[] coeffsY = new double[1];
            for (int j = 0; j < 1; ++j) {
                coeffsX[j] = 1 + (1 - 1) * rand.nextDouble();//
                coeffsY[j] = -1 + (1 + 1) * rand.nextDouble();
            }
            leaves.add(new KDSPoint(coeffsX, coeffsY));
        }
        System.out.println(leaves.size());
        TournamentTree<KDSPoint, TournamentEvent<KDSPoint>> tournamentTree = new TournamentTree<>(0, leaves, distanceFunction);

        Simulator<KDSPoint, TournamentEvent<KDSPoint>> simulator = new Simulator<>(tournamentTree, 0, 0.01, 0, Level.ALL);
        simulator.setScene(J2DScene.createJ2DSceneInFrame());
        p.draw(simulator.getScene(), 0);
        int error = simulator.run(true, true);
        if (error > 0) {
            throw new Exception();
        }
    }
}
