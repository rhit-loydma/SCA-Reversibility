import java.util.HashMap;
import java.util.HashSet;

public class Rule {
    int number;
    HashMap<String, Character> map;
    HashSet<Character> states;

    public Rule(String mode, int number) {
        this.number = number;
        this.map = new HashMap<>();
        this.states = new HashSet<>();
        switch (mode) {
            case ("wolfram") -> generateRuleMapWolfram();
            case ("simplified") -> generateRuleMapCrossing();
            default -> System.out.println("nyi");
        }
    }

    public char getNext(String neighborhood) {
        return this.map.get(neighborhood);
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

    public void generateRuleMapCrossing() {
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
            this.map.put("" + getStateCrossing(left) + getStateCrossing(right), getStateCrossing(output));
        }
        states.add('B');
        states.add('F');
        states.add('R');
        states.add('L');
    }

    public static char getStateCrossing(int c) {
        return switch (c) {
            case (0) -> 'B';
            case (1) -> 'F';
            case (2) -> 'R';
            default -> 'L';
        };
    }

    public String toString() {
        return "crossing rule: " + this.number/16 + ", turning Rule: " + this.number % 16;
    }

    public String toDebugString(){
        StringBuilder sb = new StringBuilder("\u001B[1;36m" + "  ");
        for(char c: states) { //header row
            sb.append(c);
            sb.append(" ");
        }
        sb.append('\n');
//        sb.append("-".repeat(states.size()+2));
//        sb.append('\n');
        for(char c: states) {
            sb.append("\u001B[1;36m" + c);
            sb.append("\u001B[0m" + " ");
            for(char d: states) {
                sb.append(map.get(""+c+d));
                sb.append(" ");
            }
            sb.append('\n');
        }
        return sb.toString();
    }

}
