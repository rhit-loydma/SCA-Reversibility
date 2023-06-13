import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

public class Config {
    Properties prop;

    /**
     * Class constructor
     * @param configFile file path of the .properties file
     */
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
        String[] vals = new String[]{"pattern", "surjective", "GoE", "twins"};
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

    public String getSurjectiveMode() {
        String[] vals = new String[]{"wolfram", "full", "crossing"};
        String input = this.prop.getProperty("surjective.mode");
        validateParam("surjective.mode", input, vals);
        return input;
    }

    public int getSurjectiveStartRule() {
        try {
            int input = Integer.parseInt(this.prop.getProperty("surjective.startRule"));
            if (input < 0) {
                System.out.println("surjective.startRule input must be greater than 0");
                System.exit(1);
            }
            return input;
        } catch(NumberFormatException e) {
            System.out.println("surjective.startRule input must be an int");
            System.exit(1);
        }
        return -1;
    }

    public int getSurjectiveEndRule() {
        try {
            int input = Integer.parseInt(this.prop.getProperty("surjective.endRule"));
            if (input < getSurjectiveStartRule()) {
                System.out.println("surjective.endRule can't be less than surjective.startRule");
                System.exit(1);
            }
            return input;
        } catch(NumberFormatException e) {
            System.out.println("surjective.endRule input must be an int");
            System.exit(1);
        }
        return -1;
    }

    /**
     * Validates string input against an array of possible values
     * Stops the program if a bad value is inserted
     * @param param the parameter being validated
     * @param input the user input from the .properties file
     * @param vals the array of "good" values for the parameter
     * @return the input
     */
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
