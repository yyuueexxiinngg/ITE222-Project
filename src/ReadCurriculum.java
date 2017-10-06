import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;
import java.util.Scanner;

public class ReadCurriculum {
    public Curriculum[] read() throws IOException {
        File folder = new File("src/courses");
        File[] listOfFiles = folder.listFiles();
        Curriculum[] curriculum = new Curriculum[listOfFiles.length];
        PrintStream out = new PrintStream("testing_out_curriculum_new.csv");

        for (int a = 0; a < listOfFiles.length; a++) {
            File file = listOfFiles[a];
            if (file.isFile() && file.getName().endsWith(".csv")) {
                curriculum[a] = new Curriculum();
                //String content = FileUtils.readFileToString(file);
                //Get the file name and use substring to take out the number of the year

                //Using regex to take out the year number from the file name
                String line_regex = listOfFiles[a].getName();
                //Match all the numbers
                String pattern = "(\\d+)";
                Pattern p = Pattern.compile(pattern);
                Matcher m = p.matcher(line_regex);
                if (m.find())
                    //group(0) is the entire string, group(1) is the result
                    curriculum[a].year = m.group(1);

                Scanner in;

                //Use Scanner to get inout from file
                in = new Scanner(file);
                //Out put file for testing
                //course_num is for counting how many courses are available in a specific year
                //-1 is because in the .csv file, the first line is like "IT 2014", not a course
                int course_num = -1;
                //FileReader and BufferedReader here is used for read the file and count the number of lines
                Scanner sc = new Scanner(file);

                FileReader fr = new FileReader(file);
                BufferedReader br = new BufferedReader(fr);
                while (br.readLine() != null) {
                    course_num++;
                }

                //Create new obj of course
                HashSet<Course> courses_set = new HashSet<Course>();
                Course[] courses = new Course[course_num];

                //This is the index of obj courses and the reason why is -1 is same as course_num
                int index = -1;
                String year = "";

                while (in.hasNextLine()) {
                    //course can't start from index -1
                    if (index >= 0)
                        courses[index] = new Course();
                    //read file line by line
                    String line = in.nextLine().trim();
                    //Use split to remove the "," in each line
                    String[] split = line.split(",");
                    //index -1 is the first line of the file like "IT 2016"
                    if (index == -1) {
                        year = split[0];
                    } else if (index >= 0) {
                        courses[index].year = year;
                        courses[index].course_code = split[0];
                        courses[index].course_name += split[1];
                        courses[index].pre = new String[split.length - 2];
                        //If split num > 2 (ITE222 , ProgrammingII , Pre: ITE221)
                        if (split.length > 2) {
                            //Using for loop to ensure that got those more than 1 pre correctly i = 2 means start from pre part
                            for (int i = 2; i < split.length; i++) {
                                courses[index].pre[i - 2] = split[i];
                            }
                        }
                    }
                    index++;
                }

                curriculum[a].course = courses;
                in.close();
            }
        }

        //Only print for testing
        String print = "";
        for (int i = 0; i < curriculum.length; i++) {
            for (int a = 0; a < curriculum[i].course.length; a++) {
                if (curriculum[i].course[a].pre.length > 0) {
                    String pre = "";
                    for (int b = 0; b < curriculum[i].course[a].pre.length; b++) {
                        pre += curriculum[i].course[a].pre[b]+",";
                    }
                    print += (i + 1) + " " + curriculum[i].course[a].year + " " + (a + 1) + " " + curriculum[i].course[a].course_code + " " + curriculum[i].course[a].course_name + " Pre:" + pre + "\n";
                } else {
                    print += (i + 1) + " " + curriculum[i].course[a].year + " " + (a + 1) + " " + curriculum[i].course[a].course_code + " " + curriculum[i].course[a].course_name + "\n";
                }
            }
        }
        out.println(print);
        out.close();
        return curriculum;
    }

    public HashSet<String> readElectives() throws IOException{
        HashSet<String> electives = new HashSet<>();
        Scanner in = new Scanner(new File("src/other/RemoveElectives.csv"));
        while(in.hasNextLine()){
            String line = in.nextLine();
            electives.add(line);
        }
        in.close();
        return electives;
    }

    public static String getCourseName(String course_code) throws IOException{
        File folder = new File("src/courses");
        File[] listOfFiles = folder.listFiles();
        for (int a = 0; a < listOfFiles.length; a++) {
            File file = listOfFiles[a];
            if(file.getName().contains(ReadConfiguration.current_curriculum)){
                Scanner in = new Scanner(file);
                while (in.hasNext()){
                    String line = in.nextLine();
                    String[] split = line.split(",");
                    if(split[0].contains(course_code)){
                        return split[1];
                    }
                }
            }
        }
        return null;
    }

}
