import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class Calculate {
    //String is the key of years
    HashMap<String, HashSet > curriculum = new HashMap<>();

    private void getCurriculum() throws IOException {
        //Read curriculum first
        ReadCurriculum rc = new ReadCurriculum();
        Curriculum[] curriculum = rc.read();
        for (int i = 0; i < curriculum.length; i++) {
            //Using the set to make the compare after easier
            HashSet<String> courses = new HashSet<>();

            for (int a = 0; a < curriculum[i].course.length; a++) {
                //Get all the courses for array of courses under the obj curriculum and out them to th set
                courses.add(curriculum[i].course[a].course_code);
            }
            //Put the final set to the map
            this.curriculum.put(curriculum[i].year, courses);
        }
    }

    public void checkAvaliableCourses() throws IOException {
        //Read all the students profile first
        ReadStudentProfile rs = new ReadStudentProfile();
        Student[] students = rs.read();
        getCurriculum();
        //System.out.println(curriculum.get("2014"));
        //Using a map to collect every students course that taken for compare after
        HashMap<Integer, HashSet> student_course_taken = new HashMap<>();
        HashMap<Integer,String> student_year = new HashMap<>();
        for (int i = 0; i < students.length; i++) {
            student_year.put(students[i].ID,students[i].year);
            //Using set to collect one student's courses
            HashSet<String> courses_taken = new HashSet<>();
            for (int a = 0; a < students[i].course.length; a++) {
                //Have to create a new set and get from the curriculum in order to use for each
                HashSet<String> nset = curriculum.get(students[i].year);
                for(String course_code: nset){
                    //Use for each to check the courses that the student taken and only collect those belong to IT
                    if(course_code.equals(students[i].course[a].course_code)){
                        courses_taken.add(course_code);
                    }
                }
            }
            //Finally put every students to the map
            student_course_taken.put(students[i].ID, courses_taken);
        }

        HashMap<Integer,HashSet> student_course_not_take = new HashMap<>();
        System.out.println(curriculum.get("2014"));

        for(Integer id : student_course_taken.keySet()){
            HashSet<String> nset = student_course_taken.get(id);
            HashSet<String> full_curriculum = (HashSet<String>) curriculum.get(student_year.get(id)).clone();
           // full_curriculum = curriculum.get(student_year.get(id));

            Iterator<String> itr = full_curriculum.iterator();
            while (itr.hasNext()){
                String course_code = itr.next();
                for(String course_taken : nset){
                    if(course_taken.equals(course_code)){
                        itr.remove();
                    }
                }
            }
            student_course_not_take.put(id,full_curriculum);
        }
        System.out.println(curriculum.get("2014"));
        System.out.println(student_course_taken.get(16));
        System.out.println(student_course_not_take.get(16));



        HashSet<String> course_not_take = curriculum.get("2014");



        for (int ID : student_course_taken.keySet()) {
          //  System.out.println(ID + " " + student_course_taken.get(ID));
        }
    }
}
