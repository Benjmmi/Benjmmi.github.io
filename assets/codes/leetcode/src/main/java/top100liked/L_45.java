package top100liked;

public class L_45 {

    public int jump(int[] nums) {

        if (nums.length <= 1) return 0;

        int step = 0;
        int maxIndex = 0;
        for (int i = 0; i < nums.length && maxIndex < nums.length - 1; i++) {
            int len = maxIndex;
            for (int j = i; j <= len; j++) {
                if (j + nums[j] > maxIndex) {
                    i = j;
                    maxIndex = j + nums[j];
                }
            }
            step++;
        }

        return step;
    }

    public static void main(String[] args) {
        L_45 l45 = new L_45();
        System.out.println(l45.jump(new int[]{7, 0, 9, 6, 9, 6, 1, 7, 9, 0, 1, 2, 9, 0, 3}));
        System.out.println(l45.jump(new int[]{2, 3, 0, 1, 4}));
        System.out.println(l45.jump(new int[]{2, 1}));
        System.out.println(l45.jump(new int[]{1, 2, 3}));
        System.out.println(l45.jump(new int[]{2}));
    }
}
