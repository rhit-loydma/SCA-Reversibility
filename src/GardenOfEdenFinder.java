import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class GardenOfEdenFinder {
    public static void main(String[] args) {
        HashMap<String,Character> map = new HashMap<>();
        map.put("000", '0');
        map.put("001", '1');
        map.put("010", '1');
        map.put("011", '1');
        map.put("100", '0');
        map.put("101", '1');
        map.put("110", '1');
        map.put("111", '0');

        for(String k: map.keySet()) {
            System.out.println(k + " " + findPredecessors(k, false, generateRuleMap(115)));
        }
    }

    public static HashMap<String, Character> generateRuleMap(int rule) {
        HashMap<String, Character> map = new HashMap<>();
        String bin = Integer.toString(rule, 2);
        bin = "0".repeat(8 - bin.length()) + bin;
        for(int i = 0; i < 8; i++) {
            String neighborhood = Integer.toBinaryString(i);
            neighborhood = "0".repeat(3 - neighborhood.length()) + neighborhood;
            map.put(neighborhood, bin.charAt(7 - i));
        }
        return map;
    }

    public static ArrayList<String> findPredecessors(String config, boolean wrapsAround, HashMap<String, Character> rule) {
        ArrayList<String> a = new ArrayList<>();

        for(String n: rule.keySet()) {
            if(rule.get(n) == config.charAt(0)) {
                a.add(n);
            }
        }

        for(int i = 1; i < config.length(); i++) {
            ArrayList<String> b = new ArrayList<>();
            char curChar = config.charAt(i);
            for(String path: a) { //iterate through current possibilities
                String last = path.substring(path.length() - 2);
                //todo optimize so we just add on a state instead of going through every neighborhood
                for(String n: rule.keySet()) { //every neighborhood
                    String first = n.substring(0, 2);
                    if(rule.get(n) == curChar && last.equals(first)) {
                        b.add(path + n.substring(n.length() - 1));
                    }
                }
            }
            a = b;
        }

        if(wrapsAround) {
            for(int i = 0; i < a.size(); i++) {
                String cur = a.get(i);
                String first = cur.substring(0,2);
                String last = cur.substring(cur.length()-2);
                if(!first.equals(last)) {
                    a.remove(i--);
                }
            }
        }

        return a;
    }
}
