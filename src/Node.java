import java.util.*;

public class Node {
    Set<String> neighborhoods;
    char b;
    HashMap<Character, Node> children;
    HashSet<Character> states;
    boolean frontier;

    public Node(Rule rule) {
        this.b = '_';
        this.neighborhoods = rule.map.keySet();
        this.states = rule.states;
        Container<Set<String>> prev = new Container<>();
        this.children = new HashMap<>();
        for(Character s: this.states) {
            children.put(s, new Node(s, this.neighborhoods, rule, prev));
        }
    }

    public Node(char b, Set<String> neighborhoods, Rule rule, Container<Set<String>> prev) {
        this.b = b;
        this.states = rule.states;

        //find this node's neighborhoods list based on b value
        this.neighborhoods = new HashSet<>();
        for(String c: neighborhoods) {
            String last = c.substring(1);
            for(Character s: this.states) {
                String x = last + s;
                if(rule.getNext(x) == b) {
                    this.neighborhoods.add(x);
                }
            }
        }

        //check for frontier node, stops infinite loop
        this.frontier = prev.contains(this.neighborhoods);

        if (!this.neighborhoods.isEmpty() && !this.frontier) {
            prev.add(this.neighborhoods);
            this.children = new HashMap<>();
            for(Character s: this.states) {
                children.put(s, new Node(s, this.neighborhoods, rule, prev));
            }
        }
    }

    public  boolean isSurjective() {
        if(this.neighborhoods.isEmpty()) {
            return false;
        } else if(this.frontier) {
            return true;
        }
        for(Character s: this.states) {
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
