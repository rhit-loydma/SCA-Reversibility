public class SimplifiedRule extends Rule{
	public SimplifiedRule(int number) {
		super(number);
	}

	@Override
	public void setRuleCounts() {
		this.maxC = 16;
		this.maxT = 16;
	}

	@Override
	public void populateStates() {
		this.states.add('B');
		this.states.add('F');
		this.states.add('R');
		this.states.add('L');
	}

	@Override
	public void generateRuleMap() {
		String bin = Integer.toString(this.number, 2);
		bin = "0".repeat(8 - bin.length()) + bin;
		for(int i = 0; i < 16; i++) {
			//get neighborhood
			int left = i / 4;
			int right = i % 4;
			//get output state
			int tIndex = (left/2)*2 + (right/2);
			int cIndex = (left%2)*2 + (right%2) + 4;
			int output = (bin.charAt(tIndex)-'0')*2 + (bin.charAt(cIndex)-'0');
			this.map.put("" + getState(left) + getState(right), getState(output));
		}
	}

	@Override
	public String getDebugColor(char c) {
		return switch (c) {
			case 'B', 'R', 'F', 'L' -> "\u001B[32m"; //green, 2 strands
			default -> "\u001B[31m"; //red, no strands
		};
	}

	public static char getState(int c) {
		return switch (c) {
			case (0) -> 'B';
			case (1) -> 'F';
			case (2) -> 'R';
			default -> 'L';
		};
	}
}
