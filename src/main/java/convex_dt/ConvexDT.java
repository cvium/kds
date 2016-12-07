package convex_dt;

import dcel.HalfEdge;
import kds.KDSPoint;
import static convex_dt.Utils.*; // not very nice, but it's to avoid having to write Utils.<func> everywhere
import static convex_dt.ConvexShape.*;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;

/**
 * Created by cvium on 29-11-2016.
 */
public class ConvexDT {
    private HalfEdge base;
    private HalfEdge lcand;
    private HalfEdge rcand;
    private ConvexShape shape;
    private ArrayList<KDSPoint> points;

    public ConvexDT() {
    }

    public ConvexDT(ArrayList<KDSPoint> points, ConvexShape shape) {
        throw new NotImplementedException();
    }

    public HalfEdge computeSmallDelaunay(ArrayList<KDSPoint> points) {
        throw new NotImplementedException();
    }

    public HalfEdge findLowerSupport(HalfEdge left, HalfEdge right) {
        if (lowerThan(left.getOrigin(), right.getOrigin())) {
            while (shape.inInfCircle(rNext(right).getOrigin(), left.getOrigin(), right.getOrigin()) ==
                    infCircleEnum.INSIDE && lessThan(rNext(right).getOrigin(), right.getOrigin())) {
                right = rNext(right);
            }
            if (shape.inInfCircle(right.getDestination(), left.getOrigin(), right.getOrigin()) == infCircleEnum.INSIDE) {
                right = rPrev(right);
            } else if (shape.inInfCircle(left.getDestination(), left.getOrigin(), right.getOrigin()) == infCircleEnum.INSIDE) {
                left = rPrev(left);
            } else {
                return connect(right.getTwin(), oPrev(left));
            }
        } else {
            // TODO ????
            throw new NotImplementedException();
        }

        // TODO ??????
        return connect(right, left);
    }

    public void makeBundle(HalfEdge edge) {
        if (edge != lNext(lNext(lNext(edge)))) {
            if (lNext(edge).isBridge()) delete(lNext(edge));
            if (dNext(edge).isBridge()) delete(dNext(edge));
            connect(edge, lPrev(edge)).markBridge();
        }
    }

    public HalfEdge unBundle(HalfEdge edge) {
        HalfEdge fixed, moving, returnEdge, junk;
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
            junk = connect(fixed, moving);
            moving = lNext(moving);
        }

