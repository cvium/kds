package convex_dt;

import ProGAL.dataStructures.SortToolPoint2dAroundOrigo;
import ProGAL.geom2d.viewer.J2DScene;
import convex_dt.shapes.ConvexShape;
import dcel.DCEL;
import dcel.HalfEdge;
import kds.KDSPoint;
import utils.Helpers;

import static utils.Helpers.*; // not very nice, but it's to avoid having to write Helpers.<func> everywhere
import static convex_dt.shapes.ConvexShape.*;
import static java.lang.Thread.sleep;
import static java.util.Collections.sort;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by cvium on 29-11-2016.
 */
public class ConvexDT {
    private HalfEdge base;
    private HalfEdge lcand;
    private HalfEdge rcand;
    private ConvexShape shape;
    private ArrayList<KDSPoint> points;
    private J2DScene scene;
    private DCEL dcel;
    private ArrayList<infCircleEnum> illegalValues;
    private double t = 0;

    public double getTime() {
        return t;
    }

    public void setTime(double t) {
        this.t = t;
        shape.setTime(t);
    }

    public ConvexDT() {
    }

    public ConvexDT(ArrayList<KDSPoint> points, ConvexShape shape, J2DScene scene) {
        this.points = points;
        this.shape = shape;
        this.scene = scene;
        this.dcel = new DCEL(scene, points);
        if (scene == null) this.dcel.setVisualize(false);
        illegalValues = new ArrayList<>();
        illegalValues.add(infCircleEnum.INSIDE);
        illegalValues.add(infCircleEnum.INVALID);
    }

    public DCEL getDcel() {
        return dcel;
    }

    public void setScene(J2DScene scene) {
        this.scene = scene;
    }
    public J2DScene getScene() {
        return scene;
    }

    public HalfEdge computeSmallDelaunay(ArrayList<KDSPoint> points) throws Exception {
        if (points.size() == 2) {
            // sort them
            //sort(points);
            KDSPoint a = points.get(0);
            KDSPoint b = points.get(1);
            HalfEdge e1 = dcel.createEdge(a, b);
            e1.setFace(dcel.createFace(e1));
            if (e1.getOrigin().getPoint(t).y() < e1.getDestination().getPoint(t).y())
                return e1;
            return e1.getTwin();
        } else if (points.size() == 3) {
            // sort them in ccw order
            ArrayList<KDSPoint> sorted = Helpers.sortCCW(points);
            KDSPoint a = sorted.get(0);
            KDSPoint b = sorted.get(1);
            KDSPoint c = sorted.get(2);

            HalfEdge last = null;

            if (!illegalValues.contains(shape.inInfCircle(c, a, b)) || !illegalValues.contains(shape.inInfCircle(c, a, b))) {
                last = dcel.createEdge(a, b);
                last.setFace(dcel.createFace(last));
                last.getTwin().setFace(last.getFace());
            }

            // EVER HEARD OF DRY CODE????? ME NEITHER
            if (!illegalValues.contains(shape.inInfCircle(a, b, c)) || !illegalValues.contains(shape.inInfCircle(a, c, b))) {
                HalfEdge current = dcel.createEdge(b, c);
                if (last != null) {
                    last.setNext(current);
                    last.getTwin().setPrev(current.getTwin());

                    current.setPrev(last);
                    current.setFace(last.getFace());
                    current.getTwin().setNext(last.getTwin());
                    current.getTwin().setFace(last.getTwin().getFace());
                }
                last = current;
            }

            if (!illegalValues.contains(shape.inInfCircle(b, c, a)) || !illegalValues.contains(shape.inInfCircle(b, a, c))) {
                // last should NEVER be null but intellij is a bitch and I hate warnings
                if (last != null && last.getPrev() != null) {
                    connect(last, last.getPrev());
                }
                else if (last != null) {
                    HalfEdge current = dcel.createEdge(c, a);
                    if (last.getDestination() == c) {
                        last.setNext(current);
                        last.getTwin().setPrev(current.getTwin());

                        current.setPrev(last);
                        current.setFace(last.getFace());
                        current.getTwin().setNext(last.getTwin());
                        current.getTwin().setFace(last.getTwin().getFace());
                    } else {
                        current.setNext(last);
                        current.setFace(last.getFace());
                        current.getTwin().setPrev(last.getTwin());
                        current.getTwin().setFace(last.getTwin().getFace());

                        last.setPrev(current);
                        last.getTwin().setNext(current.getTwin());
                    }
                }
            }

//            HalfEdge e1 = dcel.createEdge(a, b);
//            HalfEdge e2 = dcel.createEdge(b, c);
//            // connect a -> b with b -> c and a <- b with b <- c
//            e1.setNext(e2);
//            e1.setFace(dcel.createFace(e1));
//            e1.getTwin().setPrev(e2.getTwin());
//            e1.getTwin().setFace(dcel.createFace(e1.getTwin()));
//
//            e2.setPrev(e1);
//            e2.setFace(e1.getFace());
//            e2.getTwin().setNext(e1.getTwin());
//            e2.getTwin().setFace(e1.getTwin().getFace());


//            // don't create a triangle if they are collinear
//            if (leftOf(a, b, c) || rightOf(a, b, c)) {
//                // connect and create a triangle
//                System.out.println("Creating triangle!");
//                connect(e2, e1);
//            }
            // find the lowest point (y-coordinate only)
////            dcel.draw(scene);
            try{Thread.sleep(0);}catch (Exception ex){}
            KDSPoint lowestPoint;
            if (a.getY() < b.getY()) lowestPoint = a;
            else lowestPoint = b;
            if (lowestPoint.getY() > c.getY()) lowestPoint = c;

            // find the ccw edge incident to lowest point
            HalfEdge candidateEdge = lowestPoint.getIncidentEdge();
            if (candidateEdge == null) throw new RuntimeException();
            if (candidateEdge.getPrev() != null && !isCCW(candidateEdge.getPrev().getOrigin(), candidateEdge.getOrigin(), candidateEdge.getDestination()))
                candidateEdge = candidateEdge.getTwin().getNext();
            return candidateEdge;
        } else {
            throw new Exception();
        }
    }

