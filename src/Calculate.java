import java.io.IOException;
import java.util.*;

public class Calculate {
    //String is the key of years
    private HashMap<String, HashSet> curriculum = new HashMap<>();
    private HashMap<Integer, String> student_year = new HashMap<>();
    private HashMap<Integer, Integer> courses_taken_num = new HashMap<>();
    private HashMap<String, Integer> total_courses_num = new HashMap<>();
    private HashMap<String, Integer> students_num = new HashMap<>();
    private HashSet<String> electives;

    private void getCurriculum() throws IOException {
        //Read curriculum first
        ReadCurriculum rc = new ReadCurriculum();
        Curriculum[] curriculum = rc.read();
        this.electives = rc.readElectives();
        for (int i = 0; i < curriculum.length; i++) {
            //Using the set to make the compare after easier
            HashSet<String> courses = new HashSet<>();

            for (int a = 0; a < curriculum[i].course.length; a++) {
                //Get all the courses for array of courses under the obj curriculum and out them to th set
                courses.add(curriculum[i].course[a].course_code);
            }
            //Put the final set to the map
            this.curriculum.put(curriculum[i].year, courses);
            this.total_courses_num.put(curriculum[i].year, curriculum[i].course.length);
        }
    }

    //The reason why using void is because HashMap are referencing
    private void courseSubstitution(HashMap<Integer, HashSet> students_course_not_take) throws IOException {
        //Read substitution file first
        Substitution substitution = new ReadCourseSubstitution().read();
        for (int id : students_course_not_take.keySet()) {
            //Create a new set to store the course after substitute
            HashSet<String> course_not_take_subs = new HashSet<>();
            //Check the student is using the old curriculum or nor
            if (student_year.get(id).equals(substitution.from_year)) {
                //Get the courses he not take
                for (String course_code : (HashSet<String>) students_course_not_take.get(id)) {
                    //Check the course current working with is in the substitution file or not
                    if (substitution.substitution.get(course_code) != null) {
                        //If it is, then put the new one in to the set
                        course_not_take_subs.add(substitution.substitution.get(course_code));
                    } else {
                        //If it is not, them put the course code in to the set directly
                        course_not_take_subs.add(course_code);
                    }
                }
                //Finally put the new set in to the Map
                students_course_not_take.put(id, course_not_take_subs);
            }
        }
    }

    //For remove the electives courses since they are opening every term randomly
    private void removeElectives(HashMap<Integer, HashSet> students_course_not_take) {
        for (int id : students_course_not_take.keySet()) {
            //Get the courses that student not take and remove from it
            students_course_not_take.get(id).removeAll(electives);
        }
    }

