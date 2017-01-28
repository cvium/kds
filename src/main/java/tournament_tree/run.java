package tournament_tree;

import ProGAL.geom2d.viewer.J2DScene;
import kds.KDSPoint;
import kds.Simulator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by clausvium on 30/12/16.
 */
public class run {

    public static void main(String[] args) throws Exception {
        int failedRuns = 0;
        int numRuns = 1000;
        int numCoeffs = 10;
        for (int run = 0; run < numRuns; ++run) {
            Random rand = new Random();

            KDSPoint p = new KDSPoint(new double[]{0.1, 0.2, 0.4},
                    new double[]{0.3, 2, 0.7});
            DistanceFunction<KDSPoint> distanceFunction = new DistanceFunction<>(p);
            ArrayList<KDSPoint> leaves = new ArrayList<>();

            for (int i = 0; i < 100; ++i) {
                double[] coeffsX = new double[numCoeffs];
                double[] coeffsY = new double[numCoeffs];
                for (int j = 0; j < numCoeffs; ++j) {
                    coeffsX[j] = 1 + (1 + 1) * rand.nextDouble();//
                    coeffsY[j] = -1 + (1 + 1) * rand.nextDouble();
                }
                leaves.add(new KDSPoint(coeffsX, coeffsY));
            }
            System.out.println(leaves.size());
            TournamentTree<KDSPoint> tournamentTree = new TournamentTree<>(0, leaves, distanceFunction);

            Simulator<KDSPoint, TournamentEvent<KDSPoint>> simulator = new Simulator<>(tournamentTree, 0, 0.1, 10, Level.ALL);
            //simulator.setScene(J2DScene.createJ2DSceneInFrame());
            //p.draw(simulator.getScene(), 0);
            int error = simulator.run(false, true);
            if (error != 0) {
                ++failedRuns;
            }
        }
        Logger.getGlobal().info("Failed runs: " + failedRuns + "/" + numRuns);
    }
}
