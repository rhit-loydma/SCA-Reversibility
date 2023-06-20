import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
        String bc = config.getBoundaryCondition();
        int max = 16;
        String mode = config.getMode();
        if(mode.equals("expanded")) {
            max = 512;
        }
        if(turning == -1) {
            for(int i = 0; i < max; i++) {
                outputMessage("Turning Rule: " + i, 2);
                if(crossing == -1) {
                    for(int j = 0; j < max; j++) {
                        outputMessage("Crossing Rule: " + j, 3);
                        generatePattern(new Rule(mode, i*max + j), start, height, bc);
                    }
                } else {
                    generatePattern(new Rule(mode, i*max + crossing), start, height, bc);
                }
            }
        } else {
            outputMessage("Turning Rule: " + turning, 2);
            if(crossing == -1) {
                for(int j = 0; j < max; j++) {
                    outputMessage("Crossing Rule: " + j, 3);
                    generatePattern(new Rule(mode, turning*max + j), start, height, bc);
                }
            } else {
                outputMessage("Crossing Rule: " + crossing, 3);
                generatePattern(new Rule(mode, turning*max + crossing), start, height, bc);
            }
        }
    }

    public static void surjectiveMode() {
        int max = 16;
        String mode = config.getMode();
        if(mode.equals("expanded")) {
            max = 512;
        }
        int[][] arr = new int[max][max];

        int crossing = config.getCrossingRule();
        int turning = config.getTurningRule();
        if(turning == -1) {
            for(int i = 0; i < max; i++) {
                outputMessage("Turning Rule: " + i, 2);
                if(crossing == -1) {
                    for(int j = 0; j < max; j++) {
                        outputMessage("Crossing Rule: " + j, 3);
                        arr[i][j] = checkSurjective(new Rule(mode, i*max+j));
                    }
                } else {
                    outputMessage("Crossing Rule: " + crossing, 3);
                    checkSurjective(new Rule(mode, i*max+crossing));
                }
            }
        } else {
            outputMessage("Turning Rule: " + turning, 2);
            if(crossing == -1) {
                for(int j = 0; j < max; j++) {
                    outputMessage("Crossing Rule: " + j, 3);
                    checkSurjective(new Rule(mode, turning*max+j));
                }
            } else {
                outputMessage("Crossing Rule: " + crossing, 3);
                checkSurjective(new Rule(mode, turning*max+crossing));
            }
        }

        if(config.getLogging()) {
            logData("data/Surjective/"+config.getMode()+".csv",arr);
        }
    }

    public static void twinsMode() {
        String mode = config.getMode();
        int start = config.getStartWidth();
        int end = config.getEndWidth();
        String bc = config.getBoundaryCondition();
        int crossing = config.getCrossingRule();
        int turning = config.getTurningRule();
        boolean parity = config.getParity();

        int max = 16;
        if(mode.equals("expanded")) {
            max = 512;
        }
        int[][] arr = new int[max][max];
        for(int w = start; w <= end; w++) {
            outputMessage(w + " " + java.time.LocalTime.now(), 1);
            if (turning == -1) {
                for (int i = 0; i < max; i++) {
                    outputMessage("Turning Rule: " + i, 2);
                    if (crossing == -1) {
                        for (int j = 0; j < max; j++) {
                            outputMessage("Crossing Rule: " + j, 3);
                            arr[i][j] = findTwins(new Rule(mode, i * max + j), w, bc, parity);
                        }
                    } else {
                        outputMessage("Crossing Rule: " + crossing, 3);
                        findTwins(new Rule(mode, i * max + crossing), w, bc, parity);
                    }
                }
            } else {
                outputMessage("Turning Rule: " + turning, 2);
                if (crossing == -1) {
                    for (int j = 0; j < max; j++) {
                        outputMessage("Crossing Rule: " + j, 3);
                        findTwins(new Rule(mode, turning * max + j), w, bc, parity);
                    }
                } else {
                    outputMessage("Crossing Rule: " + crossing, 3);
                    findTwins(new Rule(mode, turning * max + crossing), w, bc, parity);
                }
            }
            if(config.getLogging()) {
                if(bc.equals("reflect")) {
                    logData("data/Twins/"+ config.getMode()+"/" + w + "_" + bc + "_" + parity + ".csv",arr);
                } else {
                    logData("data/Twins/"+ config.getMode()+"/" + w + "_" + bc + ".csv",arr);
                }
            }
        }
    }

    public static void GoEMode() {
        String mode = config.getMode();
        int start = config.getStartWidth();
        int end = config.getEndWidth();
        String bc = config.getBoundaryCondition();
        int crossing = config.getCrossingRule();
        int turning = config.getTurningRule();
        boolean parity = config.getParity();
        int max = 16;
        if(mode.equals("expanded")) {
            max = 512;
        }
        int[][] arr = new int[max][max];
        for(int w = start; w <= end; w++) {
            outputMessage(w + " " + java.time.LocalTime.now(), 1);
            if(turning == -1) {
                for(int i = 0; i < max; i++) {
                    outputMessage("Turning Rule: " + i, 2);
                    if(crossing == -1) {
                        for(int j = 0; j < max; j++) {
                            outputMessage("Crossing Rule: " + j, 3);
                            arr[i][j] = findGoEs(new Rule(mode, i*max+j),w,bc, parity);
                        }
                    } else {
                        outputMessage("Crossing Rule: " + crossing, 3);
                        findGoEs(new Rule(mode, i*max+crossing),w,bc, parity);
                    }
                }
            } else {
                outputMessage("Turning Rule: " + turning, 2);
                if(crossing == -1) {
                    for(int j = 0; j < max; j++) {
                        outputMessage("Crossing Rule: " + j, 3);
                        findGoEs(new Rule(mode, turning*max+j),w,bc, parity);
                    }
                } else {
                    outputMessage("Crossing Rule: " + crossing, 3);
                    findGoEs(new Rule(mode, turning*max+crossing),w,bc, parity);
                }
            }
            if(config.getLogging()) {
                if(bc.equals("reflect")) {
                    logData("data/GardenOfEden/"+ config.getMode()+"/" + w + "_" + bc + "_" + parity + ".csv",arr);
                } else {
                    logData("data/GardenOfEden/"+ config.getMode()+"/" + w + "_" + bc + ".csv",arr);
                }
            }
        }
    }

    public static void generatePattern(Rule rule, char[] start, int height, String boundaryCondition) {
        Stack<String> rows = new Stack<>();
        Queue<String> rowsBracelet = new LinkedList<>();
        Row r = new Row(start, rule, false, boundaryCondition);
        for(int i = 0; i < height; i++) {
            rows.push(r.toString() + "     " + i + "\n");
            rowsBracelet.add(r.toStringBracelet() + '\n');
            r = r.getSuccessor();
        }

        StringBuilder sb = new StringBuilder(rule.toString() + "\n");
        StringBuilder sb2 = new StringBuilder();
        while(!rows.isEmpty()){
            sb.append(rows.pop());
            sb2.append(rowsBracelet.remove());
        }
        System.out.println(sb.toString());
        outputMessage(sb2.toString(), 1);
        outputMessage("\n"+rule.toDebugString(), 4);
    }

    public static int checkSurjective(Rule rule) {
        outputMessage("\n"+rule.toDebugString(), 4);
        Node n = new Node(rule);
        if(n.isSurjective()) {
            System.out.println("Rule " + rule.number + " is surjective");
            return 1;
        } else {
            outputMessage("Rule " + rule.number + " is not surjective", 1);
            return 0;
        }
    }

    public static int findTwins(Rule rule, int width, String boundaryCondition, boolean parity) {
        Container<String> configs = new Container<>();
        generateStrings(rule.states, width, "", configs);
        double val = 0;
        for(String s: configs.items) {
            Row r = new Row(s.toCharArray(), rule, parity, boundaryCondition);
//            HashSet<Row> twins = r.findTwins();
//            if(twins.size() == 1) {
//                outputMessage(r.toString() + "has no twins under " + rule.toString(), 4);
//            } else {
//                outputMessage(r.toString() + "under rule " + rule.toString() + " has " + twins.size() + " twin(s)", 4);
//                for(Row t: twins) {
//                    outputMessage("  " + t.toString(), 5);
//                }
//                val++;
//            }
            val += r.numTwins();
        }
        outputMessage("\n"+rule.toDebugString(), 4);
        return (int) val;
    }

    public static int findGoEs(Rule rule, int width, String boundaryCondition, boolean parity) {
        Container<String> configs = new Container<>();
        generateStrings(rule.states, width, "", configs);
        ArrayList<Row> goes = new ArrayList<>();
        ArrayList<Row> nonGoes = new ArrayList<>();
        for(String s: configs.items) {
            Row r = new Row(s.toCharArray(), rule, parity, boundaryCondition);
            if(r.findPredecessors().isEmpty()) {
                goes.add(r);
            } else {
                nonGoes.add(r);
            }
        }
        outputMessage(rule.toString() + " width " + width + " has " + goes.size() + " GoE(s)", 4);
        for(Row r: goes) {
            outputMessage("  " + r.toString(), 5);
        }
        outputMessage(rule.toString() + " width " + width + " has " + nonGoes.size() + " non-GoE(s)", 4);
        for(Row r: nonGoes) {
            outputMessage("  " + r.toString(), 5);;
        }
        outputMessage("\n"+rule.toDebugString(), 4);
        return goes.size();
    }

    public static void generateStrings(ArrayList<Character> states, int width, String path, Container<String> configs) {
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

    public static void outputMessage(String msg, int level) {
        if(config.getOutputLevel() >= level) {
            System.out.println(msg);
        }
    }
}