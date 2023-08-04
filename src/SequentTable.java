import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class SequentTable {
	HashMap<String, HashMap<String, SequentCell>> cells;
	Rule rule;

	public SequentTable(Rule rule) {
		this.rule = rule;
		cells = new HashMap<>();
		for (String s : rule.neighborhoods()) {
			cells.put(s, new HashMap<>());
		}
	}

	public boolean isInjective() {
		return this.populateTable() && this.reduce() && this.assignWeights() && this.checkSameLeftMost();
	}

	//steps 1,2,3
	public boolean populateTable() {
		//step 1: partition neighborhoods based on what states they go into
		HashMap<Character, ArrayList<String>> state2neighborhoods = new HashMap<>();
		for(char c: rule.states) {
			state2neighborhoods.put(c, new ArrayList<>());
		}
		for(String n: rule.neighborhoods()) {
			state2neighborhoods.get(rule.getNext(n)).add(n);
		}

		//step 2: create a sequent table for each states
		for(char s: state2neighborhoods.keySet()) {
			ArrayList<String> neighborhoods = state2neighborhoods.get(s);
			for(int i = 0; i < neighborhoods.size() - 1; i++) {
				String a = neighborhoods.get(i);
				for(int j = i + 1; j < neighborhoods.size(); j++) {
					String b = neighborhoods.get(j);
					//step 3: enter sequent sets corresponding to the box
					SequentCell cell = new SequentCell(a, b, rule, this);
					if(!cell.findSequentSets()) {
						return false;
					}
					cells.get(a).put(b, cell);
				}
			}
		}
		return true;
	}

	//step 4
	public boolean reduce() {
		boolean changed = true;
		while(changed) {
			changed = false;
			for(String a: cells.keySet()) {
				HashMap<String, SequentCell> row = cells.get(a);
				for(String b: row.keySet()) {
					SequentCell cell = cells.get(a).get(b);
					if(cell.reduce()){
						changed = true;
					}
				}
			}
		}
		return true;
	}

	//step 5
	public boolean assignWeights() {
		for(String a: cells.keySet()) {
			HashMap<String, SequentCell> row = cells.get(a);
			for(String b: row.keySet()) {
				SequentCell cell = cells.get(a).get(b);
				Container<SequentCell> cont = new Container<>();
				if(!cell.crossed && cell.getWeight(cont) <= 0) {
					return false;
				}
			}
		}
		return true;
	}

	//step 6
	public boolean checkSameLeftMost() {
		for(String a: cells.keySet()) {
			HashMap<String, SequentCell> row = cells.get(a);
			for(String b: row.keySet()) {
				SequentCell cell = cells.get(a).get(b);
				if(!cell.crossed) {
					String aLeft = cell.a.substring(0, cell.a.length()-1);
					String bLeft = cell.b.substring(0, cell.b.length()-1);
					if(aLeft.equals(bLeft)) {
						return false;
					}
				}
			}
		}
		return true;
	}

	public SequentCell getCell(String a, String b) {
		SequentCell cell = this.cells.get(a).get(b);
		if(cell == null) {
			return this.cells.get(b).get(a);
		}
		return cell;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(String a: cells.keySet()) {
			sb.append(a);
			sb.append(": ");
			HashMap<String, SequentCell> row = cells.get(a);
			for(String b: row.keySet()) {
				sb.append(b);
				sb.append(": ");
				SequentCell cell = cells.get(a).get(b);
				sb.append(cell.toString());
				sb.append(" ");
			}
			sb.append('\n');
		}
		return sb.toString();
	}
}
