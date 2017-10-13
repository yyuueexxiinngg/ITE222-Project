import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

public class ReadCoursesOpened {
    public HashMap<String, Integer> read() throws IOException {
        HashMap<String, Integer> courses_opened = new HashMap<>();
        Scanner in = new Scanner(new File("src/other/Courses opened.csv"));
        while (in.hasNextLine()) {
            String line = in.nextLine();
            String[] split = line.split(",");
            //If it contains null means it is elective course
            if (!split[1].contains("null")){
                courses_opened.put(split[0], Integer.parseInt(split[1]));
            }else {
                courses_opened.put(split[0],-1);
            }
        }
        return courses_opened;
    }
}
