import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Test
 */
public class Test {
    public static void main(String[] args) throws FileNotFoundException {
        File file = new File(args[0]);
        Scanner s = new Scanner(file);
        int a = s.nextInt();
        int b = s.nextInt();
        System.out.println(a+b);
        s.close();
    }
    
}