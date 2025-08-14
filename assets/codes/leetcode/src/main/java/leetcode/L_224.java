package leetcode;

import java.util.Stack;
import java.util.Vector;

public class L_224 {
    public int calculate(String s) {
        char[] cs = s.toCharArray();
        Stack<String> stack = new Stack<>();
        int tempValue = 0;
        boolean hasValue = false;
        for (char c : cs) {
            if (c == ' ') {
                continue;
            }
            if (c == '-' || c == '+' || c == '(') {
                if (hasValue) {
                    stack.push(String.valueOf(tempValue));
                    tempValue = 0;
                    hasValue = false;
                }
                stack.push(String.valueOf(c));
                continue;
            }
            if (c == ')') {
                if (hasValue) {
                    stack.push(String.valueOf(tempValue));
                    hasValue = false;
                    tempValue = 0;
                }

                int sum = 0;
                String tt = "0";
                while (true) {
                    String temp = stack.pop();
                    if (temp.equals("(")) {
                        sum += Integer.parseInt(tt);
                        break;
                    }
                    if (temp.equals("+") || temp.equals("-")) {
                        sum += (temp.equals("-") ? -1 : 1) * Integer.parseInt( tt);
                        tt = "0";
                    } else {
                        tt = temp;
                    }
                }
                stack.push(String.valueOf(sum));
                continue;
            }
            tempValue = tempValue * 10 + c - '0';
            hasValue = true;
        }
        if (hasValue) {
            stack.push(String.valueOf(tempValue));
        }

        int sum = 0;
        String tt = "0";
        while (!stack.isEmpty()) {
            String temp = stack.pop();
            if (temp.equals("+") || temp.equals("-")) {
                sum += (temp.equals("-") ? -1 : 1) * Integer.parseInt( tt);
                tt = "0";
            } else {
                tt = temp;
            }
        }
        sum += Integer.parseInt(tt);

        return sum;
    }

    public static void main(String[] args) {
        L_224 l224 = new L_224();
        {
            System.out.println(l224.calculate("- (3 + (4 + 5))"));
        }
        {
            System.out.println(l224.calculate("(1+(4+5+2)-3)+(6+8)"));
        }
        {
            System.out.println(l224.calculate("2147483647"));
        }
        {
            System.out.println(l224.calculate("1 + 1"));
        }
        {
            System.out.println(l224.calculate(" 2-1 + 2 "));
        }
        {
            System.out.println(l224.calculate("1"));
        }
        {
            System.out.println(l224.calculate("-1"));
        }
        {
            System.out.println(l224.calculate("1-(     -2)"));
        }
        System.out.println(Integer.parseInt("+1234"));
        System.out.println(Integer.parseInt("-1234"));
    }
}