        return returnEdge;
    }

    public void unBundleAll(HalfEdge edge) {
        HalfEdge current;
        for (int direction = 1; direction <= 2; ++direction) {
            current = oNext(edge);
            while (current != edge) {
                if (current.isBridge()) current = unBundle(current);
                else current = oNext(current);
            }
            edge = edge.getTwin();
        }
    }

    public HalfEdge produceONext(HalfEdge edge) {
        if (lNext(edge).isBridge()) {
            delete(lNext(edge));
            makeBundle(connect(lNext(edge), edge).getTwin());
        } else if (oNext(edge).isBridge()) {
            delete(oNext(edge));
            makeBundle(oPrev(connect(edge, lPrev(edge))));
        }
        return oNext(edge);
    }

    public HalfEdge produceOPrev(HalfEdge edge) {
        if (lPrev(edge).isBridge()) {
            delete(lPrev(edge));
            makeBundle(connect(lPrev(edge), edge).getTwin());
        } else if (oPrev(edge).isBridge()) {
            delete(oPrev(edge));
            makeBundle(oNext(connect(edge, lNext(edge))));
        }
        return oPrev(edge);
    }

    public HalfEdge connect(HalfEdge a, HalfEdge b) {
        // TODO: does this work?
        HalfEdge newEdge = new HalfEdge(a.getDestination(), b.getOrigin());
        a.setNext(newEdge);
        b.setPrev(newEdge);
        // hackish way to create the twin
        newEdge.getTwin();
        return newEdge;
    }

    public void delete(HalfEdge e) {
        // remove e from its prev and next edges, TODO: IS THAT ENOUGH?
        e.getPrev().setNext(null);
        e.getNext().setPrev(null);
        // have to remove the twin as well
        e.getTwin().getPrev().setNext(null);
        e.getTwin().getNext().setPrev(null);
    }

    public HalfEdge connectLeft() {
        unBundleAll(lcand);
        // \__   Base is directed from right to left, so base dest must be connected to lcand's origin
        return connect(base.getTwin(), lcand.getTwin());
    }

    public HalfEdge connectRight() {
        unBundleAll(rcand);
        // TODO: should this be reversed? THINK NOT
        // __/   Base is directed from left to right, so base dest must be connected to rcand's origin
        return connect(base.getTwin(), rcand.getTwin());
    }

    public HalfEdge computeLcand() {
        HalfEdge current = null, top = null, t = null;
        lcand = rPrev(base);

        if (shape.inInfCircle(lcand.getDestination(), base.getOrigin(), base.getDestination()) == infCircleEnum.BEFORE) {
            lcand = produceONext(lcand);
            while (shape.inInfCircle(lcand.getDestination(), base.getOrigin(), base.getDestination()) == infCircleEnum.BEFORE) {
                delete(oPrev(lcand));
                lcand = produceONext(lcand);
            }
            if (isValid(lcand)) delete(oPrev(lcand));
            else lcand = oPrev(lcand);
        }

        if (isValid(lcand)) {
            current = oNext(lcand);
            top = lcand;

            switch(shape.inCircle(lcand.getOrigin(), lcand.getDestination(), base.getOrigin(), current.getDestination())) {
                case INSIDE:
                    if (current.isBridge() || rPrev(current).isBridge()) {
                        current = produceONext(oPrev(current));
                    }
                    if (leftOf(current.getDestination(), lcand.getOrigin(), lcand.getDestination())) {
                        if (rPrev(current) != dPrev(lcand))
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
                        break;
                        //return lcand;
                    }
                    break;
                case ONBEFORE:
                    if (leftOf(current.getDestination(), current.getOrigin(), lcand.getDestination())) {
                        current = rPrev(current);
                    } else{
                        return lcand;
                    }
                    break;
                case ONAFTER:
                    top = dNext(top);
                    t = current;
                    current = oNext(current);
                    delete(current);
                    break;
                default:
                    //return lcand;
                    break;
            }
        } else {
            System.out.println("lcand not valid???");
        }

        // TODO top shouldn't be null I think
        //assert top != null;
        //assert current != null;

        if (top != lcand) {
            makeBundle(lNext(connect(top, lcand)));
            top = oNext(lcand);
        }
        if (oPrev(current) != top) {
            makeBundle(oPrev(connect(top, oPrev(current))));
        }
        return lcand;
    }

    public HalfEdge computeRcand() {
        HalfEdge current = null, top = null, t = null;
        // TODO ?
        HalfEdge base_twin = base.getTwin();
        rcand = lNext(base_twin);

        if (shape.inInfCircle(rcand.getDestination(), base_twin.getOrigin(), base_twin.getDestination()) == infCircleEnum.BEFORE) {
            rcand = produceOPrev(rcand);
            while (shape.inInfCircle(rcand.getDestination(), base_twin.getOrigin(), base_twin.getDestination()) == infCircleEnum.BEFORE) {
                delete(oNext(rcand));
                rcand = produceOPrev(rcand);
            }
            if (isValid(rcand)) delete(oNext(rcand));
            else rcand = oNext(rcand);
        }

        if (isValid(rcand)) {
            current = oPrev(rcand);
            top = rcand;
            switch(shape.inCircle(rcand.getOrigin(), rcand.getDestination(), base_twin.getOrigin(), current.getDestination())) {
                case INSIDE:
                    if (current.isBridge() || lNext(current).isBridge()) {
                        current = produceOPrev(lNext(current));
                    }
                    if (leftOf(current.getDestination(), rcand.getOrigin(), rcand.getDestination())) {
                        if (lNext(current) != rcand) {

                        }
                    }
                    break;
                case ONBEFORE:
                    break;
                case ONAFTER:
                    break;
                default:
                    // we have rcand
                    break;

            }

        }


        throw new NotImplementedException();
    }

    public HalfEdge delaunay(ArrayList<KDSPoint> points) {
        if (points.size() < 4) return computeSmallDelaunay(points);
        else {
            int split = (int) Math.floor(points.size() / 2);

            ArrayList<KDSPoint>left = (ArrayList<KDSPoint>) points.subList(0, split);
            ArrayList<KDSPoint> right = (ArrayList<KDSPoint>) points.subList(split + 1, points.size());

            HalfEdge lleft = delaunay(left);
            HalfEdge lright = delaunay(right);

            base = findLowerSupport(lleft, lright);
            boolean leftLower = lowerThan(lleft.getOrigin(), lright.getOrigin());
            HalfEdge lower;
            if (leftLower) {
                if (lleft.getOrigin() == base.getDestination()) lower = base.getTwin();
                else lower = lleft;
            } else {
                lower = rNext(lright);
            }

            lcand = computeLcand();
            rcand = computeRcand();

            if (isValid(lcand) && isValid(rcand)) {
                switch(shape.inCircle(base.getOrigin(), base.getDestination(), lcand.getDestination(), rcand.getDestination())) {
                    case INSIDE:
                        base = connectRight();
                        break;
                    case ON:
                    case ONBEFORE:
                    case ONAFTER:
                        if (rightOf(rcand.getDestination(), lcand.getOrigin(), lcand.getDestination())) {
                            base = connectRight();
                        } else {
                            base = connectLeft();
                        }
                        break;
                    default:
                        base = connectLeft();
                }
            }
            else if (isValid(lcand)) base = connectLeft();
            else if (isValid(rcand)) base = connectRight();
            else if (shape.inInfCircle(rcand.getDestination(), base.getOrigin(), base.getDestination()) == infCircleEnum.AFTER) {
                while (shape.inInfCircle(lcand.getDestination(), lcand.getOrigin(), rcand.getDestination()) == infCircleEnum.INSIDE) {
                    lcand = rPrev(lcand);
                }
                base = connect(rcand, oPrev(lcand));
                delete(rcand);
            }
            else if (shape.inInfCircle(rcand.getDestination(), base.getOrigin(), base.getDestination()) == infCircleEnum.AFTER) {
                while (shape.inInfCircle(rcand.getDestination(), lcand.getDestination(), rcand.getOrigin()) == infCircleEnum.INSIDE) {
                    rcand = lNext(rcand);
                }
                base = connect(lPrev(rcand), lcand.getTwin());
                delete(lcand);
            }

            if (!leftLower) lower = rPrev(lower);

            return lower;
        }
    }

    public boolean isValid(HalfEdge e) {
        return shape.inInfCircle(e.getDestination(), base.getOrigin(), base.getDestination()) == infCircleEnum.INSIDE;
    }
}
