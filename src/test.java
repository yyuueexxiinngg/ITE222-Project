import org.apache.commons.io.*;

import java.io.*;
import java.util.Scanner;

public class test {
    public Curriculum[] ReadCurriculum() throws IOException {
        File folder = new File("src/courses");
        File[] listOfFiles = folder.listFiles();
        Curriculum[] curriculum = new Curriculum[listOfFiles.length];
        PrintStream out = new PrintStream("testing_out_curriculum.csv");

        for (int a = 0; a < listOfFiles.length; a++) {
            File file = listOfFiles[a];
            if (file.isFile() && file.getName().endsWith(".csv")) {
                curriculum[a] = new Curriculum();
                String content = FileUtils.readFileToString(file);
                String[] year_split = listOfFiles[a].getName().split(" ");

                curriculum[a].year = year_split[1].substring(0, year_split[1].indexOf('.'));

                System.out.println(curriculum[a].year);
                Scanner in;

                //Use Scanner to get inout from file
                in = new Scanner(content);
                //Out put file for testing
                //course_num is for counting how many courses are available in a specific year
                //-1 is because in the .csv file, the first line is like "IT 2014", not a course
                int course_num = -1;
                //FileReader and BufferedReader here is used for read the file and count the number of lines
                FileReader fr = new FileReader(file);
                BufferedReader br = new BufferedReader(fr);
                while (br.readLine() != null) {
                    course_num++;
                }

                //Create new obj of course
                Course[] courses = new Course[course_num];

                //This is the index of obj courses and the reason why is -1 is same as course_num
                int index = -1;
                String year = "";

                while (in.hasNextLine()) {
                    //course can start from index -1
                    if (index >= 0)
                        courses[index] = new Course();
                    //Read file line by line
                    String line = in.nextLine().trim();
                    //Use split to remove the "," in each line
                    String[] split = line.split(",");
                    //index -1 is the first line of the file like "IT 2016"
                    if (index == -1) {
                        year = split[0];
                    } else if (index >= 0) {
                        courses[index].year = year;
                        courses[index].course_code = split[0];
                        //Using loop is because there are something like "Pre ITE221,ITE475"
                        for (int i = 1; i < split.length; i++) {
                            courses[index].course_name += split[i];
                        }
                    }
                    index++;
                }

                curriculum[a].course = courses;
                //Output to a file to test
                /*for (int i = 0; i < courses.length; i++) {
                    System.out.println((i + 1) + " " + courses[i].year + " " + courses[i].course_code + " " + courses[i].course_name);
                    out.println((i + 1) + " " + courses[i].year + " " + courses[i].course_code + " " + courses[i].course_name);
                }*/
                in.close();
            }
        }

        String print = "";
        for (int i = 0; i < curriculum.length; i++) {
            for (int a = 0; a < curriculum[i].course.length; a++) {
                print += (i + 1) + " " + curriculum[i].course[a].year + " " + (a + 1) + " " + curriculum[i].course[a].course_code + " " + curriculum[i].course[a].course_name + "\n";
            }
        }
        out.println(print);
        out.close();
        return curriculum;
    }
}
