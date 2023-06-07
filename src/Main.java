public class Main {
    static Config config;
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

    public static void patternMode(){
        Generator g = new Generator(config.getPatternCrossingRule(), config.getPatternTurningRule());
        System.out.print(g.generatePattern(config.getPatternString(), config.getPatternHeight()));
    }
}