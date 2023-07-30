import java.io.*;
import java.util.*;

public class Main {
    static Config config;

    public static void main(String[] args) {
        config = new Config("config.properties");

        ArrayList<String> models = new ArrayList<>();
        ArrayList<String> types = new ArrayList<>();
        ArrayList<String> bcs = new ArrayList<>();
        ArrayList<Integer> widths = new ArrayList<>();

        String type = config.getType();
        if(type.equals("configs")) {
            models.add("bracelet");
            models.add("weaving");
            models.add("totalistic");
            models.add("multicolored");

            types.add("GoEs");
            types.add("orphans");
            types.add("twins");

            bcs.add("periodic");
            bcs.add("null");
            bcs.add("reflect");
            bcs.add("copy");
            bcs.add("previous");

            widths.add(2);
            widths.add(3);
            widths.add(4);
            widths.add(5);
        } else if(type.equals("properties")) {
            models.add("bracelet");
            models.add("weaving");
            models.add("totalistic");
            models.add("multicolored");

            types.add("balance");
            types.add("surjective");
            types.add("injective");

            bcs.add("null");
            widths.add(1);
        } else {
            models.add(config.getMode());
            types.add(config.getType());
            bcs.add(config.getBoundaryCondition());

            int start = config.getStartWidth();
            int end = start;
            if(config.getType().equals("twins") || config.getType().equals("GoE") || config.getType().equals("orphans")) {
                end = config.getEndWidth();
            }
            for(int i = start; i <= end; i++) {
                widths.add(i);
            }
        }

        for(String m: models) {
            outputMessage("Model " + m + " " + java.time.LocalTime.now(), 1);
            for(String t: types) {
                outputMessage("Type " + t + " " + java.time.LocalTime.now(), 1);
                for(String b: bcs) {
                    outputMessage("Boundary Condition " + b + " " + java.time.LocalTime.now(), 1);
                    for(int w: widths) {
                        outputMessage("Width " + w + " " + java.time.LocalTime.now(), 1);
                        runExperiment(t, m, w, b);
                    }
                }
            }
        }
    }

