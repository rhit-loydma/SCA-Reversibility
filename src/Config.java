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
    public String getType(){
        String input = this.prop.getProperty("type");
        String[] vals = new String[]{"pattern", "surjective", "GoE"};
        return this.validateParam("type",input, vals);
    }

    public String getMode(){
        String input = this.prop.getProperty("mode");
        //TODO get wolfram mode to work with pattern mode
        String[] vals = new String[]{"full", "simplified", "wolfram"};
        return this.validateParam("mode",input, vals);
    }

    public int getTurningRule() {
        try {
            int input = Integer.parseInt(this.prop.getProperty("turningRule"));
            int max = 15;
            if(getMode().equals("full")) {
                max = 511;
            }
            if (input < 0 || input > max) {
                System.out.println("turningRule input must be between 0 and " + max);
                System.exit(1);
            }
            return input;
        } catch(NumberFormatException e) {
            if(this.prop.getProperty("turningRule").equals("ALL")) {
                return -1;
            }
            System.out.println("turningRule input must be an int");
            System.exit(1);
        }
        return 0;
    }

    public int getCrossingRule() {
        try {
            int input = Integer.parseInt(this.prop.getProperty("crossingRule"));
            int max = 15;
            if(getMode().equals("full")) {
                max = 511;
            }
            if (input < 0 || input > max) {
                System.out.println("crossingRule input must be between 0 and " + max);
                System.exit(1);
            }
            return input;
        } catch(NumberFormatException e) {
            if(this.prop.getProperty("crossingRule").equals("ALL")) {
                return -1;
            }
            System.out.println("crossingRule input must be an int");
            System.exit(1);
        }
        return 0;
    }

    //section: pattern experiment params
    public String getPatternString() {
        String[] vals;
        if(getMode().equals("full")) {
            vals = new String[]{"SNN", "NNN", "NSN", "UNN", "UUN", "NUN", "SSR", "SSL"};
        } else {
            vals = new String[]{"L", "R", "F", "B"};
        }
        String input = this.prop.getProperty("pattern.startingString");
        String[] cells = input.split(" ");
        for(int i = 0; i < cells.length; i++) {
            validateParam("pattern.StartingString cell " + i, cells[i], vals);
        }
        return input;
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

    public int getGoEstartWidth() {
        try {
            int input = Integer.parseInt(this.prop.getProperty("goe.startWidth"));
            if (input < 0) {
                System.out.println("goe.startWidth input must be greater than 0");
                System.exit(1);
            }
            return input;
        } catch(NumberFormatException e) {
            System.out.println("goe.startWidth input must be an int");
            System.exit(1);
        }
        return 0;
    }

    public int getGoEendWidth() {
        try {
            int input = Integer.parseInt(this.prop.getProperty("goe.endWidth"));
            if (input < getGoEstartWidth()) {
                System.out.println("goe.startWidth can't be less than goe.endWidth");
                System.exit(1);
            }
            return input;
        } catch(NumberFormatException e) {
            System.out.println("goe.startWidth input must be an int");
            System.exit(1);
        }
        return -1;
    }

    public boolean getGoEwrapsAround() {
        String input = this.prop.getProperty("goe.wrapsAround");
        if(input.equals("true")) {
            return true;
        } else if(input.equals("false")) {
            return false;
        } else {
            System.out.println("goe.wrapsAround input must be 'true' or 'false'");
            System.exit(1);
        }
        return false;
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
