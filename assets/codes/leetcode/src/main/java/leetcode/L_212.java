package leetcode;

import com.sun.deploy.util.StringUtils;

import java.util.*;

public class L_212 {
    int w = 0;
    int h = 0;
    char[][] board;

    public List<String> findWords(char[][] board, String[] words) {
        this.board = board;
        List<String> list = new ArrayList<>();
        // 存放开始坐标
        List<int[]>[] startLoc = new ArrayList[26];

        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                int c = board[i][j] - 'a';
                if (startLoc[c] == null) {
                    startLoc[c] = new ArrayList<>();
                }
                startLoc[c].add(new int[]{i, j});
            }
        }

        w = board.length;
        h = board[0].length;

        for (int i = 0; i < words.length; i++) {
            char[] word = words[i].toCharArray();
            List<int[]> match = startLoc[word[0] - 'a'];
            if (match == null) {
                continue;
            }
            for (int[] ints : match) {
                List<String> visited = new ArrayList<>();
                if (find(word, 0, ints[0], ints[1], visited)) {
                    list.add(words[i]);
                    break;
                }
            }
        }
        return list;
    }

    // bfs 不行
    // dfs 试一下
    public boolean find(char[] word, int index, int i, int j, List<String> visited) {
        if (index == word.length) {
            return true;
        }
        if (board[i][j] != word[index]) {
            visited.remove(i + "_" + j);
            return false;
        }
        visited.add(i + "_" + j);
        if (i + 1 < w) {
            String key = (i + 1) + "_" + (j);
            if (!visited.contains(key) && find(word, index + 1, i + 1, j, visited)) {
                return true;
            }
        }
        if (i - 1 >= 0) {
            String key = (i - 1) + "_" + (j);
            if (!visited.contains(key) && find(word, index + 1, i - 1, j, visited)) {
                return true;
            }
        }
        if (j + 1 < h) {
            String key = (i) + "_" + (j + 1);
            if (!visited.contains(key) && find(word, index + 1, i, j + 1, visited)) {
                return true;
            }
        }
        if (j - 1 >= 0) {
            String key = (i) + "_" + (j - 1);
            if (!visited.contains(key) && find(word, index + 1, i, j - 1, visited)) {
                return true;
            }
        }
        if (index+1 == word.length) {
            return true;
        }
        visited.remove(i + "_" + j);
        return false;
    }

    public static void main(String[] args) {
        L_212 l212 = new L_212();
        {
            List<String> result = l212.findWords(new char[][]{{'a','b','c'},{'a','e','d'},{'a','f','g'}}, new String[]{"abcdefg"});
            System.out.println(result);
        }
//        {
//            List<String> result = l212.findWords(new char[][]{{'o', 'a', 'a', 'n'}, {'e', 't', 'a', 'e'}, {'i', 'h', 'k', 'r'}, {'i', 'f', 'l', 'v'}}, new String[]{"oath", "pea", "eat", "rain"});
//            System.out.println(result);
//        }
//
//        {
//            List<String> result = l212.findWords(new char[][]{{'a', 'b', 'c', 'e'}, {'x', 'x', 'c', 'd'}, {'x', 'x', 'b', 'a'}}
//                    , new String[]{"abc", "abcd"});
//            System.out.println(result);
//        }
//        {
//            List<String> result = l212.findWords(new char[][]{{'o', 'a', 'a', 'n'}, {'e', 't', 'a', 'e'}, {'i', 'h', 'k', 'r'}, {'i', 'f', 'l', 'v'}}, new String[]{"oath", "aan", "naa", "rain"});
//            System.out.println(result);
//        }
//
//        {
//            List<String> result = l212.findWords(new char[][]{{'a', 'b'}, {'c', 'd'}}, new String[]{"abcd"});
//            System.out.println(result);
//        }
//        {
//            List<String> result = l212.findWords(new char[][]{{'a', 'a'}}, new String[]{"aaa"});
//            System.out.println(result);
//        }
    }
}
