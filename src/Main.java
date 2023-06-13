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
        String mode = config.getExperimentType();
        switch (mode){
            case("pattern"):
                patternMode();
                break;
            case("surjective"):
                surjetiveMode();
                break;
            default:
                System.out.println("Not yet implemented");
        }

//        Container<String> GOEs = new Container<>();
//        n.findGoEs("", GOEs);
//        System.out.println(GOEs.toString());
    }

    /**
     * Prints a pattern using initial conditions defined in a config file
     */
    public static void patternMode(){
        Generator g = new Generator(config.getPatternCrossingRule(), config.getPatternTurningRule());
        System.out.print(g.generatePattern(config.getPatternString(), config.getPatternHeight()));
    }

    public static void surjetiveMode() {
        int startRule = config.getSurjectiveStartRule();
        int endRule = config.getSurjectiveEndRule();
        HashSet<Character> states = new HashSet<>();
        switch(config.getSurjectiveMode()) {
            case("wolfram"):
                states.add('0');
                states.add('1');
                break;
            case("full"):
                System.out.println("nyi");
                break;
            case("crossing"):
                System.out.println("nyi");
                break;
            default:
                System.out.println("nyi");
                break;
        }

        HashMap<String, Character> map;
        for(int i = startRule; i <= endRule; i++) {
            map = getRuleMap(i);
            Node n = new Node(map, states);
            if(n.isSurjective()) {
                System.out.println("Rule " + i + " is surjective");
            } else {
                System.out.println("Rule " + i + " is not surjective");
            }
        }
    }

    public static HashMap<String, Character> getRuleMap(int rule) {
        switch(config.getSurjectiveMode()) {
            case("wolfram"):
                return generateRuleMapWolfram(rule);
            case("full"):
                System.out.println("nyi");
                break;
            case("crossing"):
                System.out.println("nyi");
                break;
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


}