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
            default:
                System.out.println("Not yet implemented");
        }
    }

    /**
     * Prints a pattern using initial conditions defined in a config file
     */
    public static void patternMode(){
        Generator g = new Generator(config.getPatternCrossingRule(), config.getPatternTurningRule());
        System.out.print(g.generatePattern(config.getPatternString(), config.getPatternHeight()));
    }
}