    public HalfEdge findLowerSupport(HalfEdge left, HalfEdge right) {
        assert left != null && right != null;
        if (lowerThan(left.getOrigin(), right.getOrigin())) {
            // no clue what the point of this while is
            while (rNext(right) != null && shape.inInfCircle(rNext(right).getOrigin(), left.getOrigin(), right.getOrigin()) ==
                    infCircleEnum.INSIDE && lessThan(rNext(right).getOrigin(), right.getOrigin())) {
                System.out.println("Entered the weird while in lowersupport");
                if (rNext(right) == null) break;
                right = rNext(right);
            }
            while (true) {
////                right.draw(scene, 0, Color.BLACK);
////                left.draw(scene, 0, Color.BLACK);
                try{Thread.sleep(0);}catch (Exception ex){}
                if (shape.inInfCircle(right.getDestination(), left.getOrigin(), right.getOrigin()) == infCircleEnum.INSIDE) {
                    // see below
                    System.out.println("right");
                    if (rPrev(right) == null) right = right.getTwin();
                    else right = rPrev(right);
                } else if (shape.inInfCircle(left.getDestination(), left.getOrigin(), right.getOrigin()) == infCircleEnum.INSIDE) {
                    // see below
                    System.out.println("left");
                    if (rPrev(left) == null) left = left.getTwin();
                    else left = rPrev(left);
                } else {
                    if (oPrev(left) == null) return connect(right.getTwin(), left);
                    return connect(right.getTwin(), oPrev(left));
                }
                System.out.println("Am I stuck in lowersupport?");
            }
        } else {
            // TODO I think it should move CW around left as the 'candidate' for lower support can't be on the right
            // side of left when the lowest point in right is lower
            while (rNext(left) != null && shape.inInfCircle(rNext(left).getOrigin(), left.getOrigin(), right.getOrigin()) ==
                    infCircleEnum.INSIDE && lessThan(rNext(left).getOrigin(), left.getOrigin())) {
                if (rNext(left) == null) break;
                left = rNext(left);
            }
            while (true) {
////                right.draw(scene, 0, Color.ORANGE);
////                left.draw(scene, 0, Color.ORANGE);
                try{Thread.sleep(0);}catch (Exception ex){}
                try {sleep(0);} catch (InterruptedException e) {}
                // if rNext.org is inside the inf circle with left.org and right.org on boundary, then it's a better candidate
                if ((rNext(left) != null && illegalValues.contains(shape.inInfCircle(rNext(left).getOrigin(), left.getOrigin(), right.getOrigin())))
                        && illegalValues.contains(shape.inInfCircle(left.getDestination(), left.getOrigin(), right.getOrigin()))) {
                    // sort of a hack to make sure we get the right edge in the case where the left side is invalid
                    // as lower support. We assume that its twin is ok.
                    System.out.println("left");
                    if (rNext(left) == null) left = left.getTwin();
                    else left = rNext(left);
                } else if ((rNext(right) != null && illegalValues.contains(shape.inInfCircle(rNext(right).getOrigin(), left.getOrigin(), right.getOrigin())))
                        || illegalValues.contains(shape.inInfCircle(right.getDestination(), left.getOrigin(), right.getOrigin()))) {
                    // see above
                    System.out.println("right");
                    if (rNext(right) == null) right = right.getTwin();
                    else right = rNext(right);
                } else {
                    if (oNext(left) == null) return connect(right.getTwin(), left);
                    return connect(right.getTwin(), oNext(left));
                }
                System.out.println("Am I stuck in lowersupport2?");
            }
        }
    }

