import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Rule {
    public static final char SNN = '/';
    public static final char NNN = '_';
    public static final char NSN = '\\';
    public static final char UNN = '[';//'\u23b8';
    public static final char UUN = 'U';//'\u2016';
    public static final char NUN = ']';//'\u23b9';
    public static final char SSR = 'R';
    public static final char SSL = 'L';

    int number;
    HashMap<String, Character> map;
    ArrayList<Character> states;
    int maxT;
    int maxC;

    public Rule(String mode, int number) {
        this.number = number;
        this.map = new HashMap<>();
        this.states = new ArrayList<>();
        this.maxT = 16;
        this.maxC = 16;
        switch (mode) {
            case ("wolfram") -> generateRuleMapWolfram();
            case ("original") -> {
                maxT = 512;
                maxC = 512;
                generateRuleMapOriginal();
            }
            case ("expanded") -> {
                maxT = 256;
                maxC = 16;
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

        String crossing = Integer.toString(this.number%maxC, 2);
        crossing = "0".repeat(9 - crossing.length()) + crossing;

        String turning = Integer.toString(this.number/maxC, 2);
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
        return "turning rule: " + this.number/maxC + ", crossing rule: " + this.number % maxC;
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

    public boolean isBalanced() {
        HashMap<Character, Integer> counts = new HashMap<>();
        for(char c: states) {
            counts.put(c,0);
        }
        for(char l: states) {
            for(char r: states) {
                char state = map.get("" + l + r);
                counts.put(state,counts.get(state) + 1);
            }
        }
        for(char c: states) {
            if(counts.get(c) != states.size()) {
                return false;
            }
        }
        return true;
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

        String crossing = Integer.toString(this.number%maxC, 2);
        //get 4-bit rule
        crossing = "0".repeat(4 - crossing.length()) + crossing;
        //fill in pre-determined bits
        crossing = crossing.substring(0,2) + "1" + crossing.substring(2,4) + "1002";

        String turning = Integer.toString(this.number/maxC, 2);
        //get 8-bit rule
        turning = "0".repeat(8 - turning.length()) + turning;
        //fill in pre-determined bits
        turning = turning + "2";

//        System.out.println("Crossing: " + this.number%maxC + " " + crossing);
//        System.out.println("Turning: " + this.number/maxC + " " + turning);

        for(char left: states) {
            for(char right: states) {
                //need to get crossing and turning status of each cell
                int leftC = getTopThreadLeft(left);
                int leftT = getTurningStatusLeft(left);
                int rightC = getTopThreadRight(right);
                int rightT = getTurningStatusRight(right);
                //use those to calculate indexes
                int crossingIndex = 3 * leftC + rightC;
                int turningIndex = 3 * leftT + rightT;
                //take binary strings reps of rules, get char at index
                int c = crossing.charAt(crossingIndex)-'0';
                int t = turning.charAt(turningIndex)-'0';

                //check for absent left thread for turning status
                int l = t;
                if(left == 'N' || left == 'f' || left == 'l') {
                    l = 2;
                }

                //check for absent right thread for turning status
                int r = t;
                if(right == 'N' || right == 'b' || right == 'r') {
                    r = 2;
                }

                //check if to make sure there are threads
                int cs = l*10 + r;
                c = switch(cs) {
                    case 0, 11 ->  c; //exists, exists
                    case 2, 12 -> 1; //upright, none -> left is on top
                    case 20, 21 -> 0; //none, exists -> right is on top
                    case 22 -> 2; //none, none -> no thread is on top
                    default -> 2;
                };
//                System.out.println("Neighborhood: " + left + " " + right);
//                System.out.println("Statuses: " + leftT + " " + leftC + " " + rightT + " " + rightC);
//                System.out.println("Indexes: " + turningIndex + " " + crossingIndex);
//                System.out.println("Before checks: " + t + " " + c);
//                System.out.println("After checks: " + l + " " + r + " " + c);
//                System.out.println();
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
    public static int getTopThreadLeft(char cell) {
        return switch (cell) {
            case 'L', 'B', 'b' -> 0;
            case 'F', 'R', 'r' -> 1;
            default -> 2; //f , l , N
        };
    }

    public static int getTopThreadRight(char cell) {
        return switch (cell) {
            case 'L', 'B', 'l' -> 0;
            case 'F', 'R', 'f' -> 1;
            default -> 2; //b, r, N
        };
    }

    public static int getTurningStatusLeft(char cell) {
        return switch (cell) {
            case 'b', 'F', 'B' -> 0;
            case 'r', 'L', 'R' -> 1;
            default -> 2; //f, l, N
        };
    }

    public static int getTurningStatusRight(char cell) {
        return switch (cell) {
            case 'f', 'F', 'B' -> 0;
            case 'l', 'L', 'R' -> 1;
            default -> 2; //b, r, N
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
            case 20 -> 'b';
            case 102 -> 'f';
            case 112 -> 'l';
            case 21 -> 'r';
            default -> 'N';
        };
    }

}
