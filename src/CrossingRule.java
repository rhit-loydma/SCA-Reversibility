import java.util.HashMap;

public class CrossingRule {
    int rule;
    CrossingStatus[] arr;
    HashMap<CrossingStatus, Integer> leftMap;
    HashMap<CrossingStatus, Integer> rightMap;

    /**
     * Class constructor
     * @param rule the decimal representation of the rule
     */
    public CrossingRule(int rule) {
        this.rule = rule;
        String b = Integer.toBinaryString(rule);
        int offset = 9 - b.length();
        b = "0".repeat(offset) + b;
        arr = new CrossingStatus[9];
        for(int i = 0; i < 9; i++) {
           if(b.charAt(8 - i) == '0'){
               this.arr[i] = CrossingStatus.RIGHT;
           } else {
               this.arr[i] = CrossingStatus.LEFT;
           }
        }

        leftMap = new HashMap<CrossingStatus, Integer>();
        leftMap.put(CrossingStatus.RIGHT, 0);
        leftMap.put(CrossingStatus.NO, 1);
        leftMap.put(CrossingStatus.LEFT, 2);

        rightMap = new HashMap<CrossingStatus, Integer>();
        rightMap.put(CrossingStatus.LEFT, 0);
        rightMap.put(CrossingStatus.NO, 1);
        rightMap.put(CrossingStatus.RIGHT, 2);
    }

    /**
     * Using a neighborhood, calculate the next iteration's crossing status
     * @param left the left cell in the neighborhood
     * @param right the right cell in the neighborhood
     * @return the CrossingStatus of a cell in the next iteration
     */
    public CrossingStatus getStatus(Cell left, Cell right) {
        int l = this.leftMap.get(left.crossingStatus);
        int r = this.rightMap.get(right.crossingStatus);
        //row-major order
        int index = 3 * l + r;
        return this.arr[index];
    }
}
