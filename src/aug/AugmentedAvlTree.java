package aug;

public abstract class AugmentedAvlTree<K extends Comparable<K>, V, A> {
    private Node root;

    /**
     * Constructs an empty aug Avl tree.
     */
    public AugmentedAvlTree() {
        root = new Node();
    }

    public Node getRoot() {
        return root;
    }

    protected abstract A getDefaultProperty();

    protected abstract A augmentation(Node left, Node right);

    protected abstract Propagation<A> propertySearch(Node curr, A target);

    public Node searchByKey(K target) {
        return root.searchByKey(target);
    }

    public Node searchByProperty(A target) {
        return root.searchByProperty(target);
    }

    /**
     * Inserts the key-value pair into the Avl tree.
     * Behaviour requires that if curr.key <= k then the pair will be inserted
     * into the right subtree.
     * @param k Key to be inserted.
     * @param v Value to be inserted.
     */
    public void insert(K k, V v) {
        root = root.insert(k, v);
    }

    public Node deleteByKey(K k) {
        Node.DeletionResult result = root.deleteByKey(k);
        root = result.root;
        return result.deleted;
    }

    public Node deleteByProperty(A p) {
        Node.DeletionResult result = root.deleteByProperty(p);
        root = result.root;
        return result.deleted;
    }

    /**
     * Chops the entire search tree.
     */
    protected void chop() {
        root = new Node();
    }

    /**
     * An intermediate propagation result of a property search.
     * @param <A> Type of the property.
     */
    public static class Propagation<A> {
        public final int order;
        public final A retarget;

        /**
         * Constructor of a propergation immutable object.
         * @param order Go to the right subtree for positive value. Go to the
         *              left for negative value. Terminate the search for 0.
         * @param retarget A new target for a property search.
         */
        public Propagation(int order, A retarget) {
            this.order = order;
            this.retarget = retarget;
        }
    }

    /**
     * An implementation of a balanced aug binary search tree. (AVL)
     */
    protected class Node {
        private K key;
        private V value;
        private A property;
        private int height;
        private Node left;
        private Node right;

        public Node() {
            property = getDefaultProperty();
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }

        public A getProperty() {
            return property;
        }

        public Node getLeft() {
            return left;
        }

        public Node getRight() {
            return right;
        }

        public boolean isNil() {
            return left == null && right == null;
        }

        private Node searchByKey(K target) {
            if (isNil()) {
                return null;
            } else if (key.equals(target)) {
                return this;
            } else if (key.compareTo(target) <= 0) {
                return right.searchByKey(target);
            } else {
                return left.searchByKey(target);
            }
        }

        private Node searchByProperty(A target) {
            if (isNil()) {
                return null;
            }

            Propagation<A> result = propertySearch(this, target);
            if (result.order > 0) {
                return right.searchByProperty(result.retarget);
            } else if (result.order < 0) {
                return left.searchByProperty(result.retarget);
            } else {
                return this;
            }
        }

        /**
         * Inserts a key-value pair into the tree.
         * @param k Key.
         * @param v Value.
         * @return The new root of the tree.
         */
        private Node insert(K k, V v) {
            if (isNil()) {
                left = new Node();
                right = new Node();
                initializeLeaf(k, v);
            } else if (key.compareTo(k) <= 0) {
                right = right.insert(k, v);
            } else {
                left = left.insert(k, v);
            }
            return balance();
        }

        /**
         * Deletes the node by a key k.
         * @param k The key to be deleted.
         * @return The new root of the tree.
         */
        private DeletionResult deleteByKey(K k) {
            DeletionResult result;
            if (isNil()) {
                return new DeletionResult(this, null);
            }

            if (key.equals(k)) {
                if (left.isNil() && right.isNil()) {
                    return new DeletionResult(new Node(), this);
                } else if (left.isNil()) {
                    return new DeletionResult(right, this);
                } else if (right.isNil()) {
                    return new DeletionResult(left, this);
                }

                Node deleted = new Node();
                deleted.key = key;
                deleted.value = value;

                /* Delete minimum of the right subtree. */
                result = right.deleteMinimalKey();
                this.right = result.root;
                this.key = result.deleted.key;
                this.value = result.deleted.value;

                result = new DeletionResult(null, deleted);
            } else if (key.compareTo(k) <= 0) {
                result = right.deleteByKey(k);
                right = result.root;
            } else {
                result = left.deleteByKey(k);
                left = result.root;
            }

            result.root = balance();
            return result;
        }

