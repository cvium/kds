package weight_balanced_tree;


/**
 * Created by clausvium on 26/12/16.
 */
public class WeightBalancedTree<Key extends Comparable<Key>> {
    private double alpha = 0.288; // the weight ratio used for balancing - default value as described in
                                  // Advanced Data Structures by Peter Brass
    private double epsilon = 0.005;
    private WBTNode<Key> root;

    public WeightBalancedTree(int alpha) {
        this.alpha = alpha;
    }

    public WeightBalancedTree(int alpha, Key root) {
        this.alpha = alpha;
        this.root = new WBTNode<>(root, 1);
    }

    public WeightBalancedTree(int alpha, WBTNode<Key> root) {
        this.alpha = alpha;
        this.root = root;
        if (root.getLeftChild() == null && root.getRightChild() == null) this.root.setWeight(1);
    }

    public void insert(Key key) {
        if (root == null) {
            root = new WBTNode<>(key, 1);
            return;
        }

        // else -- NOTE: I do not use a stack to keep track of the traversed path. It can be done by keeping a parent
        //               pointer in every node. Requires more space (obviously), but it makes the code nicer imo.
        WBTNode<Key> tmp_node = root;
        WBTNode<Key> last_node = root;

        // find the location to insert
        while (tmp_node != null) {
            last_node = tmp_node;
            if (key.compareTo(tmp_node.getKey()) < 0) tmp_node = tmp_node.getLeftChild();
            else if (key.compareTo(tmp_node.getKey()) >= 0) tmp_node = tmp_node.getRightChild();
            else break;
        }

        // TODO handle duplicate key
        assert tmp_node == null || key.compareTo(tmp_node.getKey()) != 0;

        // key is distinct, let's insert it
        WBTNode<Key> new_node = new WBTNode<>(key, 1);
        new_node.setParent(last_node);
        // attach the new node to the last node
        if (key.compareTo(last_node.getKey()) < 0) last_node.setLeftChild(new_node);
        else last_node.setRightChild(new_node);
        // increment parent weight ??
        //new_node.getParent().setWeight(new_node.getParent().getWeight() + 1);

        // time to rebalance the tree
        rebalance(last_node);
    }

    public WBTNode<Key> delete(Key key) {
        if (root == null) return null;

        // find the node to delete
        WBTNode<Key> node = find(key);

        if (node == null) return null;

        // should be safe to assume that the keys match, otherwise I suck
        assert node.getKey() == key;

        WBTNode<Key> parent = node.getParent();
        boolean isLeftChild = parent.getLeftChild() == node;
        WBTNode<Key> balance_node = node.getParent(); // the node on which we start the balancing

        if (node.isLeaf()) {
            // just remove it!
            node.setParent(null);
            if (isLeftChild) parent.setLeftChild(null);
            else parent.setRightChild(null);
        } else if (node.getLeftChild().isNull() || node.getRightChild().isNull()) {
            // we can simply splice its child to its parent
            WBTNode<Key> child = node.getLeftChild().isNull() ? node.getRightChild() : node.getLeftChild();
            if (node.getParent().getLeftChild() == node) node.getParent().setLeftChild(child);
            else node.getParent().setRightChild(child);
        } else {
            // have to find a predecessor or successor to replace it. Pick it based on weights to hopefully avoid the
            // number of rotations. Delete it to avoid duplicate and then put its key inside node
            WBTNode<Key> replacement;
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
            // replace the key
            node.setKey(replacement.getKey());
            balance_node = replacement.getParent();
        }

        // time to rebalance the tree
        rebalance(balance_node);
        return node;
    }

