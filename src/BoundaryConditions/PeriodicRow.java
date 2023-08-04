package BoundaryConditions;

import Core.Row;
import Core.Rule;

import java.util.ArrayList;
import java.util.HashSet;

public class PeriodicRow extends Row {
	public PeriodicRow(char[] cells, Rule rule, boolean parity) {
		super(cells, rule, parity);
	}

	@Override
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

		return new PeriodicRow(newCells, rule, !parity);
	}

	@Override
	public HashSet<Row> checkPredecessors(ArrayList<String> a) {
		HashSet<Row> set = new HashSet<>();
		for (String cur : a) {
			char first = cur.charAt(0);
			char last = cur.charAt(cur.length()-1);
			if(first == last) {
				String s = cur.substring(0, cur.length() - 1);
				set.add(new PeriodicRow(s.toCharArray(), this.rule, !this.parity));
			}
		}
		return set;
	}
}
