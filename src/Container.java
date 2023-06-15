import java.util.ArrayList;
import java.util.HashSet;

public class Container<T> {
    public ArrayList<T> items;

    public Container() {
        this.items = new ArrayList<>();
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

    public int size() { return this.items.size(); }
}
