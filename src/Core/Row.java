package Core;

import java.util.ArrayList;
import java.util.HashSet;

public abstract class Row {
    public static final String THREAD = "\u001B[1;36m";
    public static final String GRID = "\u001B[31m";
    public static final String RESET = "\u001B[0m";
    public char[] cells;
    protected Rule rule;
    protected boolean parity;

    public Row(char[] cells, Rule rule, boolean parity) {
        this.cells = cells;
        this.rule = rule;
        this.parity = parity;
    }

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

    //outputs the row in a format that can be used at braceletbook.com
    public String toStringBracelet() {
        StringBuilder s = new StringBuilder();
        for (char c : this.cells) {
            switch (c) {
                case 'L','l','A','a' -> s.append("f,");
                case 'R','r','D','d' -> s.append("b,");
                case 'F','f','W','w' -> s.append("fb,");
                default -> s.append("bf,");
            }
        }
        s.setLength(s.length()-1);
        return s.toString();
    }

    public abstract Row getSuccessor();

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

        return this.checkPredecessors(a);
    }

    public abstract HashSet<Row> checkPredecessors(ArrayList<String> a);

    public HashSet<Row> findTwins() {
        Row s = this.getSuccessor();
        return s.findPredecessors();
    }

    public double numTwins(int method) {
        switch(method) {
            case -2 -> { return numTwinsAboveStates(); }
            case -1 -> { return numTwinsAboveOne(); }
            default -> { return numTwinsMultiples(method); }
        }
    }

    public double numTwinsAboveStates() {
        int size = this.findTwins().size();
        if(size > this.rule.states.size()) {
            return (double) (size - this.rule.states.size()) / size;
        }
        return 0;
    }

    public double numTwinsAboveOne() {
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
