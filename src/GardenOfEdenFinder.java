import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class GardenOfEdenFinder {

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
