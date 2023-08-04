package BoundaryConditions;

import Core.Row;
import Core.Rule;

import java.util.ArrayList;
import java.util.HashSet;

public class NullRow extends Row {
	public NullRow(char[] cells, Rule rule, boolean parity) {
		super(cells, rule, parity);
	}

	@Override
	public Row getSuccessor() {
		char[] newCells = new char[this.cells.length - 1];

		String neighborhood;
		for(int i = 0; i < this.cells.length - 1; i++) {
			neighborhood = "" + this.cells[i] + this.cells[i+1];
			newCells[i] = this.rule.getNext(neighborhood);
		}

		return new NullRow(newCells, rule, !parity);
	}

	@Override
	public HashSet<Row> checkPredecessors(ArrayList<String> a) {
		HashSet<Row> set = new HashSet<>();
		for (String cur : a) {
			set.add(new NullRow(cur.toCharArray(), this.rule, !this.parity));
		}
		return set;
	}
}
