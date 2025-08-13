package leetcode;

import java.util.Stack;

public class L_20 {

    public boolean isValid(String s) {
        Stack<Character> stacks = new Stack<>();
        char[] chars = s.toCharArray();
        for (char item : chars) {
            Character c = null;
            if (item == ')' || item == ']' || item == '}') {
                c = stacks.isEmpty() ? 'A' : stacks.pop();
            }
            if (item == ')' && c == '(') {
                continue;
            }
            if (item == ']' && c == '[') {
                continue;
            }
            if (item == '}' && c == '{') {
                continue;
            }
            if (item == '(' || item == '[' || item == '{') {
                stacks.push(item);
            } else {
                return false;
            }
        }

        return stacks.isEmpty();
    }

    public static void main(String[] args) {
        L_20 l20 = new L_20();
        {
            System.out.println(l20.isValid("()"));
        }
        {
            System.out.println(l20.isValid("()[]{}"));
        }
        {
            System.out.println(l20.isValid("(]"));
        }
        {
            System.out.println(l20.isValid("([])"));
        }
        {
            System.out.println(l20.isValid("([)]"));
        }
    }
}
