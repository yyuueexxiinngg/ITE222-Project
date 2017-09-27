import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

public class CourseSubtitution {
    private HashMap<Integer, Course[]> subtitution = new HashMap<>();
    private HashMap<String,String> course_subtitution = new HashMap<>();
    String previous_year = "";
    String current_year = "";

    private void read() throws IOException {
        Scanner in = new Scanner(new File("src/other/Course Substitution from 2016 to 2014.csv"));
        int subtitution_num = -1;
        while (in.hasNextLine()) {
            String line = in.nextLine();
            subtitution_num++;
        }
        in = new Scanner(new File("src/other/Course Substitution from 2016 to 2014.csv"));
        int line_index = -1;

        Course[] previous_courses = new Course[subtitution_num];
        Course[] current_courses = new Course[subtitution_num];
        while (in.hasNextLine()) {
            line_index++;
            String line = in.nextLine();
            String[] split = line.split(",");
            if (line_index == 0) {
                previous_year = split[1];
                current_year = split[3];
            } else {
                course_subtitution.put(split[0],split[1]);


/*                previous_courses[line_index - 1] = new Course();
                previous_courses[line_index - 1].year = previous_year;
                previous_courses[line_index - 1].course_code = split[0];

                current_courses[line_index - 1] = new Course();
                current_courses[line_index - 1].year = current_year;
                current_courses[line_index - 1].course_code = split[2];*/
            }
        }
        subtitution.put(Integer.parseInt(previous_year), previous_courses);
        subtitution.put(Integer.parseInt(current_year), current_courses);
        for(String test:course_subtitution.keySet()){
            System.out.println(test+" to "+course_subtitution.get(test));
        }
    }

    public void process() throws IOException {
        read();
        ReadStudentProfile rc = new ReadStudentProfile();
        Student[] students = rc.read();
        for (int i = 0; i < students.length; i++) {
            if (students[i].year.equals(previous_year)) {
                for (int a = 0; a < students[i].course.length; a++) {

                }
            }
        }

    }
}
