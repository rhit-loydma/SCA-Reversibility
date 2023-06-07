public class Main {
    public static void main(String[] args) {
        Config config = new Config("config.properties");
        System.out.println(config.getPatternHeight());
    }
}