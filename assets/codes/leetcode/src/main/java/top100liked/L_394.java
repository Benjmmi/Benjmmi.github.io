package top100liked;

import java.util.Stack;

// TODO 多练
public class L_394 {
    public String decodeString(String s) {
        Stack<String> stack = new Stack<>();
        int len = s.length();
        String currentString = "";
        String currentNum = "";
        for (int i = 0; i < len; i++) {
            char c = s.charAt(i);
            if (c >= '0' && c <= '9') {
                currentNum += c;
            } else if (c == '[') {
                stack.push(currentString);
                stack.push(currentNum);
                currentNum = "";
                currentString = "";
            } else if (c == ']') {
                String preNumber = stack.pop();
                String preString = stack.pop();
                int num = preNumber.isEmpty() ? 1 : Integer.parseInt(preNumber);
                StringBuilder sub = new StringBuilder();
                for (int j = 0; j < num; j++) {
                    sub.append(currentString);
                }
                currentString = preString + sub;
            } else {
                currentString += c;
            }

        }
        return currentString;
    }

    public static void main(String[] args) {
        L_394 l = new L_394();
        System.out.println(l.decodeString("3[a]2[bc]"));
        System.out.println(l.decodeString("3[a2[c]]"));
    }
}