    public void makeBundle(HalfEdge edge) {
        if (edge != lNext(lNext(lNext(edge)))) {
            if (lNext(edge).isBridge()) delete(lNext(edge));
            if (dNext(edge).isBridge()) delete(dNext(edge));
            connect(edge, lPrev(edge)).markBridge();
        }
    }

    public HalfEdge unBundle(HalfEdge edge) {
        HalfEdge fixed, moving, returnEdge;
        if (onLine(oNext(edge).getDestination(), edge.getOrigin(), edge.getDestination())) {
            fixed = oPrev(edge);
            moving = lNext(lNext(edge));
            returnEdge = oNext(edge);
        } else {
            fixed = lNext(edge);
            moving = lNext(oPrev(edge));
            returnEdge = oPrev(edge);
        }

        while (fixed != moving) {
            connect(fixed, moving); // junk?
            moving = lNext(moving);
        }

        return returnEdge;
    }

    public void unBundleAll(HalfEdge edge) {
        HalfEdge current;
        for (int direction = 1; direction <= 2; ++direction) {
            current = oNext(edge);
            while (current != edge && current != null) {
                if (current.isBridge()) current = unBundle(current);
                else current = oNext(current);
            }
            edge = edge.getTwin();
        }
    }

    public HalfEdge produceONext(HalfEdge edge) {
        if (lNext(edge) != null && lNext(edge).isBridge()) {
            delete(lNext(edge));
            makeBundle(connect(lNext(edge), edge).getTwin());
        } else if (oNext(edge) != null && oNext(edge).isBridge()) {
            delete(oNext(edge));
            makeBundle(oPrev(connect(edge, lPrev(edge))));
        }
        return oNext(edge);
    }

    public HalfEdge produceOPrev(HalfEdge edge) {
        if (lPrev(edge) != null && lPrev(edge).isBridge()) {
            delete(lPrev(edge));
            makeBundle(connect(lPrev(edge), edge).getTwin());
        } else if (oPrev(edge) != null && oPrev(edge).isBridge()) {
            delete(oPrev(edge));
            makeBundle(oNext(connect(edge, lNext(edge))));
        }
        return oPrev(edge);
    }

