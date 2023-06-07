public class Row {
    public static final String THREAD = "\u001B[1;36m";
    public static final String GRID = "\u001B[31m";
    Cell[] cells;
    CrossingRule cRule;
    TurningRule tRule;
    Boolean parity;

    public Row(String rep, int cRule, int tRule, boolean parity) {
        String[] cs = rep.split(" ");
        this.cells = new Cell[cs.length];
        for (int i = 0; i < cs.length; i++) {
            this.cells[i] = new Cell(cs[i]);
        }
        this.cRule = new CrossingRule(cRule);
        this.tRule = new TurningRule(tRule);
        this.parity = parity;
    }

    public Row(Cell[] cells, CrossingRule cRule, TurningRule tRule, boolean parity) {
        this.cells = cells;
        this.cRule = cRule;
        this.tRule = tRule;
        this.parity = parity;
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        if (parity) {
            s.append("  ");
        }
        s.append(GRID + "|");
        for (Cell c : this.cells) {
            s.append(THREAD + c.toString());
            s.append(GRID + "|");
        }
        s.append("\n");
        if (parity) {
            s.append("  ");
        }
        s.append("-----".repeat(this.cells.length - 1));
        return s.toString();
    }

    public Row getSuccessor() {
        Cell[] newCells = new Cell[this.cells.length];

        for (int i = 0; i < this.cells.length; i++) {
            int l, r;
            if (!parity) { //next row has parity
                l = i;
                r = (i + 1) % this.cells.length;
            } else {
                l = (i - 1 + this.cells.length) % this.cells.length;
                r = i;
            }
            Cell left = this.cells[l];
            Cell right = this.cells[r];
            CrossingStatus cs = this.cRule.getStatus(left, right);
            TurningStatus ts = this.tRule.getStatus(left, right);

            // check for left thread
            TurningStatus lts = ts;
            if (!(left.left == TurningStatus.SLANTED || left.right == TurningStatus.UPRIGHT)) {
                lts = TurningStatus.NO;
            }

            // check for right thread
            TurningStatus rts = ts;
            if (!(right.right == TurningStatus.SLANTED || right.left == TurningStatus.UPRIGHT)) {
                rts = TurningStatus.NO;
            }

            // check there is actually a cross (2 threads that are slanted)
            if(!(lts == TurningStatus.SLANTED && rts == TurningStatus.SLANTED)) {
                cs = CrossingStatus.NO;
            }

            newCells[i] = new Cell(cs, lts, rts);
        }

        return new Row(newCells, this.cRule, this.tRule, !this.parity);
    }
}
