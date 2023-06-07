import java.util.Stack;

public class Generator {
    int cRule;
    int tRule;
    public Generator(int crossingRule, int turningRule) {
        this.cRule = crossingRule;
        this.tRule = turningRule;
    }

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
