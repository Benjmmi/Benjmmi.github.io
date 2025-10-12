package top100liked;

public class L_238 {
    public int[] productExceptSelf(int[] nums) {
        int n = nums.length;
        int[] answer = new int[nums.length];
        int[] preSum = new int[nums.length];
        int[] afterSum = new int[nums.length];
        preSum[0] = nums[0];
        afterSum[n - 1] = nums[n - 1];
        for (int i = 1; i < n; i++) {
            preSum[i] = preSum[i - 1] * nums[i];
        }
        for (int i = n - 2; i >= 0; i--) {
            afterSum[i] = afterSum[i + 1] * nums[i];
        }
        for (int i = 0; i < n; i++) {
            if (i + 1 >= n) {
                answer[i] = preSum[i - 1];
            } else if (i - 1 < 0) {
                answer[i] = afterSum[i + 1];
            } else {
                answer[i] = preSum[i - 1] * afterSum[i + 1];
            }
        }
        return answer;
    }

    public static void main(String[] args) {
        L_238 l238 = new L_238();
        l238.productExceptSelf(new int[]{1, 2, 3, 4});
        l238.productExceptSelf(new int[]{1, 2});
    }
}
