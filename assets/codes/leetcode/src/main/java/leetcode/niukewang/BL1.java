package samle.niukewang;

import java.util.Scanner;

public class BL1 {

    public static void main(String[] args) {
        System.out.println("fasfasf");
        Scanner sc = new Scanner(System.in);
        while(sc.hasNextLine()){
            long N = Long.parseLong(sc.nextLine());
            System.out.println(new BL1().a(N));
        }
    }

    public String a(long n) {
        String result = "";
        while (n > 0) {
            if (n % 2 == 0) {
                result = "3" + result;
                n = (n - 2) / 2;
            } else {
                result = "2" + result;
                n = (n - 1) / 2;
            }
        }
        return result;
    }
}
