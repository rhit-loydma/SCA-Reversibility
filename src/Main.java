import java.util.*;

public class Main {
    static Config config;

    /**
     * Initializes the config parser and starts an experiment mode
     * @param args
     */
    public static void main(String[] args) {
        config = new Config("config.properties");
        String mode = config.getType();
        switch (mode) {
            case ("pattern") -> patternMode();
            case ("surjective") -> surjetiveMode();
            case ("twins") -> twinsMode();
            default -> //GoE
                    GoEMode();
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

    public static void twinsMode() {
        char[] cells = config.getTwinString();
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
                        findTwins(new Rule(mode, i*max + j), cells);
                    }
                } else {
                    findTwins(new Rule(mode, i*max + crossing), cells);
                }
            }
        } else {
            if(crossing == -1) {
                for(int j = 0; j < max; j++) {
                    findTwins(new Rule(mode, turning*max + j), cells);
                }
            } else {
                findTwins(new Rule(mode, turning*max + crossing), cells);
            }
        }
    }

    public static void GoEMode() {
        String mode = config.getMode();
        int start = config.getGoEstartWidth();
        int end = config.getGoEendWidth();
        boolean wrap = config.getGoEwrapsAround();
        int crossing = config.getCrossingRule();
        int turning = config.getTurningRule();
        int max = 16;
        if(mode.equals("full")) {
            max = 512;
        }
        for(int w = start; w <= end; w++) {
            if(turning == -1) {
                for(int i = 0; i < max; i++) {
                    if(crossing == -1) {
                        for(int j = 0; j < max; j++) {
                            findGoEs(new Rule(mode, i*max+j),w,wrap);
                        }
                    } else {
                        findGoEs(new Rule(mode, i*max+crossing),w,wrap);
                    }
                }
            } else {
                if(crossing == -1) {
                    for(int j = 0; j < max; j++) {
                        findGoEs(new Rule(mode, turning*max+j),w,wrap);
                    }
                } else {
                    findGoEs(new Rule(mode, turning*max+crossing),w,wrap);
                }
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

    public static void findTwins(Rule rule, char[] cells) {
        Row r = new Row(cells, rule, false, true);
        HashSet<Row> twins = r.findTwins();
        if(twins.size() == 0){
            System.out.println(r.toString() + " has no twins under " + rule.toString());
        } else {
            System.out.println(r.toString() + " under rule " + rule.toString() + " has " + twins.size() + " twin(s):");
            for(Row t: twins) {
                System.out.println("  " + t.toString());
            }
        }
        //System.out.println(rule.toDebugString());
    }

    public static void findGoEs(Rule rule, int width, boolean wrapAround) {
        Container<String> configs = new Container<>();
        generateStrings(rule.states, width, "", configs);
        ArrayList<Row> goes = new ArrayList<>();
        ArrayList<Row> nonGoes = new ArrayList<>();
        for(String s: configs.items) {
            Row r = new Row(s.toCharArray(), rule, false, wrapAround);
            if(r.findPredecessors().isEmpty()) {
                goes.add(r);
            } else {
                nonGoes.add(r);
            }
        }
        System.out.println(rule.toString() + " has " + goes.size() + " GoE(s):");
        for(Row r: goes) {
            System.out.println("  " + r.toString());
        }
        System.out.println(rule.toString() + " has " + nonGoes.size() + " non-GoE(s):");
        for(Row r: nonGoes) {
            System.out.println("  " + r.toString());
        }
        System.out.println(rule.toDebugString());
    }

    public static void generateStrings(HashSet<Character> states, int width, String path, Container<String> configs) {
        if(width <= 0) {
            configs.add(path);
            return;
        }
        for(Character s: states) {
            generateStrings(states, width - 1,path + s, configs);
        }
    }
}