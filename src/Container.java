import java.util.HashSet;

public class Container<T> {
    private HashSet<T> items;

    public Container() {
        this.items = new HashSet<>();
    }

    public void add(T s) {
        this.items.add(s);
    }

    public boolean contains(T s) {
        return this.items.contains(s);
    }

    public String toString() {
        return this.items.toString();
    }
}
