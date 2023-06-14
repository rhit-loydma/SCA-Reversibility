import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

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
        String start = config.getPatternString();
        int height = config.getPatternHeight();
        int crossing = config.getCrossingRule();
        int turning = config.getTurningRule();
        int max = 16;
        if(config.getMode().equals("full")) {
            max = 512;
        }
        if(crossing == -1) {
            for(int i = 0; i < max; i++) {
                if(turning == -1) {
                    for(int j = 0; j < max; j++) {
                        Generator g = new Generator(i, j);
                        System.out.print(g.generatePattern(start, height));
                    }
                } else {
                    Generator g = new Generator(i, turning);
                    System.out.print(g.generatePattern(start, height));
                }
            }
        } else {
            if(turning == -1) {
                for(int j = 0; j < max; j++) {
                    Generator g = new Generator(crossing, j);
                    System.out.print(g.generatePattern(start, height));
                }
            } else {
                Generator g = new Generator(crossing, turning);
                System.out.print(g.generatePattern(start, height));
            }
        }
    }

    public static void surjetiveMode() {
        int max = 16;
        if(config.getMode().equals("full")) {
            max = 512;
        }

        int crossing = config.getCrossingRule();
        int turning = config.getTurningRule();
        if(crossing == -1) {
            for(int i = 0; i < max; i++) {
                if(turning == -1) {
                    for(int j = 0; j < max; j++) {
                        int rule = j*max + i;
                        checkSurjective(rule);
                    }
                } else {
                    int rule = turning*max + i;
                    checkSurjective(rule);
                }
            }
        } else {
            if(turning == -1) {
                for(int j = 0; j < max; j++) {
                    int rule = j*max + crossing;
                    checkSurjective(rule);
                }
            } else {
                int rule = turning*max + crossing;
                checkSurjective(rule);
            }
        }
    }

    public static void checkSurjective(int rule) {
        HashSet<Character> states = getStates();
        HashMap<String, Character> map = getRuleMap(rule);
        Node n = new Node(map, states);
        if(n.isSurjective()) {
            System.out.println("Rule " + rule + " is surjective");
        } else {
            System.out.println("Rule " + rule + " is not surjective");
        }
    }

    public static HashSet<Character> getStates() {
        HashSet<Character> states = new HashSet<>();
        switch (config.getMode()) {
            case ("wolfram") -> {
                states.add('0');
                states.add('1');
            }
            case ("simplified") -> {
                states.add('B');
                states.add('F');
                states.add('R');
                states.add('L');
            }
            default -> {
                System.out.println("nyi");
            }
        }
        return states;
    }

    public static HashMap<String, Character> getRuleMap(int rule) {
        switch(config.getMode()) {
            case("wolfram"):
                return generateRuleMapWolfram(rule);
            case("full"):
                System.out.println("nyi");
                break;
            case("simplified"):
                return generateRuleMapCrossing(rule);
            default:
                System.out.println("nyi");
                break;
        }
        return new HashMap<String, Character>();
    }

    public static HashMap<String, Character> generateRuleMapWolfram(int rule) {
        HashMap<String, Character> map = new HashMap<>();
        String bin = Integer.toString(rule, 2);
        bin = "0".repeat(8 - bin.length()) + bin;
        for(int i = 0; i < 8; i++) {
            String neighborhood = Integer.toBinaryString(i);
            neighborhood = "0".repeat(3 - neighborhood.length()) + neighborhood;
            map.put(neighborhood, bin.charAt(7 - i));
        }
        return map;
    }

    public static HashMap<String, Character> generateRuleMapCrossing(int rule) {
        HashMap<String, Character> map = new HashMap<>();
        String bin = Integer.toString(rule, 2);
        bin = "0".repeat(8 - bin.length()) + bin;
        for(int i = 0; i < 16; i++) {
            //get neighborhood
            int left = i / 4;
            int right = i % 4;
            //get output state
            int tIndex = (left/2)*2 + (right/2);
            int cIndex = (left%2)*2 + (right%2) + 4;
            int output = (bin.charAt(tIndex)-'0')*2 + (bin.charAt(cIndex)-'0');
            map.put("" + getStateCrossing(left) + getStateCrossing(right), getStateCrossing(output));
        }
        return map;

    }

    public static char getStateCrossing(int c) {
        return switch (c) {
            case (0) -> 'B';
            case (1) -> 'F';
            case (2) -> 'R';
            default -> 'L';
        };
    }
}