    private void rebalance(WBTNode<Key> tmp_node) {
        while (!tmp_node.isNull()) {
            tmp_node.setWeight(tmp_node.getLeftChild().getWeight() + tmp_node.getRightChild().getWeight());

            if (tmp_node.getRightChild().getWeight() < alpha * tmp_node.getWeight()) {

                if (tmp_node.getLeftChild().getLeftChild().getWeight() > (alpha + epsilon) * tmp_node.getWeight()) {
                    rotate_right(tmp_node);

                    int tmp_left = tmp_node.getRightChild().getLeftChild().getWeight();
                    int tmp_right = tmp_node.getRightChild().getRightChild().getWeight();
                    tmp_node.getRightChild().setWeight(tmp_left + tmp_right);
                } else if (!tmp_node.getLeftChild().isNull()){
                    rotate_left(tmp_node.getLeftChild());
                    rotate_right(tmp_node);

                    tmp_node.getRightChild().setWeight(tmp_node.getRightChild().getLeftChild().getWeight()
                            + tmp_node.getRightChild().getRightChild().getWeight());
                    tmp_node.getLeftChild().setWeight(tmp_node.getLeftChild().getLeftChild().getWeight()
                            + tmp_node.getLeftChild().getRightChild().getWeight());
                }
            } else if (tmp_node.getLeftChild().getWeight() < alpha * tmp_node.getWeight()) {
                if (tmp_node.getRightChild().getRightChild().getWeight() > (alpha + epsilon) * tmp_node.getWeight()) {
                    rotate_left(tmp_node);
                    tmp_node.getLeftChild().setWeight(tmp_node.getLeftChild().getLeftChild().getWeight() +
                            tmp_node.getLeftChild().getRightChild().getWeight());

                } else {
                    rotate_right(tmp_node.getRightChild());
                    rotate_left(tmp_node.getLeftChild());
                    tmp_node.getRightChild().setWeight(tmp_node.getRightChild().getLeftChild().getWeight() +
                            tmp_node.getRightChild().getRightChild().getWeight());

                    tmp_node.getLeftChild().setWeight(tmp_node.getLeftChild().getLeftChild().getWeight() +
                            tmp_node.getLeftChild().getRightChild().getWeight());
                }
            }

            tmp_node = tmp_node.getParent();
        }
    }

    public WBTNode<Key> find(Key key) {
        WBTNode<Key> tmp_node;

        if (root == null) return null;
        else if (root.getKey() == key) return root;

        else tmp_node = root;

        while (!tmp_node.isNull()) {
            if (key.compareTo(tmp_node.getKey()) < 0) tmp_node = tmp_node.getLeftChild();
            else if (key.compareTo(tmp_node.getKey()) >= 0) tmp_node = tmp_node.getRightChild();
            else break;
        }

        return !tmp_node.isNull() && tmp_node.getKey() == key ? tmp_node : null;
    }

    public void rotate_left(WBTNode<Key> node) {
        WBTNode<Key> tmp_node = node.getLeftChild();
        Key tmp_key = node.getKey();

        node.setLeftChild(node.getRightChild());
        node.setKey(node.getRightChild().getKey());
        node.setRightChild(node.getLeftChild().getRightChild());

        node.getLeftChild().setRightChild(node.getLeftChild().getLeftChild());
        node.getLeftChild().setLeftChild(tmp_node);
        node.getLeftChild().setKey(tmp_key);
    }

    public void rotate_right(WBTNode<Key> node) {
        WBTNode<Key> tmp_node = node.getLeftChild();
        Key tmp_key = node.getKey();

        node.setRightChild(node.getLeftChild());
        node.setKey(node.getLeftChild().getKey());
        node.setLeftChild(node.getRightChild().getLeftChild());

        node.getRightChild().setLeftChild(node.getRightChild().getRightChild());
        node.getRightChild().setRightChild(tmp_node);
        node.getRightChild().setKey(tmp_key);
    }

    public WBTNode<Key> predecessor(WBTNode<Key> node) {
        if (node == null || node.isNull()) return null;

        if (!node.getLeftChild().isNull()) {
            return findMaximum(node.getLeftChild());
        }

        WBTNode<Key> ancestor = node.getParent();
        WBTNode<Key> child = node;
        while (!ancestor.isNull() && ancestor.getLeftChild() == child) {
            child = ancestor;
            ancestor = ancestor.getParent();
        }

        return ancestor;
    }

    public WBTNode<Key> predecessor(Key key) {
        return predecessor(find(key));
    }

    public WBTNode<Key> successor(WBTNode<Key> node) {
        if (node == null || node.isNull()) return null;

        if (!node.getRightChild().isNull()) {
            return findMinimum(node.getRightChild());
        }

        WBTNode<Key> ancestor = node.getParent();
        WBTNode<Key> child = node;

        while (!ancestor.isNull() && ancestor.getRightChild() == child) {
            child = ancestor;
            ancestor = ancestor.getParent();
        }

        return ancestor;
    }

    public WBTNode<Key> successor(Key key) {
        return successor(find(key));
    }

    public WBTNode<Key> findMaximum(WBTNode<Key> node) {
        if (node == null || node.isNull()) return null;

        WBTNode<Key> maximum = node;
        while (!maximum.isNull()) {
            maximum = maximum.getRightChild();
        }

        return maximum.getParent();
    }

    public WBTNode<Key> findMinimum(WBTNode<Key> node) {
        if (node == null || node.isNull()) return null;

        WBTNode<Key> minimum = node;
        while (!minimum.isNull()) {
            minimum = minimum.getLeftChild();
        }

        return minimum.getParent();
    }
}
