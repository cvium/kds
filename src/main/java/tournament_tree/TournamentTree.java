package tournament_tree;

import kds.Event;
import kds.EventQueue;
import kds.KDS;
import kds.solvers.EigenSolver;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import utils.Primitive;

import java.util.ArrayList;

/**
 * Created by clausvium on 22/12/16.
 */
public class TournamentTree<P extends Primitive, EventType extends Event<P>> implements KDS<P, EventType> {
    private double alpha = 0.288; // the weight ratio used for balancing - default value as described in
                                  // Advanced Data Structures by Peter Brass
    private double epsilon = 0.005;
    private TournamentNode<P> root; // aka the winner
    private EigenSolver solver = new EigenSolver();
    private TournamentTreeWinner<P> winnerFunction;
    private EventQueue<EventType> eq;
    private ArrayList<TournamentNode<P>> leaves;
    private int nextKey = 0;

    public TournamentTree(TournamentTreeWinner<P> winnerFunction) {
        this.winnerFunction = winnerFunction;
        this.eq = new EventQueue<>();
        this.leaves = new ArrayList<>();
    }

    public TournamentTree(double t, ArrayList<P> leaves, TournamentTreeWinner<P> winnerFunction) {
        this.winnerFunction = winnerFunction;
        this.eq = new EventQueue<>();
        this.leaves = new ArrayList<>();
        initialize(t, leaves);
        if (!audit(t)) System.out.println("bleh");
    }

    @Override
    public boolean audit(double t) {
        double smallestDistance = Double.MAX_VALUE;
        for (TournamentNode<P> leaf : leaves) {
            double val = winnerFunction.computeValue(leaf.getObject());
            if (smallestDistance > val) smallestDistance = val;
        }
        return winnerFunction.computeValue(root.getObject()) == smallestDistance && validate(root);
    }

    public boolean validate(TournamentNode<P> node) {
        if (node == null || node.isNull()) return true;
        boolean leftValid = validate(node.getLeftChild());
        boolean rightValid = validate(node.getRightChild());

        boolean valid = leftValid && rightValid;

        if (!valid) return false; // no need to check, TODO print out offenders

        if (!node.getLeftChild().isNull()) {
            valid = node.getLeftChild().getKey() < node.getKey();
        }
        if (!node.getRightChild().isNull()) {
            valid = valid && node.getKey() <= node.getRightChild().getKey();
        }
        return valid;
    }

    @Override
    public void initialize(double starttime) {
        // TODO
        throw new NotImplementedException();
    }

    @Override
    public void initialize(double starttime, ArrayList<P> leaves) {
        // create the leaf nodes
        for (P leaf : leaves) {
            this.leaves.add(insert(starttime, nextKey, leaf));
        }
    }

    @Override
    public void update(EventType event, double t) {

    }

    @Override
    public EventQueue<EventType> getEventQueue() {
        return null;
    }

    public ArrayList<P> getPrimitives() {
        ArrayList<P> primitives = new ArrayList<>();
        for (TournamentNode<P> leaf : leaves) {
            primitives.add(leaf.getObject());
        }
        return primitives;
    }

    public TournamentNode<P> getWinner() {
        return root;
    }

    public void setWinner(TournamentNode<P> winner) {
        this.root = winner;
    }

    /**
     * Inserts P object with int key at time t
     *
     * @param t the time at which the object is inserted
     * @param key the key of the node
     * @param object the object associated with the key
     * @return the new leaf node
     */
    public TournamentNode<P> insert(double t, int key, P object) {
        if (root == null) {
            root = new TournamentNode<>(key, object);
            return root;
        }

        // else -- NOTE: I do not use a stack to keep track of the traversed path. It can be done by keeping a parent
        //               pointer in every node. Requires more space (obviously), but it makes the code nicer imo.
        TournamentNode<P> leaf = root;

        // find the location to insert, which is always a leaf
        while (!leaf.isLeaf()) {
            if (key < leaf.getKey()) leaf = leaf.getLeftChild();
            else leaf = leaf.getRightChild();
        }

        // TODO handle duplicate key
        //assert tmp_node == null || key != tmp_node.getKey();

        // let's insert it
        TournamentNode<P> new_node = new TournamentNode<>(key, object);
        // attach the new node to the leaf node and alter the old leaf node such that it becomes an internal node
        // which means the old leaf must be moved down a level and become the new leaf's sibling
        if (key < leaf.getKey()) {
            leaf.setLeftChild(new_node);
            leaf.setRightChild(new TournamentNode<>(nextKey++, leaf.getObject()));
        }
        else  {
            // here we have to swap the keys as we always let inner nodes have the key of its right leaf
            // it is safe to do as everything in the left subtree is less than the new key
            leaf.setRightChild(new_node);
            leaf.setLeftChild(new TournamentNode<>(nextKey++, leaf.getObject()));
            leaf.setKey(new_node.getKey());
        }
        // time to rebalance the tree
        rebalance(t, leaf);

        this.nextKey += 1; // update the key counter
        return new_node;
    }

