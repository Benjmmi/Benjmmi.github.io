package leetcode;

import java.util.*;

public class L_637 {


    public static void main(String[] args) {
        System.out.println(new L_637().averageOfLevels(null));
    }

    Map<Integer, Integer> flows = new HashMap<>();
    Map<Integer, Double> sums = new HashMap<>();

    public List<Double> averageOfLevels(TreeNode root) {
        averageOfLevels(root, 0);

        List<Double> doubles = new ArrayList<>();
        for (int i = 0; i < flows.size(); i++) {
            doubles.add(sums.get(i) / (flows.get(i) * 1.0));
        }
        return doubles;
    }


    public void averageOfLevels(TreeNode root, int flow) {
        if (root == null) {
            return;
        }
        flows.put(flow, flows.getOrDefault(flow, 0) + 1);
        sums.put(flow, sums.getOrDefault(flow, 0.0) + root.val);
        averageOfLevels(root.right, flow + 1);
        averageOfLevels(root.left, flow + 1);
    }

    public List<Double> averageOfLevels2(TreeNode root) {
        if (root == null) {
            return null;
        }
        List<Double> doubles = new ArrayList<>();
        Queue<TreeNode> queue = new ArrayDeque<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            int size = queue.size();
            double sum = 0;
            for (int i = 0; i < size; i++) {
                TreeNode treeNode = queue.poll();
                if (treeNode.left != null) {
                    queue.offer(treeNode.left);
                }
                if (treeNode.right != null) {
                    queue.offer(treeNode.right);
                }
                sum += treeNode.val;
            }
            doubles.add(sum / size);
        }
        return doubles;
    }


}