    private HashMap<String, Integer> getBasicCourse() throws IOException {
        HashMap<String, Integer> score = new HashMap<>();
        Curriculum[] curriculum = new ReadCurriculum().read();
        //First get curriculum
        for (int i = 1; i < curriculum.length; i++) {
            //If is current year's curriculum
            if (curriculum[i].year.contains(ReadConfiguration.current_curriculum)) {
                for (int a = 0; a < curriculum[i].course.length; a++) {
                    //Check those have pre conditions
                    if (curriculum[i].course[a].pre.length > 0) {
                        for (int b = 0; b < curriculum[i].course[a].pre.length; b++) {
                            //Search im the map that the course is already exist
                            if ((score.get(curriculum[i].course[a].pre[b])) != null) {
                                //Not exist put 1
                                score.put(curriculum[i].course[a].pre[b], score.get(curriculum[i].course[a].pre[b]) + 1);
                            } else {
                                //Exist, +1
                                score.put(curriculum[i].course[a].pre[b], 1);
                            }
                        }
                    }
                }
            }
        }

        //Check again but this time to add more score to the more basic one. i.e. Add ITE220 score to INT120
        for (String course_code : score.keySet()) {
            for (int i = 1; i < curriculum.length; i++) {
                if (curriculum[i].year.contains(ReadConfiguration.current_curriculum)) {
                    for (int a = 0; a < curriculum[i].course.length; a++) {
                        if (curriculum[i].course[a].pre.length > 0) {
                            for (int b = 0; b < curriculum[i].course[a].pre.length; b++) {
                                if (course_code.equals(curriculum[i].course[a].pre[b])) {
                                    if (score.get(curriculum[i].course[a].course_code) != null) {
                                        //Add ITE220 scores to ITE120
                                        score.put(course_code, score.get(course_code) + score.get(curriculum[i].course[a].course_code));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return score;
    }

    private boolean graduationCheck(int id) {
        boolean is_about_to_graduate = false;
        if ((total_courses_num.get(student_year.get(id)) - electives.size() - courses_taken_num.get(id) < 5)) {
            is_about_to_graduate = true;
//            System.out.println("No." + id + " is about to graduate");
        } else {
//            System.out.println("No." + id + " still has more courses to learn");
        }
        return is_about_to_graduate;
    }

    public HashMap<Integer, HashSet> checkAvailableCourses() throws IOException {
        //Read all the students profile first
        ReadStudentProfile rs = new ReadStudentProfile();
        Student[] students = rs.read();

        getCurriculum();

        //Using a map to collect every students course that taken for compare after
        HashMap<Integer, HashSet> student_course_taken = new HashMap<>();
        for (int i = 0; i < students.length; i++) {
            student_year.put(students[i].ID, students[i].year);
            //Using set to collect one student's courses
            HashSet<String> courses_taken = new HashSet<>();
            for (int a = 0; a < students[i].course.length; a++) {
                //Have to create a new set and get from the curriculum in order to use for each
//                HashSet<String> nset = curriculum.get(students[i].year);
                //Force the object convert to HashSet<String>. The reason why it is an obj is because <String> is defined by user. Java does not know the type inside HashSet
                for (String course_code : (HashSet<String>) curriculum.get(students[i].year)) {
                    //Use for each to check the courses that the student taken and only collect those belong to IT
                    if (course_code.equals(students[i].course[a].course_code)) {
                        courses_taken.add(course_code);
                        //Then count how many courses he already taken
                        if (courses_taken_num.get(students[i].ID) == null) {
                            courses_taken_num.put(students[i].ID, 1);
                        } else {
                            courses_taken_num.put(students[i].ID, courses_taken_num.get(students[i].ID) + 1);
                        }
                    }
                }
            }
            //Finally put every students to the map
            student_course_taken.put(students[i].ID, courses_taken);
        }
        //Create a new map for checking which courses that students not take yet
        HashMap<Integer, HashSet> students_course_not_take = new HashMap<>();

        for (Integer id : student_course_taken.keySet()) {
//            HashSet<String> nset = student_course_taken.get(id);
            //A new set here is copy a curriculum and compare it with course taken. If is match, then remove it. Finally we will get the courses that students not taken. Again don't forget to convert
            HashSet<String> course_not_take = (HashSet<String>) curriculum.get(student_year.get(id)).clone();
            // full_curriculum = curriculum.get(student_year.get(id));

            //Use iterator to loop the data in the set and compare to the curriculum
            Iterator<String> itr = course_not_take.iterator();
            while (itr.hasNext()) {
                String course_code = itr.next();
                for (String course_taken : (HashSet<String>) student_course_taken.get(id)) {
                    if (course_taken.equals(course_code)) {
                        itr.remove();
                    }
                }
            }
            students_course_not_take.put(id, course_not_take);
        }
        //Considering the substitution

        courseSubstitution(students_course_not_take);
        //Remove all the electives courses since they are randomly open
        removeElectives(students_course_not_take);

        HashMap<Integer, HashSet> students_courses_can_take = (HashMap<Integer, HashSet>) students_course_not_take.clone();
        //Get all the pre conditions base on the year of the student in
        for (int id : student_year.keySet()) {
            //Read the curriculum first. The curriculum here contains all the courses
            Curriculum[] curriculum = new ReadCurriculum().read();
            for (int i = 0; i < curriculum.length; i++) {
                //Check the year that the students in and only load that year's course
                if (curriculum[i].year.equals(student_year.get(id))) {
                    for (int a = 0; a < curriculum[i].course.length; a++) {
                        //If the course has pre conditions, then move on
                        if (curriculum[i].course[a].pre.length > 0) {
                            //If this student not take the courses that have the pre condition, then move on
                            if (students_course_not_take.get(id).contains(curriculum[i].course[a].course_code)) {
                                for (int b = 0; b < curriculum[i].course[a].pre.length; b++) {
                                    //If the student not take the pre courses yet, than remove the courses from courses that he can take
                                    if (!student_course_taken.get(id).contains(curriculum[i].course[a].pre[b])) {
                                        students_courses_can_take.get(id).remove(curriculum[i].course[a].course_code);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        for (int id : student_course_taken.keySet()) {
            //System.out.println(id+" "+student_course_taken.get(id));
        }
        return students_courses_can_take;
    }

    public HashMap<String, Integer> calculate() throws IOException {
        getBasicCourse();
        //Get all the courses that all students can take
        HashMap<Integer, HashSet> students_courses_can_take = checkAvailableCourses();
        //To store the score
        HashMap<String, Integer> score = new HashMap<>();

        //The string is course code and Integer is how many times dose the course appears in pre condition
        HashMap<String, Integer> basic_courses = getBasicCourse();
        for (int id : students_courses_can_take.keySet()) {
            Iterator itr = students_courses_can_take.get(id).iterator();
            if (graduationCheck(id)) {
                //For students about to graduate
                while (itr.hasNext()) {
                    //Processing course a student can take
                    String course_code = (String) itr.next();
                    //If this course already exist then add score
                    if (score.get(course_code) != null) {
                        score.put(course_code, score.get(course_code) + ReadConfiguration.add_score_for_graduation);
                    } else {
                        //If it is not, create it
                        score.put(course_code, ReadConfiguration.add_score_for_graduation);
                    }

                    //Count the number of students in this course
                    if (students_num.get(course_code) == null) {
                        students_num.put(course_code, 1);
                    } else {
                        students_num.put(course_code, students_num.get(course_code) + 1);
                    }

                }
            } else {
                //For normal students
                while (itr.hasNext()) {
                    String course_code = (String) itr.next();
                    if (score.get(course_code) != null) {
                        score.put(course_code, score.get(course_code) + ReadConfiguration.add_score_for_normal_student);
                    } else {
                        score.put(course_code, ReadConfiguration.add_score_for_normal_student);
                    }

                    if (students_num.get(course_code) == null) {
                        students_num.put(course_code, 1);
                    } else {
                        students_num.put(course_code, students_num.get(course_code) + 1);
                    }

                }
            }
        }


        for (String course_code : basic_courses.keySet()) {
            //Check basic course, if this course is basic course, add more score
            if (score.get(course_code) != null && score.get(course_code) > 0) {
                score.put(course_code, score.get(course_code) + (basic_courses.get(course_code) * ReadConfiguration.add_score_for_basic_course));
            } else {
                score.put(course_code, basic_courses.get(course_code) * ReadConfiguration.add_score_for_basic_course);
            }
        }


        if(ReadConfiguration.if_deduct_score_to_courses_opened){
            HashMap<String, Integer> courses_opened = new ReadCoursesOpened().read();
            for (String course_code : score.keySet()) {
                //If the course are opened last term
                if (courses_opened.get(course_code) != null && score.get(course_code) > 0) {
                    //Deduct score to the course
                    score.put(course_code, score.get(course_code) - courses_opened.get(course_code));
                }
            }
        }
        return score;
    }

    public HashMap<String, Integer> getStudentsNum() throws IOException {
        if (!this.students_num.isEmpty()) {
            return this.students_num;
        } else {
            calculate();
            return this.students_num;
        }
    }

}