    /**
     * Update the winner of the node. Let it percolate up the tree. O(log^2 n)
     *
     * @param node the node in which the winner must be updated
     */
    private void updateWinners(TournamentNode<P> node) {
        TournamentNode<P> current = node;
        while (!current.isNull()) {
            // TODO
            current = current.getParent();
        }
    }

    /**
     * Update the winner of this node.
     *
     * @param node
     */
    private void updateWinner(double t, TournamentNode<P> node) {
        if (node.getLeftChild().isNull() && node.getRightChild().isNull()) {}
        else if (node.getLeftChild().isNull()) {
            node.setObject(node.getRightChild().getObject());
        } else if (node.getRightChild().isNull()) {
            node.setObject(node.getLeftChild().getObject());
        } else {
            P leftWinner = node.getLeftChild().getObject();
            P rightWinner = node.getRightChild().getObject();

            node.setObject(winnerFunction.findWinner(leftWinner, rightWinner));
        }
        node.createEvent(t);
    }

    public TournamentNode<P> delete(double t, int key) {
        // find the node to delete
        TournamentNode<P> node = find(key);

        if (node == null) return null;

        // should be safe to assume that the doubles match, otherwise I suck
        assert node.getKey() == key;

        return delete(t, node);
    }

    public TournamentNode<P> delete(double t, TournamentNode<P> node) {
        if (root == null || node == null || root.isNull() || node.isNull()) return null;

        TournamentNode<P> parent = node.getParent();
        boolean isLeftChild = parent.getLeftChild() == node;
        TournamentNode<P> balance_node = node.getParent(); // the node on which we start the balancing

        if (node.isLeaf()) {
            // just remove it!
            node.setParent(null);
            if (isLeftChild) parent.setLeftChild(null);
            else parent.setRightChild(null);
            // since we only store data in the internal nodes, we have to delete the parent and possibly the ancestors
            delete(t, parent.getKey());
        } else if (node.getLeftChild().isNull() || node.getRightChild().isNull()) {
            // we can simply splice its child to its parent
            TournamentNode<P> child = node.getLeftChild().isNull() ? node.getRightChild() : node.getLeftChild();
            if (node.getParent().getLeftChild() == node) node.getParent().setLeftChild(child);
            else node.getParent().setRightChild(child);
        } else {
            // have to find a predecessor or successor to replace it. Pick it based on weights to hopefully avoid the
            // number of rotations. Delete it to avoid duplicate and then put its double inside node
            TournamentNode<P> replacement;
            if (node.getLeftChild().getWeight() > node.getRightChild().getWeight()) {
                replacement = predecessor(node);
                // manually delete the replacement from the tree to avoid rotating before we're done
                assert replacement.isLeaf();
                replacement.getParent().setRightChild(null);
            } else {
                replacement = successor(node);
                // manually delete the replacement from the tree to avoid rotating before we're done
                assert replacement.isLeaf();
                replacement.getParent().setLeftChild(null);
            }
            // replace the double
            node.setKey(replacement.getKey());
            balance_node = replacement.getParent();
        }

        // time to rebalance the tree
        rebalance(t, balance_node);
        return node;
    }

    private void rebalance(double t, TournamentNode<P> tmp_node) {
        while (!tmp_node.isNull()) {
            tmp_node.setWeight(tmp_node.getLeftChild().getWeight() + tmp_node.getRightChild().getWeight());

            if (tmp_node.getRightChild().getWeight() < alpha * tmp_node.getWeight()) {

                if (tmp_node.getLeftChild().getLeftChild().getWeight() > (alpha + epsilon) * tmp_node.getWeight()) {
                    rotate_right(t, tmp_node);
                } else if (!tmp_node.getLeftChild().isNull()){
                    rotate_left(t, tmp_node.getLeftChild());
                    rotate_right(t, tmp_node);
                }
            } else if (tmp_node.getLeftChild().getWeight() < alpha * tmp_node.getWeight()) {
                if (tmp_node.getRightChild().getRightChild().getWeight() > (alpha + epsilon) * tmp_node.getWeight()) {
                    rotate_left(t, tmp_node);

                } else if (!tmp_node.getRightChild().isNull()){
                    rotate_right(t, tmp_node.getRightChild());
                    rotate_left(t, tmp_node.getLeftChild());
                }
            } else {
                // even if we don't rotate, we have to update the winner certificate
                updateWinner(t, tmp_node);
            }

            tmp_node = tmp_node.getParent();
        }
    }

