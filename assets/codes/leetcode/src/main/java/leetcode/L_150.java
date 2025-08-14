package leetcode;

import java.util.Stack;

public class L_150 {
    public int evalRPN(String[] tokens) {

        Stack<Integer> integers = new Stack<>();
        for (String token : tokens) {
            if (token.equals("*")) {
                Integer v1 = integers.pop();
                Integer v2 = integers.pop();
                integers.push(v1 * v2);
            } else if (token.equals("+")) {
                Integer v1 = integers.pop();
                Integer v2 = integers.pop();
                integers.push(v1 + v2);
            } else if (token.equals("/")) {
                Integer v1 = integers.pop();
                Integer v2 = integers.pop();
                integers.push(v2 / v1);
            } else if (token.equals("-")) {
                Integer v1 = integers.pop();
                Integer v2 = integers.pop();
                integers.push(v2 - v1);
            } else {
                integers.push(Integer.parseInt(token));
            }
        }

        return integers.pop();
    }

    public static void main(String[] args) {
        L_150 l150 = new L_150();
        {
            System.out.println(l150.evalRPN(new String[]{"2", "1", "+", "3", "*"}));
        }
        {
            System.out.println(l150.evalRPN(new String[]{"4", "13", "5", "/", "+"}));
        }
        {
            System.out.println(l150.evalRPN(new String[]{"10", "6", "9", "3", "+", "-11", "*", "/", "*", "17", "+", "5", "+"}));
        }
    }

}
