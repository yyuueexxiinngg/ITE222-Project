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

    public void checkAvailableCourses() throws IOException {
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
//                HashSet<String> nset = curriculum.get(students[i].year);
                //Force the object convert to HashSet<String>. The reason why it is an obj is because <String> is defined by user. Java does not know the type inside HashSet
                for(String course_code: (HashSet<String>)curriculum.get(students[i].year)){
                    //Use for each to check the courses that the student taken and only collect those belong to IT
                    if(course_code.equals(students[i].course[a].course_code)){
                        courses_taken.add(course_code);
                    }
                }
            }
            //Finally put every students to the map
            student_course_taken.put(students[i].ID, courses_taken);
        }
        //Create a new map for checking which courses that students not take yet
        HashMap<Integer,HashSet> student_course_not_take = new HashMap<>();

        System.out.println(curriculum.get("2014"));

        for(Integer id : student_course_taken.keySet()){
//            HashSet<String> nset = student_course_taken.get(id);
            //A new set here is copy a curriculum and compare it with course taken. If is match, then remove it. Finally we will get the courses that students not taken. Again don't forget to convert
            HashSet<String> course_not_take = (HashSet<String>) curriculum.get(student_year.get(id)).clone();
           // full_curriculum = curriculum.get(student_year.get(id));

            //Use iterator to loop the data in the set and compare to the curriculum
            Iterator<String> itr = course_not_take.iterator();
            while (itr.hasNext()){
                String course_code = itr.next();
                for(String course_taken : (HashSet<String>)student_course_taken.get(id)){
                    if(course_taken.equals(course_code)){
                        itr.remove();
                    }
                }
            }
            student_course_not_take.put(id,course_not_take);
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