    public HalfEdge connect(HalfEdge a, HalfEdge b) {
        // TODO: does this work?
        HalfEdge newEdge = dcel.connect(a, b);//dcel.createEdge(a.getDestination(), b.getOrigin());
//        a.setNext(newEdge);
//        b.setPrev(newEdge);
//        newEdge.setNext(b);
//        newEdge.setPrev(a);
//
//        newEdge.setFace(a.getFace());
//        b.setFace(a.getFace());
//        b.getTwin().setFace(a.getTwin().getFace());
//        newEdge.getTwin().setFace(a.getTwin().getFace());

        return newEdge;
    }

    public void delete(HalfEdge e) {
        dcel.deleteEdge(e);
    }

    public HalfEdge connectLeft() {
        System.out.println("connectLeft()");
        unBundleAll(lcand);
        // \__   Base is directed from right to left, so base origin must be connected to lcand's origin
        return connect(base.getTwin(), lcand.getTwin());
    }

    public HalfEdge connectRight() {
        System.out.println("connectRight()");
        unBundleAll(rcand);
        // TODO: should this be reversed? THINK NOT
        // __/   Base is directed from right to left, so base dest must be connected to rcand's origin
        return connect(base, rcand.getTwin()).getTwin();
    }

    public HalfEdge computeLcand() throws Exception {
        HalfEdge current = null, top = null, t = null;
        boolean foundLcand = false;
        lcand = rPrev(base);
////        lcand.draw(scene, 0, Color.ORANGE);
        sleep(0);
        assert lcand != null;
        assert base != null;

        if (shape.inInfCircle(lcand.getDestination(), base.getOrigin(), base.getDestination()) == infCircleEnum.BEFORE) {
            lcand = produceONext(lcand) != null ? produceONext(lcand) : lcand;
            while (shape.inInfCircle(lcand.getDestination(), base.getOrigin(), base.getDestination()) == infCircleEnum.BEFORE) {
                delete(oPrev(lcand));
                if (produceONext(lcand) == null) break;
                lcand = produceONext(lcand);
//                lcand.draw(scene, 0, Color.ORANGE);
                sleep(0);
            }
            if (isValid(lcand)) delete(oPrev(lcand));
            else lcand = oPrev(lcand);
//            lcand.draw(scene, 0, Color.ORANGE);
            sleep(0);
        }

        if (isValid(lcand)) {
            current = oNext(lcand);
            top = lcand;
            while (true) {
                if (foundLcand || current == null) break;
                assert lcand != null;
                assert current != null;
                assert base != null;
                switch (shape.inCircle(lcand.getOrigin(), lcand.getDestination(), base.getOrigin(), current.getDestination())) {
                    case INSIDE:
                        if (current.isBridge() || rPrev(current).isBridge()) {
                            current = produceONext(oPrev(current));
                        }
                        if (leftOf(lcand.getOrigin(), lcand.getDestination(), current.getDestination())) {
                            if (rPrev(current) != dPrev(lcand) && rPrev(current) != null && dPrev(lcand) != null)
                                makeBundle(connect(lcand, current.getTwin()).getTwin());
                            if (current != oNext(lcand))
                                makeBundle(lNext(connect(lPrev(lcand), current.getTwin())));
                            t = produceONext(lcand);
                            delete(lcand);
                            lcand = t;
                            current = oNext(lcand);
                            top = lcand;
                        } else {
                            // We have lcand
                            foundLcand = true;
                        }
                        break;
                    case ONBEFORE:
                        if (leftOf(current.getOrigin(), lcand.getDestination(), current.getDestination())) {
                            current = rPrev(current);
                        } else {
                            foundLcand = true;
                        }
                        break;
                    case ONAFTER:
                        top = dNext(top);
                        t = current;
                        current = oNext(current);
                        delete(current);
                        break;
                    default:
                        foundLcand = true;
                        break;
                }
//                lcand.draw(scene, 0, Color.ORANGE);
                sleep(0);
//                current.draw(scene, 0, Color.YELLOW);
                sleep(0);
                System.out.println("Am I stuck in lcand?");
            }
        } else {
            System.out.println("lcand not valid???");
        }

        // TODO top shouldn't be null I think
        //assert top != null;
        //assert current != null;

        if (top != lcand && lcand != null && top != null) {
            makeBundle(lNext(connect(top, lcand)));
            top = oNext(lcand);
        }
        if (current != null && oPrev(current) != top) {
            makeBundle(oPrev(connect(top, oPrev(current))));
        }
        //base = base.getTwin();
        return lcand;
    }

