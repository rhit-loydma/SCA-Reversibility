package BoundaryConditions;

import Core.Row;
import Core.Rule;

import java.util.ArrayList;
import java.util.HashSet;

public class ReflectedRow extends Row {
	public ReflectedRow(char[] cells, Rule rule, boolean parity) {
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
			char left = getReflectedCell(this.cells[0]);
			newCells[0] = this.rule.getNext("" + left + this.cells[0]);

			//get right edge
			char right = getReflectedCell(this.cells[this.cells.length-1]);
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

		return new ReflectedRow(newCells, rule, !parity);
	}

	public static char getReflectedCell(char cell) {
		switch (cell) {
			case 'L' -> { return 'R'; }
			case 'R' -> { return 'L'; }
			case 'F' -> { return 'B'; }
			case 'B' -> {return 'F';}
			case 'l' -> { return 'r'; }
			case 'r' -> { return 'l'; }
			case 'f' -> { return 'b'; }
			case 'b' -> {return 'f';}
			case 'A' -> { return 'D'; }
			case 'D' -> { return 'A'; }
			case 'W' -> { return 'S'; }
			case 'S' -> {return 'W';}
			case 'a' -> { return 'd'; }
			case 'd' -> { return 'a'; }
			case 'w' -> { return 's'; }
			case 's' -> {return 'w';}
			default -> {return 'N'; }
		}
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

				char leftBoundary = getReflectedCell(second);
				char rightBoundary = getReflectedCell(secondLast);

				if (first == leftBoundary && last == rightBoundary) {
					set.add(new ReflectedRow(cur.substring(1, cur.length() - 1).toCharArray(), this.rule, true));
				}
			} else {
				set.add(new ReflectedRow(cur.toCharArray(), this.rule, false));
			}
		}
		return set;
	}
}
