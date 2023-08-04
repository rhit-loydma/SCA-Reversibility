package BoundaryConditions;

import Core.Row;
import Core.Rule;

import java.util.ArrayList;
import java.util.HashSet;

public class SecondOrderRow extends Row {
	char prevLeft;
	char prevRight;

	public SecondOrderRow(char[] cells, Rule rule, boolean parity, char prevLeft, char prevRight) {
		super(cells, rule, parity);
		this.prevLeft = prevLeft;
		this.prevRight = prevRight;
	}

	@Override
	public Row getSuccessor() {
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

		return new SecondOrderRow(newCells, rule, !parity, this.cells[0], this.cells[this.cells.length-1]);
	}

	@Override
	public HashSet<Row> checkPredecessors(ArrayList<String> a) {
		HashSet<Row> set = new HashSet<>();
		for (String cur : a) {
			if (!this.parity) {
				char second = cur.charAt(0);
				char secondLast = cur.charAt(cur.length()-1);

				SecondOrderRow curRow = new SecondOrderRow(cur.substring(1, cur.length() - 1).toCharArray(), this.rule, true, '_', '_');
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
				set.add(new SecondOrderRow(cur.toCharArray(), this.rule, false, ' ', ' '));
			}
		}
		return set;
	}
}
