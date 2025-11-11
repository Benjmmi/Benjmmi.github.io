package top75;

import java.util.ArrayList;
import java.util.List;

public class L_1431 {
    public List<Boolean> kidsWithCandies(int[] candies, int extraCandies) {
        int max = Integer.MIN_VALUE;
        for (int i : candies) {
            if (i > max) {
                max = i;
            }
        }
            List<Boolean> kids = new ArrayList<>();
        for (int candy : candies) {
            kids.add(candy + extraCandies > max);
        }
        return kids;
    }

    public static void main(String[] args) {

    }
}
