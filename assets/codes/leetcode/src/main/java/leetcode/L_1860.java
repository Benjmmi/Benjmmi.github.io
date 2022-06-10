package leetcode;

public class L_1860 {
    public static void main(String[] args) {
        int[] i = new L_1860().memLeak(2,2);
        for (int i1 : i) {
            System.out.println(i1);
        }
        i = new L_1860().memLeak(8,11);
        for (int i1 : i) {
            System.out.println(i1);
        }
    }
    public int[] memLeak(int memory1, int memory2) {

        int i = 0;
        while (true){
            i++;
            if (memory1 >= i || memory2 >= i){
                if (memory1 == memory2 ){
                    memory1 -= i;
                    continue;
                }
                if (memory1 > memory2){
                    memory1 -= i;
                    continue;
                }else {
                    memory2 -= i;
                    continue;
                }

            }
            break;
        }

        return new int[]{i,memory1,memory2};
    }
}
