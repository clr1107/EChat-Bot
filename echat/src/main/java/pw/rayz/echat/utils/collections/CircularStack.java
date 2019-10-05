package pw.rayz.echat.utils.collections;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

public class CircularStack<T> extends AbstractCollection<T> {
    private final int size;
    private final T[] objects;
    private final AtomicInteger head = new AtomicInteger(0);

    public CircularStack(int size) {
        this.size = size;
        this.objects = (T[]) new Object[size];
    }

    public CircularStack(Collection<T> collection) {
        this(collection.size());
        collection.forEach(this::push);
    }

    public CircularStack(CircularStack<T> stack) {
        this.size = stack.size;
        this.objects = stack.objects;
    }

    public static final class CircularStackIterator<T> implements Iterator<T> {
        private final Object[] objects;
        private int pointer = 0;

        private CircularStackIterator(CircularStack<T> stack) {
            this.objects = stack.objects;
        }

        @Override
        public boolean hasNext() {
            return pointer < objects.length && objects[pointer] != null;
        }

        @Override
        public T next() {
            return (T) objects[pointer++];
        }
    }

    public void push(@Nonnull T t) {
        synchronized (objects) {
            objects[head.get()] = t;
        }

        head.set(Math.floorMod(head.get() + 1, size));
    }

    public T pop() {
        head.set(Math.floorMod(head.get() - 1, size));
        T t;

        synchronized (objects) {
            t = (T) objects[head.get()];
            objects[head.get()] = null;
        }

        return t;
    }

    public T peek() {
        synchronized (objects) {
            int index = Math.floorMod(head.get() - 1, size);
            return (T) objects[index];
        }
    }

    @Override
    public boolean isEmpty() {
        return peek() == null;
    }

    @Override
    public boolean add(T t) {
        push(t);
        return true;
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return new CircularStackIterator<>(this);
    }

    @Override
    public int size() {
        return size;
    }
}
