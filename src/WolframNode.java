import org.w3c.dom.Node;

import java.util.*;

public class WolframNode {
    Set<String> neighborhoods;
    int b;
    WolframNode left;
    WolframNode right;
    boolean frontier;

    public WolframNode(HashMap<String, Integer> rule) {
        this.b=-1;
        this.neighborhoods = rule.keySet();
        Container<Set<String>> prev = new Container<>();
        this.left = new WolframNode(0, this.neighborhoods, rule, prev);
        this.right = new WolframNode(1, this.neighborhoods, rule, prev);
    }

    public WolframNode(int b, Set<String> neighborhoods, HashMap<String, Integer> rule, Container<Set<String>> prev) {
        this.b = b;

        //find this node's neighborhoods list based on b value
        this.neighborhoods = new HashSet<>();
        for(String c: neighborhoods) {
            String last = c.substring(1);
            String x = last + "0";
            if(rule.get(x) == b) {
                this.neighborhoods.add(x);
            }

            String y = last + "1";
            if(rule.get(y) == b) {
                this.neighborhoods.add(y);
            }
        }

        //check for frontier node, stops infinite loop
        this.frontier = prev.contains(this.neighborhoods);

        if (!this.neighborhoods.isEmpty() && !this.frontier) {
            prev.add(this.neighborhoods);
            this.left = new WolframNode(0, this.neighborhoods, rule, prev);
            this.right = new WolframNode(1, this.neighborhoods, rule, prev);
        }
    }

    public  boolean isSurjective() {
        if(this.neighborhoods.isEmpty()) {
            return false;
        } else if(this.frontier) {
            return true;
        }
        return this.left.isSurjective() && this.right.isSurjective();
    }

    public void findGoEs(String path, Container<String> container) {
        if(this.neighborhoods.isEmpty()) {
            container.add(path);
            return;
        } else if(this.frontier) {
            return;
        }
        this.left.findGoEs(path + "0", container);
        this.right.findGoEs(path + "1", container);
    }

}