    public HalfEdge computeRcand() throws Exception {
        HalfEdge current = null, top = null, t = null;
        boolean foundRcand = false;
        // base must be directed from left to right for this
        rcand = lNext(base.getTwin());

        if (rcand != null && shape.inInfCircle(rcand.getDestination(), base.getOrigin(), base.getDestination()) == infCircleEnum.BEFORE) {
            rcand = produceOPrev(rcand);
            while (shape.inInfCircle(rcand.getDestination(), base.getDestination(), base.getOrigin()) == infCircleEnum.BEFORE) {
                delete(rNext(rcand).getTwin());
                if (produceOPrev(rcand) == null) break;
                rcand = produceOPrev(rcand);
            }
            if (isValid(rcand)) delete(rNext(rcand).getTwin());
            else rcand = rNext(rcand).getTwin();
        }

        if (isValid(rcand)) {
            current = oPrev(rcand);
//            rcand.draw(scene, 0, Color.green);
//            current.draw(scene, 0, Color.BLUE);
            sleep(0);
            top = rcand;
            while (true) {
                assert rcand != null;
                if (foundRcand) break;
                switch (shape.inCircle(rcand.getDestination(), rcand.getOrigin(), base.getDestination(), current.getDestination())) {
                    case INSIDE:
                        if (current.isBridge() || lNext(current).isBridge()) {
                            current = produceOPrev(oNext(current));
                        }
                        if (rightOf(rcand.getOrigin(), rcand.getDestination(), current.getDestination())) {
                            if (lNext(current) != dNext(rcand)) {
                                makeBundle(connect(rcand, current.getTwin()).getTwin());
                            }
                            if (current != oPrev(rcand)) {
                                makeBundle(rPrev(connect(rNext(rcand), current.getTwin())));
                            }
                            t = produceOPrev(rcand);
                            delete(rcand);
                            rcand = t;
                            current = oPrev(rcand);
                            top = rcand;
                        } else {
                            foundRcand = true;
                        }
                        break;
                    case ONBEFORE:
                        if (rightOf(current.getOrigin(), rcand.getDestination(), current.getDestination())) {
                            current = rNext(current);
                        } else {
                            foundRcand = true;
                        }
                        break;
                    case ONAFTER:
                        top = dPrev(current);
                        t = current;
                        current = oPrev(current);
                        delete(current);
                        break;
                    default:
                        // we have rcand
                        foundRcand = true;
                        break;

                }
                System.out.println("Am I stuck in rcand?");
            }
        }

        if (top != null && rcand != null && top != rcand) {
            makeBundle(rPrev(connect(top, rcand)));
            top = oPrev(rcand);
        }
        if (current != null && top != null && oNext(current) != top) {
            makeBundle(oNext(connect(top, oNext(current))));
        }

        return rcand;
    }

    public HalfEdge delaunay() throws Exception {
        return this.delaunay(this.points);
    }

