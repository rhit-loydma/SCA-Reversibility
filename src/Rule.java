import java.util.*;

public abstract class Rule {

    int number;
    HashMap<String, Character> map;
    ArrayList<Character> states;
    int maxT;
    int maxC;

    public Rule(int number) {
        this.number = number;
        this.map = new HashMap<>();
        this.states = new ArrayList<>();
        this.setRuleCounts();
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
        return "turning rule: " + this.number/maxC + ", crossing rule: " + this.number % maxC;
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
        for (char l : states) {
            for (char r : states) {
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
        Queue<String> q = new LinkedList<>();
        q.add("");
        while(!q.isEmpty()) {
//            System.out.println(q.toString());
//            System.out.println(map.toString());
            pattern = q.poll();
            if(pattern.length() == 0) {
                for(char c: this.states) {
                    q.add(pattern+c);
                }
            } else if(pattern.length() <= stoppingWidth) {
                boolean check = true;
                for(ArrayList<String> list : map.values()) {
                    for(String s: list) {
                        if(pattern.contains(s)) {
                            check = false;
                            break;
                        }
                    }
                }
                if(check) {
                    Row r = new Row(pattern.toCharArray(), this, false, "null");
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
}
