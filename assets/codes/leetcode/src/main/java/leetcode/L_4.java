package leetcode;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class L_4 {

    public double findMedianSortedArrays(int[] nums1, int[] nums2) {
        if (nums1.length < nums2.length) {
            return findMedianSortedArrays(nums2, nums1);
        }
        int m = nums1.length;
        int n = nums2.length;

        // 2. 初始化二分查找的范围
        int low = 0;
        int high = m; // i 的取值范围是 [0, m]

        // `halfLen` 是合并后数组左半部分的元素总数
        int halfLen = (m + n + 1) / 2;

        // nums1 -> l1,r1 = l1<r1 :. nums1[i-1] < nums1[i]
        // nums2 -> l2,r2 = l2<r2 :. nums2[j-1] < nums2[j]
        //  l1 & r2 && l2 < r1  :. nums1[i-1] < nums[j] && nums1[j-1] < nums[i]
        //

        while (low <= high) {
            // 3. 计算分割线 i 和 j
            // i 是 nums1 在左半部分的元素个数
            int i = low + (high - low) / 2; // 防止溢出
            // j 是 nums2 在左半部分的元素个数
            int j = halfLen - i;

            // 4. 获取 L1, R1, L2, R2 四个边界值，并处理边界情况
            int nums1_L1 = (i == 0) ? Integer.MIN_VALUE : nums1[i - 1]; // L1
            int nums1_R1 = (i == m) ? Integer.MAX_VALUE : nums1[i];     // R1
            int nums2_L2 = (j == 0) ? Integer.MIN_VALUE : nums2[j - 1]; // L2
            int nums2_R2 = (j == n) ? Integer.MAX_VALUE : nums2[j];     // R2
            if (nums1_L1 > nums2_R2) {
                // i 太大了，需要向左移动
                high = i - 1;
            } else if (nums2_L2 > nums1_R1) {
                // i 太小了，需要向右移动
                low = i + 1;
            } else {
                // 6. 找到了完美的分割线，计算中位数

                // 左半部分的最大值
                double maxLeft = Math.max(nums1_L1, nums2_L2);

                // 如果总长度是奇数，中位数就是左半部分的最大值
                if ((m + n) % 2 == 1) {
                    return maxLeft;
                }

                // 如果总长度是偶数，还需要右半部分的最小值
                double minRight = Math.min(nums1_R1, nums2_R2);

                return (maxLeft + minRight) / 2.0;
            }
        }

        return 0;
    }

    public static void main(String[] args) {
        L_4 l4 = new L_4();
        System.out.println(l4.findMedianSortedArrays(new int[]{1, 3}, new int[]{2}));
    }
}
