import java.util.Stack;

public class Generator {
    int cRule;
    int tRule;

    /**
     * Class constructor for a certain rule set
     * @param crossingRule
     * @param turningRule
     */
    public Generator(int crossingRule, int turningRule) {
        this.cRule = crossingRule;
        this.tRule = turningRule;
    }

    /**
     * Generates a Stranded Cellular Automata pattern for a starting string and certain height
     * @param start the starting row
     * @param height the number of rows to include (including the starting row)
     * @return the pattern
     */
    public String generatePattern(String start, int height) {
        Stack<String> rows = new Stack<>();
        Row r = new Row(start, this.cRule, this.tRule, true);
        for(int i = 0; i < height; i++) {
            rows.push(r.toString() + "     " + i + "\n");
            r = r.getSuccessor();
        }

        StringBuilder sb = new StringBuilder("Crossing Rule: " + this.cRule
                + "\nTurning Rule: " + this.tRule + "\n");
        while(!rows.isEmpty()){
            sb.append(rows.pop());
        }
        return sb.toString();
    }
}
