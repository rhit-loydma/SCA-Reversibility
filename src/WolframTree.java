import java.util.*;

public class WolframTree {
    HashMap<String, Integer> map;
    int rule;
    WolframNode root;

    public WolframTree(int rule) {
        this.map = this.generateRuleMap(rule);
        this.rule = rule;
        this.root = new WolframNode(this.map);
    }

    public HashMap<String, Integer> generateRuleMap(int rule) {
        HashMap<String, Integer> map = new HashMap<>();
        String bin = Integer.toString(rule, 2);
        bin = "0".repeat(8 - bin.length()) + bin;
        for(int i = 0; i < 8; i++) {
            String neighborhood = Integer.toBinaryString(i);
            neighborhood = "0".repeat(3 - neighborhood.length()) + neighborhood;
            map.put(neighborhood, bin.charAt(7 - i) - '0');
        }
        return map;
    }

    public void determineSurjectivity() {
        if(this.root.isSurjective()) {
            System.out.println("Rule " + this.rule + " is surjective.");
        }
    }

    public void findGoEs() {
        Container<String> container = new Container<>();
        this.root.findGoEs("", container);
        System.out.println("Rule " + this.rule + " has Garden of Edens: " + container.toString());
    }

    static class Container<T> {
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
}
