import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class Row {
    public static final String THREAD = "\u001B[1;36m";
    public static final String GRID = "\u001B[31m";
    public static final String RESET = "\u001B[0m";
    char[] cells;
    Rule rule;
    boolean parity;
    String bc;

    public Row(char[] cells, Rule rule, boolean parity, String boundaryCondition) {
        this.cells = cells;
        this.rule = rule;
        this.parity = parity;
        this.bc = boundaryCondition;
    }

    /**
     * Creates a string representation of the row
     * Used for pattern display
     * @return string representation of the row
     */
    public String toString() {
        StringBuilder s = new StringBuilder();
        if (parity) {
            s.append("  ");
        }
        s.append(GRID + "|");
        for (char c : this.cells) {
            s.append(THREAD + " " + c + " ");
            s.append(GRID + "|" + RESET);
        }
        if (!parity) {
            s.append("  ");
        }
        return s.toString();
    }

    public String toStringBracelet() {
        StringBuilder s = new StringBuilder();
        for (char c : this.cells) {
            switch (c) {
                case 'L' -> s.append("f,");
                case 'R' -> s.append("b,");
                case 'F' -> s.append("fb,");
                default -> s.append("bf,");
            }
        }
        s.setLength(s.length()-1);
        return s.toString();
    }

    /**
     * Creates the next iteration of a row in a SCA
     * @return the next iteration's row
     */
    public Row getSuccessor() {
        return switch (this.bc) {
            case "wrap" -> this.getSuccessorWrap();
            case "reflect" -> this.getSuccessorReflect();
            default -> this.getSuccessorNone();
        };
//            CrossingStatus cs = this.cRule.getStatus(left, right);
//            TurningStatus ts = this.tRule.getStatus(left, right);
//
//            // if there is no left thread, there should be no left turning status
//            TurningStatus lts = ts;
//            if (!(left.left == TurningStatus.SLANTED || left.right == TurningStatus.UPRIGHT)) {
//                lts = TurningStatus.NO;
//            }
//
//            // if there is no right thread, there should be no right turning status
//            TurningStatus rts = ts;
//            if (!(right.right == TurningStatus.SLANTED || right.left == TurningStatus.UPRIGHT)) {
//                rts = TurningStatus.NO;
//            }
//
//            // if there is no cross (2 slanted threads), then there should be no crossing status
//            if(!(lts == TurningStatus.SLANTED && rts == TurningStatus.SLANTED)) {
//                cs = CrossingStatus.NO;
//            }
    }

    public Row getSuccessorWrap() {
        char[] newCells = new char[this.cells.length];

        String neighborhood;
        for(int i = 0; i < this.cells.length; i++) {
            int l, r;
            if (!parity) { //next row will have parity
                l = i;
                r = (i + 1) % this.cells.length;
            } else {
                l = (i - 1 + this.cells.length) % this.cells.length;
                r = i;
            }
            neighborhood = "" + this.cells[l] + this.cells[r];
            newCells[i] = this.rule.getNext(neighborhood);
        }

        return new Row(newCells, rule, !parity, this.bc);
    }

    public Row getSuccessorReflect() {
        char[] newCells;
        String neighborhood;
        int offset;

        if(this.parity) { //row with less cells, need to use reflection rules
            newCells = new char[this.cells.length + 1];

            //get left edge
            char left = getReflectedCell(this.cells[0]);
            newCells[0] = this.rule.getNext("" + left + this.cells[0]);

            //get right edge
            char right = getReflectedCell(this.cells[this.cells.length-1]);
            newCells[this.cells.length] = this.rule.getNext("" + this.cells[this.cells.length-1] + right);

            offset = 1;
        } else { //"normal" case
            newCells = new char[this.cells.length - 1];
            offset = 0;
        }

        for(int i = 0; i < this.cells.length - 1; i++) {
            neighborhood = "" + this.cells[i] + this.cells[i+1];
            newCells[i+offset] = this.rule.getNext(neighborhood);
        }

        return new Row(newCells, rule, !parity, this.bc);
    }

    public static char getReflectedCell(char cell) {
        switch (cell) {
            case 'L' -> { return 'R'; }
            case 'R' -> { return 'L'; }
            case 'F' -> { return 'B'; }
            default -> {return 'F'; }
        }
    }

    public Row getSuccessorNone() {
        char[] newCells = new char[this.cells.length];

        String neighborhood;
        for(int i = 0; i < this.cells.length - 1; i++) {
            neighborhood = "" + this.cells[i] + this.cells[i+1];
            newCells[i] = this.rule.getNext(neighborhood);
        }

        return new Row(newCells, rule, !parity, this.bc);
    }

    public HashSet<Row> findPredecessors() {
        ArrayList<String> a = new ArrayList<>();

        for(String n: rule.map.keySet()) {
            if(rule.getNext(n) == this.cells[0]) {
                a.add(n);
            }
        }

        for(int i = 1; i < this.cells.length; i++) {
            ArrayList<String> b = new ArrayList<>();
            char curChar = this.cells[i];
            for(String path: a) { //iterate through current possibilities
                String last = path.substring(path.length() - 1);
                for(char c: rule.states) { //every neighborhood
                    if(rule.getNext(last + c) == curChar) {
                        b.add(path + c);
                    }
                }
            }
            a = b;
        }
        HashSet<Row> set = new HashSet<>();
        for (String cur : a) {
            char first = cur.charAt(0);
            char last = cur.charAt(cur.length()-1);
            if (!(this.bc.equals("wrap")) || first == last) {
                String s = cur.substring(0, cur.length() - 1);
                set.add(new Row(s.toCharArray(), this.rule, !this.parity, this.bc));
            }
        }
        return set;
    }

    public HashSet<Row> findTwins() {
        Row s = this.getSuccessor();
        return s.findPredecessors();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof Row)) {
            return false;
        }

        Row r = (Row) o;
        return this.cells == r.cells;
    }
}
