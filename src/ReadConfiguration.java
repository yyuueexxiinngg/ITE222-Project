import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class ReadConfiguration {
    static String current_curriculum;
    static int courses_of_lecture;
    public static void readConfiguration() throws IOException{
        Scanner in = new Scanner(new File("src/other/Configuration"));
        while (in.hasNext()){
            String line = in.nextLine();
            String[] split = line.split("=");
            if(line.contains("CURRENT_CURRICULUM")){
                current_curriculum = split[1];
            }else if(line.contains("COURSES_OF_LECTURER")){
                courses_of_lecture = Integer.parseInt(split[1]);
            }
        }
    }
}
