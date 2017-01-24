package tournament_tree;

import utils.Primitive;

/**
 * Created by clausvium on 28/12/16.
 */
public interface TournamentTreeWinner<P extends Primitive> {
    P findWinner(P first, P second);
    double computeValue(double t, P p);
    P getP();
}
