import java.io.*;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.*;

public class ReadStudentProfile {
    private Student[] readProfile() throws IOException {
        File folder = new File("src/students");
        File[] listOfFiles = folder.listFiles();
        Student[] students = new Student[listOfFiles.length];


        for (int i = 0; i < listOfFiles.length; i++) {
            File file = listOfFiles[i];
            if (file.isFile() && file.getName().endsWith(".csv")) {
                students[i] = new Student();
                //Remove ".csv" then using parseInt to convert String to Int
                students[i].ID = Integer.parseInt(file.getName().substring(0, file.getName().indexOf(".")));
                String content = FileUtils.readFileToString(file);
                Scanner in = new Scanner(content);
                String[] split;

                //When reading file, we need cut of those course with "F", I'm using credit to check whether pass the course or not. But transferred courses are no credit. So check is transferred or not first.
                Boolean is_tranferred = false;
                //Other thing is there is no credit for current semester, so we should also check it.
                int semester_num = 0;
                int semester_index = 0;
                //Course code is used to put all the course code founded in file together to easily count by split them again
                String course_code = "";
                while (in.hasNextLine()) {
                    String line = in.nextLine();
                    //Some files are contain " \" " between "," which would make split error. Here using replaceAll to remove those " .
                    line = line.replaceAll("\"", "");
                    //After remove " split them by ,
                    split = line.split(","); // Remove ","

                    //Here to check how many semester did the student take in order to check the current semester by comparing the semester_index with semester_num
                    if (split.length == 5 && split[4].contains("Semester")) {
                        semester_num++;
                    }
                }
                // After checking semester num, we need read the file again
                in = new Scanner(content);

                while (in.hasNextLine()) {
                    String line = in.nextLine();
                    line = line.replaceAll("\"", "");
                    split = line.split(","); // Remove ","

                    if (split.length == 5 && split[4].contains("Tranferred")) {
                        is_tranferred = true;
                    } else if (split.length == 5 && split[4].contains("Semester")) {
                        /* Pattern are like:
                        * Tranfeerd
                        * Course ***
                        * Semester ***
                        * We read line by line, so close here*/
                        is_tranferred = false;
                    }

                    for (int a = 0; a < split.length; a++) {
                        if (a == 4 && split[a].contains("Semester")) {
                            //Here to check which semester we are working on
                            semester_index++;
                        }
                        if (is_tranferred) {
                            if (a == 7 && !split[a].contains("COURSECODE")) {
                                course_code += split[a].trim() + ",";
                            }
                        } else {
                            if (a == 7 && !split[a].contains("COURSECODE")) {
                                course_code += split[a].trim() + ",";
                            } else if (a == 10 && !split[a].contains("CREDITTOTAL") && split[a].contains("-") && semester_index != semester_num) {
                                //When get an "F" , credit total would become "-". Don't forget to check if is the current semester.
                                //If there is an "F", then remove the last course
                                course_code = course_code.substring(0, course_code.lastIndexOf(","));

                                //Some student don't have transferred course, this would be out of index
                                if (course_code.indexOf(",") != -1) {
                                    course_code = course_code.substring(0, course_code.lastIndexOf(","));
                                }
                                course_code += ",";
                            }
                        }
                    }
                }
                //Split again
                split = course_code.split(",");
                students[i].course = new Course[split.length];
                for (int a = 0; a < split.length; a++) {
                    students[i].course[a] = new Course();
                    students[i].course[a].course_code = split[a];
                }
/*                for(int a=0;a<students[i].course.length;a++){
                    if(students[i].ID==14){
                        System.out.print(students[i].course[a].course_code+",");
                    }
                }*/
                in.close();
            }
        }
        return students;
    }

    public String getName(int id)throws IOException{
        Student[] students = read();
        for(int i=0;i<students.length;i++){
            if(id==students[i].ID){
                return students[i].name;
            }
        }
        return null;
    }


    public Student[] read() throws IOException {
        PrintStream out = new PrintStream("testing_out_student_profile.csv");
        Scanner in = new Scanner(new File("src/other/Studentlist-ITE222project.csv"));
        Student[] students = readProfile();
        String test = "";
        //Using line_index to skip the first line
        int line_index = 0;
        while (in.hasNextLine()) {
            String line = in.nextLine();
            String[] split = line.split(",");

            if (line_index > 0) {
                for (int i = 0; i < students.length; i++) {
                    if (students[i].ID == Integer.parseInt(split[0])) {

                        String coure_out_test = "";

                        students[i].name = split[1];
                        String pattern = "(\\d+)";
                        Pattern p = Pattern.compile(pattern);
                        Matcher m = p.matcher(split[2]);
                        if (m.find()) {
                            //group(0) is the entire string, group(1) is the result
                            students[i].year = m.group(1);
                        }

                        for (int a = 0; a < students[i].course.length; a++) {
                            if (m.find()) {
                                //group(0) is the entire string, group(1) is the result
                                students[i].course[a].year = m.group(1);
                            }
                            coure_out_test += students[i].course[a].course_code + ",";
                        }
                        test += "ID: " + students[i].ID + " Name: " + students[i].name + " Year:" + students[i].year + " Courses Num: " + students[i].course.length + " Courses:"  + coure_out_test + "\n";
                    }
                }
            }
            line_index++;
        }
        out.println(test);
        out.close();
        return students;
    }
}
