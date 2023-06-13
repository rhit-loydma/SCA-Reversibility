import java.util.HashMap;
import java.util.HashSet;

public class Main {
    static Config config;

    /**
     * Initializes the config parser and starts an experiment mode
     * @param args
     */
    public static void main(String[] args) {
//        config = new Config("config.properties");
//        String mode = config.getExperimentType();
//        switch (mode){
//            case("pattern"):
//                patternMode();
//                break;
//            default:
//                System.out.println("Not yet implemented");
//        }

//        for(int i = 0; i < 256; i++) {
//            WolframTree tree = new WolframTree(i);
//            tree.determineSurjectivity();
//            tree.findGoEs();
//        }

        HashMap<String,Character> map = new HashMap<>();
        map.put("000", '1');
        map.put("001", '1');
        map.put("010", '1');
        map.put("011", '1');
        map.put("100", '1');
        map.put("101", '1');
        map.put("110", '1');
        map.put("111", '1');

        HashSet<Character> states = new HashSet<>();
        states.add('0');
        states.add('1');

        Node n = new Node(map, states);
        if(n.isSurjective()) {
            System.out.println("Rule is surjective");
        }
        Container<String> GOEs = new Container<>();
        n.findGoEs("", GOEs);
        System.out.println(GOEs.toString());
    }

    /**
     * Prints a pattern using initial conditions defined in a config file
     */
    public static void patternMode(){
        Generator g = new Generator(config.getPatternCrossingRule(), config.getPatternTurningRule());
        System.out.print(g.generatePattern(config.getPatternString(), config.getPatternHeight()));
    }
}