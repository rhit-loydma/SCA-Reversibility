import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Main {
    static Config config;
    static HashSet<String> prev;

    public static void main(String[] args) {
        config = new Config("config.properties");

        int start = config.getStartWidth();
        int end = start;
        if(config.getType().equals("twins") || config.getType().equals("GoE") || config.getType().equals("orphans")) {
            end = config.getEndWidth();
        }

        int maxT = 16;
        int maxC = 16;
        String mode = config.getMode();
        if(mode.equals("expanded")) {
            maxT = 256;
        } else if (mode.equals("expanded2")) {
            maxT = 256;
            maxC = 256;
        } else if (mode.equals("original") || mode.equals("totalistic")) {
            maxT = 512;
            maxC = 512;
        } else if (mode.equals("multicolored")) {
            maxT = 65536;
            maxC = 65536;
        }
        ArrayList<Integer> crossing = getRules(config.getCrossingRule(), maxC);
        ArrayList<Integer> turning = getRules(config.getTurningRule(), maxT);

        int[] line;
        String filename = "";
        FileWriter file = null;

        for(int w = start; w <= end; w++) {
            outputMessage(w + " " + java.time.LocalTime.now(), 1);

            //file setup
            if(config.getLogging()){
                filename = getFilename(w);
                try {
                    makeDirectories(filename);
                    file = new FileWriter(filename);

                    line = new int[crossing.size()+1];
                    line[0] = -1;
                    for(int j = 0; j < crossing.size(); j++) {
                        line[j+1] = crossing.get(j);
                    }
                    logData(file,line);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            //iterate through rules
            for(int i: turning) {
                outputMessage("Turning Rule: " + i, 2);
                line = new int[crossing.size()+1];
                line[0] =  i;
                for(int j = 0; j < crossing.size(); j++) {
                    int c = crossing.get(j);
                    outputMessage("Crossing Rule: " + c, 3);
                    line[j+ 1] = performComputation(createRule(i * maxC + c), w);
                }
                if(config.getLogging()) {
                    logData(file, line);
                }
            }

            //close file
            if(config.getLogging()){
                try {
                    file.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static ArrayList<Integer> getRules(int input, int max) {
      ArrayList<Integer> rules = new ArrayList<>();
      switch (input) {
          case -2: //bit-balanced
              int length = (int) (Math.log(max) / Math.log(2));
              ArrayList<Character> bits = new ArrayList<>();
              for(int i = 0; i < ((double)length) / 2; i++) {
                  bits.add('1');
                  bits.add('0');
              }
              HashSet<String> c = new HashSet<>();
              generateStringsNoRep(bits, length, "", c);
              for(String s: c) {
                  rules.add(Integer.parseInt(s, 2));
              }
              Collections.sort(rules);
              break;
          case -1: //all
              for(int i = 0; i < max; i++) {
                  rules.add(i);
              }
              break;
          default:
              rules.add(input);
      }
      return rules;
    }

    public static Rule createRule(int number) {
        String mode = config.getMode();
        switch (mode) {
            case "wolfram" -> { return new WolframRule(number); }
            case "original" -> { return new OriginalRule(number); }
            case "expanded" -> { return new ExpandedRule(number); }
            case "expanded2" -> { return new ExpandedRule2(number); }
            case "totalistic" -> { return new TotalisticRule(number); }
            case "multicolored" -> { return new MulticoloredRule(number); }
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
            case ("orphans") -> {
                return findOrphans(rule, width);
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
//        for(int i = 0; i < height; i++) {
//            sb.append(r.toString() + "     " + i + "\n");
//            sb2.append(r.toStringBracelet() + '\n');
//            r = r.getSuccessor();
//        }
//        System.out.println(sb.toString());
//        outputMessage(sb2.toString(), 1);
        System.out.println(r.findPredecessors().toString());
        return 0;
    }

    public static int checkBalanced(Rule rule) {
        if(rule.isBalanced()) {
            outputMessage(rule.toString() + " is balanced", 2);
            return 1;
        }
        outputMessage(rule.toString() + " is not balanced", 3);
        return 0;
    }

    public static int checkSurjective(Rule rule) {
        if(rule.isSurjective()) {
            outputMessage(rule.toString() + " is surjective", 2);
            return 1;
        }
        outputMessage(rule.toString() + " is not surjective", 3);
        return 0;
    }

    public static int checkInjective(Rule rule) {
        if(rule.isInjective()) {
            outputMessage(rule.toString() + " is injective", 2);
            return 1;
        }
        outputMessage(rule.toString() + " is not injective", 3);
        return 0;
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

    public static int findOrphans(Rule rule, int width) {
        HashMap<Integer,ArrayList<String>> map = new HashMap<>();
        for(int i = 0; i <= width; i++) {
            map.put(i, new ArrayList<>());
        }
        rule.findOrphans(width, map);

        ArrayList<String> orphans = map.get(width);
        outputMessage(rule.toString() + " has " + orphans.size() + " orphans of width " + width, 4);
        for(String r: orphans) {
            outputMessage("  " + r, 5);
        }
        return orphans.size();
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

    public static void generateStringsNoRep(ArrayList<Character> states, int width, String path, HashSet<String> configs) {
        if(width <= 0) {
            configs.add(path);
            return;
        }
        if(states.contains('1')) {
            ArrayList<Character> newStates = new ArrayList<>(states);
            newStates.remove((Object)'1');
            generateStringsNoRep(newStates, width - 1,path + '1', configs);
        }
        if(states.contains('0')) {
            ArrayList<Character> newStates = new ArrayList<>(states);
            newStates.remove((Object)'0');
            generateStringsNoRep(newStates, width - 1,path + '0', configs);
        }
    }

    public static String getFilename(int width) {
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
            default -> { return ""; }
        }

        String bc = config.getBoundaryCondition();
        if(logParity && (bc.equals("reflect") || bc.equals("previous") || bc.equals("copy"))) {
            filename += "_" + config.getParity();
        }

        filename += ".csv";
        return filename;
    }

    public static void makeDirectories(String filename) {
        String[] parts = filename.split("/");
        String soFar = "";
        for(int i = 0; i < parts.length - 1; i++) {
            soFar += parts[i] + "/";
            File directory = new File(soFar);
            if (! directory.exists()){
                directory.mkdir();
            }
        }
    }

    public static void logData(FileWriter file, int[] data) {
        try {
            StringBuilder sb = new StringBuilder();
            for(int i = 0; i < data.length; i++) {
                sb.append(data[i]);
                sb.append(',');
            }
            sb.setLength(sb.length()-1);
            sb.append('\n');
            file.write(sb.toString());
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