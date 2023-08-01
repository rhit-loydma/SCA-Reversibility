import java.util.ArrayList;

public class MulticoloredRule extends Rule{
	public MulticoloredRule(int c, int t) {
		super(c, t);
	}

	@Override
	public void setRuleCounts() {
		this.maxC = 65536;
		this.maxT = 65536;
	}

	@Override
	public void populateStates() {
		this.states.add('B');
		this.states.add('F');
		this.states.add('R');
		this.states.add('L');
		this.states.add('b');
		this.states.add('f');
		this.states.add('r');
		this.states.add('l');
		this.states.add('s');
		this.states.add('w');
		this.states.add('d');
		this.states.add('a');
		this.states.add('S');
		this.states.add('W');
		this.states.add('D');
		this.states.add('A');
	}

	@Override
	public void generateRuleMap() {
		crossing = "0".repeat(16 - crossing.length()) + crossing;
		turning = "0".repeat(16 - turning.length()) + turning;
//		System.out.println(crossing);
//		System.out.println(turning);

		for(char left: states) {
			for (char right : states) {
//				System.out.println("\n" + left + "" + right);
				//get statuses
				int lt = getLeftColor(left) * 2 + getTurningStatus(left);
				int lc = getLeftColor(left) * 2 + getCrossingStatus(left);
				int rt = getRightColor(right) * 2 + getTurningStatus(right);
				int rc = getRightColor(right) * 2 + getCrossingStatus(right);
//				System.out.println(left + " " + lt + " " + lc);
//				System.out.println(right + " " + rt + " " + rc);

				//calculate indexes
				int tIndex = 4 * lt + rt;
				int cIndex = 4 * lc + rc;
//				System.out.println(cIndex + " " + tIndex);

				//get relevant bits
				int turningBit = turning.charAt(tIndex)-'0';
				int crossingBit = crossing.charAt(cIndex)-'0';
//				System.out.println(crossingBit + " " + turningBit);

				map.put("" + left + right, getOutput(turningBit, crossingBit, getLeftColor(left), getRightColor(right)));
			}
		}
	}
	public int getTurningStatus(char c) {
		return switch (c) {
			case 'B', 'F', 'b', 'f', 'S', 'W', 's', 'w' -> 0;
			default -> 1;
		};
	}

	public int getCrossingStatus(char c) {
		return switch (c) {
			case 'R', 'B', 'r', 'b', 'D', 'S', 'd', 's' -> 0;
			default -> 1;
		};
	}

	public int getLeftColor(char c) {
		return switch (c) {
			case 'L', 'R', 'F', 'B', 'l', 'b', 'd', 'w' -> 1;
			default -> 0;
		};
	}

	public int getRightColor(char c) {
		return switch (c) {
			case 'L', 'R', 'F', 'B', 'r', 'f', 'a', 's' -> 1;
			default -> 0;
		};
	}

	public char getOutput(int t, int c, int l, int r) {
		int state = t * 1000 + c * 100 + l * 10 + r;
		return switch (state) {
			case 1111 -> 'L';
			case 1011 -> 'R';
			case 111 -> 'F';
			case 11 -> 'B';
			case 1110 -> 'l';
			case 1001-> 'r';
			case 110 -> 'f';
			case 1 -> 'b';
			case 1101 -> 'a';
			case 1010 -> 'd';
			case 101-> 'w';
			case 10 -> 's';
			case 1100-> 'A';
			case 1000 -> 'D';
			case 100 -> 'W';
			default -> 'S';
		};
	}

	@Override
	public String getDebugColor(char c) {
		return switch (c) {
			case 'B', 'R', 'F', 'L' -> "\u001B[34m"; //blue, both color A
			case 'b', 'r', 'f', 'l' -> "\u001B[32m"; //green, color A on top
			case 's', 'd', 'w', 'a' -> "\u001B[35m"; //magenta, color B on top
			default -> "\u001B[31m"; //red, both color B
		};
	}

	@Override
	public String toDebugString(){
		ArrayList<Character> left = new ArrayList<>();
		left.add('B');
		left.add('F');
		left.add('R');
		left.add('L');
		left.add('b');
		left.add('l');
		left.add('w');
		left.add('d');
		left.add('f');
		left.add('r');
		left.add('s');
		left.add('a');
		left.add('S');
		left.add('W');
		left.add('D');
		left.add('A');

		ArrayList<Character> right = new ArrayList<>();
		right.add('B');
		right.add('F');
		right.add('R');
		right.add('L');
		right.add('f');
		right.add('r');
		right.add('s');
		right.add('a');
		right.add('b');
		right.add('l');
		right.add('w');
		right.add('d');
		right.add('S');
		right.add('W');
		right.add('D');
		right.add('A');

		StringBuilder sb = new StringBuilder("\u001B[1;36m" + "  ");
		for(char c: right) { //header row
			sb.append(c);
			sb.append(" ");
		}
		sb.append('\n');
		for(char c: left) {
			sb.append("\u001B[1;36m" + c);
			sb.append("\u001B[0m "); //reset bold
			for(char d: right) {
				char output = map.get(""+c+d);
				sb.append(getDebugColor(output));
				sb.append(map.get(""+c+d));
				sb.append(" ");
			}
			sb.append('\n');
		}
		sb.append("\u001B[0m"); //reset colors
		return sb.toString();
	}
}

