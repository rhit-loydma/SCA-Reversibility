import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Properties;

public class Config {
    Properties prop;
    public Config(String configFile) {
        try {
            FileReader file = new FileReader(configFile);
            this.prop = new Properties();
            this.prop.load(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public String getExperimentType(){
        String input = this.prop.getProperty("experimentType");
        String[] vals = new String[]{"pattern", "reversible", "twins", "GoE"};
        return this.validateParam("experimentType",input, vals);
    }

    //section: pattern experiment params
    public String getPatternString() {
        String[] vals = new String[]{"SNN", "NNN", "NSN", "UNN", "UUN", "NUN", "SSR", "SSL"};
        String input = this.prop.getProperty("pattern.startingString");
        String[] cells = input.split(" ");
        for(int i = 0; i < cells.length; i++) {
            validateParam("pattern.StartingString cell " + i, cells[i], vals);
        }
        return input;
    }

    public int getPatternTurningRule() {
        try {
            int input = Integer.parseInt(this.prop.getProperty("pattern.turningRule"));
            if (input < 0 || input > 511) {
                System.out.println("pattern.turningRule input must be between 0 and 511");
                System.exit(1);
            }
            return input;
        } catch(NumberFormatException e) {
            System.out.println("pattern.turningRule input must be an int");
            System.exit(1);
        }
        return 0;
    }

    public int getPatternCrossingRule() {
        try {
            int input = Integer.parseInt(this.prop.getProperty("pattern.crossingRule"));
            if (input < 0 || input > 511) {
                System.out.println("pattern.crossingRule input must be between 0 and 511");
                System.exit(1);
            }
            return input;
        } catch(NumberFormatException e) {
            System.out.println("pattern.crossingRule input must be an int");
            System.exit(1);
        }
        return 0;
    }

    public int getPatternHeight() {
        try {
            int input = Integer.parseInt(this.prop.getProperty("pattern.height"));
            if (input < 0) {
                System.out.println("pattern.height ust be greater than 0");
                System.exit(1);
            }
            return input;
        } catch(NumberFormatException e) {
            System.out.println("pattern.height input must be an int");
            System.exit(1);
        }
        return 0;
    }

    //validates string input against an array of possible values
    private String validateParam(String param, String input, String[] vals) {
        for(String v : vals) {
            if(input.equals(v)){
                return input;
            }
        }
        System.out.println("Invalid input for param " + param);
        System.exit(1);
        return null;
    }
}
