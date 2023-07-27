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
        String[] vals = new String[]{"pattern", "balance", "surjective", "injective", "GoE", "orphans", "twins", "predecessors"};
        return this.validateParam("type",input, vals);
    }

    public String getMode(){
        String input = this.prop.getProperty("mode");
        String[] vals = new String[]{"weaving", "bracelet", "wolfram", "original", "totalistic", "multicolored"};
        return this.validateParam("mode",input, vals);
    }

    public int getOutputLevel() {
        try {
            int input = Integer.parseInt(this.prop.getProperty("outputLevel"));
            if (input < 0 || input > 5) {
                System.out.println("outputLevel input must be between 0 and 5");
                System.exit(1);
            }
            return input;
        } catch(NumberFormatException e) {
            System.out.println("outputLevel input must be an int");
            System.exit(1);
        }
        return 5;
    }

    public int getLoggingMode() {
        String input = this.prop.getProperty("logging");
        switch (input) {
            case "none" -> {
                return 0;
            }
            case "matrix" -> {
                if (this.getType().equals("pattern")) {
                    System.out.println("logging can only occur on all rules and not in pattern mode");
                    System.exit(1);
                }
                return 1;
            }
            case "list" -> {
                if (this.getType().equals("pattern")) {
                    System.out.println("logging can only occur on all rules and not in pattern mode");
                    System.exit(1);
                }
                return 2;
            }
            default -> {
                System.out.println("logging input must be 'none', 'matrix', or 'list'");
                System.exit(1);
            }
        }
        return 0;
    }

    public int getTurningRule() {
        try {
            int input = Integer.parseInt(this.prop.getProperty("turningRule"));
            int max = 15;
            if(getMode().equals("weaving")) {
                max = 255;
            } else if(getMode().equals("original") || getMode().equals("totalistic")) {
                max = 511;
            } else if (getMode().equals("multicolored")) {
                max = 65535;
            }
            if (input < 0 || input > max) {
                System.out.println("turningRule input must be between 0 and " + max);
                System.exit(1);
            }
            return input;
        } catch(NumberFormatException e) {
            if(this.prop.getProperty("turningRule").equals("ALL")) {
                return -1;
            } else if(this.prop.getProperty("turningRule").equals("BIT-BALANCED")) {
                return -2;
            } else if(this.prop.getProperty("turningRule").equals("FROM-FILE")) {
                return -3;
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
            if(getMode().equals("original") || getMode().equals("totalistic")) {
                max = 511;
            } else if (getMode().equals("multicolored")) {
                max = 65535;
            }
            if (input < 0 || input > max) {
                System.out.println("crossingRule input must be between 0 and " + max);
                System.exit(1);
            }
            return input;
        } catch(NumberFormatException e) {
            if(this.prop.getProperty("crossingRule").equals("ALL")) {
                return -1;
            } else if(this.prop.getProperty("crossingRule").equals("BIT-BALANCED")) {
                return -2;
            } else if(this.prop.getProperty("crossingRule").equals("FROM-FILE")) {
                return -3;
            }
            System.out.println("crossingRule input must be an int");
            System.exit(1);
        }
        return 0;
    }

    public String getBoundaryCondition(){
        String input = this.prop.getProperty("boundaryCondition");
        String[] vals = new String[]{"periodic", "reflect", "copy", "previous", "null"};
        return this.validateParam("boundaryCondition",input, vals);
    }

    //section: pattern experiment params
    public char[] getPatternString() {
//        String[] vals = switch (getMode()) {
//            case ("original") -> new String[]{Rule.SNN + "", Rule.NNN + "", Rule.NSN + "",
//                    Rule.UNN + "", Rule.UUN + "", Rule.NUN + "",
//                    Rule.SSR + "", Rule.SSL + ""};
//            case ("simplified") -> new String[]{"L", "R", "F", "B"};
//            default -> new String[]{"0", "1"};
//        };
        String input = this.prop.getProperty("pattern.startingString");
        char[] cells = input.toCharArray();
//        for(int i = 0; i < cells.length; i++) {
//            validateParam("pattern.StartingString cell " + i, ""+cells[i], vals);
//        }
        return input.toCharArray();
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

    //section: twin, GoE experiment params
    public boolean getParity() {
        String input = this.prop.getProperty("parity");
        if(input.equals("true")) {
            return true;
        } else if(input.equals("false")) {
            return false;
        } else {
            System.out.println("logging input must be 'true' or 'false'");
            System.exit(1);
        }
        return false;
    }

    public int getStartWidth() {
        try {
            int input = Integer.parseInt(this.prop.getProperty("startWidth"));
            if (input < 0) {
                System.out.println("startWidth input must be greater than 0");
                System.exit(1);
            }
            return input;
        } catch(NumberFormatException e) {
            System.out.println("startWidth input must be an int");
            System.exit(1);
        }
        return 0;
    }

    public int getEndWidth() {
        try {
            int input = Integer.parseInt(this.prop.getProperty("endWidth"));
            if (input < getStartWidth()) {
                System.out.println("startWidth can't be less than goe.endWidth");
                System.exit(1);
            }
            return input;
        } catch(NumberFormatException e) {
            System.out.println("startWidth input must be an int");
            System.exit(1);
        }
        return -1;
    }

    public int getCountingMethod() {
        String input = this.prop.getProperty("twins.countingMethod");
        try {
            int number = Integer.parseInt(input);
            if (number < 1) {
                System.out.println("The twins counting method must be 'surjective', 'redundant', or an int greater than 0");
                System.exit(1);
            }
            return number;
        } catch(NumberFormatException e) {
            switch (input) {
                case "surjective" -> { return -2; }
                case "redundant" -> { return -1; }
                default -> {
                    System.out.println("The twins counting method must be 'surjective', 'redundant', or an int greater than 0");
                    System.exit(1);
                }
            }
        }
        return 0;
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
