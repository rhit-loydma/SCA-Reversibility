import java.util.*;

public class WolframNode {
    Set<String> neighborhoods;
    int b;
    WolframNode left;
    WolframNode right;
    boolean frontier;
    boolean terminal;
    HashMap<String, Integer> rule;
    Set<Set<String>> prev;

    public WolframNode(HashMap<String, Integer> rule) {
        this.b=-1;
        this.rule = rule;
        this.prev = new HashSet<>();
        this.neighborhoods = rule.keySet();
    }

    public WolframNode(int b, Set<String> neighborhoods, HashMap<String, Integer> rule, Set<Set<String>> prev) {
        this.b = b;
        this.rule = rule;
        this.prev = prev;

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

        //check for terminal node
        if(this.neighborhoods.isEmpty()) {
            this.terminal = true;
        } else {
            //check for frontier node, stops infinite loop
            for(Set<String> n: prev) {
                if (this.neighborhoods.equals(n)) {
                    this.frontier = true;
                    break;
                }
            }

            this.prev.add(this.neighborhoods);
        }
    }

    //has to be done separately, so we can do breadth first
    public void generateChildren() {
        if(!this.frontier && !this.terminal) {
            this.left = new WolframNode(0, this.neighborhoods, this.rule, prev);
            this.right = new WolframNode(1, this.neighborhoods, this.rule, prev);
        }
    }

    public  boolean isSurjective() {
        if(this.terminal) {
            return false;
        } else if(this.frontier) {
            return true;
        }
        return this.left.isSurjective() && this.right.isSurjective();
    }

    public void findGoEs(String path, WolframTree.GOEcontainer container) {
        if(this.b != -1) {
            path += this.b;
        }
        if(this.terminal) {
            container.add(path);
            return;
        } else if(this.frontier) {
            return;
        }
        this.left.findGoEs(path, container);
        this.right.findGoEs(path, container);
    }

}
