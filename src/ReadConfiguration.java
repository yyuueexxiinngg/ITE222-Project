import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class ReadConfiguration {
    static String current_curriculum;
    static int courses_of_lecture;
    static int courses_of_student;
    static int add_score_for_graduation;
    static int add_score_for_normal_student;
    static int add_score_for_basic_course;
    static boolean if_deduct_score_to_courses_opened;

    public static void readConfiguration() throws IOException {
        current_curriculum = "2016";
        courses_of_lecture = 4;
        courses_of_student = 4;
        add_score_for_graduation = 4;
        add_score_for_normal_student = 2;
        if_deduct_score_to_courses_opened = true;

        Scanner in = new Scanner(new File("src/other/Configuration"));
        while (in.hasNext()) {
            String line = in.nextLine();
            if (line.startsWith("//") && in.hasNextLine()) {
                line = in.nextLine();
            }
            String[] split = line.split("=");
            switch (split[0]) {
                case "current_curriculum":
                    current_curriculum = split[1];
                    break;
                case "courses_of_lecturer":
                    courses_of_lecture = Integer.parseInt(split[1]);
                    break;
                case "courses_of_student":
                    courses_of_student = Integer.parseInt(split[1]);
                    break;
                case "add_score_for_normal_student":
                    add_score_for_normal_student = Integer.parseInt(split[1]);
                    break;
                case "add_score_for_graduation":
                    add_score_for_graduation = Integer.parseInt(split[1]);
                    break;
                case "add_score_for_basic_course":
                    add_score_for_basic_course = Integer.parseInt(split[1]);
                    break;
                case "if_deduct_score_to_courses_opened":
                    if (split[1].contains("false")) {
                        if_deduct_score_to_courses_opened = false;
                    }
                    break;
            }
        }
    }
}
