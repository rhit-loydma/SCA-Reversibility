public class ExpandedRule extends Rule{
	public ExpandedRule(int number) {
		super(number);
	}

	@Override
	public void setRuleCounts() {
		maxT = 256;
		maxC = 16;
	}

	@Override
	public void populateStates() {
		states.add('B');
		states.add('F');
		states.add('R');
		states.add('L');
		states.add('b');
		states.add('f');
		states.add('r');
		states.add('l');
		states.add('N');
	}

	@Override
	public void generateRuleMap() {
		String crossing = Integer.toString(this.number%maxC, 2);
		//get 4-bit rule
		crossing = "0".repeat(4 - crossing.length()) + crossing;
		//fill in pre-determined bits
		crossing = crossing.substring(0,2) + "1" + crossing.substring(2,4) + "1002";

		String turning = Integer.toString(this.number/maxC, 2);
		//get 8-bit rule
		turning = "0".repeat(8 - turning.length()) + turning;
		//fill in pre-determined bits
		turning = turning + "2";

//        System.out.println("Crossing: " + this.number%maxC + " " + crossing);
//        System.out.println("Turning: " + this.number/maxC + " " + turning);

		for(char left: states) {
			for(char right: states) {
				//need to get crossing and turning status of each cell
				int leftC = getTopThreadLeft(left);
				int leftT = getTurningStatusLeft(left);
				int rightC = getTopThreadRight(right);
				int rightT = getTurningStatusRight(right);
				//use those to calculate indexes
				int crossingIndex = 3 * leftC + rightC;
				int turningIndex = 3 * leftT + rightT;
				//take binary strings reps of rules, get char at index
				int c = crossing.charAt(crossingIndex)-'0';
				int t = turning.charAt(turningIndex)-'0';

				//check for absent left thread for turning status
				int l = t;
				if(left == 'N' || left == 'f' || left == 'l') {
					l = 2;
				}

				//check for absent right thread for turning status
				int r = t;
				if(right == 'N' || right == 'b' || right == 'r') {
					r = 2;
				}

				//check if to make sure there are threads
				int cs = l*10 + r;
				c = switch(cs) {
					case 0, 11 ->  c; //exists, exists
					case 2, 12 -> 1; //upright, none -> left is on top
					case 20, 21 -> 0; //none, exists -> right is on top
					case 22 -> 2; //none, none -> no thread is on top
					default -> 2;
				};
//                System.out.println("Neighborhood: " + left + " " + right);
//                System.out.println("Statuses: " + leftT + " " + leftC + " " + rightT + " " + rightC);
//                System.out.println("Indexes: " + turningIndex + " " + crossingIndex);
//                System.out.println("Before checks: " + t + " " + c);
//                System.out.println("After checks: " + l + " " + r + " " + c);
//                System.out.println();
				map.put("" + left + right, getOutputExpanded(l,r,c));
			}
		}
	}

	@Override
	public String getDebugColor(char c) {
		return switch (c) {
			case 'B', 'R', 'F', 'L'-> "\u001B[32m"; //green, 2 strands
			case 'b', 'r', 'f', 'l'-> "\u001B[33m"; //yellow, 1 strand;
			default -> "\u001B[31m"; //red, no strands
		};
	}

	public static int getTopThreadLeft(char cell) {
		return switch (cell) {
			case 'L', 'B', 'b' -> 0;
			case 'F', 'R', 'r' -> 1;
			default -> 2; //f , l , N
		};
	}

	public static int getTopThreadRight(char cell) {
		return switch (cell) {
			case 'L', 'B', 'l' -> 0;
			case 'F', 'R', 'f' -> 1;
			default -> 2; //b, r, N
		};
	}

	public static int getTurningStatusLeft(char cell) {
		return switch (cell) {
			case 'b', 'F', 'B' -> 0;
			case 'r', 'L', 'R' -> 1;
			default -> 2; //f, l, N
		};
	}

	public static int getTurningStatusRight(char cell) {
		return switch (cell) {
			case 'f', 'F', 'B' -> 0;
			case 'l', 'L', 'R' -> 1;
			default -> 2; //b, r, N
		};
	}

	public static char getOutputExpanded(int leftTurning, int rightTurning, int crossing) {
		int c = crossing * 100 + leftTurning * 10 + rightTurning;
		//crossing: 0 R, 1 L, 2 No
		//turning: 0 U, 1 S, 2 No
		return switch (c) {
			case 0 -> 'B';
			case 100 -> 'F';
			case 11 -> 'R';
			case 111 -> 'L';
			case 20 -> 'b';
			case 102 -> 'f';
			case 112 -> 'l';
			case 21 -> 'r';
			default -> 'N';
		};
	}
}
