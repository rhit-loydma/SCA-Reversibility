package Core;

import java.util.*;
import BoundaryConditions.NullRow;

public abstract class Rule {

    protected HashMap<String, Character> map;
    protected ArrayList<Character> states;
    protected int t;
    protected int c;
    protected int maxT;
    protected int maxC;
    protected String crossing;
    protected String turning;

    public Rule(int c, int t) {
        this.map = new HashMap<>();
        this.states = new ArrayList<>();
        this.setRuleCounts();
        this.c = c;
        this.crossing = Integer.toString(c, 2);
        this.t = t;
        this.turning = Integer.toString(t, 2);
        this.populateStates();
        this.generateRuleMap();
    }

    public abstract void setRuleCounts();
    public abstract void populateStates();

    public abstract void generateRuleMap();

    public char getNext(String neighborhood) {
        return this.map.get(neighborhood);
    }

    public Set<String> neighborhoods() {
        return this.map.keySet();
    }

    public String toString() {
        return "turning rule: " + this.t + ", crossing rule: " + this.c;
    }

    public String toDebugString(){
        StringBuilder sb = new StringBuilder("\u001B[1;36m" + "  ");
        for(char c: states) { //header row
            sb.append(c);
            sb.append(" ");
        }
        sb.append('\n');
        for(char c: states) {
            sb.append("\u001B[1;36m" + c);
            sb.append("\u001B[0m "); //reset bold
            for(char d: states) {
                char output = map.get(""+c+d);
                sb.append(getDebugColor(output));
                sb.append(map.get(""+c+d));
                sb.append(" ");
            }
            sb.append('\n');
        }
        sb.append("\u001B[0m"); //reset colors
        return sb.toString();
    }

    public abstract String getDebugColor(char c);

    public boolean isBalanced() {
        HashMap<Character, Integer> counts = new HashMap<>();
        for (char c : states) {
            counts.put(c, 0);
        }
        System.out.println(map.toString());
        for (char l : states) {
            for (char r : states) {
                System.out.println("" + l + r);
                char state = map.get("" + l + r);
                counts.put(state, counts.get(state) + 1);
            }
        }
        //System.out.println(counts.toString());
        for (char c : states) {
            if (counts.get(c) != states.size()) {
                return false;
            }
        }
        return true;
    }

    public boolean isSurjective() {
        if(!this.isBalanced()) {
            return false;
        }
        Node n = new Node(this);
        return n.isSurjective();
    }

    public boolean isInjective() {
        if(!this.isSurjective()) {
            return false;
        }
        SequentTable table = new SequentTable(this);
        return table.isInjective();
    }

    public void findOrphans(int stoppingWidth, HashMap<Integer, ArrayList<String>> map) {
        String pattern;
        int length = 0;
        Queue<String> q = new LinkedList<>();
        for(char c: this.states) {
            q.add(""+c);
        }
        while(!q.isEmpty()) {
            pattern = q.poll();
            if(pattern.length() != length) {
                length = pattern.length();
            }
            if(length <= stoppingWidth) {
                if(this.needToCheck(pattern, map)) {
                    Row r = new NullRow(pattern.toCharArray(), this, false);
                    if(r.findPredecessors().isEmpty()) { //found orphan
                        map.get(pattern.length()).add(pattern);
                    } else {
                        for(char c: this.states) {
                            q.add(pattern+c);
                        }
                    }
                }
            }
        }


    }

    public boolean needToCheck(String pattern, HashMap<Integer, ArrayList<String>> map) {
        for(ArrayList<String> list : map.values()) {
            for(String s: list) {
                if(pattern.endsWith(s)) {
                    return false;
                }
            }
        }
        return true;
    }
}
