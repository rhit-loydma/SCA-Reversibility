import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Main {
    static Config config;

    public static void main(String[] args) {
        config = new Config("config.properties");

        int start = config.getStartWidth();
        int end = start;
        if(config.getType().equals("twins") || config.getType().equals("GoE")) {
            end = config.getEndWidth();
        }

        int maxT = 16;
        int maxC = 16;
        String mode = config.getMode();
        if(mode.equals("expanded")) {
            maxT = 256;
        } else if (mode.equals("original")) {
            maxT = 512;
            maxC = 512;
        }
        int[][] arr = new int[maxT][maxC];

        int crossing = config.getCrossingRule();
        int turning = config.getTurningRule();

        for(int w = start; w <= end; w++) {
            outputMessage(w + " " + java.time.LocalTime.now(), 1);
            if (turning == -1) {
                for (int i = 0; i < maxT; i++) {
                    outputMessage("Turning Rule: " + i, 2);
                    if (crossing == -1) {
                        for (int j = 0; j < maxC; j++) {
                            outputMessage("Crossing Rule: " + j, 3);
                            arr[i][j] = performComputation(createRule(i * maxC + j), w);
                        }
                    } else {
                        outputMessage("Crossing Rule: " + crossing, 3);
                        performComputation(createRule(i * maxC + crossing), w);
                    }
                }
            } else {
                outputMessage("Turning Rule: " + turning, 2);
                if (crossing == -1) {
                    for (int j = 0; j < maxC; j++) {
                        outputMessage("Crossing Rule: " + j, 3);
                        performComputation(createRule(turning * maxC + j), w);
                    }
                } else {
                    outputMessage("Crossing Rule: " + crossing, 3);
                    performComputation(createRule(turning * maxC + crossing), w);
                }
            }
            if(config.getLogging()) {
                handleLogging(arr, w);
            }
        }

    }

    public static Rule createRule(int number) {
        String mode = config.getMode();
        switch (mode) {
            case "wolfram" -> { return new WolframRule(number); }
            case "original" -> { return new OriginalRule(number); }
            case "expanded" -> { return new ExpandedRule(number); }
            default -> { return new SimplifiedRule(number); }
        }
    }

    public static int performComputation(Rule rule, int width) {
        outputMessage("\n"+rule.toDebugString(), 4);
        String bc = config.getBoundaryCondition();
        boolean parity = config.getParity();
        switch (config.getType()) {
            case ("pattern") -> {
                char[] start = config.getPatternString();
                int height = config.getPatternHeight();
                return generatePattern(rule, start, height, bc);
            }
            case ("balanced") -> {
                return checkBalanced(rule);
            }
            case ("surjective") -> {
                return checkSurjective(rule);
            }
            case ("injective") -> {
                return checkInjective(rule);
            }
            case ("twins") -> {
                int method = config.getCountingMethod();
                return findTwins(rule, width, bc, parity, method);
            }
            default -> {
                return findGoEs(rule, width, bc, parity);
            }
        }
    }

    public static int generatePattern(Rule rule, char[] start, int height, String boundaryCondition) {
        StringBuilder sb = new StringBuilder(rule.toString() + "\n");
        StringBuilder sb2 = new StringBuilder();
        Row r = new Row(start, rule, false, boundaryCondition);
        for(int i = 0; i < height; i++) {
            sb.append(r.toString() + "     " + i + "\n");
            sb2.append(r.toStringBracelet() + '\n');
            r = r.getSuccessor();
        }
        System.out.println(sb.toString());
        outputMessage(sb2.toString(), 1);
        return 0;
    }

    public static int checkBalanced(Rule rule) {
        if(rule.isBalanced()) {
            outputMessage("Rule " + rule.toString() + " is balanced", 2);
            return 1;
        } else {
            outputMessage("Rule " + rule.toString() + " is not balanced", 3);
            return 0;
        }
    }

    public static int checkSurjective(Rule rule) {
        Node n = new Node(rule);
        if(n.isSurjective()) {
            outputMessage("Rule " + rule.number + " is surjective", 2);
            return 1;
        } else {
            outputMessage("Rule " + rule.number + " is not surjective", 3);
            return 0;
        }
    }

    public static int checkInjective(Rule rule) {
        SequentTable table = new SequentTable(rule);
        if(table.isInjective()) {
            outputMessage("Rule " + rule.number + " is injective", 2);
            return 1;
        } else {
            outputMessage("Rule " + rule.number + " is not injective", 3);
            return 0;
        }
    }

    public static int findTwins(Rule rule, int width, String boundaryCondition, boolean parity, int method) {
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
            val += r.numTwins(method);
        }
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

    public static void handleLogging(int[][] arr, int width) {
        String filename = "data/" + config.getType() + "/";

        String type = config.getType();
        boolean logParity = false;
        switch (type) {
            case "injective", "surjective", "balanced" -> filename += config.getMode() + "_" + type;
            case "twins" -> {
                filename += config.getMode() + "/" + width + "_" + config.getCountingMethod() + "_" + config.getBoundaryCondition();
                logParity = true;
            }
            case "GoE" -> {
                filename += config.getMode() + "/" + width + "_" + config.getBoundaryCondition();
                logParity = true;
            }
            default -> { return; }
        }

        String bc = config.getBoundaryCondition();
        if(logParity && (bc.equals("reflect") || bc.equals("previous") || bc.equals("copy"))) {
            filename += "_" + config.getParity();
        }

        filename += ".csv";
        logData(filename,arr);
    }

    public static void logData(String filename, int[][] data) {
        try {
            String[] parts = filename.split("/");
            String soFar = "";
            for(int i = 0; i < parts.length - 1; i++) {
                soFar += parts[i] + "/";
                File directory = new File(soFar);
                if (! directory.exists()){
                    directory.mkdir();
                }
            }

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