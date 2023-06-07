import java.util.HashMap;

public class TurningRule {
    int rule;
    TurningStatus[] arr;
    HashMap<TurningStatus, Integer> map;

    public TurningRule(int rule) {
        this.rule = rule;
        String b = Integer.toBinaryString(rule);
        int offset = 9 - b.length();
        b = "0".repeat(offset) + b;
        arr = new TurningStatus[9];
        for(int i = 0; i < 9; i++) {
            if(b.charAt(8 - i) == '0'){
                this.arr[i] = TurningStatus.UPRIGHT;
            } else {
                this.arr[i] = TurningStatus.SLANTED;
            }
        }

        map = new HashMap<TurningStatus, Integer>();
        map.put(TurningStatus.UPRIGHT, 0);
        map.put(TurningStatus.NO, 1);
        map.put(TurningStatus.SLANTED, 2);
    }

    public TurningStatus getStatus(Cell left, Cell right) {
        int l;
        if (left.left == TurningStatus.SLANTED) {
            l = this.map.get(TurningStatus.SLANTED);
        } else if (left.right == TurningStatus.UPRIGHT) {
            l = this.map.get(TurningStatus.UPRIGHT);
        } else {
            l = this.map.get(TurningStatus.NO);
        }

        int r;
        if (right.right == TurningStatus.SLANTED) {
            r = this.map.get(TurningStatus.SLANTED);
        } else if (right.left == TurningStatus.UPRIGHT) {
            r = this.map.get(TurningStatus.UPRIGHT);
        } else {
            r = this.map.get(TurningStatus.NO);
        }

        int index = 3 * l + r;
        return this.arr[index];
    }
}
