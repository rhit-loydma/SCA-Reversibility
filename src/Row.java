import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Row {
    public static final String THREAD = "\u001B[1;36m";
    public static final String GRID = "\u001B[31m";
    char[] cells;
    Rule rule;
    boolean parity;
    boolean wrapAround;

    public Row(char[] cells, Rule rule, boolean parity, boolean wrapAround) {
        this.cells = cells;
        this.rule = rule;
        this.parity = parity;
        this.wrapAround = wrapAround;
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
            s.append(GRID + "|");
        }
        if (!parity) {
            s.append("  ");
        }
        return s.toString();
    }

    /**
     * Creates the next iteration of a row in a SCA
     * @return the next iteration's row
     */
    public Row getSuccessor() {
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
        return new Row(newCells, rule, !parity, this.wrapAround);
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
                String last = path.substring(path.length() - 2);
                //todo optimize so we just add on a state instead of going through every neighborhood
                for(String n: rule.map.keySet()) { //every neighborhood
                    String first = n.substring(0, 2);
                    if(rule.getNext(n) == curChar && last.equals(first)) {
                        b.add(path + n.substring(n.length() - 1));
                    }
                }
            }
            a = b;
        }

        if(this.wrapAround) {
            for(int i = 0; i < a.size(); i++) {
                String cur = a.get(i);
                String first = cur.substring(0,2);
                String last = cur.substring(cur.length()-2);
                if(!first.equals(last)) {
                    a.remove(i--);
                }
            }
        }

        HashSet<Row> set = new HashSet<>();
        for(String r: a){
            set.add(new Row(r.toCharArray(), this.rule, !this.parity, this.wrapAround));
        }
        return set;
    }
}
