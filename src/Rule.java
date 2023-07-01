import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Rule {
    int number;
    HashMap<String, Character> map;
    ArrayList<Character> states;
    int max;

    public static final char SNN = '/';
    public static final char NNN = '_';
    public static final char NSN = '\\';
    public static final char UNN = '[';//'\u23b8';
    public static final char UUN = 'U';//'\u2016';
    public static final char NUN = ']';//'\u23b9';
    public static final char SSR = 'R';
    public static final char SSL = 'L';

    public Rule(String mode, int number) {
        this.number = number;
        this.map = new HashMap<>();
        this.states = new ArrayList<>();
        this.max = 16;
        switch (mode) {
            case ("wolfram") -> generateRuleMapWolfram();
            case ("original") -> {
                max = 512;
                generateRuleMapOriginal();
            }
            case ("expanded") -> {
                max = 512;
                generateRuleMapExpanded();
            }
            default -> generateRuleMapSimplified();
        }
    }

    public char getNext(String neighborhood) {
        return this.map.get(neighborhood);
    }

    public Set<String> neighborhoods() {
        return this.map.keySet();
    }

    public void generateRuleMapWolfram() {
        String bin = Integer.toString(this.number, 2);
        bin = "0".repeat(8 - bin.length()) + bin;
        for(int i = 0; i < 8; i++) {
            String neighborhood = Integer.toBinaryString(i);
            neighborhood = "0".repeat(3 - neighborhood.length()) + neighborhood;
            this.map.put(neighborhood, bin.charAt(7 - i));
        }
        states.add('0');
        states.add('1');
    }

    public void generateRuleMapSimplified() {
        String bin = Integer.toString(this.number, 2);
        bin = "0".repeat(8 - bin.length()) + bin;
        for(int i = 0; i < 16; i++) {
            //get neighborhood
            int left = i / 4;
            int right = i % 4;
            //get output state
            int tIndex = (left/2)*2 + (right/2);
            int cIndex = (left%2)*2 + (right%2) + 4;
            int output = (bin.charAt(tIndex)-'0')*2 + (bin.charAt(cIndex)-'0');
            this.map.put("" + getStateSimplified(left) + getStateSimplified(right), getStateSimplified(output));
        }
        states.add('B');
        states.add('F');
        states.add('R');
        states.add('L');
    }

    public static char getStateSimplified(int c) {
        return switch (c) {
            case (0) -> 'B';
            case (1) -> 'F';
            case (2) -> 'R';
            default -> 'L';
        };
    }

    public void generateRuleMapOriginal() {
        generateOriginalStates();

        String crossing = Integer.toString(this.number%max, 2);
        crossing = "0".repeat(9 - crossing.length()) + crossing;

        String turning = Integer.toString(this.number/max, 2);
        turning = "0".repeat(9 - turning.length()) + turning;

        for(char left: states) {
            for(char right: states) {
                //need to get crossing and turning status of each cell
                int leftC = getCrossingStatusLeft(left);
                int leftT = getTurningStatus(left);
                int rightC = getCrossingStatusRight(right);
                int rightT = getTurningStatus(right);
                //use those to calculate indexes
                int crossingIndex = 8-(3 * leftC + rightC);
                int turningIndex = 8-(3 * leftT + rightT);
                //take binary strings reps of rules, get char at index
                int c = crossing.charAt(crossingIndex)-'0';
                int t = turning.charAt(turningIndex)-'0';

                //check for absent left thread for turning status
                int l = t;
                if(left == NNN || left == SNN || left == UNN) {
                    l = -1;
                }

                //check for absent right thread for turning status
                int r = t;
                if(right == NNN || right == NSN || right == NUN) {
                    r = -1;
                }

                //check if there is a cross
                if (!(l == 1 && r == 1)) {
                    c = -1;
                }

                map.put("" + left + right, getOutputOriginal(l,r,c));
            }
        }
    }

    public void generateOriginalStates() {
        states.add(SNN);
        states.add(NNN);
        states.add(NSN);
        states.add(UNN);
        states.add(UUN);
        states.add(NUN);
        states.add(SSR);
        states.add(SSL);
    }
    public static int getCrossingStatusLeft(char cell) {
        return switch (cell) {
            case SSR -> 0;
            case SSL -> 2;
            default -> 1;
        };
    }

    public static int getCrossingStatusRight(char cell) {
        return switch (cell) {
            case SSR -> 2;
            case SSL -> 0;
            default -> 1;
        };
    }

    public static int getTurningStatus(char cell) {
        return switch (cell) {
            case NNN -> 1;
            case UNN, NUN, UUN -> 2;
            default -> 0; //slanted
        };
    }

    public static char getOutputOriginal(int leftTurning, int rightTurning, int crossing) {
        switch (crossing) {
            case 1 -> {return SSL;}
            case 0 -> {return SSR;}
            default -> { //no crossing
                switch (leftTurning) {
                    case 1 -> {return SNN;}
                    case 0 -> {
                        if (rightTurning == 0) {return UUN;}
                        else {return UNN;}
                    }
                    default -> { //no left thread
                        return switch (rightTurning) {
                            case 1 -> NSN;
                            case 0 -> NUN;
                            default -> NNN;
                        };
                    }
                }
            }
        }
    }

    public String toString() {
        return "turning rule: " + this.number/max + ", crossing rule: " + this.number % max;
    }

    public String toDebugString(){
        StringBuilder sb = new StringBuilder("\u001B[1;36m" + "  ");
        for(char c: states) { //header row
            sb.append(c);
            sb.append(" ");
        }
        sb.append('\n');
        for(char c: states) {
            sb.append("\u001B[1;36m" + c);
            sb.append("\u001B[0m "); //reset bold
            for(char d: states) {
                char output = map.get(""+c+d);
                sb.append(getDebugColor(output));
                sb.append(map.get(""+c+d));
                sb.append(" ");
            }
            sb.append('\n');
        }
        sb.append("\u001B[0m"); //reset colors
        return sb.toString();
    }

    public static String getDebugColor(char c) {
        return switch (c) {
            case 'B', 'R', 'F', 'L', UUN -> "\u001B[32m"; //green, 2 strands
            case 'b', 'r', 'f', 'l', SNN, NSN, UNN, NUN -> "\u001B[33m"; //yellow, 1 strand;
            default -> "\u001B[31m"; //red, no strands
        };
    }

    public void generateRuleMapExpanded() {
        generateExpandedStates();

        String crossing = Integer.toString(this.number%max, 2);
        crossing = "0".repeat(9- crossing.length()) + crossing;

        String turning = Integer.toString(this.number/max, 2);
        turning = "0".repeat(9- turning.length()) + turning;

        for(char left: states) {
            for(char right: states) {
                //need to get crossing and turning status of each cell
                int leftC = getCrossingStatusExpanded(left);
                int leftT = getTurningStatusExpanded(left);
                int rightC = getCrossingStatusExpanded(right);
                int rightT = getTurningStatusExpanded(right);
                //use those to calculate indexes
                int crossingIndex = (3 * leftC + rightC);
                int turningIndex = (3 * leftT + rightT);
                //take binary strings reps of rules, get char at index
                int c = crossing.charAt(crossingIndex)-'0';
                int t = turning.charAt(turningIndex)-'0';

                //check for absent left thread for turning status
                int l = t;
                if(left == 'N' || left == 'f' || left == 'r') {
                    l = 2;
                }

                //check for absent right thread for turning status
                int r = t;
                if(right == 'N' || right == 'b' || right == 'l') {
                    r = 2;
                }

                //check if to make sure there are threads
                if (l == 2 && r == 2) {
                    c = 2;
                }

                map.put("" + left + right, getOutputExpanded(l,r,c));
            }
        }
    }

    public void generateExpandedStates() {
        states.add('B');
        states.add('F');
        states.add('R');
        states.add('L');
        states.add('b');
        states.add('f');
        states.add('r');
        states.add('l');
        states.add('N');
    }
    public static int getCrossingStatusExpanded(char cell) {
        return switch (cell) {
            case 'B', 'R' -> 0;
            case 'F', 'L' -> 1;
            default -> 2;
        };
    }


    public static int getTurningStatusExpanded(char cell) {
        return switch (cell) {
            case 'f', 'b', 'F', 'B' -> 0;
            case 'l', 'r', 'L', 'R' -> 1;
            default -> 2;
        };
    }

    public static char getOutputExpanded(int leftTurning, int rightTurning, int crossing) {
        int c = crossing * 100 + leftTurning * 10 + rightTurning;
        //crossing: 0 R, 1 L, 2 No
        //turning: 0 U, 1 S, 2 No
        return switch (c) {
            case 0 -> 'B';
            case 100 -> 'F';
            case 11 -> 'R';
            case 111 -> 'L';
            case 220 -> 'b';
            case 202 -> 'f';
            case 221 -> 'l';
            case 212 -> 'r';
            default -> 'N';
        };
    }

}
