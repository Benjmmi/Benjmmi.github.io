package nowcoder;

import java.io.IOException;
import java.util.Scanner;

public class BL20 {
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String s = scanner.nextLine();
            int k = Integer.parseInt(s.split(" ")[0]);

            s = s.substring(s.split(" ")[0].length() + 1);
            System.out.println(find(k, s));
        }
//        System.out.println(find(2,"misakamikotodaisuki"));
//        System.out.println(find(3,"!bakabaka~ bakabaka~ 1~2~9!"));
//        System.out.println(find(3,"3.1415926535897932384626433832795028841971693993751o582097494459211451488946419191919l91919hmmhmmahhhhhhhhhh"));
//        System.out.println(find(7,"www.bilibili.com/av170001"));
//        System.out.println(find(1,"111"));
//        System.out.println(find(1,"!!@@##$$%%^^&&**(())__++--==::;;,,<<>>..//??]][[}}{{3"));
    }

    public static String find(int index, String str) {

        while (true) {
            if (str.length() < index){
                return "Myon~";
            }
            char c = str.charAt(0);
            int lastIndex = str.lastIndexOf(c);
            if (lastIndex == 0) {
                index--;
                if (index == 0) {
                    return "[" + c + "]";
                }
            }
            if ((c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9') || (c >= 'a' && c <= 'z')){
                str = str.replaceAll(String.valueOf(c), "");
            } else {
                str = str.replaceAll(String.format("\\%c",c), "");
            }
        }
    }
}
