import java.util.*;

public class WolframTree {

    public static void main(String[] args) {
        for(int i = 0; i < 256; i++) {
            //determineSurjectivity(i);
            findGoEs(i);
        }
//        System.out.println(generateRuleMap(2).toString());
//        findGoEs(2);
    }

    public static HashMap<String, Integer> generateRuleMap(int rule) {
        HashMap<String, Integer> map = new HashMap<>();
        String bin = Integer.toString(rule, 2);
        bin = "0".repeat(8 - bin.length()) + bin;
        for(int i = 0; i < 8; i++) {
            String neighborhood = Integer.toBinaryString(i);
            neighborhood = "0".repeat(3 - neighborhood.length()) + neighborhood;
            map.put(neighborhood, bin.charAt(7 - i) - '0');
        }
        return map;
    }

    public static WolframNode buildTree(int rule) {
        HashMap<String, Integer> map = generateRuleMap(rule);

        WolframNode root = new WolframNode(map);
        Queue<WolframNode> q = new LinkedList<>();
        q.add(root);
        while(!q.isEmpty()) {
            WolframNode cur = q.poll();
            if(cur != null) {
//                System.out.println(cur.neighborhoods.toString());
                cur.generateChildren();
                q.add(cur.left);
                q.add(cur.right);
            }
        }
        return root;
    }

    public static void determineSurjectivity(int rule) {
        WolframNode root = buildTree(rule);
        if(root.isSurjective()) {
            System.out.println("Rule " + rule + " is surjective.");
        }
    }

    public static void findGoEs(int rule) {
        WolframNode root = buildTree(rule);
        GOEcontainer container = new GOEcontainer();
        root.findGoEs("", container);
        System.out.println("Rule " + rule + " has Garden of Edens: " + container.GoEs.toString());
    }

    static class GOEcontainer {
        ArrayList<String> GoEs;

        public GOEcontainer() {
            this.GoEs = new ArrayList<>();
        }

        public void add(String s) {
            this.GoEs.add(s);
        }
    }


}
