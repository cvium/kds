package dynotree;

import ProGAL.geom2d.Circle;
import ProGAL.geom2d.Point;
import ProGAL.geom2d.viewer.J2DScene;
import utils.MyMouseListener;

import java.awt.*;
import java.util.ArrayList;

import static java.lang.Thread.sleep;

/**
 * Created by clausvium on 25/12/16.
 */
public class run {

    public static void main(String[] args) throws Exception {

        J2DScene scene = J2DScene.createJ2DSceneInFrame();
        MyMouseListener mouseListener = new MyMouseListener(scene);

        scene.setCameraCenter(new Point(0, 0));
        scene.addClickListener(mouseListener);

        while (mouseListener.getPoints().size() < 10) {
            sleep(100);
        }

        LinkCutTree<Point> lct = new LinkCutTree<>();

        ArrayList<Node<Point>> nodes = new ArrayList<>();
        int i = 0;
        for (Point p : mouseListener.getPoints()) {
            nodes.add(new Node<>(p));

            if (i > 1) {
                lct.link(nodes.get(i-1), nodes.get(i));
            }

            ++i;
        }

        System.out.println("DONE");

    }
}
