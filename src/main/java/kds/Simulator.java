package kds;

import ProGAL.geom2d.viewer.J2DScene;

import java.io.Console;
import java.io.IOException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by clausvium on 28/12/15.
 */

public class Simulator<PointType extends KDSPoint, EventType extends Event<PointType>> {
    private static final Logger LOGGER = Logger.getLogger( Simulator.class.getName() );
    boolean visualize = true;
    KDS<PointType, EventType> kds;
    double timestep;
    double starttime;
    double endtime;
    Level loggerLevel;
    J2DScene scene;

    public Level getLoggerLevel() {
        return loggerLevel;
    }

    public void setLoggerLevel(Level loggerLevel) {
        this.loggerLevel = loggerLevel;
    }

    public Simulator(KDS<PointType, EventType> kds, double starttime, double timestep, double endtime,
                     Level loggerLevel) {
        this.kds = kds;
        this.starttime = starttime;
        this.timestep = timestep;
        this.endtime = endtime;
        this.loggerLevel = loggerLevel;
        LOGGER.setLevel(this.loggerLevel);
        Logger topLogger = java.util.logging.Logger.getLogger("");
        topLogger.getHandlers()[0].setLevel( this.loggerLevel );
    }

    public double getTimestep() {
        return timestep;
    }

    public void setTimestep(double timestep) {
        this.timestep = timestep;
    }



    public boolean isVisualizing() {
        return visualize;
    }
    public void setVisualization(boolean visualize) {
        this.visualize = visualize;
    }
    public int run(boolean visualize) throws IOException {
        LOGGER.log(Level.INFO, "Starting simulation");

        if (visualize) scene = J2DScene.createJ2DSceneInFrame();
        /*
        Color[] c = {Color.BLUE, Color.RED, Color.BLACK, Color.MAGENTA, Color.GREEN, Color.CYAN, Color.GRAY,
                Color.ORANGE, Color.PINK, Color.YELLOW};

        for(int i = 0; i < N; ++i) {
            points.get(i).draw(scene, 0);
        }*/

        if (visualize) {
            for (int i = 0; i < kds.getPoints().size(); ++i) {
                kds.getPoints().get(i).draw(scene, 0);
            }
            scene.centerCamera();
            scene.autoZoom();
            scene.repaint();
        }

        double t = starttime;
        boolean event = false;
        int errors = 0;
        while (t <= endtime) {
            LOGGER.log(Level.FINER, "Time: {0}", t);
            ArrayList<EventType> es;
            try {
                while (kds.getEventQueue().firstKey() <= t) {
                    es = kds.getEventQueue().poll();
                    for (EventType e : es) {
                        if (e.isValid()) {
                            event = true;
                            e.process(e.getFailureTime());
                            LOGGER.log(Level.FINER, "EVENT at time t={0}", e.getFailureTime());
                        }
                    }
                }
            } catch (NoSuchElementException e) {
                // do nothing
            }
            if (!kds.audit(t)) {
                LOGGER.log(Level.SEVERE, "Auditing failed");
                ++errors;
            } else {
                LOGGER.log(Level.FINE, "Auditing succeeded");
            }
            event = false;
            if (visualize) {
                scene.removeAllShapes();
                for(int i = 0; i < kds.getPoints().size(); ++i) {
                    kds.getPoints().get(i).draw(scene, t);
                }
                scene.repaint();
                try { Thread.sleep(100); } catch (InterruptedException e) {}
            }
            t += timestep;
        }
        LOGGER.log(Level.INFO, "End of simulation, {0} errors", errors);
        //System.exit(0);
        return errors;
    }
}
