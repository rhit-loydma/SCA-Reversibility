import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
            case ("surjective") -> surjectiveMode();
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

    public static void surjectiveMode() {
        int max = 16;
        String mode = config.getMode();
        if(mode.equals("full")) {
            max = 512;
        }
        int[][] arr = new int[max][max];

        int crossing = config.getCrossingRule();
        int turning = config.getTurningRule();
        if(turning == -1) {
            for(int i = 0; i < max; i++) {
                if(crossing == -1) {
                    for(int j = 0; j < max; j++) {
                        arr[i][j] = checkSurjective(new Rule(mode, i*max+j));
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

        if(config.getLogging()) {
            logData("data/Surjective/"+config.getMode()+".csv",arr);
        }
    }

    public static void twinsMode() {
        char[] cells = config.getTwinString().toCharArray();
        int crossing = config.getCrossingRule();
        int turning = config.getTurningRule();
        int max = 16;
        String mode = config.getMode();
        if(mode.equals("full")) {
            max = 512;
        }
        int[][] arr = new int[max][max];
        if(turning == -1) {
            for(int i = 0; i < max; i++) {
                if(crossing == -1) {
                    for(int j = 0; j < max; j++) {
                        arr[i][j] = findTwins(new Rule(mode, i*max + j), cells);
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
        if(config.getLogging()) {
            logData("data/Twins/"+ config.getMode()+"/" + config.getTwinString() + ".csv",arr);
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
        int[][] arr = new int[max][max];
        for(int w = start; w <= end; w++) {
            System.out.println(w);
            if(turning == -1) {
                for(int i = 0; i < max; i++) {
                    if(crossing == -1) {
                        for(int j = 0; j < max; j++) {
                            arr[i][j] = findGoEs(new Rule(mode, i*max+j),w,wrap);
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
            if(config.getLogging()) {
                logData("data/GardenOfEden/"+ config.getMode()+"/" + w + "_" + config.getGoEwrapsAround() + ".csv",arr);
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

        StringBuilder sb = new StringBuilder(rule.toString() + "\n");
        while(!rows.isEmpty()){
            sb.append(rows.pop());
        }
        System.out.println(sb.toString());
        if(config.getVerbose()) {
            System.out.println("\n"+rule.toDebugString());
        }
    }

    public static int checkSurjective(Rule rule) {
        if(config.getVerbose()) {
            System.out.println("\n"+rule.toDebugString());
        }
        Node n = new Node(rule);
        if(n.isSurjective()) {
            System.out.println("Rule " + rule.number + " is surjective");
            return 1;
        } else {
            System.out.println("Rule " + rule.number + " is not surjective");
            return 0;
        }
    }

    public static int findTwins(Rule rule, char[] cells) {
        Row r = new Row(cells, rule, false, true);
        HashSet<Row> twins = r.findTwins();
        if(twins.size() == 0){
            System.out.println(r.toString() + "has no twins under " + rule.toString());
        } else {
            System.out.println(r.toString() + "under rule " + rule.toString() + " has " + twins.size() + " twin(s)");
            if(config.getVerbose()){
                for(Row t: twins) {
                    System.out.println("  " + t.toString());
                }
            }
        }
        if(config.getVerbose()) {
            System.out.println("\n"+rule.toDebugString());
        }
        return twins.size();
    }

    public static int findGoEs(Rule rule, int width, boolean wrapAround) {
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
        System.out.println(rule.toString() + " width " + width + " has " + goes.size() + " GoE(s)");
        if(config.getVerbose()) {
            for(Row r: goes) {
                System.out.println("  " + r.toString());
            }
        }
        System.out.println(rule.toString() + " width " + width + " has " + nonGoes.size() + " non-GoE(s)");
        if(config.getVerbose()) {
            for(Row r: nonGoes) {
                System.out.println("  " + r.toString());
            }
            System.out.println("\n"+rule.toDebugString());
        }
        return goes.size();
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

    public static void logData(String filename, int[][] data) {
        try {
            FileWriter file = new FileWriter(filename);
            StringBuilder sb = new StringBuilder();
            for(int i = 0; i < data.length; i++) {
                for(int j = 0; j < data[0].length; j++) {
                    sb.append(data[i][j]);
                    sb.append(',');
                }
                sb.setLength(sb.length()-1);
                sb.append('\n');
            }
            sb.setLength(sb.length()-1);
            file.write(sb.toString());
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}