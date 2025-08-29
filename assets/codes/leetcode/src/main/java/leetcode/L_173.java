package leetcode;

import java.util.ArrayList;
import java.util.List;

public class L_173 {

    static class BSTIterator {
        TreeNode root;
        List<Integer> numbers;
        int i = 0;

        boolean left_right = true;

        public BSTIterator(TreeNode root) {
            this.root = root;
            numbers = new ArrayList<>();
            preorder(root);
        }

        public int next() {
            int val = numbers.get(i);
            i++;
            return val;
        }

        public boolean hasNext() {
            return i < numbers.size();
        }

        public void preorder(TreeNode root) {
            if (root == null){
                return;
            }
            preorder(root.left);
            numbers.add(root.val);
            preorder(root.right);
        }
    }

    public static void main(String[] args) {

    }
}
