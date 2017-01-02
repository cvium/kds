package tournament_tree;

import kds.EventQueue;
import kds.KDS;
import kds.solvers.EigenSolver;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import utils.Primitive;

import java.util.ArrayList;

/**
 * Created by clausvium on 22/12/16.
 */
public class TournamentTree<P extends Primitive> implements KDS<P, TournamentEvent<P>> {
    private double alpha = 0.288; // the weight ratio used for balancing - default value as described in
                                  // Advanced Data Structures by Peter Brass
    private double epsilon = 0.005;
    private TournamentNode<P> root; // aka the winner
    private EigenSolver solver = new EigenSolver();
    private TournamentTreeWinner<P> winnerFunction;
    private EventQueue<TournamentEvent<P>> eq;
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
        if (!audit(t))
            audit(t);
    }

    @Override
    public boolean audit(double t) {
        double smallestDistance = Double.MAX_VALUE;
        ArrayList<Double> distances = new ArrayList<>();
        for (TournamentNode<P> leaf : leaves) {
            double val = winnerFunction.computeValue(leaf.getWinner());
            if (smallestDistance > val) smallestDistance = val;
            distances.add(val);
        }
        boolean valid = true;
        if (winnerFunction.computeValue(root.getWinner()) != smallestDistance) {
            valid = false;
            System.out.println("Winner not valid. Is: " + winnerFunction.computeValue(root.getWinner()) + " should be: " + smallestDistance);
            System.out.println("Distances: " + distances);
        }
        if (!validate(root)) {
            valid = false;
            System.out.println("The tree itself is not valid :(");
        }
        return valid;
    }

    public boolean validate(TournamentNode<P> node) {
        if (node == null || node.isNull()) return true;
        boolean leftValid = validate(node.getLeftChild());
        boolean rightValid = validate(node.getRightChild());

        boolean valid = leftValid && rightValid;

        if (!valid) return false; // no need to check, TODO print out offenders

        if (!node.getLeftChild().isNull()) {
            valid = node.getLeftChild().getKey() < node.getKey() && isBalanced(node.getLeftChild(), node);
        }
        if (!node.getRightChild().isNull()) {
            valid = valid && node.getKey() <= node.getRightChild().getKey() && isBalanced(node.getRightChild(), node);
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
            this.leaves.add(insert(starttime, ++nextKey, leaf));
        }
    }

    @Override
    public void process(TournamentEvent<P> event, double t) {
        eq.remove(event);

        P oldWinner = event.getNode().getWinner();
        if (oldWinner == event.getNode().getLeftChild().getWinner()) {
            event.getNode().setWinner(event.getNode().getRightChild().getWinner());
        } else {
            event.getNode().setWinner(event.getNode().getLeftChild().getWinner());
        }

        event.getNode().createEvent(solver, event.getFailureTime(), winnerFunction, true);

        // percolate
        updateWinners(t, event.getNode().getParent());
    }

    @Override
    public EventQueue<TournamentEvent<P>> getEventQueue() {
        return eq;
    }

    public ArrayList<P> getPrimitives() {
        ArrayList<P> primitives = new ArrayList<>();
        for (TournamentNode<P> leaf : leaves) {
            primitives.add(leaf.getWinner());
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
     * Inserts P winner with int key at time t
     *
     * @param t the time at which the winner is inserted
     * @param key the key of the node
     * @param winner the winner associated with the key
     * @return the new leaf node
     */
    public TournamentNode<P> insert(double t, int key, P winner) {
        if (root == null) {
            root = new TournamentNode<>(key, winner);
            root.setRightChild(new TournamentNode<>(key, winner));
            return root;
        }

        // else -- NOTE: I do not use a stack to keep track of the traversed path. It can be done by keeping a parent
        //               pointer in every node. Requires more space (obviously), but it makes the code nicer imo.
        TournamentNode<P> leaf = root;

        // find the location to insert, which is always a leaf
        while (!leaf.isNull()) {
            if (key < leaf.getKey()) leaf = leaf.getLeftChild();
            else leaf = leaf.getRightChild();
        }

        // since we stopped at a null node, grab its parent
        leaf = leaf.getParent();

        // TODO handle duplicate key
        //assert tmp_node == null || key != tmp_node.getKey();

        // let's insert it
        TournamentNode<P> new_node = new TournamentNode<>(key, winner);
        // attach the new node to the leaf node and alter the old leaf node such that it becomes an internal node
        // which means the old leaf must be moved down a level and become the new leaf's sibling
        if (key < leaf.getKey()) {
            leaf.setLeftChild(new_node);
            leaf.setRightChild(new TournamentNode<>(leaf.getKey(), leaf.getWinner()));
        }
        else  {
            // here we have to swap the keys as we always let inner nodes have the key of its right leaf
            // it is safe to do as everything in the left subtree is less than the new key
            leaf.setRightChild(new_node);
            leaf.setLeftChild(new TournamentNode<>(leaf.getKey(), leaf.getWinner()));
            leaf.setKey(new_node.getKey());
        }
        // time to rebalance the tree
        rebalance(t, leaf);

        return new_node;
    }

    /**
     * Update the winner of the node. Let it percolate up the tree. O(log^2 n)
     *
     * @param node the node in which the winner must be updated
     */
    private void updateWinners(double t, TournamentNode<P> node) {
        TournamentNode<P> current = node;
        while (!current.isNull()) {
            // TODO
            updateWinner(t, current);
            current = current.getParent();
        }
    }

    /**
     * Update the winner of this node.
     *
     * @param node
     */
    private void updateWinner(double t, TournamentNode<P> node) {
        if (node == null || node.isNull() || node.getLeftChild().isNull() && node.getRightChild().isNull()) return;

        // update the positions
        node.getWinner().updatePosition(t);
        if (!node.getLeftChild().isNull()) node.getLeftChild().getWinner().updatePosition(t);
        if (!node.getRightChild().isNull()) node.getRightChild().getWinner().updatePosition(t);

        if (node.getLeftChild().isNull()) {
            node.setWinner(node.getRightChild().getWinner());
        } else if (node.getRightChild().isNull()) {
            node.setWinner(node.getLeftChild().getWinner());
        } else {
            P leftWinner = node.getLeftChild().getWinner();
            P rightWinner = node.getRightChild().getWinner();

            node.setWinner(winnerFunction.findWinner(leftWinner, rightWinner));
        }

        if (node.getEvent() != null) eq.remove(node.getEvent());
        eq.add(node.createEvent(solver, t, winnerFunction, false));
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
            tmp_node.updateWeights();

            if (!isBalanced(tmp_node.getRightChild(), tmp_node)) {

                if (!tmp_node.getLeftChild().getLeftChild().isNull() &&
                        tmp_node.getLeftChild().getLeftChild().getWeight() > (alpha + epsilon) * tmp_node.getWeight()) {
                    rotate_right(t, tmp_node);
                } else if (!tmp_node.getLeftChild().isNull()){
                    rotate_left(t, tmp_node.getLeftChild());
                    rotate_right(t, tmp_node);
                }
            } else if (!isBalanced(tmp_node.getLeftChild(), tmp_node)) {
                if (!tmp_node.getRightChild().getRightChild().isNull() &&
                        tmp_node.getRightChild().getRightChild().getWeight() > (alpha + epsilon) * tmp_node.getWeight()) {
                    rotate_left(t, tmp_node);

                } else if (!tmp_node.getRightChild().isNull()){
                    rotate_right(t, tmp_node.getRightChild());
                    rotate_left(t, tmp_node.getLeftChild());
                }
            }
            // even if we don't rotate, we have to update the winner certificate
            updateWinner(t, tmp_node);

            tmp_node = tmp_node.getParent();
        }
    }

    public boolean isBalanced(TournamentNode<P> node, TournamentNode<P> other_node) {
        return node.getWeight() >= alpha * other_node.getWeight();
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
        System.out.println("rotate_left");
        TournamentNode<P> parent = node.getParent();

        TournamentNode<P> newRoot = node.getRightChild();
        TournamentNode<P> rightChild = node.getRightChild().getLeftChild();
        if (!rightChild.isNull()) node.setRightChild(rightChild);

        if (!parent.isNull() && parent.isLeftChild(node)) {
            parent.setLeftChild(newRoot);
        } else if (!parent.isNull()) {
            parent.setRightChild(newRoot);
        } else {
            // this means that 'node' is the root and since 'newRoot' is a child of 'node', we have to manually
            // reset its parent to avoid a weird cycle
            newRoot.setParent(null);
            // update root
            root = newRoot;
        }
        newRoot.setLeftChild(node);
        // update b's weights and then a's
        node.updateWeights();
        newRoot.updateWeights();

        // update the winners, we don't let it percolate though as there may be more rotations
        updateWinner(t, node);
        //updateWinner(t, newRoot);
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
        System.out.println("rotate_right");
        TournamentNode<P> parent = node.getParent();

        TournamentNode<P> newRoot = node.getLeftChild();
        TournamentNode<P> leftChild = newRoot.getRightChild();
        if (!leftChild.isNull()) node.setLeftChild(leftChild);

        if (!parent.isNull() && parent.isLeftChild(node)) {
            parent.setLeftChild(newRoot);
        } else if (!parent.isNull()) {
            parent.setRightChild(newRoot);
        } else {
            // this means that 'node' is the root and since 'newRoot' is a child of 'node', we have to manually
            // reset its parent to avoid a weird cycle
            newRoot.setParent(null);
            root = newRoot;
        }
        newRoot.setRightChild(node);
        // process a's weights and then b's
        node.updateWeights();
        newRoot.updateWeights();

        // process the winners, we don't let it percolate though as there may be more rotations
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
