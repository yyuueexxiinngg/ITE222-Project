import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.util.*;

public class Output {
    private List<Map.Entry<String, Integer>> score_list;
    HashSet<String> courses;
    HashMap<String, Integer> courses_opened;

    public void run() throws IOException {
        courses_opened = new ReadCoursesOpened().read();
        //form1 is suggestion form
        System.out.println("Processing output form1 for suggestion...");
        form1();
        //Form2 is the lists of a course that student can take
        form2();
        System.out.println("Processing output student list 100%\nProcessing output suggestion for student...");
        //Form3 is suggestion for student
        form3();
        System.out.println("All the processing are finished, thanks for using ^-^");
    }

    private HashSet random(int min, int max, int n) {
        //Here is to get random number
        HashSet<Integer> random = new HashSet<>();
        while (random.size() < n) {
            random.add((int) (Math.random() * (max - min + 1)) + min);
        }
        return random;
    }

    private void form1() throws IOException {
        //Read configuration first
        ReadConfiguration.readConfiguration();
        PrintStream out = new PrintStream("src/output/Suggestion.csv");
        String out_str = "Course Code,Course Name,Score,Student Num,Is opened\n";
        Calculate c = new Calculate();
        //Get the score of each course, key is course code
        HashMap<String, Integer> score = c.calculate();
        //Get the student number of each course, key is course code
        HashMap<String, Integer> students_num = c.getStudentsNum();

        //Using comparator to sort the courses by score
        Comparator<Map.Entry<String, Integer>> valueComparator = new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return o2.getValue() - o1.getValue();
            }
        };
        this.score_list = new ArrayList<>(score.entrySet());
        Collections.sort(score_list, valueComparator);
        for (Map.Entry<String, Integer> entry : score_list) {
            if (courses_opened.get(entry.getKey()) != null) {
                //Check whether the course is opened or not
                out_str += entry.getKey() + "," + ReadCurriculum.getCourseName(entry.getKey()) + "," + entry.getValue() + "," + students_num.get(entry.getKey()) + ",Yes" + "\n";
            } else {
                out_str += entry.getKey() + "," + ReadCurriculum.getCourseName(entry.getKey()) + "," + entry.getValue() + "," + students_num.get(entry.getKey()) + ",No" + "\n ";
            }
        }

        //Create random open filed for elective courses
        HashMap<String, HashSet> major_electives = new HashMap<>();
        HashSet<String> random_open = new HashSet<>();
        //Read the file first
        Scanner in = new Scanner(new File("src/other/Major electives.csv"));
        while (in.hasNextLine()) {
            String line = in.nextLine();
            String[] split = line.split(",");
            HashSet<String> course_code = new HashSet<>();
            for (int i = 1; i < split.length; i++) {
                course_code.add(split[i]);
            }
            //Put the electives courses in to map according to the major
            major_electives.put(split[0], course_code);
        }

        for (String major : major_electives.keySet()) {
            String[] courses = ((HashSet<String>) major_electives.get(major)).toArray(new String[0]);
            if (major.contains("E-Commerce")) {
                for (int random : (HashSet<Integer>) random(0, 1, 1)) {
                    //Random 1 course from E-C
                    if (courses_opened.get(courses[random]) != null) {
                        //if the course is opened last term, then change one
                        if (random == 0) {
                            random_open.add(courses[1]);
                        } else {
                            random_open.add(courses[0]);
                        }
                    }else {
                        random_open.add(courses[random]);
                    }
                }
            } else if (major.contains("Software")) {
                for (int random : (HashSet<Integer>) random(0, 4, 2)) {
                    //Same as above
                    if (courses_opened.get(courses[random]) != null || random_open.contains(random)) {
                        //Care about the index of array
                        if (random + 1 <= 4) {
                            random_open.add(courses[random + 1]);
                        } else {
                            random_open.add(courses[random - 1]);
                        }
                    }
                    random_open.add(courses[random]);
                }
            } else {
                //Same as above
                for (int random : (HashSet<Integer>) random(0, 4, 2)) {
                    if (courses_opened.get(courses[random]) != null || random_open.contains(random)) {
                        if (random + 1 <= 4) {
                            random_open.add(courses[random + 1]);
                        } else {
                            random_open.add(courses[random - 1]);
                        }
                    }
                    random_open.add(courses[random]);
                }
            }
        }

        out_str += "\nRandom Electives\nCourse code,Course Name\n";

        for (String course_code : random_open) {
            out_str += course_code + "," + ReadCurriculum.getCourseName(course_code) + "\n";
        }


        Lecturer[] lecturers = new ReadBlackList().read();
        HashMap<String, HashSet> lectures_with_courses = new HashMap<>();
        HashMap<String, HashSet> black_list = new HashMap<>();
        courses = new HashSet<>();
        int index = 0;
        //Get courses in the list
        for (Map.Entry<String, Integer> entry : score_list) {
            //Here is to take out the courses we need according to how many course a teacher can teach
            if (index < (ReadConfiguration.courses_of_lecture * lecturers.length) - 5) {
                courses.add(entry.getKey());
                index++;
            }
        }
        for (String course_code : random_open) {
            //Also the random courses should assign to teacher
            courses.add(course_code);
        }

        for (int i = 0; i < lecturers.length; i++) {
            HashSet<String> list = new HashSet<>();
            for (int a = 0; a < lecturers[i].blak_list.length; a++) {
                list.add(lecturers[i].blak_list[a]);
            }
            black_list.put(lecturers[i].name, list);
        }

        //Clone a new set so we can remove form it while keep the list
        HashSet<String> assign_courses = (HashSet<String>) courses.clone();
        System.out.println(assign_courses);
        for (int i = 0; i < lecturers.length; i++) {
            HashSet<String> course = new HashSet<>();
            Iterator<String> itr = assign_courses.iterator();
            for (int a = 0; a < ReadConfiguration.courses_of_lecture; a++) {
                while (itr.hasNext()) {
                    String course_code = itr.next();
                    for (String banned : (HashSet<String>) black_list.get(lecturers[i].name)) {
                        //if the course this teacher could not teach, then move to next course
                        if (banned.equals(course_code)) {
                            if (itr.hasNext()) {
                                course_code = itr.next();
                            }
                        }
                    }
                    course.add(course_code);
                    itr.remove();
                    break;
                }
            }
            lectures_with_courses.put(lecturers[i].name, course);
        }

        //Print the courses that already opened this term base on course assigned
        String out_courses_opened_str = "";
        PrintStream out_courses_opened = new PrintStream("src/other/Courses opened.csv");

        out_str += "\nLecture name,Assigned courses\n";
        for (String key : lectures_with_courses.keySet()) {
            out_str += key;
            for (String course_code : (HashSet<String>) lectures_with_courses.get(key)) {
                out_str += "," + course_code;
                if (score.get(course_code) != null && score.get(course_code) != 0) {
                    //Use 1 / score to make the biggest became smallest
                    out_courses_opened_str += course_code + "," + (int) (1.0 / score.get(course_code) * 500) + "\n";
                } else {
                    out_courses_opened_str += course_code + "," + score.get(course_code) + "\n";
                }
            }
            out_str += "\n";
        }
        out.print(out_str);
        out_courses_opened.print(out_courses_opened_str.substring(0, out_courses_opened_str.length() - 1));

        out.close();
        out_courses_opened.close();
    }

    private void form2() throws IOException {
        PrintStream out;
        String out_str = "";
        //Count is fot display the progress
        double count = 1;
        double percent = 0;
        //Reading major electives courses for decide whether output it or not
        HashSet<String> major_electives_course_code = new HashSet<>();
        Scanner in = new Scanner(new File("src/other/Major electives.csv"));
        while (in.hasNextLine()) {
            String line = in.nextLine();
            String[] split = line.split(",");
            for (int i = 1; i < split.length; i++) {
                major_electives_course_code.add(split[i]);
            }
        }


        for (String course : courses) {
            percent = (count / courses.size()) * 100 - 0.1;
            DecimalFormat df = new DecimalFormat("#.0");
            System.out.println("Processing output student list " + df.format(percent) + "%");
            boolean is_elective = false;
            //Check the course if is elective out not first
            for (String major_course_code : major_electives_course_code) {
                if (course.equals(major_course_code)) {
                    is_elective = true;
                }
            }

            if (!is_elective) {
                //If it is not, then start to output
                out = new PrintStream("src/output/studentList/" + course + ".csv");
                //Get the course name by course code
                out_str += course + "," + ReadCurriculum.getCourseName(course) + "\nID,Name\n";

                //Read the courses that student can take
                HashMap<Integer, HashSet> courses_can_take = new Calculate().checkAvailableCourses();
                for (int id : courses_can_take.keySet()) {
                    for (String course_code : (HashSet<String>) courses_can_take.get(id)) {
                        //If the student can take this course, then add them to here
                        if (course_code.contains(course)) {
                            out_str += id + "," + new ReadStudentProfile().getName(id) + "\n";
                        }
                    }
                }
                out.println(out_str);
                out_str = "";

            }
            count++;
        }
    }

    private void form3() throws IOException {
        PrintStream out = new PrintStream("src/output/StudentsSuggestion.csv");
        String out_str = ",,1st,1st,2nd,2nd\n";

        //Same list as form1
        Calculate c = new Calculate();
        HashMap<String, Integer> score = c.calculate();
        Comparator<Map.Entry<String, Integer>> valueComparator = new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return o2.getValue() - o1.getValue();
            }
        };
        this.score_list = new ArrayList<>(score.entrySet());
        Collections.sort(score_list, valueComparator);

        HashMap<Integer, HashSet> courses_can_take = new Calculate().checkAvailableCourses();
        for (int id : courses_can_take.keySet()) {
            //Check the courses that the student can take
            out_str += id + "," + new ReadStudentProfile().getName(id);
            //Court is to count how many course are suggested
            int count = 0;

            for (String course_code : (HashSet<String>) courses_can_take.get(id)) {
                for (Map.Entry<String, Integer> entry : score_list) {
                    //Suggest the course base on score of the course
                    if (course_code.contains(entry.getKey())) {
                        out_str += "," + entry.getKey();
                    }
                }
                count++;
                //Base on the courses a student can take
                if (count == ReadConfiguration.courses_of_student) {
                    break;
                }
            }
            out_str += "\n";
        }

        out.println(out_str);
    }
}