        /**
         * Deletes the minimum of the right subtree and balance on the way up.
         * @return A two-tuple (new root, deleted node).
         */
        private DeletionResult deleteMinimalKey() {
            if (left.isNil()) {
                return new DeletionResult(right, this);
            } else if (left.height == 1) {
                Node temp = left;
                left = new Node();
                return new DeletionResult(balance(), temp);
            } else{
                DeletionResult result = left.deleteMinimalKey();
                left = result.root;
                result.root = balance();
                return result;
            }
        }

        /**
         * A helper class for the return value of deleteMinimalKey.
         */
        private class DeletionResult {
            private Node root;
            private final Node deleted;

            private DeletionResult(Node root, Node deleted) {
                this.root = root;
                this.deleted = deleted;
            }
        }

        /**
         * Deletes the node with property p.
         * @param p The property to be spotted and deleted.
         * @return The new root of the tree.
         */
        private DeletionResult deleteByProperty(A p) {
            DeletionResult result;
            Propagation<A> guide = propertySearch(this, p);
            if (isNil()) {
                return new DeletionResult(this, null);
            }

            if (guide.order > 0) {
                result = right.deleteByProperty(guide.retarget);
                right = result.root;
            } else if (guide.order < 0) {
                result = left.deleteByProperty(guide.retarget);
                left = result.root;
            } else {
                if (left.isNil() && right.isNil()) {
                    return new DeletionResult(new Node(), this);
                } else if (left.isNil()) {
                    return new DeletionResult(right, this);
                } else if (right.isNil()) {
                    return new DeletionResult(left, this);
                }
                Node deleted = new Node();
                deleted.key = key;
                deleted.value = value;

                /* Delete minimum of the right subtree. */
                result = right.deleteMinimalKey();
                this.right = result.root;
                this.key = result.deleted.key;
                this.value = result.deleted.value;

                result = new DeletionResult(null, deleted);
            }

            result.root = balance();
            return result;
        }

        private void maintain() {
            if (isNil()) {
                return;
            }
            height = max(left.height, right.height) + 1;
            property = augmentation(left, right);
        }

        private int max(int a, int b) {
            return a > b ? a : b;
        }

        private Node balance() {
            Node balanced;
            int bf = balanceFactor();
            if (bf < 2 && bf > -2) {
                maintain();
                return this;
            }

            if (bf == 2) {
                if (right.balanceFactor() == -2) {
                    /* Double left */
                    Node r = right;
                    Node rl = right.left;
                    Node rlr = right.left.right;

                    this.right = rl;
                    rl.right = r;
                    r.left = rlr;
                    r.maintain();
                    rl.maintain();
                    this.maintain();
                    balanced = this.balance();
                } else {
                    /* Single left */
                    Node r = right;
                    Node rl = right.left;
                    this.right = rl;
                    r.left = this;
                    this.maintain();
                    r.maintain();
                    balanced = r;
                }
            } else {
                if (left.balanceFactor() == 2) {
                    /* Double right*/
                    Node l = left;
                    Node lr = left.right;
                    Node lrl = left.right.left;

                    this.left = lr;
                    lr.left = l;
                    l.left = lrl;
                    l.maintain();
                    lr.maintain();
                    this.maintain();
                    balanced = this.balance();
                } else {
                    /* Single right*/
                    Node l = left;
                    Node lr = left.right;
                    this.left = lr;
                    l.right = this;
                    this.maintain();
                    l.maintain();
                    balanced = l;
                }
            }

            assert balanced != null;
            return balanced;
        }

        private void initializeLeaf(K k, V v) {
            key = k;
            value = v;
            height = 1;
            property = augmentation(left, right);
        }

        private int balanceFactor() {
            return right.height - left.height;
        }

    }
}
