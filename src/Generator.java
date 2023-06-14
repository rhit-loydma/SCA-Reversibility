import java.util.HashMap;
import java.util.Stack;

public class Generator {
    HashMap<String, Character> rule;

    public Generator(HashMap<String, Character> rule) {
        this.rule = rule;
    }

    /**
     * Generates a Stranded Cellular Automata pattern for a starting string and certain height
     * @param start the starting row
     * @param height the number of rows to include (including the starting row)
     * @return the pattern
     */
    public String generatePattern(char[] start, int height) {
        Stack<String> rows = new Stack<>();
        Row r = new Row(start, this.rule, true, true);
        for(int i = 0; i < height; i++) {
            rows.push(r.toString() + "     " + i + "\n");
            r = r.getSuccessor();
        }

        StringBuilder sb = new StringBuilder("Rule: " + this.rule + "\n");
        while(!rows.isEmpty()){
            sb.append(rows.pop());
        }
        return sb.toString();
    }
}
