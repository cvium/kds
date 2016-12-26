package dynotree;

/**
 * Created by clausvium on 23/12/16.
 */
public class LinkCutTree<Key> {

    public void insert(Node<Key> x) {

    }

    public void delete(Node<Key> x) {
        splay(x);
    }

    public void splay(Node<Key> x) {
        while (!x.isRoot()) {
            Node<Key> q = x.getParent();
            if (q.isRoot()) {
                if (q.getLeftChild() == x) rotate_right(x);
                else rotate_left(x);
            } else {
                Node<Key> r = q.getParent();
                if (r.getLeftChild() == q) {
                    if (q.getLeftChild() == x) {
                        rotate_right(q);
                        rotate_right(x);
                    }
                    else {
                        rotate_left(x);
                        rotate_right(x);
                    }
                } else {
                    if (q.getRightChild() == x) {
                        rotate_left(q);
                        rotate_left(x);
                    }
                    else {
                        rotate_right(x);
                        rotate_right(x);
                    }
                }
            }
        }
        x.update();
    }

    public void evert(Node<Key> x) {

    }

    /**
     * Exposes node x, which means x becomes the root of its tree and the leftmost node
     *
     * @param x
     */
    public void expose(Node<Key> x) {
        Node<Key> tmp = null;

        for (Node<Key> p = x; p != null; p = p.getParent()) {
            splay(p);
            p.setLeftChild(tmp);
            p.update();
            tmp = p;
        }
        splay(x);
    }

    /**
     * Returns the root of the tree containing x
     *
     * @param x the node from which we must find the root of the tree
     * @return root of the tree containing x
     */
    public Node<Key> root(Node<Key> x) {
        expose(x);
        while (x.getRightChild() != null) {
            x = x.getRightChild();
        }
        splay(x);
        return x;
    }

    /**
     * Connects x and y. It is assumed x is the root of its tree
     *
     * @param x The root node to connect to y
     * @param y The node to be parent of x
     */
    public void link(Node<Key> x, Node<Key> y) {
        expose(x);
        assert x.getRightChild() != null;
        x.setParent(y);
    }

    /**
     * Cuts the edge between x and its parent
     *
     * @param x the node to cut at
     */
    public void cut(Node<Key> x) {
        assert !x.isRoot();
        expose(x);

        x.getLeftChild().setParent(null);
        x.setLeftChild(null);

        x.update();
    }

    public void rotate_left(Node<Key> x) {
        Node<Key> q = x.getParent();
        Node<Key> r = q.getParent();
        q.normalize();
        x.normalize();
        q.setRightChild(x.getLeftChild());

        if (q.getRightChild() != null) q.getRightChild().setParent(q);
        x.setLeftChild(q);
        q.setParent(x);

        x.setParent(r);
        if (x.getParent() != null) {
            if (r.getLeftChild() == q) r.setLeftChild(x);
            else if (r.getRightChild() == q) r.setRightChild(x);
        }
        q.update();
    }

    public void rotate_right(Node<Key> x) {
        Node<Key> q = x.getParent();
        Node<Key> r = q.getParent();
        q.normalize();
        x.normalize();

        q.setLeftChild(x.getRightChild());
        if ((q.getLeftChild()) != null) q.getLeftChild().setParent(q);
        x.setRightChild(q);
        q.setParent(x);

        x.setParent(r);
        if (x.getParent() != null) {
            if (r.getLeftChild() == q) r.setLeftChild(x);
            else if (r.getRightChild() == q) r.setRightChild(x);
        }
        q.update();
    }
}
