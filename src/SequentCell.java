import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;

public class SequentCell {
    String a, b;
    boolean crossed;
    boolean symbol;
    SequentTable table;
    HashSet<String> tuples;
    Rule rule;

    //step 2
    public SequentCell(String a, String b, Rule rule, SequentTable table) {
        this.a = a;
        this.b = b;
        this.rule = rule;
        this.table = table;
    }

    //step 3
    public boolean findSequentSets() {
        if(a.substring(1).equals(b.substring(1))) {
            this.symbol = true;
        }

        this.tuples = new HashSet<>();
        for(char x: rule.states) {
            String newA = a.substring(1) + x;
            for(char y: rule.states) {
                String newB = b.substring(1) + y;
                if(!newA.equals(newB) && rule.getNext(newA) == rule.getNext(newB)) {
                    tuples.add(newA + " " + newB);
                    if((a.equals(newA) && b.equals(newB)) || (b.equals(newA) && a.equals(newB)) ) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    //step 4
    public boolean reduce() {
        if(this.crossed) {
            return false;
        }

        boolean changed = false;
        HashSet<String> toRemove = new HashSet<>();
        SequentCell cell;
        for(String tuple: tuples) {
            String[] pair = tuple.split(" ", 2);
            cell = table.getCell(pair[0], pair[1]);
            if(cell.crossed) {
                toRemove.add(tuple);
            }
        }
        tuples.removeAll(toRemove);

        if(this.tuples.isEmpty() && !this.symbol) {
            this.crossed = true;
            changed = true;
        }

        return changed;
    }

    public int getWeight(Container<SequentCell> visited) {
        if(visited.contains(this)) {
            return Integer.MIN_VALUE;
        }
        visited.add(this);

        int max = 0;
        if(this.symbol) {
            max = 1;
        }

        int weight;
        for(String tuple: tuples) {
            String[] pair = tuple.split(" ", 2);
            weight = table.getCell(pair[0], pair[1]).getWeight(visited);
            if(weight == 0) {
                return 0;
            } else if(weight > max) {
                max = weight;
            }
        }
        return max + 1;
    }

    @Override
    public String toString() {
        return this.tuples.toString() + " " + this.symbol;
    }
}