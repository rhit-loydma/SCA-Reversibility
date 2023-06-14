import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;

public class Main {
    static Config config;

    /**
     * Initializes the config parser and starts an experiment mode
     * @param args
     */
    public static void main(String[] args) {
        config = new Config("config.properties");
        String mode = config.getType();
        switch (mode){
            case("pattern"):
                patternMode();
                break;
            case("surjective"):
                surjetiveMode();
                break;
            default: //GoE
                System.out.println("Not yet implemented");
        }
    }

    /**
     * Prints a pattern using initial conditions defined in a config file
     */
    public static void patternMode(){
        char[] start = config.getPatternString();
        int height = config.getPatternHeight();
        int crossing = config.getCrossingRule();
        int turning = config.getTurningRule();
        int max = 16;
        String mode = config.getMode();
        if(mode.equals("full")) {
            max = 512;
        }
        if(turning == -1) {
            for(int i = 0; i < max; i++) {
                if(crossing == -1) {
                    for(int j = 0; j < max; j++) {
                        generatePattern(new Rule(mode, i*max + j), start, height);
                    }
                } else {
                    generatePattern(new Rule(mode, i*max + crossing), start, height);
                }
            }
        } else {
            if(crossing == -1) {
                for(int j = 0; j < max; j++) {
                    generatePattern(new Rule(mode, turning*max + j), start, height);
                }
            } else {
                generatePattern(new Rule(mode, turning*max + crossing), start, height);
            }
        }
    }

    public static void surjetiveMode() {
        int max = 16;
        String mode = config.getMode();
        if(mode.equals("full")) {
            max = 512;
        }

        int crossing = config.getCrossingRule();
        int turning = config.getTurningRule();
        if(turning == -1) {
            for(int i = 0; i < max; i++) {
                if(crossing == -1) {
                    for(int j = 0; j < max; j++) {
                        checkSurjective(new Rule(mode, i*max+j));
                    }
                } else {
                    checkSurjective(new Rule(mode, i*max+crossing));
                }
            }
        } else {
            if(crossing == -1) {
                for(int j = 0; j < max; j++) {
                    checkSurjective(new Rule(mode, turning*max+j));
                }
            } else {
                checkSurjective(new Rule(mode, turning*max+crossing));
            }
        }
    }

    public static void generatePattern(Rule rule, char[] start, int height) {
        Stack<String> rows = new Stack<>();
        Row r = new Row(start, rule, true, true);
        for(int i = 0; i < height; i++) {
            rows.push(r.toString() + "     " + i + "\n");
            r = r.getSuccessor();
        }

        StringBuilder sb = new StringBuilder("Rule: " + rule.number + "\n");
        while(!rows.isEmpty()){
            sb.append(rows.pop());
        }
        System.out.println(sb.toString());
    }

    public static void checkSurjective(Rule rule) {
        Node n = new Node(rule);
        if(n.isSurjective()) {
            System.out.println("Rule " + rule.number + " is surjective");
        } else {
            System.out.println("Rule " + rule.number + " is not surjective");
        }
    }
}