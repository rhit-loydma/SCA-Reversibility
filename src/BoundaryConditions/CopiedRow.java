package BoundaryConditions;

import Core.Row;
import Core.Rule;

import java.util.ArrayList;
import java.util.HashSet;

public class CopiedRow extends Row {
	public CopiedRow(char[] cells, Rule rule, boolean parity) {
		super(cells, rule, parity);
	}

	@Override
	public Row getSuccessor() {
		char[] newCells;
		String neighborhood;
		int offset;

		if(this.parity) { //row with less cells, need to use reflection rules
			newCells = new char[this.cells.length + 1];

			//get left edge
			char left = this.cells[0];
			newCells[0] = this.rule.getNext("" + left + this.cells[0]);

			//get right edge
			char right = this.cells[this.cells.length-1];
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

		return new CopiedRow(newCells, rule, !parity);
	}

	@Override
	public HashSet<Row> checkPredecessors(ArrayList<String> a) {
		HashSet<Row> set = new HashSet<>();
		for (String cur : a) {
			if (!this.parity) {
				char first = cur.charAt(0);
				char second = cur.charAt(1);

				char last = cur.charAt(cur.length() - 1);
				char secondLast = cur.charAt(cur.length() - 2);

				char leftBoundary = second;
				char rightBoundary = secondLast;

				if (first == leftBoundary && last == rightBoundary) {
					set.add(new CopiedRow(cur.substring(1, cur.length() - 1).toCharArray(), this.rule, true));
				}
			} else {
				set.add(new CopiedRow(cur.toCharArray(), this.rule, false));
			}
		}
		return set;
	}
}
