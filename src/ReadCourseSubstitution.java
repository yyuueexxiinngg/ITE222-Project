import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

public class ReadCourseSubstitution {
    public HashMap<String,String> read() throws IOException {
        HashMap<String, String> substitution = new HashMap<>();
        Scanner in = new Scanner(new File("src/other/Course Subtitution from 2016 to 2014.csv"));
        int line_index = -1;
        while (in.hasNextLine()) {
            String line = in.nextLine();
            String[] split = line.split(",");
            if (line_index > 0) {
                substitution.put(split[0],split[1]);
            }
            line_index++;
        }
        return substitution;
    }
}
