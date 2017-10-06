import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;

public class Output {
    private List<Map.Entry<String, Integer>> score_list;
    HashSet<String> courses;

    public void run() throws IOException {
        form1();
        //form2();
        form3();
        HashSet<Integer> random = random(0, 5, 1);
    }

    private HashSet random(int min, int max, int n) {
        HashSet<Integer> random = new HashSet<>();
        while (random.size() < n) {
            random.add((int) (Math.random() * (max - min + 1)) + min);
        }
        return random;
    }

    private void form1() throws IOException {
        ReadConfiguration.readConfiguration();
        PrintStream out = new PrintStream("src/output/form1.csv");
        String out_str = "Course Code,Course Name,Score,Student Num\n";
        Calculate c = new Calculate();
        HashMap<String, Integer> score = c.calculate();
        HashMap<String, Integer> students_num = c.getStudentsNum();
        Comparator<Map.Entry<String, Integer>> valueComparator = new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return o2.getValue() - o1.getValue();
            }
        };
        this.score_list = new ArrayList<>(score.entrySet());
        Collections.sort(score_list, valueComparator);
        for (Map.Entry<String, Integer> entry : score_list) {
            out_str += entry.getKey() + "," + ReadCurriculum.getCourseName(entry.getKey()) + "," + entry.getValue() + "," + students_num.get(entry.getKey()) + "\n";
            // System.out.println(ReadCurriculum.getCourseName(entry.getKey()) + " " + entry.getKey() + "'s score: " + entry.getValue() + " Students num:" + students_num.get(entry.getKey()));
        }

        HashMap<String, HashSet> major_electives = new HashMap<>();
        HashSet<String> random_open = new HashSet<>();
        Scanner in = new Scanner(new File("src/other/Major electives.csv"));
        while (in.hasNextLine()) {
            String line = in.nextLine();
            String[] split = line.split(",");
            HashSet<String> course_code = new HashSet<>();
            for (int i = 1; i < split.length; i++) {
                course_code.add(split[i]);
            }
            major_electives.put(split[0], course_code);
        }

        for (String major : major_electives.keySet()) {
            String[] courses = ((HashSet<String>) major_electives.get(major)).toArray(new String[0]);
            if (major.contains("E-Commerce")) {
                for (int random : (HashSet<Integer>) random(0, 1, 1)) {
                    random_open.add(courses[random]);
                }
            } else if (major.contains("Software")) {
                for (int random : (HashSet<Integer>) random(0, 4, 2)) {
                    random_open.add(courses[random]);
                }
            } else {
                for (int random : (HashSet<Integer>) random(0, 4, 2)) {
                    random_open.add(courses[random]);
                }
            }
        }

        out_str += "\nRandom Electives\nCourse code,Course Name\n";

        for (String course_code : random_open) {
            out_str += course_code + "," + ReadCurriculum.getCourseName(course_code) + "\n";
        }
        out.println(out_str);


        Lecturer[] lecturers = new ReadBlackList().read();
        HashMap<String, HashSet> lectures_with_courses = new HashMap<>();
        HashMap<String, HashSet> black_list = new HashMap<>();
        courses = new HashSet<>();
        int index = 0;
        for (Map.Entry<String, Integer> entry : score_list) {
            if (index < (ReadConfiguration.courses_of_lecture * lecturers.length) - 5) {
                courses.add(entry.getKey());
                index++;
            }
        }
        for (String course_code : random_open) {
            courses.add(course_code);
        }

        for (int i = 0; i < lecturers.length; i++) {
            HashSet<String> list = new HashSet<>();
            for (int a = 0; a < lecturers[i].blak_list.length; a++) {
                list.add(lecturers[i].blak_list[a]);
            }
            black_list.put(lecturers[i].name, list);
        }

        for (int i = 0; i < lecturers.length; i++) {
            HashSet<String> course = new HashSet<>();
            Iterator<String> itr = courses.iterator();
            while (itr.hasNext()) {
                String course_code = itr.next();
                for (int a = 0; a < lecturers[i].blak_list.length; a++) {
                    if (!course_code.contains(lecturers[i].blak_list[a])) {
                        course.add(course_code);
                    }
                }
            }
        }

        for (String name : black_list.keySet()) {
            System.out.println(name + " " + black_list.get(name));
        }

/*        for (int i = 0; i < lecturers.length; i++) {
            HashSet<String> course = new HashSet<>();
            while (course.size()<ReadConfiguration.courses_of_lecture){

            }
            lectures_with_courses.put(lecturers[i].name,course);
        }*/


        for (String key : lectures_with_courses.keySet()) {
            System.out.println(key + " " + lectures_with_courses.get(key));
        }


    }

    private void form2() throws IOException {
        PrintStream out;
        String out_str = "";

        for (String course : courses) {
            out = new PrintStream("src/output/studentList/" + course + ".csv");
            out_str += course + "," + ReadCurriculum.getCourseName(course) + "\nID,Name\n";

            HashMap<Integer, HashSet> courses_can_take = new Calculate().checkAvailableCourses();
            for (int id : courses_can_take.keySet()) {
                for (String course_code : (HashSet<String>) courses_can_take.get(id)) {
                    if (course_code.contains(course)) {
                        out_str += id + "," + new ReadStudentProfile().getName(id) + "\n";
                    }
                }
            }
            out.println(out_str);
            out_str = "";
        }
    }

    private void form3() throws IOException {
        PrintStream out = new PrintStream("src/output/form3.csv");
        String out_str = ",";

        for (String course : courses) {
            out_str += course + ",";
        }
        String[] split = out_str.split(",");

        out_str += "\n";

        HashMap<Integer, HashSet> courses_can_take = new Calculate().checkAvailableCourses();
        for (int i = 0; i < split.length; i++) {
            for (int id : courses_can_take.keySet()) {
                for (String course_code : (HashSet<String>) courses_can_take.get(id)) {
                    if (split[i].contains(course_code)) {
                        out_str += new ReadStudentProfile().getName(id) + i + "\n";
                    }
                }
            }
        }


        for (String course : courses) {

            for (int id : courses_can_take.keySet()) {
                for (String course_code : (HashSet<String>) courses_can_take.get(id)) {
                    if (course_code.contains(course)) {
                        out_str += new ReadStudentProfile().getName(id);
                    }
                }
            }
        }

        out.println(out_str);
    }
}
