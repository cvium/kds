/**
 * Created by clausvium on 21/12/15.
 */
package sortedList;

import ProGAL.geom2d.Point;
import ProGAL.geom2d.viewer.J2DScene;
import ProGAL.geom2d.*;
import kds.KDSPoint;
import kds.Simulator;

import java.awt.*;
import java.lang.reflect.Array;
import java.util.*;
import java.util.function.DoubleToIntFunction;
import java.util.logging.Level;

class run {
    private static int N = 50;
    private static int M = 3;
    private static int T = 10;
    private static double TIMESTEP = 0.1;
    private static Level loggerLevel = Level.FINE;

    public static void main(String[] args) throws Exception {
        SortedList kds = new SortedList(N, M);
        Simulator<KDSPoint, SortedEvent> sim = new Simulator<>(kds, TIMESTEP, T, loggerLevel);
        sim.run(0, true);
    }
}