    public TournamentNode<P> find(double key) {
        TournamentNode<P> tmp_node;

        if (root == null) return null;
        else if (root.getKey() == key) return root;

        else tmp_node = root;

        while (!tmp_node.isNull()) {
            if (key < tmp_node.getKey()) tmp_node = tmp_node.getLeftChild();
            else if (key >= tmp_node.getKey()) tmp_node = tmp_node.getRightChild();
            else break;
        }

        return !tmp_node.isNull() && tmp_node.getKey() == key ? tmp_node : null;
    }

    public void rotate_left(double t, TournamentNode<P> node) {
//        TournamentNode<P> tmp_node = node.getLeftChild();
//        double tmp_key = node.getKey();
//
//        node.setLeftChild(node.getRightChild());
//        node.setKey(node.getRightChild().getKey());
//        node.setRightChild(node.getLeftChild().getRightChild());
//
//        node.getLeftChild().setRightChild(node.getLeftChild().getLeftChild());
//        node.getLeftChild().setLeftChild(tmp_node);
//        node.getLeftChild().setKey(tmp_key);

        /*
               b               a
              / \             / \
             c   a    -->    b   e
                / \         / \
               d   e       c   d
         */

        TournamentNode<P> parent = node.getParent();

        TournamentNode<P> newRoot = node.getRightChild();
        TournamentNode<P> rightChild = node.getRightChild().getLeftChild();
        node.setRightChild(rightChild);
        newRoot.setLeftChild(node);

        if (!parent.isNull() && parent.isLeftChild(node)) {
            parent.setLeftChild(newRoot);
        } else if (!parent.isNull()) {
            parent.setRightChild(newRoot);
        }
        // update b's weights and then a's
        node.updateWeights();
        newRoot.updateWeights();

        // update the winners, we don't let it percolate though as there may be more rotations
        updateWinner(t, node);
        updateWinner(t, newRoot);
    }

    public void rotate_right(double t, TournamentNode<P> node) {
//        TournamentNode<P> tmp_node = node.getLeftChild();
//        double tmp_key = node.getKey();

//        node.setRightChild(node.getLeftChild());
//        node.setKey(node.getLeftChild().getKey());
//        node.setLeftChild(node.getRightChild().getLeftChild());
//
//        node.getRightChild().setLeftChild(node.getRightChild().getRightChild());
//        node.getRightChild().setRightChild(tmp_node);
//        node.getRightChild().setKey(tmp_key);

        /*
                a             b
               / \           / \
              b   e   -->   c   a
             / \               / \
            c   d             d   e
         */

        TournamentNode<P> parent = node.getParent();

        TournamentNode<P> newRoot = node.getLeftChild();
        node.setLeftChild(newRoot.getRightChild());
        newRoot.setRightChild(node);

        if (!parent.isNull() && parent.isLeftChild(node)) {
            parent.setLeftChild(newRoot);
        } else if (!parent.isNull()) {
            parent.setRightChild(newRoot);
        }
        // update a's weights and then b's
        node.updateWeights();
        newRoot.updateWeights();

        // update the winners, we don't let it percolate though as there may be more rotations
        updateWinner(t, node);
        updateWinner(t, newRoot);
    }

    public TournamentNode<P> predecessor(TournamentNode<P> node) {
        if (node == null || node.isNull()) return null;

        if (!node.getLeftChild().isNull()) {
            return findMaximum(node.getLeftChild());
        }

        TournamentNode<P> ancestor = node.getParent();
        TournamentNode<P> child = node;
        while (!ancestor.isNull() && ancestor.getLeftChild() == child) {
            child = ancestor;
            ancestor = ancestor.getParent();
        }

        return ancestor;
    }

    public TournamentNode<P> predecessor(double key) {
        return predecessor(find(key));
    }

    public TournamentNode<P> successor(TournamentNode<P> node) {
        if (node == null || node.isNull()) return null;

        if (!node.getRightChild().isNull()) {
            return findMinimum(node.getRightChild());
        }

        TournamentNode<P> ancestor = node.getParent();
        TournamentNode<P> child = node;

        while (!ancestor.isNull() && ancestor.getRightChild() == child) {
            child = ancestor;
            ancestor = ancestor.getParent();
        }

        return ancestor;
    }

    public TournamentNode<P> successor(double key) {
        return successor(find(key));
    }

    public TournamentNode<P> findMaximum(TournamentNode<P> node) {
        if (node == null || node.isNull()) return null;

        TournamentNode<P> maximum = node;
        while (!maximum.isNull()) {
            maximum = maximum.getRightChild();
        }

        return maximum.getParent();
    }

    public TournamentNode<P> findMinimum(TournamentNode<P> node) {
        if (node == null || node.isNull()) return null;

        TournamentNode<P> minimum = node;
        while (!minimum.isNull()) {
            minimum = minimum.getLeftChild();
        }

        return minimum.getParent();
    }
}
