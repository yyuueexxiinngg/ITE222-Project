import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

public class ReadCourseSubstitution {
    private Substitution substitution = new Substitution();

    public Substitution read() throws IOException {
        //The first string is previous and the second one is current
        HashMap<String, String> substitution = new HashMap<>();
        Scanner in = new Scanner(new File("src/other/Course Subtitution from 2016 to 2014.csv"));
        int line_index = 0;
        while (in.hasNextLine()) {
            String line = in.nextLine();
            String[] split = line.split(",");
            if (line_index > 0) {
                substitution.put(split[0],split[1]);
            }else {
                this.substitution.from_year = split[0];
                this.substitution.to_year = split[1];
            }
            line_index++;
        }
        this.substitution.substitution = substitution;
        return this.substitution;
    }
}
