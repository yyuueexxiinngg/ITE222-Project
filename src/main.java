import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;

public class main {
    public static void main(String[] args) throws IOException {
        Scanner in = new Scanner(System.in);
        System.out.println("Remove course opened in last term before process? \n1 --- Yes\n2 --- No");
        int selection = in.nextInt();
        if(selection == 1){
            PrintStream out = new PrintStream("src/other/Courses opened.csv");
            out.print("");
            out.close();
        }

        Output o = new Output();
        o.run();
    }
}
