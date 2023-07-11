import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

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
        for (char c : states) {
            if (counts.get(c) != states.size()) {
                return false;
            }
        }
        return true;
    }
}
