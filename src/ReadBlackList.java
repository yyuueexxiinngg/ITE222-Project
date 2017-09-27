import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

public class ReadBlackList {
    public Lecturer[] read() throws IOException {
        PrintStream out = new PrintStream("testing_out_lecturers.csv");
        Lecturer[] lecturers;
        //Get input from file
        Scanner in = new Scanner(new File("src/other/BlackList.csv"));
        //Use num++ at the first line when reading lines, and because the first line is useless, we should skip this number by start form -1
        int lecturers_num = -1;

        String test = "";
        //Read the file first time to count how many lectures
        while (in.hasNextLine()) {
            String line = in.nextLine();
            lecturers_num++;
        }
        //Crate up lectures obj
        lecturers = new Lecturer[lecturers_num];
        //Read the file again to process
        in = in = new Scanner(new File("src/other/BlackList.csv"));
        //For count the lecture current work with
        int line_index = -1;
        while (in.hasNextLine()) {
            line_index++;
            String line = in.nextLine();
            if (line_index > 0) {

                lecturers[line_index - 1] = new Lecturer();
                String[] split = line.split(",");
                lecturers[line_index - 1].name = split[0];
                //One lecture might have more than one course he couldn't teach
                lecturers[line_index - 1].blak_list = new String[split.length - 1];
                for (int i = 0; i < lecturers[line_index - 1].blak_list.length; i++) {
                    lecturers[line_index - 1].blak_list[i] = split[i + 1];
                }
            }
        }

        //Only for output testing
        for (int i = 0; i < lecturers.length; i++) {
            String list = "";
            if (lecturers[i].blak_list.length > 0) {
                for (int a = 0; a < lecturers[i].blak_list.length; a++) {
                    list += lecturers[i].blak_list[a] +",";
                }
            }
            test += "Name: " + lecturers[i].name + " Blacklist: " + list + "\n";
        }
        out.println(test);
        out.close();
        return lecturers;
    }
}
