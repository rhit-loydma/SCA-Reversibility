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
        char[] newCells = new char[this.cells.length - 1];

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
            if(this.bc.equals("wrap")) {
                char first = cur.charAt(0);
                char last = cur.charAt(cur.length()-1);
                if(first == last) {
                    String s = cur.substring(0, cur.length() - 1);
                    set.add(new Row(s.toCharArray(), this.rule, !this.parity, this.bc));
                }
            } else if (this.bc.equals("reflect") && !this.parity) {
                char first = cur.charAt(0);
                char second = cur.charAt(1);

                char last = cur.charAt(cur.length() - 1);
                char secondLast = cur.charAt(cur.length() - 2);

                if(first == getReflectedCell(second) && last == getReflectedCell(secondLast)) {
                    set.add(new Row(cur.substring(1,cur.length()-1).toCharArray(), this.rule, !this.parity, this.bc));
                }
            } else {
                set.add(new Row(cur.toCharArray(), this.rule, !this.parity, this.bc));
            }
        }
        return set;
    }

    public HashSet<Row> findTwins() {
        Row s = this.getSuccessor();
        return s.findPredecessors();
    }

    public double numTwins(int method) {
        switch(method) {
            case -2 -> { return numTwinsSurjective(); }
            case -1 -> { return numTwinsRedundant(); }
            default -> { return numTwinsMultiples(method); }
        }
    }

    public double numTwinsSurjective() {
        int size = this.findTwins().size();
        if(size > this.rule.states.size()) {
            return (double) (size - this.rule.states.size()) / size;
        }
        return 0;
    }

    public double numTwinsRedundant() {
        int size = this.findTwins().size();
        return (double) (size - 1) / size;
    }

    public double numTwinsMultiples(int count) {
        int size = this.findTwins().size();
        if(size == count) {
            return (double) 1 / count;
        }
        return 0;
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
