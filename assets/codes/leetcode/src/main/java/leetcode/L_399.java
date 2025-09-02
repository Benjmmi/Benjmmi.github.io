package leetcode;

import java.util.*;

public class L_399 {

    public double[] calcEquation(List<List<String>> equations, double[] values, List<List<String>> queries) {
        double[] result = new double[queries.size()];
        // 已经扫描的
        Map<String, Double> black = new HashMap<>();
        List<Set<String>> andCollections = new ArrayList<>();
        // 所有代数数量
        Queue<String> list = new ArrayDeque<>();
        Map<Integer, List<String>> map = new HashMap<>();
        int index = 0;
        for (List<String> equation : equations) {
            if (!list.contains(equation.get(0))) {
                list.add(equation.get(0));
            }
            if (!list.contains(equation.get(1))) {
                list.add(equation.get(1));
            }
            map.put(index, equation);
            index++;
        }
        Queue<String> temp = new ArrayDeque<>();
        List<String> exist = new ArrayList<>();
        while (!list.isEmpty()) {
            String item = temp.isEmpty() ? list.poll() : temp.poll();
            exist.add(item);
            List<Integer> white = new ArrayList<>();
            if (!black.containsKey(item)) {
                Set<String> andCollection = new HashSet<>();
                andCollection.add(item);
                andCollections.add(andCollection);
                black.put(item, 1.0);
            }
            dsf(item, black, map, values, white, andCollections, temp);
            for (Integer integer : white) {
                map.remove(integer);
            }
            for (String s : exist) {
                temp.remove(s);
                list.remove(s);
            }

            if (map.isEmpty()) {
                break;
            }
        }

        for (int i = 0; i < queries.size(); i++) {
            List<String> query = queries.get(i);
            String from = query.get(0);
            String to = query.get(1);
            if (!black.containsKey(from) || !black.containsKey(to)) {
                result[i] = -1.0;
                continue;
            }
            boolean find = false;
            for (Set<String> andCollection : andCollections) {
                if (andCollection.contains(from) && andCollection.contains(to)) {
                    find = true;
                    break;
                }
            }
            if (find) {
                result[i] = black.get(from) / black.get(to);
            } else {
                result[i] = -1.0;
            }
        }

        return result;
    }

    public void dsf(String key, Map<String, Double> black, Map<Integer, List<String>> map, double[] values, List<Integer> white, List<Set<String>> andCollections, Queue<String> temp) {
        for (Map.Entry<Integer, List<String>> item : map.entrySet()) {
            Integer index = item.getKey();
            List<String> value = item.getValue();
            if (!value.contains(key)) {
                continue;
            }
            String from = value.get(0);
            String to = value.get(1);

            boolean b = false;
            if (from.equals(key) && !black.containsKey(to)) {
                black.put(to, black.get(from) / values[index]);
                b = true;
                temp.add(to);
            }
            if (to.equals(key) && !black.containsKey(from)) {
                black.put(from, black.get(to) * values[index]);
                b = true;
                temp.add(from);
            }
            if (!b) {
                continue;
            }
            white.add(index);
            for (Set<String> andCollection : andCollections) {
                if (andCollection.contains(from) || andCollection.contains(to)) {
                    andCollection.add(from);
                    andCollection.add(to);
                }
            }
        }
    }


    public static void main(String[] args) {
        L_399 l399 = new L_399();
        {
            List<List<String>> equations = new ArrayList<>();
            {
                List<String> equation = new ArrayList<>();
                equation.add("a");
                equation.add("b");
                equations.add(equation);
            }
            {
                List<String> equation = new ArrayList<>();
                equation.add("a");
                equation.add("c");
                equations.add(equation);
            }
            {
                List<String> equation = new ArrayList<>();
                equation.add("d");
                equation.add("e");
                equations.add(equation);
            }
            {
                List<String> equation = new ArrayList<>();
                equation.add("d");
                equation.add("f");
                equations.add(equation);
            }
            {
                List<String> equation = new ArrayList<>();
                equation.add("a");
                equation.add("d");
                equations.add(equation);
            }
            {
                List<String> equation = new ArrayList<>();
                equation.add("aa");
                equation.add("bb");
                equations.add(equation);
            }
            {
                List<String> equation = new ArrayList<>();
                equation.add("aa");
                equation.add("cc");
                equations.add(equation);
            }
            {
                List<String> equation = new ArrayList<>();
                equation.add("dd");
                equation.add("ee");
                equations.add(equation);
            }
            {
                List<String> equation = new ArrayList<>();
                equation.add("dd");
                equation.add("ff");
                equations.add(equation);
            }
            {
                List<String> equation = new ArrayList<>();
                equation.add("aa");
                equation.add("dd");
                equations.add(equation);
            }
            {
                List<String> equation = new ArrayList<>();
                equation.add("a");
                equation.add("aa");
                equations.add(equation);
            }
            double[] values = new double[]{2.0, 3.0, 4.0, 5.0, 7.0, 5.0, 8.0, 9.0, 3.0, 2.0, 2.0};
            List<List<String>> queries = new ArrayList<>();
            {
                List<String> query = new ArrayList<>();
                query.add("ff");
                query.add("a");
                queries.add(query);
            }
            System.out.println(l399.calcEquation(equations, values, queries));

        }
        {
            List<List<String>> equations = new ArrayList<>();
            {
                List<String> equation = new ArrayList<>();
                equation.add("a");
                equation.add("b");
                equations.add(equation);
            }
            {
                List<String> equation = new ArrayList<>();
                equation.add("c");
                equation.add("d");
                equations.add(equation);
            }
            double[] values = new double[]{1.0, 1.0};
            List<List<String>> queries = new ArrayList<>();
            {
                List<String> query = new ArrayList<>();
                query.add("a");
                query.add("c");
                queries.add(query);
            }
            {
                List<String> query = new ArrayList<>();
                query.add("b");
                query.add("d");
                queries.add(query);
            }
            {
                List<String> query = new ArrayList<>();
                query.add("b");
                query.add("a");
                queries.add(query);
            }
            {
                List<String> query = new ArrayList<>();
                query.add("d");
                query.add("c");
                queries.add(query);
            }
            System.out.println(l399.calcEquation(equations, values, queries));

        }
    }
}
