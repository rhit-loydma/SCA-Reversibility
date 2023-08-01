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
    char prevLeft;
    char prevRight;

    public Row(char[] cells, Rule rule, boolean parity, String boundaryCondition) {
        this.cells = cells;
        this.rule = rule;
        this.parity = parity;
        this.bc = boundaryCondition;
    }

    //used for the previous row boundary condition
    public Row(char[] cells, Rule rule, boolean parity, char prevLeft, char prevRight) {
        this.cells = cells;
        this.rule = rule;
        this.parity = parity;
        this.bc = "previous";
        this.prevLeft = prevLeft;
        this.prevRight = prevRight;
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
            case "periodic" -> this.getSuccessorWrap();
            case "reflect", "copy" -> this.getSuccessorReflect();
            case "previous" -> this.getSuccessorPrevious();
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
            char left = this.cells[0];
            if(this.bc.equals("reflect")) {
                left = getReflectedCell(left);
            }
            newCells[0] = this.rule.getNext("" + left + this.cells[0]);

            //get right edge
            char right = this.cells[this.cells.length-1];
            if(this.bc.equals("reflect")) {
                right = getReflectedCell(right);
            }
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

    public Row getSuccessorPrevious() {
        char[] newCells;
        String neighborhood;
        int offset;

        if(this.parity) { //row with less cells, need to use previous cells
            newCells = new char[this.cells.length + 1];
            newCells[0] = this.rule.getNext("" + this.prevLeft + this.cells[0]);
            newCells[this.cells.length] = this.rule.getNext("" + this.cells[this.cells.length-1] + this.prevRight);
            offset = 1;
        } else { //"normal" case
            newCells = new char[this.cells.length - 1];
            offset = 0;
        }

        for(int i = 0; i < this.cells.length - 1; i++) {
            neighborhood = "" + this.cells[i] + this.cells[i+1];
            newCells[i+offset] = this.rule.getNext(neighborhood);
        }

        return new Row(newCells, rule, !parity, this.cells[0], this.cells[this.cells.length-1]);
    }

    public static char getReflectedCell(char cell) {
        switch (cell) {
            case 'L' -> { return 'R'; }
            case 'R' -> { return 'L'; }
            case 'F' -> { return 'B'; }
            case 'B' -> {return 'F';}
            case 'l' -> { return 'r'; }
            case 'r' -> { return 'l'; }
            case 'f' -> { return 'b'; }
            case 'b' -> {return 'f';}
            case OriginalRule.NNN -> {return OriginalRule.NNN;}
            case OriginalRule.SNN -> {return OriginalRule.NSN;}
            case OriginalRule.NSN -> {return OriginalRule.SNN;}
            case OriginalRule.UNN -> {return OriginalRule.NUN;}
            case OriginalRule.NUN -> {return OriginalRule.UNN;}
            case OriginalRule.UUN -> {return OriginalRule.UUN;}
            default -> {return 'N'; }
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

        return switch (this.bc) {
            case "periodic" -> this.findPredecessorsWrap(a);
            case "reflect", "copy" -> this.findPredecessorsReflect(a);
            case "previous" -> this.findPredecessorsPrevious(a);
            default -> this.findPredecessorsNone(a);
        };
    }

    public HashSet<Row> findPredecessorsWrap(ArrayList<String> a) {
        HashSet<Row> set = new HashSet<>();
        for (String cur : a) {
            char first = cur.charAt(0);
            char last = cur.charAt(cur.length()-1);
            if(first == last) {
                String s = cur.substring(0, cur.length() - 1);
                set.add(new Row(s.toCharArray(), this.rule, !this.parity, this.bc));
            }
        }
        return set;
    }

    public HashSet<Row> findPredecessorsReflect(ArrayList<String> a) {
        HashSet<Row> set = new HashSet<>();
        for (String cur : a) {
            if (!this.parity) {
                char first = cur.charAt(0);
                char second = cur.charAt(1);

                char last = cur.charAt(cur.length() - 1);
                char secondLast = cur.charAt(cur.length() - 2);

                char leftBoundary;
                char rightBoundary;
                if(this.bc.equals("reflect")) {
                    leftBoundary = getReflectedCell(second);
                    rightBoundary = getReflectedCell(secondLast);
                } else {
                    leftBoundary = second;
                    rightBoundary = secondLast;
                }
                if (first == leftBoundary && last == rightBoundary) {
                    set.add(new Row(cur.substring(1, cur.length() - 1).toCharArray(), this.rule, true, this.bc));
                }
            } else {
                set.add(new Row(cur.toCharArray(), this.rule, false, this.bc));
            }
        }
        return set;
    }

    public HashSet<Row> findPredecessorsPrevious(ArrayList<String> a) {
        HashSet<Row> set = new HashSet<>();
        for (String cur : a) {
            if (!this.parity) {
                char second = cur.charAt(0);
                char secondLast = cur.charAt(cur.length()-1);

                Row curRow = new Row(cur.substring(1, cur.length() - 1).toCharArray(), this.rule, true, this.bc);
                for(Row row: curRow.findPredecessors()) {
                    char[] chars = row.cells;
                    char first = chars[0];
                    char last = chars[chars.length-1];
                    if(rule.getNext(""+first + second) == this.cells[0]
                        && rule.getNext(""+secondLast + last) == this.cells[this.cells.length-1]) {
                        set.add(row);
                    }
                }
            } else {
                set.add(new Row(cur.toCharArray(), this.rule, false, this.bc));
            }
        }
        return set;
    }

    public HashSet<Row> findPredecessorsNone(ArrayList<String> a) {
        HashSet<Row> set = new HashSet<>();
        for (String cur : a) {
            set.add(new Row(cur.toCharArray(), this.rule, !this.parity, this.bc));
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
