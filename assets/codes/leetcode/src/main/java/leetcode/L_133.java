package leetcode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class L_133 {

    class Node {
        public int val;
        public List<Node> neighbors;

        public Node() {
            val = 0;
            neighbors = new ArrayList<Node>();
        }

        public Node(int _val) {
            val = _val;
            neighbors = new ArrayList<Node>();
        }

        public Node(int _val, ArrayList<Node> _neighbors) {
            val = _val;
            neighbors = _neighbors;
        }
    }
    List<Node> cache = new ArrayList<>();
    Node copy = null;
    Map<Node, Node> nodeNodeMap = new HashMap<>();

    public Node cloneGraph(Node node) {
        if (node == null) {
            return copy;
        }
        copy = new Node();
        copy.neighbors = new ArrayList<>();
        copy.val = node.val;

        dfs(node, copy);
        return copy;
    }

    public void dfs(Node node, Node deepCopy) {
        if (node == null) {
            return;
        }
        cache.add(node);
        nodeNodeMap.put(node, deepCopy);
        if (deepCopy.neighbors == null) {
            deepCopy.neighbors = new ArrayList<>();
        }
        for (Node neighbor : node.neighbors) {
            if (cache.contains(neighbor)) {
                deepCopy.neighbors.add(nodeNodeMap.get(neighbor));
                continue;
            }
            Node newNode = new Node();
            newNode.val = neighbor.val;
            deepCopy.neighbors.add(newNode);
            dfs(neighbor, newNode);
        }
    }


    public static void main(String[] args) {

    }
}
