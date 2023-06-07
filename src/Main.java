public class Main {
    public static void main(String[] args) {
        Config config = new Config("config.properties");
        CrossingRule c = new CrossingRule(config.getPatternCrossingRule());
        TurningRule t = new TurningRule(config.getPatternTurningRule());
        Row r = new Row(config.getPatternString(), c, t, false);
        System.out.print(r.getSuccessor().toString());
        System.out.print(r.toString());
    }
}