    public static void runExperiment(String type, String model, int width, String bc) {
        int maxT = 16;
        int maxC = 16;
        switch (model) {
            case "weaving" -> maxT = 256;
            case "original", "totalistic" -> {
                maxT = 512;
                maxC = 512;
            }
            case "multicolored" -> {
                maxT = 65536;
                maxC = 65536;
            }
        }
        ArrayList<Integer> crossing = getRules(config.getCrossingRule(), maxC);
        ArrayList<Integer> turning = getRules(config.getTurningRule(), maxT);

        int[] line;
        float[][] heatmap = new float[0][];
        int turningBits = 0;
        int crossingBits = 0;
        ArrayList<Integer> list;
        String filename;
        int logging = config.getLoggingMode();
        FileWriter file = null;

        if(logging==3) {
            turningBits = (int) (Math.log(maxT) / Math.log(2)) + 1;
            crossingBits = (int) (Math.log(maxC) / Math.log(2)) + 1;
            heatmap = new float[turningBits][crossingBits];
        }

        //file setup
        if(logging!= 0){
            filename = getFilename(model, type, bc, width);
            try {
                makeDirectories(filename);
                file = new FileWriter(filename);

                if(logging==1) { //header row  for matrix logging
                    line = new int[crossing.size()+1];
                    line[0] = -1;
                    for(int j = 0; j < crossing.size(); j++) {
                        line[j+1] = crossing.get(j);
                    }
                    logDataMatrix(file,line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //iterate through rules
        for(int i: turning) {
            outputMessage("Turning Rule: " + i, 2);
            line = new int[crossing.size()+1];
            line[0] =  i;
            list = new ArrayList<>();
            int tb = 0;
            int tc = 0;
            if(logging==3) {
                tb = (int) Integer.toString(i, 2).chars().filter(num -> num == '1').count();
                tc = combinations(turningBits - 1, tb);
            }
            for(int j = 0; j < crossing.size(); j++) {
                int c = crossing.get(j);
                outputMessage("Crossing Rule: " + c, 3);
                int output = performComputation(type, createRule(model, i, c), width, bc);
                line[j+1] = output;
                if(output == 1) {
                    list.add(c);
                }
                if(logging==3) {
                    int cb = (int) Integer.toString(c, 2).chars().filter(num -> num == '1').count();
                    float norm = ((float) output) / (tc * combinations(crossingBits - 1, cb));
                    heatmap[tb][cb] += norm;
                }
            }
            if(logging== 1) {
                logDataMatrix(file, line);
            } else if (logging == 2 && list.size() > 0) {
                logDataList(file, i, list);
            }
        }
        if(logging==3) {
            logDataHeatmap(file, heatmap);
        }

        //close file
        if(logging != 0 && file != null){
            try {
                file.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static ArrayList<Integer> getRules(int input, int max) {
      ArrayList<Integer> rules = new ArrayList<>();
        switch (input) {
            case -3 -> { //from file
                try {
                    File file = new File("rulesToSearch.txt");
                    Scanner reader = new Scanner(file);
                    while (reader.hasNextLine()) {
                        rules.add(Integer.parseInt(reader.nextLine()));
                    }
                    reader.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
            case -2 -> { //bit-balanced
                int length = (int) (Math.log(max) / Math.log(2));
                ArrayList<Character> bits = new ArrayList<>();
                for (int i = 0; i < ((double) length) / 2; i++) {
                    bits.add('1');
                    bits.add('0');
                }
                HashSet<String> c = new HashSet<>();
                generateStringsNoRep(bits, length, "", c);
                for (String s : c) {
                    rules.add(Integer.parseInt(s, 2));
                }
                Collections.sort(rules);
            }
            case -1 -> { //all
                for (int i = 0; i < max; i++) {
                    rules.add(i);
                }
            }
            default -> rules.add(input);
        }
      return rules;
    }

    public static Rule createRule(String mode, int t, int c) {
        switch (mode) {
            case "wolfram" -> { return new WolframRule(c,t); }
            case "original" -> { return new OriginalRule(c,t); }
            case "weaving" -> { return new WeavingRule(c,t); }
            case "totalistic" -> { return new TotalisticRule(c,t); }
            case "multicolored" -> { return new MulticoloredRule(c,t); }
            default -> { return new BraceletRule(c,t); }
        }
    }

    public static int performComputation(String type, Rule rule, int width, String bc) {
        outputMessage("\n"+rule.toDebugString(), 4);
        boolean parity = config.getParity();
        switch (type) {
            case ("pattern") -> {
                char[] start = config.getPatternString();
                int height = config.getPatternHeight();
                return generatePattern(rule, start, height, bc);
            }
            case ("predecessors") -> {
                char[] start = config.getPatternString();
                return generatePredecessors(rule, start, bc);
            }
            case ("balance") -> {
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
                return findTwins(rule, width, bc, method);
            }
            case ("orphans") -> {
                return findOrphans(rule, width);
            }
            default -> {
                return findGoEs(rule, width, bc);
            }
        }
    }

    public static int generatePattern(Rule rule, char[] start, int height, String boundaryCondition) {
        StringBuilder sb = new StringBuilder(rule.toString() + "\n");
        StringBuilder sb2 = new StringBuilder();
        Row r = new Row(start, rule, false, boundaryCondition);
        for(int i = 0; i < height; i++) {
            sb.append(r.toString()).append("     ").append(i).append("\n");
            sb2.append(r.toStringBracelet()).append('\n');
            r = r.getSuccessor();
        }
        System.out.println(sb);
        outputMessage(sb2.toString(), 1);
        return 0;
    }

    public static int generatePredecessors(Rule rule, char[] start, String boundaryCondition) {
        StringBuilder sb = new StringBuilder(rule.toString() + "\n");
        Row r = new Row(start, rule, false, boundaryCondition);
        HashSet<Row> pred = r.findPredecessors();
        outputMessage("Row " + r.toString() + " has " + pred.size() + " predecessors under " + rule, 1);
        for(Row p: pred) {
            sb.append("     ").append(p.toString()).append("\n");
        }
        outputMessage(sb.toString(), 3);
        return 0;
    }

    public static int checkBalanced(Rule rule) {
        if(rule.isBalanced()) {
            outputMessage(rule + " is balanced", 2);
            return 1;
        }
        outputMessage(rule + " is not balanced", 3);
        return 0;
    }

    public static int checkSurjective(Rule rule) {
        if(rule.isSurjective()) {
            outputMessage(rule + " is surjective", 2);
            return 1;
        }
        outputMessage(rule + " is not surjective", 3);
        return 0;
    }

    public static int checkInjective(Rule rule) {
        if(rule.isInjective()) {
            outputMessage(rule + " is injective", 2);
            return 1;
        }
        outputMessage(rule + " is not injective", 3);
        return 0;
    }

    public static int findTwins(Rule rule, int width, String boundaryCondition, int method) {
        Container<String> configs = new Container<>();
        generateStrings(rule.states, width, "", configs);
        double val = 0;
        for(String s: configs.items) {
            System.out.println(s);
            Row r = new Row(s.toCharArray(), rule, false, boundaryCondition);
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

    public static int findGoEs(Rule rule, int width, String boundaryCondition) {
        Container<String> configs = new Container<>();
        generateStrings(rule.states, width, "", configs);
        ArrayList<Row> goes = new ArrayList<>();
        ArrayList<Row> nonGoes = new ArrayList<>();
        for(String s: configs.items) {
            Row r = new Row(s.toCharArray(), rule, false, boundaryCondition);
            if(r.findPredecessors().isEmpty()) {
                goes.add(r);
            } else {
                nonGoes.add(r);
            }
        }
        outputMessage(rule + " width " + width + " has " + goes.size() + " GoE(s)", 4);
        for(Row r: goes) {
            outputMessage("  " + r.toString(), 5);
        }
        outputMessage(rule + " width " + width + " has " + nonGoes.size() + " non-GoE(s)", 4);
        for(Row r: nonGoes) {
            outputMessage("  " + r.toString(), 5);
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
        outputMessage(rule + " has " + orphans.size() + " orphans of width " + width, 4);
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

    public static String getFilename(String model, String type, String bc, int width) {
        String filename = "data/";

        boolean logParity = false;
        switch (type) {
            case "injective", "surjective", "balance" -> {
                filename += "properties/";
                filename += model + "/" + type;
            }
            case "twins" -> {
                filename += type + "/";
                filename += model + "/";
                filename += bc + "/";
                filename += width + "_" + config.getCountingMethod();
                logParity = true;
            }
            case "GoE", "orphans" -> {
                filename += type + "/";
                filename += model + "/";
                filename += bc + "/";
                filename += width;
                logParity = true;
            }
            default -> { return ""; }
        }

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

    public static void logDataMatrix(FileWriter file, int[] data) {
        try {
            StringBuilder sb = new StringBuilder();
            for (int datum : data) {
                sb.append(datum);
                sb.append(',');
            }
            sb.setLength(sb.length()-1);
            sb.append('\n');
            file.write(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void logDataList(FileWriter file, int rule, ArrayList<Integer> data) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(rule);
            sb.append(":");
            for(int i: data) {
                sb.append(i);
                sb.append(',');
            }
            sb.setLength(sb.length()-1);
            sb.append('\n');
            file.write(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void logDataHeatmap(FileWriter file, float[][] data) {
        try {
            StringBuilder sb = new StringBuilder();
            for (float[] d: data) {
                for(float datum: d) {
                    sb.append(datum);
                    sb.append(',');
                }
                sb.setLength(sb.length()-1);
                sb.append('\n');
            }
            sb.setLength(sb.length()-1);
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

    public static int combinations(int n, int r) {
        return (int) (fact(n) / (fact(r) * fact(n-r)));
    }

    public static long fact(int n) {
        long i = 1;
        for(int j = 2; j <= n; j++) {
            i = i * j;
        }
        return i;
    }
}