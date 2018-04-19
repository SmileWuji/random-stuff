package aug;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class TreeList<E> extends AugmentedAvlTree<Integer, E, Integer> implements List<E> {
    private int size = 0;

    @Override
    protected Integer getDefaultProperty() {
        return 0;
    }

    @Override
    protected Integer augmentation(Node left, Node right) {
        return left.getProperty() + right.getProperty() + 1;
    }

    @Override
    protected Propagation<Integer> propertySearch(Node curr, Integer target) {
        int compared = curr.getProperty() - curr.getRight().getProperty();
        if (compared < target) {
            return new Propagation<>(
                1,
                target - curr.getLeft().getProperty() - 1
            );
        } else if (compared > target) {
            return new Propagation<>(
                -1,
                target
            );
        } else {
            return new Propagation<>(
                0,
                target
            );
        }
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return super.getRoot().isNil();
    }

    @Override
    public boolean add(E e) {
        super.insert(1, e);
        size++;
        return true;
    }
    @Override
    public boolean addAll(Collection<? extends E> collection) {
        collection.forEach( this::add );
        return true;
    }
    @Override
    public void clear() {
        super.chop();
        size = 0;
    }
    @Override
    public E get(int i) {
        if (i >= size || i < 0) {
            throw new IndexOutOfBoundsException();
        }
        return super.searchByProperty(i + 1).getValue();
    }

    @Override
    public E remove(int i) {
        Node result = super.deleteByProperty(i + 1);
        if (result != null) {
            size--;
            return result.getValue();
        }
        return null;
    }

    @Override
    public Iterator<E> iterator() {
        return listIterator(0);
    }

    @Override
    public ListIterator<E> listIterator() {
        return listIterator(0);
    }

    @Override
    public ListIterator<E> listIterator(int i) {
        return new TreeListIterator(0, size);
    }

    private class TreeListIterator implements ListIterator<E> {
        private int base;
        private int i;
        private int bound;
        private boolean valid;

        private TreeListIterator(int base, int bound) {
            valid = true;
            this.base = base;
            i = base;
            this.bound = bound;
        }

        @Override
        public boolean hasNext() {
            return valid && i < bound;
        }

        @Override
        public E next() {
            assert valid;
            return get(i++);
        }

        @Override
        public boolean hasPrevious() {
            return valid && i >= base;
        }

        @Override
        public E previous() {
            assert valid;
            return get(--i);
        }

        @Override
        public int nextIndex() {
            return i;
        }

        @Override
        public int previousIndex() {
            return i - 1;
        }

        @Override
        public void remove() {
            TreeList.super.deleteByProperty(i - 1);
            valid = false;
        }

        @Override
        public void set(E e) {
            throw new UnsupportedOperationException("Not implemented.");
        }

        @Override
        public void add(E e) {
            throw new UnsupportedOperationException("Not implemented.");
        }
    }

    @Override
    public boolean contains(Object o) {
        return stream().anyMatch( e -> e.equals(o) );
    }

    @Override
    public Object[] toArray() {
        Object[] res = new Object[size];
        TreeListIterator iter = new TreeListIterator(0, size);
        while (iter.hasNext()) {
            int i = iter.i;
            res[i] = iter.next();
        }
        return res;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T[] toArray(T[] ts) {
        return (T[]) toArray();
    }

    @Override
    public boolean remove(Object o) {
        TreeListIterator iter = new TreeListIterator(0, size);
        while (iter.hasNext()) {
            if (iter.next().equals(o)) {
                iter.remove();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        return collection.stream().allMatch( this::contains );
    }

    @Override
    public int indexOf(Object o) {
        TreeListIterator iter = new TreeListIterator(0, size);
        while (iter.hasNext()) {
            int i = iter.i;
            if (iter.next().equals(o)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        int i = -1;
        TreeListIterator iter = new TreeListIterator(0, size);
        while (iter.hasNext()) {
            if (iter.next().equals(o)) {
                i = iter.i;
            }
        }
        return i;
    }

    @Override
    public String toString() {
        return Arrays.toString(toArray());
    }

    @Override
    public boolean equals(Object o) {
        return (
            o != null &&
                o instanceof Collection &&
                containsAll((Collection) o)
        );
    }

    @Override
    public boolean addAll(int i, Collection<? extends E> collection) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public E set(int i, E e) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public void add(int i, E e) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public List<E> subList(int i, int i1) {
        throw new UnsupportedOperationException("Not implemented.");
    }

}
