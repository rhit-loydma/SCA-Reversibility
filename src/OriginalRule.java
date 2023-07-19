public class OriginalRule extends Rule{
	public static final char SNN = '/';
    public static final char NNN = '_';
    public static final char NSN = '\\';
    public static final char UNN = '[';//'\u23b8';
    public static final char UUN = 'U';//'\u2016';
    public static final char NUN = ']';//'\u23b9';
    public static final char SSR = 'R';
    public static final char SSL = 'L';

	public OriginalRule(int c, int t) {
		super(c, t);
	}

	@Override
	public void setRuleCounts() {
		maxT = 512;
		maxC = 512;
	}

	@Override
	public void populateStates() {
		states.add(SNN);
		states.add(NNN);
		states.add(NSN);
		states.add(UNN);
		states.add(UUN);
		states.add(NUN);
		states.add(SSR);
		states.add(SSL);
	}

	@Override
	public void generateRuleMap() {
		crossing = "0".repeat(9 - crossing.length()) + crossing;
		turning = "0".repeat(9 - turning.length()) + turning;

		for(char left: states) {
			for(char right: states) {
				//need to get crossing and turning status of each cell
				int leftC = getCrossingStatusLeft(left);
				int leftT = getTurningStatus(left);
				int rightC = getCrossingStatusRight(right);
				int rightT = getTurningStatus(right);
				//use those to calculate indexes
				int crossingIndex = 8-(3 * leftC + rightC);
				int turningIndex = 8-(3 * leftT + rightT);
				//take binary strings reps of rules, get char at index
				int c = crossing.charAt(crossingIndex)-'0';
				int t = turning.charAt(turningIndex)-'0';

				//check for absent left thread for turning status
				int l = t;
				if(left == NNN || left == SNN || left == UNN) {
					l = -1;
				}

				//check for absent right thread for turning status
				int r = t;
				if(right == NNN || right == NSN || right == NUN) {
					r = -1;
				}

				//check if there is a cross
				if (!(l == 1 && r == 1)) {
					c = -1;
				}

				map.put("" + left + right, getOutputOriginal(l,r,c));
			}
		}
	}

	@Override
	public String getDebugColor(char c) {
		return switch (c) {
			case UUN -> "\u001B[32m"; //green, 2 strands
			case SNN, NSN, UNN, NUN -> "\u001B[33m"; //yellow, 1 strand;
			default -> "\u001B[31m"; //red, no strands
		};
	}

	public static int getCrossingStatusLeft(char cell) {
		return switch (cell) {
			case SSR -> 0;
			case SSL -> 2;
			default -> 1;
		};
	}

	public static int getCrossingStatusRight(char cell) {
		return switch (cell) {
			case SSR -> 2;
			case SSL -> 0;
			default -> 1;
		};
	}

	public static int getTurningStatus(char cell) {
		return switch (cell) {
			case NNN -> 1;
			case UNN, NUN, UUN -> 2;
			default -> 0; //slanted
		};
	}

	public static char getOutputOriginal(int leftTurning, int rightTurning, int crossing) {
		switch (crossing) {
			case 1 -> {return SSL;}
			case 0 -> {return SSR;}
			default -> { //no crossing
				switch (leftTurning) {
					case 1 -> {return SNN;}
					case 0 -> {
						if (rightTurning == 0) {return UUN;}
						else {return UNN;}
					}
					default -> { //no left thread
						return switch (rightTurning) {
							case 1 -> NSN;
							case 0 -> NUN;
							default -> NNN;
						};
					}
				}
			}
		}
	}
}
