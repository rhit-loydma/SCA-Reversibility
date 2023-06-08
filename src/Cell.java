public class Cell {
    CrossingStatus crossingStatus;
    TurningStatus left;
    TurningStatus right;

    /**
     * Creates a cell based off a string representation
     * Possible rep values:SNN, NNN, NSN, UNN, UUN, NUN, SSR", SSL
     * Used for creating the first row in the SCA using user input
     * @param rep
     */
    public Cell(String rep) {
        char[] vals = rep.toCharArray();
        switch (vals[0]) {
            case 'S' -> this.left = TurningStatus.SLANTED;
            case 'U' -> this.left = TurningStatus.UPRIGHT;
            //N
            default -> this.left = TurningStatus.NO;
        }
        switch (vals[1]) {
            case 'S' -> this.right = TurningStatus.SLANTED;
            case 'U' -> this.right = TurningStatus.UPRIGHT;
            //N
            default -> this.right = TurningStatus.NO;
        }
        switch (vals[2]) {
            case 'L' -> this.crossingStatus = CrossingStatus.LEFT;
            case 'R' -> this.crossingStatus = CrossingStatus.RIGHT;
            //N
            default -> this.crossingStatus = CrossingStatus.NO;
        }
    }

    /**
     * Creates a cell based off crossing and turning statuses
     * Used for iterations in an SCA
     * @param crossingStatus
     * @param left
     * @param right
     */
    public Cell(CrossingStatus crossingStatus, TurningStatus left, TurningStatus right) {
        this.crossingStatus = crossingStatus;
        this.left = left;
        this.right = right;
    }

    /**
     * Creates a string representation of the cell
     * Used for pattern display
     * @return string representation of the cell
     */
    public String toString() {
        switch (this.crossingStatus) {
            case LEFT -> { return " L "; }
            case RIGHT -> { return " R "; }
            default -> {}
        }
        char l;
        switch (this.left) {
            case SLANTED -> { return " / ";}
            case UPRIGHT -> { l = '|';}
            default -> { l = ' ';}
        }
        char r;
        switch (this.right) {
            case SLANTED -> { return " \\ ";}
            case UPRIGHT -> { r = '|';}
            default -> { r = ' ';}
        }
        return l + " " + r;
    }
}
