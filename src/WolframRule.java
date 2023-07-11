public class WolframRule extends Rule {

	public WolframRule(int number) {
		super(number);
	}

	@Override
	public void setRuleCounts() {
		this.maxC = 16;
		this.maxT = 16;
	}

	@Override
	public void populateStates() {
		this.states.add('0');
		this.states.add('1');
	}

	public void generateRuleMap() {
		String bin = Integer.toString(this.number, 2);
		bin = "0".repeat(8 - bin.length()) + bin;
		for(int i = 0; i < 8; i++) {
			String neighborhood = Integer.toBinaryString(i);
			neighborhood = "0".repeat(3 - neighborhood.length()) + neighborhood;
			this.map.put(neighborhood, bin.charAt(7 - i));
		}
	}

	@Override
	public String getDebugColor(char c) {
		if(c == '1') {
			return "\u001B[32m"; //green;
		}
		return "\u001B[31m"; //red
	}
}
