public class Row {
    public static final String THREAD = "\u001B[1;36m";
    public static final String GRID = "\u001B[31m";
    Cell[] cells;
    CrossingRule cRule;
    TurningRule tRule;
    Boolean parity;

    /**
     * Class constructor. Used for creating the first row based on user input.
     * @param rep string representation of a row
     * @param cRule the decimal representation of the crossing rule
     * @param tRule the decimal representation of the turning rule
     * @param parity if the row is offset from the vertical edge
     */
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

    /**
     * Class constructor. Used for further iterations in the SCA
     * @param cells the cells that make up a row
     * @param cRule the crossing rule to use
     * @param tRule the turning rule to use
     * @param parity if the row is offset from the vertical edge
     */
    public Row(Cell[] cells, CrossingRule cRule, TurningRule tRule, boolean parity) {
        this.cells = cells;
        this.cRule = cRule;
        this.tRule = tRule;
        this.parity = parity;
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
        for (Cell c : this.cells) {
            s.append(THREAD + c.toString());
            s.append(GRID + "|");
        }
        //s.append("\n");
        if (!parity) {
            s.append("  ");
        }
        //s.append("-----".repeat(this.cells.length - 1));
        return s.toString();
    }

    /**
     * Creates the next iteration of a row in a SCA
     * @return the next iteration's row
     */
    public Row getSuccessor() {
        Cell[] newCells = new Cell[this.cells.length];

        for (int i = 0; i < this.cells.length; i++) {
            int l, r;
            if (!parity) { //next row will have parity
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

            // if there is no left thread, there should be no left turning status
            TurningStatus lts = ts;
            if (!(left.left == TurningStatus.SLANTED || left.right == TurningStatus.UPRIGHT)) {
                lts = TurningStatus.NO;
            }

            // if there is no right thread, there should be no right turning status
            TurningStatus rts = ts;
            if (!(right.right == TurningStatus.SLANTED || right.left == TurningStatus.UPRIGHT)) {
                rts = TurningStatus.NO;
            }

            // if there is no cross (2 slanted threads), then there should be no crossing status
            if(!(lts == TurningStatus.SLANTED && rts == TurningStatus.SLANTED)) {
                cs = CrossingStatus.NO;
            }

            newCells[i] = new Cell(cs, lts, rts);
        }

        return new Row(newCells, this.cRule, this.tRule, !this.parity);
    }
}
