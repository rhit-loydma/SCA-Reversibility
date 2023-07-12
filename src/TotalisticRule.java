public class TotalisticRule extends Rule{
	public TotalisticRule(int number) {
		super(number);
	}

	@Override
	public void setRuleCounts() {
		this.maxC = 512;
		this.maxT = 512;
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
		String crossing = Integer.toString(this.number%maxC, 2);
		crossing = "0".repeat(9 - crossing.length()) + crossing;

		String turning = Integer.toString(this.number/maxC, 2);
		turning = "0".repeat(9 - turning.length()) + turning;

		for(char left: states) {
			for (char right : states) {
				//add together each of the statuses
				int t = getTurningStatus(left) + getTurningStatus(right);
				int c = getCrossingStatus(left) + getCrossingStatus(right);
				int color = getColorStatus(left) + getColorStatus(right);

				//calculate indexes
				int tIndex = 3 * t + color;
				int cIndex = 3 * c + color;

				//get relevant bits
				int turningBit = turning.charAt(tIndex)-'0';
				int crossingBit = crossing.charAt(cIndex)-'0';

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

	public int getColorStatus(char c) {
		return switch (c) {
			case 'W', 'A', 'S', 'D', 'w', 'a', 's', 'd' -> 0;
			default -> 1;
		};
	}

	public int getLeftColor(char c) {
		return switch (c) {
			case 'L', 'R', 'F', 'B', 'l', 'b', 'd', 'w' -> 0;
			default -> 1;
		};
	}

	public int getRightColor(char c) {
		return switch (c) {
			case 'L', 'R', 'F', 'B', 'r', 'f', 'a', 's' -> 0;
			default -> 1;
		};
	}

	public char getOutput(int t, int c, int l, int r) {
		int state = t * 1000 + c * 100 + l * 10 + r;
		return switch (state) {
			case 1100 -> 'L';
			case 1000 -> 'R';
			case 100 -> 'F';
			case 0 -> 'B';
			case 1101 -> 'l';
			case 1010-> 'r';
			case 101 -> 'f';
			case 10 -> 'b';
			case 1110 -> 'a';
			case 1001 -> 'd';
			case 110-> 'w';
			case 1 -> 's';
			case 1111-> 'A';
			case 1011 -> 'D';
			case 111 -> 'W';
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
}
