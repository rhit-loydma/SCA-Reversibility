import java.util.*;

public class Node {
    Set<String> neighborhoods;
    char b;
    HashMap<Character, Node> children;
    HashSet<Character> states;
    boolean frontier;

    public Node(HashMap<String, Character> rule, HashSet<Character> states) {
        this.b = '_';
        this.neighborhoods = rule.keySet();
        this.states = states;
        Container<Set<String>> prev = new Container<>();
        this.children = new HashMap<>();
        for(Character s: states) {
            children.put(s, new Node(s, this.neighborhoods, rule, prev, states));
        }
    }

    public Node(char b, Set<String> neighborhoods, HashMap<String, Character> rule,
                Container<Set<String>> prev, HashSet<Character> states) {
        this.b = b;
        this.states = states;

        //find this node's neighborhoods list based on b value
        this.neighborhoods = new HashSet<>();
        for(String c: neighborhoods) {
            String last = c.substring(1);
            for(Character s: states) {
                String x = last + s;
                if(rule.get(x) == b) {
                    this.neighborhoods.add(x);
                }
            }
        }

        //check for frontier node, stops infinite loop
        this.frontier = prev.contains(this.neighborhoods);

        if (!this.neighborhoods.isEmpty() && !this.frontier) {
            prev.add(this.neighborhoods);
            this.children = new HashMap<>();
            for(Character s: states) {
                children.put(s, new Node(s, this.neighborhoods, rule, prev, states));
            }
        }
    }

    public  boolean isSurjective() {
        if(this.neighborhoods.isEmpty()) {
            return false;
        } else if(this.frontier) {
            return true;
        }
        for(Character s: states) {
            if(!children.get(s).isSurjective()) {
                return false;
            }
        }
        return true;
    }

    public void findGoEs(String path, Container<String> container) {
        if(this.neighborhoods.isEmpty()) {
            container.add(path);
            return;
        } else if(this.frontier) {
            return;
        }
        for(Character s: states) {
            children.get(s).findGoEs(path + s, container);
        }
    }

}