    public HalfEdge delaunay(ArrayList<KDSPoint> points) throws Exception {
        // kind of a hack to get updated positions without changing too much code
        for (KDSPoint p : points) {
            p.updatePosition(this.t);
        }
        Collections.sort(points);

        if (points.size() < 4) return computeSmallDelaunay(points);
        else {
            int split = (int) Math.floor(points.size() / 2);

            ArrayList<KDSPoint>left = new ArrayList<>(points.subList(0, split));
            ArrayList<KDSPoint> right = new ArrayList<>(points.subList(split, points.size()));

            HalfEdge lleft = delaunay(left);
//            lleft.draw(scene, 0, Color.PINK);
            HalfEdge lright = delaunay(right);
//            lright.draw(scene, 0, Color.PINK);
            //sleep(0);
            base = findLowerSupport(lleft, lright);
//            base.draw(scene, 0, Color.black);
            //sleep(0);
            boolean leftLower = lowerThan(lleft.getOrigin(), lright.getOrigin());
            HalfEdge lower;
            if (leftLower) {
                if (lleft.getOrigin() == base.getDestination()) lower = base.getTwin();
                else lower = lleft;
            } else {
                if (rNext(lright) == null) lower = lright;
                else lower = rNext(lright);
            }

            // merge step
            while (true) {
                boolean circleValid = true;
//                base.draw(scene, 0, Color.BLACK);
                sleep(0);
                lcand = computeLcand();
                rcand = computeRcand();
                //System.out.println(lcand.getTwin() == rcand || lcand == rcand);

                if (isValid(lcand)) System.out.println("lcand valid!");
                else System.out.println("lcand invalid!");
//                lcand.draw(scene, 0, Color.MAGENTA);
                sleep(0);
                if (isValid(rcand)) System.out.println("rcand valid!");
                else System.out.println("rcand invalid!");
//                rcand.draw(scene, 0, Color.CYAN);
                sleep(0);
                if (isValid(lcand) && isValid(rcand)) {
                    System.out.println("1");
                    switch (shape.inCircle(base.getOrigin(), base.getDestination(), lcand.getDestination(), rcand.getDestination())) {
                        case INSIDE:
                            System.out.println("rcand.dest INSIDE");
                            base = connectRight();
//                            base.draw(scene, 0, Color.BLACK);
                            sleep(0);
                            break;
                        case ON:
                        case ONBEFORE:
                        case ONAFTER:
                            System.out.println("rcand.dest ON/ONBEFORE/ONAFTER");
                            if (rightOf(lcand.getOrigin(), lcand.getDestination(), rcand.getDestination())) {
                                base = connectRight();
//                                base.draw(scene, 0, Color.BLACK);
                                sleep(0);
                            } else {
                                base = connectLeft();
//                                base.draw(scene, 0, Color.BLACK);
                                sleep(0);
                            }
                            break;
                        case INVALID:
                            System.out.println("Not a valid circle.");
                            circleValid = false;
                            break;
                        default:
                            System.out.println("rcand.dest OUTSIDE");
                            base = connectLeft();
//                            base.draw(scene, 0, Color.BLACK);
                            sleep(0);
                            break;
                    }
                }
                else if (isValid(lcand)) {
                    System.out.println("2");
                    base = connectLeft();
                }
                else if (isValid(rcand)) {
                    System.out.println("3");
                    base = connectRight();
                }
                else if (shape.inInfCircle(lcand.getDestination(), base.getOrigin(), base.getDestination()) == infCircleEnum.BEFORE) {
                    System.out.println("4");
                    while (shape.inInfCircle(lcand.getDestination(), lcand.getOrigin(), rcand.getDestination()) == infCircleEnum.INSIDE) {
                        lcand = rPrev(lcand);
                    }
                    base = connect(rcand, oPrev(lcand));
                    delete(rcand);
                } else if (shape.inInfCircle(rcand.getDestination(), base.getOrigin(), base.getDestination()) == infCircleEnum.AFTER) {
                    System.out.println("5");
                    while (shape.inInfCircle(rcand.getDestination(), lcand.getDestination(), rcand.getOrigin()) == infCircleEnum.INSIDE) {
                        rcand = lNext(rcand);
                    }
                    base = connect(lPrev(rcand), lcand.getTwin());
                    delete(lcand);
                } else {
                    System.out.println("Merge step complete!");
                    break;
                }
                // a special case for when lcand and rcand are both valid, but the incircle test fails because
                // lcand.end isn't sufficiently far to the right of base.org -> base.dest, which means the circle
                // does not exist. even though rcand is valid, it cannot be delaunay since lcand is inside the smallest
                // circle through rcand.end, base.org and base.dest
                if (!circleValid){
                    break;
                }
                System.out.println("DEBUG: Merge not complete.");
            }

            if (!leftLower) lower = rPrev(lower);

            return lower;
        }
    }

    public boolean isValid(HalfEdge e) {
        return shape.inInfCircle(e.getDestination(), base.getOrigin(), base.getDestination()) == infCircleEnum.INSIDE;
    }
}
